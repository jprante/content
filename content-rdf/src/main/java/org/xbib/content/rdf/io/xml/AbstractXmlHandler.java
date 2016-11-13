package org.xbib.content.rdf.io.xml;

import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.resource.IRI;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.LinkedList;

import javax.xml.namespace.QName;

/**
 * Abstract XML handler.
 * @param <P> parameter type
 */
public abstract class AbstractXmlHandler<P extends RdfContentParams>
        extends DefaultHandler implements XmlHandler<P> {

    protected final RdfContentParams params;

    protected final StringBuilder content;

    private final LinkedList<QName> parents;

    protected Resource resource;

    private RdfContentBuilder<P> builder;

    private String defaultPrefix;

    private String defaultNamespace;

    private int lastlevel;

    public AbstractXmlHandler(RdfContentParams params) {
        this.params = params;
        this.content = new StringBuilder();
        this.parents = new LinkedList<>();
        this.resource = new DefaultAnonymousResource();
    }

    public RdfContentParams getParams() {
        return params;
    }

    public Resource getResource() {
        return resource;
    }

    public LinkedList<QName> getParents() {
        return parents;
    }

    @Override
    public AbstractXmlHandler<P> setDefaultNamespace(String prefix, String namespaceURI) {
        this.defaultPrefix = prefix;
        this.defaultNamespace = namespaceURI;
        params.getNamespaceContext().addNamespace(prefix, namespaceURI);
        return this;
    }

    @Override
    public AbstractXmlHandler<P> setBuilder(RdfContentBuilder<P> builder) {
        this.builder = builder;
        return this;
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            openResource();
        } catch (IOException e) {
            throw new SAXException(e);
        }
        parents.push(new QName("_"));
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            closeResource();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String nsURI, String localname, String qname, Attributes atts) throws SAXException {
        try {
            QName name = makeQName(nsURI, localname, qname);
            boolean delimiter = isResourceDelimiter(name);
            if (delimiter) {
                closeResource();
                openResource();
            }
            if (skip(name)) {
                return;
            }
            int level = parents.size();
            if (!delimiter) {
                openPredicate(parents.peek(), name, lastlevel - level);
            }
            parents.push(name);
            lastlevel = level;
            if (atts != null) {
                // transform attributes as if they were elements, but with a '@' prefix
                for (int i = 0; i < atts.getLength(); i++) {
                    String attrValue = atts.getValue(i);
                    if (attrValue != null && !attrValue.isEmpty()) {
                        String newAttrname = '@' + atts.getLocalName(i);
                        QName attrQName = new QName(atts.getURI(i), newAttrname, atts.getQName(i));
                        if (!skip(attrQName)) {
                            startElement(atts.getURI(i), newAttrname, atts.getQName(i), null);
                            characters(attrValue.toCharArray(), 0, attrValue.length());
                            endElement(atts.getURI(i), newAttrname, atts.getQName(i));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endElement(String nsURI, String localname, String qname) throws SAXException {
        QName name = makeQName(nsURI, localname, qname);
        if (skip(name)) {
            content.setLength(0);
            return;
        }
        int level = parents.size();
        parents.pop();
        identify(name, content(), resource.id());
        if (!isResourceDelimiter(name) && !parents.isEmpty()) {
            try {
                closePredicate(parents.peek(), name, level - lastlevel);
            } catch (EmptyStackException e) {
                throw new SAXException(e);
            }
        }
        content.setLength(0);
        lastlevel = level;
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        content.append(new String(chars, start, length));
        addToPredicate(parents.peek(), content());
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (prefix == null || prefix.isEmpty() || XmlConstants.XML_SCHEMA_URI.equals(uri)) {
            return;
        }
        params.getNamespaceContext().addNamespace(makePrefix(prefix), uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        // we do not remove namespaces, or you will get trouble in RDF serializations...
    }

    protected String makePrefix(String prefix) {
        return prefix.replaceAll("[^a-zA-Z]+", "");
    }

    protected String makePrefix(String prefix, String localname) {
        String s = prefix.replaceAll("[^a-zA-Z]+", "");
        return s.length() > 0 ? s + ":" + localname : localname;
    }

    protected QName makeQName(String nsURI, String localname, String qname) {
        String prefix = params.getNamespaceContext().getPrefix(nsURI);
        return new QName(!isEmpty(nsURI) ? nsURI : defaultNamespace,
                !isEmpty(localname) ? localname : qname,
                !isEmpty(prefix) ? prefix : defaultPrefix);
    }

    public String content() {
        String s = content.toString().trim();
        return s.length() > 0 ? s : null;
    }

    protected void openResource() throws IOException {
        resource = new DefaultAnonymousResource();
    }

    protected void closeResource() throws IOException {
        boolean empty = resource.isEmpty();
        if (empty) {
            return;
        }
        if (builder != null) {
            builder.receive(resource.id());
            builder.receive(resource);
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public abstract boolean isResourceDelimiter(QName name);

    public abstract boolean skip(QName name);

    public abstract void identify(QName name, String value, IRI identifier);

    public abstract void openPredicate(QName parent, QName child, int level);

    public abstract void addToPredicate(QName parent, String content);

    public abstract void closePredicate(QName parent, QName child, int level);
}
