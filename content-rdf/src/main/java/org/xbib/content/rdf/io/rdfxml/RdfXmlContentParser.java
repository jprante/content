package org.xbib.content.rdf.io.rdfxml;

import org.xbib.content.rdf.Literal;
import org.xbib.content.rdf.RdfConstants;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentBuilderHandler;
import org.xbib.content.rdf.RdfContentBuilderProvider;
import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.rdf.RdfContentParser;
import org.xbib.content.rdf.RdfContentType;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.StandardRdfContentType;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.internal.DefaultLiteral;
import org.xbib.content.rdf.internal.DefaultTriple;
import org.xbib.content.rdf.io.xml.XmlHandler;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.content.resource.IRISyntaxException;
import org.xbib.content.resource.Node;
import org.xbib.content.xml.util.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RdfXmlParser is an admittedly convoluted hand-coded SAX parser for RDF/XML.
 * This is designed to be faster than wrapping Jena's parser and should obviate
 * the need to add Jena as a dependency. It should also be able to process
 * arbitrarily large RDF/XML files with minimal memory overhead, since unlike
 * Jena it does not have to store and index all the triples it encounters in a
 * model.
 * Note that the XMLLiteral datatype is not fully supported.
 * @param <R> parameter type
 */
public class RdfXmlContentParser<R extends RdfContentParams> implements RdfConstants, RdfContentParser<R> {

    private static final Logger logger = Logger.getLogger(RdfXmlContentParser.class.getName());

    private final Reader reader;

    private final Resource resource = new DefaultAnonymousResource();

    private XmlHandler<R> xmlHandler = new Handler();

    private RdfContentBuilderProvider<R> provider;

    private RdfContentBuilderHandler<R> rdfContentBuilderHandler;

    private RdfContentBuilder<R> builder;

    // counter for blank node generation
    private int bn = 0;

    public RdfXmlContentParser(InputStream in) throws IOException {
        this(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public RdfXmlContentParser(Reader reader) {
        this.reader = reader;
    }

    @Override
    public RdfContentType contentType() {
        return StandardRdfContentType.RDFXML;
    }

    public RdfXmlContentParser<R> setRdfContentBuilderProvider(RdfContentBuilderProvider<R> provider) {
        this.provider = provider;
        return this;
    }

    public RdfXmlContentParser<R> setRdfContentBuilderHandler(RdfContentBuilderHandler<R> rdfContentBuilderHandler) {
        this.rdfContentBuilderHandler = rdfContentBuilderHandler;
        return this;
    }

    @Override
    public RdfXmlContentParser<R> parse() throws IOException {
        try {
            parse(new InputSource(reader));
        } catch (SAXException ex) {
            throw new IOException(ex);
        }
        return this;
    }

    public RdfXmlContentParser<R> parse(InputSource source) throws IOException, SAXException {
        parse(XMLReaderFactory.createXMLReader(), source);
        return this;
    }

    public RdfXmlContentParser<R> parse(XMLReader reader, InputSource source) throws IOException, SAXException {
        if (provider != null) {
            builder = provider.newContentBuilder();
            builder.startStream();
        }
        if (xmlHandler != null) {
            reader.setContentHandler(xmlHandler);
        }
        reader.parse(source);
        if (builder != null) {
            if (rdfContentBuilderHandler != null) {
                rdfContentBuilderHandler.build(builder);
            }
            builder.endStream();
        }
        return this;
    }

    public RdfContentBuilder<R> getBuilder() {
        return builder;
    }

    public XmlHandler<R> getHandler() {
        return xmlHandler;
    }

    public RdfXmlContentParser<R> setHandler(XmlHandler<R> handler) {
        this.xmlHandler = handler;
        return this;
    }

    // get the most-specific language tag in scope
    private String getLanguage(Deque<Frame> stack) {
        String lang = "";
        Iterator<Frame> it = stack.descendingIterator();
        while (it.hasNext()) {
            Frame frame = it.next();
            if (frame.lang != null && !lang.startsWith(frame.lang)) {
                lang = frame.lang;
            }
        }
        return lang;
    }

    // get the xml:base in scope
    private String getBase(Deque<Frame> stack) {
        String base = "";
        Iterator<Frame> it = stack.descendingIterator();
        while (it.hasNext()) {
            Frame frame = it.next();
            if (frame.base != null) {
                base = frame.base;
            }
        }
        return base;
    }

    // is our parent a predicate?
    private boolean inPredicate(Deque<Frame> stack) {
        boolean ip = false;
        Iterator<Frame> it = stack.descendingIterator();
        while (it.hasNext()) {
            Frame frame = it.next();
            ip = frame.isPredicate;
        }
        return ip;
    }


    // if we're in a predicate, get its frame
    private Frame parentPredicateFrame(Deque<Frame> stack) throws SAXException {
        if (inPredicate(stack)) {
            Frame predicateFrame = null;
            Iterator<Frame> it = stack.descendingIterator();
            while (it.hasNext()) {
                Frame frame = it.next();
                if (frame.isPredicate) {
                    predicateFrame = frame;
                }
            }
            return predicateFrame;
        } else {
            throw new SAXException("internal parser error: cannot find enclosing predicate");
        }
    }

    // get the uriRef of the predicate we're in
    private IRI parentPredicate(Deque<Frame> stack) throws SAXException {
        Frame ppFrame = parentPredicateFrame(stack);
        return ppFrame != null ? ppFrame.node : null;
    }

    // get the nearest ancestor subject frame
    private Frame ancestorSubjectFrame(Deque<Frame> stack) throws SAXException {
        Frame subjectFrame = null;
        Iterator<Frame> it = stack.descendingIterator();
        while (it.hasNext()) {
            Frame frame = it.next();
            if (frame.isSubject) {
                subjectFrame = frame;
            }
        }
        return subjectFrame;
    }

    // get the nearest ancestor subject
    private IRI ancestorSubject(Deque<Frame> stack) throws SAXException {
        Frame subjectFrame = ancestorSubjectFrame(stack);
        return subjectFrame != null ? subjectFrame.node : null;
    }


    private Resource blankNode() {
        return new DefaultAnonymousResource("b" + (bn++));
    }

    private Resource blankNode(String s) {
        return new DefaultAnonymousResource(s);
    }

    /*
     * Resolve relative uri's against the in-scope xml:base URI.
     * IRI creation/parsing comes with very weak performance.
     */
    private IRI resolve(String uriString, Deque<Frame> stack) {
        IRI uri;
        try {
            uri = IRI.create(uriString);
        } catch (IRISyntaxException e) {
            logger.log(Level.FINE, e.getMessage(), e);
            // illegal URI, try repair
            uri = IRI.create(uriString
                    .replace(" ", "%20")
                    .replace("\"", "%22")
                    .replace("[", "%5B")
                    .replace("]", "%5D")
                    .replace("<", "%3C")
                    .replace(">", "%3E")
                    .replace("|", "%7C")
                    .replace("`", "%60")
                    .replace("\u0098", "") // Unicode "START OF STRING"
                    .replace("\u009c", "") // Unicode "STRING TERMINATOR"
            );
        }
        if (uri.isAbsolute()) {
            return uri;
        } else {
            return IRI.create(getBase(stack) + uriString);
        }
    }

    /**
     * The complicated logic to determine the subject URI ref.
     */
    private void getSubjectNode(Frame frame, Deque<Frame> stack, Attributes attrs) throws SAXException {
        String about = attrs.getValue(RDF_STRING, ABOUT);
        if (about != null) {
            frame.node = resolve(about, stack);
            if (provider != null) {
                try {
                    if (rdfContentBuilderHandler != null) {
                        rdfContentBuilderHandler.build(builder);
                    }
                    builder = provider.newContentBuilder();
                    builder.receive(frame.node);
                } catch (IOException e) {
                    throw new SAXException(e);
                }
            }
        }
        String nodeId = attrs.getValue(RDF_STRING, NODE_ID);
        if (nodeId != null) {
            if (frame.node != null) {
                throw new SAXException("ambiguous use of rdf:nodeID");
            }
            frame.node = blankNode(nodeId).id();
        }
        String rdfId = attrs.getValue(RDF_STRING, "ID");
        if (rdfId != null) {
            if (frame.node != null) {
                throw new SAXException("ambiguous use of rdf:ID");
            }
            frame.node = IRI.create(getBase(stack) + "#" + rdfId);
        }
        if (frame.node == null) {
            frame.node = blankNode().id();
        }
        frame.isSubject = true;
    }

    /**
     * The complicated logic to deal with attributes with rdf:resource, nodeID attrs.
     */
    private IRI getObjectNode(Deque<Frame> stack, Attributes attrs) throws SAXException {
        IRI node = null;
        String resource = attrs.getValue(RDF_STRING, "resource");
        if (resource != null) {
            node = resolve(resource, stack);
        }
        String nodeId = attrs.getValue(RDF_STRING, NODE_ID);
        if (nodeId != null) {
            if (node != null) {
                throw new SAXException("ambiguous use of rdf:nodeID");
            }
            node = blankNode(nodeId).id();
        }
        return node;
    }

    // here we're in a literal so we have to produce reasonably-canonical XML
    // representation of this start tag
    private void xmlLiteralStart(StringBuilder out, String ns, String qn, Attributes attrs) {
        out.append("<").append(qn);
        Map<String, String> pfxMap = new HashMap<>();
        for (int i = -1; i < attrs.getLength(); i++) {
            String aQn;
            String aNs;
            if (i < 0) {
                aQn = qn;
                aNs = ns;
            } else {
                aQn = attrs.getQName(i);
                aNs = attrs.getURI(i);
            }
            if (!"".equals(aNs)) {
                String pfx = aQn.replaceFirst(":.*", "");
                pfxMap.put(pfx, aNs);
            }
        }
        for (Map.Entry<String, String> pfxMapping : pfxMap.entrySet()) {
            out.append(" xmlns:").append(pfxMapping.getKey()).append("=\"").append(pfxMapping.getValue()).append("\"");
        }
        for (int i = 0; i < attrs.getLength(); i++) {
            String aQn = attrs.getQName(i);
            String aVal = attrs.getValue(i);
            out.append(" ").append(aQn).append("=\"").append(XMLUtil.escape(aVal)).append("\"");
        }
        out.append(">");
    }

    // produce a reasonably canonical endStream tag
    private void xmlLiteralEnd(StringBuilder out, String qn) {
        out.append("</").append(qn).append(">");
    }

    // if a language tag is in scope, apply it to the literal
    private Literal withLanguageTag(Literal l, Deque<Frame> stack) {
        String lang = getLanguage(stack);
        if (!"".equals(lang)) {
            l.lang(lang);
        }
        return l;
    }

    /**
     * Allow to override this method to control triple stream generation.
     * @param triple a triple
     * @throws IOException if yield does not work
     */
    protected void yield(Triple triple) throws IOException {
        if (builder != null) {
            builder.receive(triple);
        }
    }

    private void yield(Object s, Object p, Object o) throws IOException {
        yield(new DefaultTriple(resource.newSubject(s), resource.newPredicate(p), resource.newObject(o)));
    }

    // produce a (possibly) reified triple
    private void yield(Object s, IRI p, Object o, IRI reified) throws IOException {
        yield(s, p, o);
        if (reified != null) {
            yield(reified, RDF_TYPE, RDF_STATEMENT);
            yield(reified, RDF_SUBJECT, s);
            yield(reified, RDF_PREDICATE, p);
            yield(reified, RDF_OBJECT, o);
        }
    }

    private static class Frame {
        IRI node = null; // the subject/object
        String lang = null; // the language tag
        String base = null; // the xml:base
        String datatype = null; // a predicate's datatype
        IRI reification = null; // when reifying, the triple's uriRef
        List<IRI> collection = null; // for parseType=Collection, the items
        Triple collectionHead = null; // for parseType=Collection, the head triple
        boolean isSubject = false; // is there a subject at this frame
        boolean isPredicate = false; // is there a predicate at this frame
        boolean isCollection = false; // is the predicate at this frame a collection
        int li = 1;
    }

    private class Handler extends DefaultHandler implements XmlHandler<R> {

        private final Deque<Frame> stack = new ArrayDeque<>();

        private StringBuilder pcdata = null;

        private StringBuilder xmlLiteral = null;

        private IRINamespaceContext namespaceContext;

        private int literalLevel = 0; // level in XMLLiteral

        @Override
        public XmlHandler<R> setNamespaceContext(IRINamespaceContext namespaceContext) {
            this.namespaceContext = namespaceContext;
            return this;
        }

        @Override
        public IRINamespaceContext getNamespaceContext() {
            return namespaceContext;
        }

        @Override
        public Handler setDefaultNamespace(String prefix, String namespaceURI) {
            return this;
        }

        @Override
        public XmlHandler<R> setBuilder(RdfContentBuilder<R> builder) {
            return this;
        }

        @Override
        public void startPrefixMapping(String prefix, String uri)
                throws SAXException {
            if (builder != null) {
                builder.startPrefixMapping(prefix, uri);
            }
        }

        @Override
        public void endPrefixMapping(String prefix)
                throws SAXException {
            if (builder != null) {
                builder.endPrefixMapping(prefix);
            }
        }

        @Override
        public void startElement(String ns, String name, String qn, Attributes attrs) throws SAXException {
            try {
                if (literalLevel > 0) { // this isn't RDF; we're in an XMLLiteral
                    literalLevel++;
                    // now produce an equivalent start tag
                    xmlLiteralStart(xmlLiteral, ns, qn, attrs);
                } else { // we're in RDF
                    Frame frame = new Frame();
                    IRI iri = IRI.create(ns + name);
                    frame.lang = attrs.getValue("xml:lang");
                    frame.base = attrs.getValue("xml:base");
                    if (expectSubject(stack)) {
                        if (!iri.equals(RDF_RDF)) {
                            // get this resource's ID
                            getSubjectNode(frame, stack, attrs);
                            // we have the subject
                            if (!iri.equals(RDF_DESCRIPTION)) {
                                // this is a typed node, so assert the type
                                yield(frame.node, RDF_TYPE, iri);
                            }
                            // now process attribute-specified predicates
                            for (int i = 0; i < attrs.getLength(); i++) {
                                String aQn = attrs.getQName(i);
                                String aUri = attrs.getURI(i) + attrs.getLocalName(i);
                                String aVal = attrs.getValue(i);
                                if (!aUri.startsWith(RDF_STRING) && !aQn.startsWith("xml:")) {
                                    yield(frame.node, IRI.create(aUri), aVal);
                                }
                            }
                            // is this node the value of some enclosing predicate?
                            if (inPredicate(stack)) {
                                // is the value of the predicate a collection?
                                if (isCollectionItem(stack)) {
                                    Frame ppFrame = parentPredicateFrame(stack);
                                    ppFrame.collection.add(frame.node);
                                } else { // not a collection
                                    // this subject is the value of its enclosing predicate
                                    yield(ancestorSubject(stack), parentPredicate(stack), frame.node);
                                }
                            }
                        }
                        // do not accumulate pcdata
                        pcdata = null;
                    } else { // expect predicate
                        frame.node = iri;
                        frame.isPredicate = true;
                        // handle reification
                        String reification = attrs.getValue(RDF_STRING, "ID");
                        if (reification != null) {
                            frame.reification = IRI.create(getBase(stack) + "#" + reification);
                        }
                        // handle container items
                        if (iri.equals(RDF_LI)) {
                            Frame asf = ancestorSubjectFrame(stack);
                            frame.node = IRI.create(RDF + "_" + asf.li);
                            asf.li++;
                        }
                        // parse attrs to see if the value of this pred is a uriref
                        IRI object = getObjectNode(stack, attrs);
                        if (object != null) {
                            yield(ancestorSubject(stack), frame.node, object, frame.reification);
                        } else {
                            // this predicate encloses pcdata, prepare to accumulate
                            pcdata = new StringBuilder();
                        }
                        // handle rdf:parseType="resource"
                        String parseType = attrs.getValue(RDF_STRING, "parseType");
                        if (parseType != null) {
                            switch (parseType) {
                                case "Resource":
                                    object = object == null ? blankNode().id() : object;
                                    yield(ancestorSubject(stack), frame.node, object, frame.reification);
                                    // perform surgery on the current frame
                                    frame.node = object;
                                    frame.isSubject = true;
                                    break;
                                case "Collection":
                                    frame.isCollection = true;
                                    frame.collection = new LinkedList<>();
                                    Resource s = resource.newSubject(ancestorSubject(stack));
                                    IRI p = resource.newPredicate(frame.node);
                                    Node o = resource.newObject(blankNode());
                                    frame.collectionHead = new DefaultTriple(s, p, o);
                                    pcdata = null;
                                    break;
                                case "Literal":
                                    literalLevel = 1; // enter into a literal
                                    xmlLiteral = new StringBuilder();
                                    // which means we shouldn't accumulate pcdata!
                                    pcdata = null;
                                    break;
                                default: // handle datatype
                                    frame.datatype = attrs.getValue(RDF_STRING, "datatype");
                                    break;
                            }
                        }
                        // now handle property attributes (if we do this, then this
                        // must be an empty element)
                        object = null;
                        for (int i = 0; i < attrs.getLength(); i++) {
                            String aQn = attrs.getQName(i);
                            IRI aUri = IRI.create(attrs.getURI(i) + attrs.getLocalName(i));
                            String aVal = attrs.getValue(i);
                            if ((aUri.toString().equals(RDF_TYPE.toString()) || !aUri.toString().startsWith(RDF_STRING))
                                    && !aQn.startsWith("xml:")) {
                                if (object == null) {
                                    object = blankNode().id();
                                    yield(ancestorSubject(stack), frame.node, object);
                                }
                                if (aUri.equals(RDF_TYPE)) {
                                    yield(object, RDF_TYPE, aVal);
                                } else {
                                    Literal value = withLanguageTag(new DefaultLiteral(aVal), stack);
                                    yield(object, aUri, value);
                                }
                            }
                        }
                        // if we had to generate a node to hold properties specified
                        // as attributes, then expect an empty element and therefore
                        // don't record pcdata
                        if (object != null) {
                            pcdata = null;
                        }
                    }
                    // finally, push the frame for use in subsequent callbacks
                    stack.push(frame);
                }
            } catch (IOException e) {
                throw new SAXException(e);
            }
        }

        @Override
        public void endElement(String ns, String name, String qn) throws SAXException {
            try {
                if (literalLevel > 0) { // this isn't RDF; we're in an XMLLiteral
                    literalLevel--;
                    if (literalLevel > 0) {
                        xmlLiteralEnd(xmlLiteral, qn);
                    }
                } else { // this is RDF
                    if (inPredicate(stack)) {
                        Frame ppFrame = parentPredicateFrame(stack);
                        // this is a predicate closing
                        if (xmlLiteral != null) { // it was an XMLLiteral
                            Literal value = new DefaultLiteral(xmlLiteral.toString()).type(RDF_XMLLITERAL);
                            yield(ancestorSubject(stack), parentPredicate(stack), value);
                            xmlLiteral = null;
                        } else if (pcdata != null) { // we have an RDF literal
                            IRI u = ppFrame.datatype == null ? null : IRI.create(ppFrame.datatype);
                            Literal value = withLanguageTag(new DefaultLiteral(pcdata.toString()).type(u), stack);
                            // deal with reification
                            IRI reification = ppFrame.reification;
                            yield(ancestorSubject(stack), ppFrame.node, value, reification);
                            // no longer collect pcdata
                            pcdata = null;
                        } else if (ppFrame.isCollection) { // deal with collections
                            if (ppFrame.collection.isEmpty()) {
                                // in this case, the value of this property is rdf:nil
                                yield(ppFrame.collectionHead.subject(),
                                        ppFrame.collectionHead.predicate(),
                                        resource.newObject(RDF_NIL));
                            } else {
                                yield(ppFrame.collectionHead);
                                Object prevNode = null;
                                Object node = ppFrame.collectionHead.object();
                                for (IRI item : ppFrame.collection) {
                                    if (prevNode != null) {
                                        yield(prevNode, RDF_REST, node);
                                    }
                                    yield(node, RDF_FIRST, item);
                                    prevNode = node;
                                    node = blankNode().id();
                                }
                                yield(prevNode, RDF_REST, RDF_NIL);
                            }
                        }
                    }
                    stack.pop();
                }
            } catch (IOException e) {
                throw new SAXException(e);
            }
        }

        @Override
        public void characters(char[] chars, int start, int len) throws SAXException {
            if (literalLevel > 0) { // this isn't RDF; we're in an XMLLiteral
                XMLUtil.escape(xmlLiteral, chars, start, len);
            } else if (pcdata != null) { // we're in RDF, collecting an attribute value
                // accumulate char data
                for (int i = start; i < start + len; i++) {
                    pcdata.append(chars[i]);
                }
            }
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            if (literalLevel > 0) {
                xmlLiteral.append("<?").append(target).append(" ").append(data).append("?>");
            }
        }

        // do we expect to encounter a subject (rather than a predicate?)
        private boolean expectSubject(Deque<Frame> stack) {
            boolean b = true;
            Iterator<Frame> it = stack.descendingIterator();
            while (it.hasNext()) {
                Frame frame = it.next();
                b = !frame.isSubject;
            }
            return b;
        }

        // if we're looking at a subject, is it an item in a Collection?
        private boolean isCollectionItem(Deque<Frame> stack) throws SAXException {
            if (inPredicate(stack)) {
                Frame predicateFrame = parentPredicateFrame(stack);
                return predicateFrame != null && predicateFrame.isCollection;
            } else {
                return false;
            }
        }
    }
}
