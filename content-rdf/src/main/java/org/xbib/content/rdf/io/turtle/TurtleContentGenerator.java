package org.xbib.content.rdf.io.turtle;

import org.xbib.content.rdf.Literal;
import org.xbib.content.rdf.RdfConstants;
import org.xbib.content.rdf.RdfContentGenerator;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.rdf.io.xml.XmlConstants;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 */
public class TurtleContentGenerator implements RdfContentGenerator<TurtleContentParams>, Flushable {

    private static final char LF = '\n';
    private static final char TAB = '\t';
    private static final String TYPE = RdfConstants.NS_URI + "type";
    private final Writer writer;
    private boolean sameResource;

    private boolean sameProperty;

    private Resource lastSubject;

    private IRI lastPredicate;

    private Node lastObject;

    private final LinkedList<Resource> embedded;

    private final LinkedList<Triple> triples;

    private Triple triple;

    private boolean nsWritten;

    private final StringBuilder sb;

    private Resource resource;

    private boolean closed;

    private TurtleContentParams params = TurtleContentParams.TURTLE_CONTENT_PARAMS;

    TurtleContentGenerator(OutputStream out) throws IOException {
        this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    TurtleContentGenerator(Writer writer) throws IOException {
        this.writer = writer;
        this.resource = new DefaultAnonymousResource();
        this.nsWritten = false;
        this.sameResource = false;
        this.sameProperty = false;
        this.triples = new LinkedList<>();
        this.embedded = new LinkedList<>();
        this.sb = new StringBuilder();
    }

    @Override
    public TurtleContentParams getParams() {
        return params;
    }

    @Override
    public TurtleContentGenerator setParams(TurtleContentParams params) {
        this.params = params;
        return this;
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            if (!resource.isEmpty()) {
                receive(resource);
            }
            writer.close();
            closed = true;
        }
    }

    @Override
    public TurtleContentGenerator receive(IRI iri) throws IOException {
        if (iri != null && !iri.equals(resource.id())) {
            receive(resource);
            resource = new DefaultResource(iri);
        }
        return this;
    }

    @Override
    public TurtleContentGenerator endStream() {
        return this;
    }

    @Override
    public RdfContentGenerator<TurtleContentParams> receive(Resource resource) throws IOException {
        for (Triple t : resource.triples()) {
                writeTriple(t);
        }
        while (!embedded.isEmpty()) {
            closeEmbeddedResource();
        }
        if (sb.length() > 0) {
            sb.append('.').append(LF);
        }
        if (getParams().isWriteNamespaceContext() && !nsWritten) {
            writeNamespaces();
        }
        writer.write(sb.toString());
        sb.setLength(0);
        return this;
    }

    @Override
    public TurtleContentGenerator receive(Triple triple) {
        // Writing triple in order as they come in is a bad idea.
        // Add triple to resource and write them later in sorted, more compact order.
        resource.add(triple);
        return this;
    }

    @Override
    public TurtleContentGenerator startStream() {
        return this;
    }

    @Override
    public RdfContentGenerator<TurtleContentParams> setBaseUri(String baseUri) {
        startPrefixMapping("", baseUri);
        return this;
    }

    @Override
    public TurtleContentGenerator startPrefixMapping(String prefix, String uri) {
        if (prefix == null || prefix.isEmpty() || XmlConstants.XML_SCHEMA_URI.equals(uri)) {
            return this;
        }
        getParams().getNamespaceContext().addNamespace(prefix, uri);
        return this;
    }

    @Override
    public TurtleContentGenerator endPrefixMapping(String prefix) {
        return this;
    }

    private TurtleContentGenerator writeNamespaces() throws IOException {
        nsWritten = false;
        for (Map.Entry<String, String> entry : getParams().getNamespaceContext().getNamespaces().entrySet()) {
            if (entry.getValue().length() > 0) {
                String nsURI = entry.getValue();
                if (!RdfConstants.NS_URI.equals(nsURI)) {
                    writer.write("@prefix "
                            + entry.getKey()
                            + ": <"
                            + encodeURIString(nsURI)
                            + "> ."
                            + LF);
                    nsWritten = true;
                }
            }
        }
        if (nsWritten) {
            writer.write(LF);
        }
        return this;
    }

    private void writeTriple(Triple stmt) throws IOException {
        this.triple = stmt;
        Resource subject = stmt.subject();
        IRI predicate = stmt.predicate();
        Node object = stmt.object();
        if (subject == null || predicate == null) {
            return;
        }
        boolean sameSubject = subject.equals(lastSubject);
        boolean samePredicate = predicate.equals(lastPredicate);
        if (sameSubject) {
            if (samePredicate) {
                // write same predicates on same line
                sb.append(", ");
                writeObject(object);
            } else {
                // indent
                if (!(lastObject instanceof Resource && ((Resource) lastObject).isEmbedded())) {
                    sb.append(';').append(LF);
                    writeIndent(1);
                }
                writeIndent(embedded.size());
                writePredicate(predicate);
                writeObject(object);
            }
        } else {
            // un-indent
            // the challgene here is to find the levels to un-indent
            Resource r = embedded.isEmpty() ? null : embedded.peek();
            boolean closeEmbedded = lastSubject != null
                    && lastSubject.isEmbedded()
                    && !subject.equals(r);
            if (closeEmbedded) {
                Resource tmp = closeEmbeddedResource();
                while (tmp != null && !embedded.isEmpty() && !embedded.peek().equals(subject)) {
                    tmp = closeEmbeddedResource();
                }
            }
            if (lastSubject != null) {
                if (sameResource) {
                    if (sameProperty) {
                        sb.append(',');
                    } else {
                        sb.append(';').append(LF);
                        writeIndent(embedded.size() + 1);
                    }
                } else {
                    if (sameProperty || closeEmbedded) {
                        sb.append(";").append(LF);
                        writeIndent(1);
                    }
                    writeIndent(embedded.size());
                }
            }
            if (!sameResource) {
                writeSubject(subject);
            }
            if (!sameProperty) {
                writePredicate(predicate);
            }
            writeObject(object);
        }
    }

    private void writeSubject(Resource subject) throws IOException {
        if (subject.id() == null) {
            sb.append("<> ");
            return;
        }
        if (!subject.isEmbedded()) {
            sb.append('<').append(subject.toString()).append("> ");
        }
        lastSubject = subject;
    }

    private void writePredicate(IRI predicate) throws IOException {
        if (predicate == null) {
            sb.append("<> ");
            return;
        }
        String p = predicate.toString();
        if ("rdf:type".equals(p) || TYPE.equals(p)) {
            sb.append("a ");
        } else {
            writeURI(predicate);
            sb.append(" ");
        }
        lastPredicate = predicate;
    }

    private void writeObject(Node object) throws IOException {
        if (object instanceof Resource) {
            Resource r = (Resource) object;
            if (r.isEmbedded()) {
                openEmbeddedResource(r);
                sameResource = false;
                sameProperty = false;
            } else {
                writeURI(r.id());
            }
        } else if (object instanceof Literal) {
            writeLiteral((Literal) object);
        } else {
            throw new IllegalArgumentException("unknown value class: "
                    + (object != null ? object.getClass() : "<null>"));
        }
        lastObject = object;
    }

    private void openEmbeddedResource(Resource r) throws IOException {
        triples.push(triple);
        embedded.push(r);
        sb.append('[').append(LF);
        writeIndent(1);
    }

    private Resource closeEmbeddedResource() throws IOException {
        if (embedded.isEmpty()) {
            return null;
        }
        sb.append(LF);
        writeIndent(embedded.size());
        sb.append(']');
        Triple t = triples.pop();
        lastSubject = t.subject();
        lastPredicate = t.predicate();
        sameResource = lastSubject.equals(triple.subject());
        sameProperty = lastPredicate.equals(triple.predicate());
        return embedded.pop();
    }

    private void writeURI(IRI uri) throws IOException {
        if (uri == null) {
            sb.append("<>");
            return;
        }
        String abbrev = getParams().getNamespaceContext().compact(uri);
        if (!abbrev.equals(uri.toString())) {
            sb.append(abbrev);
            return;
        }
        if (getParams().getNamespaceContext().getNamespaceURI(uri.getScheme()) != null) {
            sb.append(uri.toString());
            return;
        }
        sb.append('<').append(encodeURIString(uri.toString())).append('>');
    }

    private void writeLiteral(Literal literal) throws IOException {
        String value = literal.object().toString();
        if (value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0 || value.indexOf('\t') >= 0) {
            sb.append("\"\"\"")
                    .append(encodeLongString(value))
                    .append("\"\"\"");
        } else {
            sb.append('\"')
                    .append(encodeString(value))
                    .append('\"');
        }
        if (literal.type() != null) {
            sb.append("^^").append(literal.type().toString());
        } else if (literal.lang() != null) {
            sb.append('@').append(literal.lang());
        }
    }

    private void writeIndent(int indentLevel) throws IOException {
        for (int i = 0; i < indentLevel; i++) {
            sb.append(TAB);
        }
    }

    private String encodeString(String value) {
        String s = value;
        s = gsub("\\", "\\\\", s);
        s = gsub("\t", "\\t", s);
        s = gsub("\n", "\\n", s);
        s = gsub("\r", "\\r", s);
        s = gsub("\"", "\\\"", s);
        return s;
    }

    private String encodeLongString(String value) {
        String s = value;
        s = gsub("\\", "\\\\", s);
        s = gsub("\"", "\\\"", s);
        return s;
    }

    private String encodeURIString(String value) {
        String s = value;
        s = gsub("\\", "\\\\", s);
        s = gsub(">", "\\>", s);
        return s;
    }

    private String gsub(String olds, String news, String text) {
        if (olds == null || olds.length() == 0) {
            return text;
        }
        if (text == null) {
            return null;
        }
        int oldsIndex = text.indexOf(olds);
        if (oldsIndex == -1) {
            return text;
        }
        StringBuilder buf = new StringBuilder(text.length());
        int prevIndex = 0;
        while (oldsIndex >= 0) {
            buf.append(text.substring(prevIndex, oldsIndex));
            buf.append(news);
            prevIndex = oldsIndex + olds.length();
            oldsIndex = text.indexOf(olds, prevIndex);
        }
        buf.append(text.substring(prevIndex));
        return buf.toString();
    }

}
