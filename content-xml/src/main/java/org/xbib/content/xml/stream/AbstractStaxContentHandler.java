package org.xbib.content.xml.stream;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

/**
 * Abstract base class for SAX <code>ContentHandler</code> implementations that use StAX as a basis. All methods
 * delegate to internal template methods, capable of throwing a <code>XMLStreamException</code>. Additionally, an
 * namespace context is used to keep track of declared namespaces.
 */
abstract class AbstractStaxContentHandler implements ContentHandler {

    private SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext();

    @Override
    public final void startDocument() throws SAXException {
        namespaceContext.clear();
        try {
            startDocumentInternal();
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle startDocument: " + ex.getMessage(), ex);
        }
    }

    protected abstract void startDocumentInternal() throws XMLStreamException;

    @Override
    public final void endDocument() throws SAXException {
        namespaceContext.clear();
        try {
            endDocumentInternal();
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle startDocument: " + ex.getMessage(), ex);
        }
    }

    protected abstract void endDocumentInternal() throws XMLStreamException;

    /**
     * Binds the given prefix to the given namespaces.
     *
     * @see SimpleNamespaceContext#bindNamespaceUri(String, String)
     */
    @Override
    public final void startPrefixMapping(String prefix, String uri) {
        namespaceContext.bindNamespaceUri(prefix, uri);
    }

    /**
     * Removes the binding for the given prefix.
     *
     * @see SimpleNamespaceContext#removeBinding(String)
     */
    @Override
    public final void endPrefixMapping(String prefix) {
        namespaceContext.removeBinding(prefix);
    }

    @Override
    public final void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        try {
            startElementInternal(toQName(uri, qName), atts, namespaceContext);
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle startElement: " + ex.getMessage(), ex);
        }
    }

    protected abstract void startElementInternal(QName name, Attributes atts, SimpleNamespaceContext namespaceContext)
            throws XMLStreamException;

    @Override
    public final void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            endElementInternal(toQName(uri, qName), namespaceContext);
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle endElement: " + ex.getMessage(), ex);
        }
    }

    protected abstract void endElementInternal(QName name, SimpleNamespaceContext namespaceContext)
            throws XMLStreamException;

    @Override
    public final void characters(char ch[], int start, int length) throws SAXException {
        try {
            charactersInternal(ch, start, length);
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle characters: " + ex.getMessage(), ex);
        }
    }

    protected abstract void charactersInternal(char[] ch, int start, int length) throws XMLStreamException;

    @Override
    public final void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        try {
            ignorableWhitespaceInternal(ch, start, length);
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle ignorableWhitespace:" + ex.getMessage(), ex);
        }
    }

    protected abstract void ignorableWhitespaceInternal(char[] ch, int start, int length) throws XMLStreamException;

    @Override
    public final void processingInstruction(String target, String data) throws SAXException {
        try {
            processingInstructionInternal(target, data);
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle processingInstruction: " + ex.getMessage(), ex);
        }
    }

    protected abstract void processingInstructionInternal(String target, String data) throws XMLStreamException;

    @Override
    public final void skippedEntity(String name) throws SAXException {
        try {
            skippedEntityInternal(name);
        } catch (XMLStreamException ex) {
            throw new SAXException("Could not handle skippedEntity: " + ex.getMessage(), ex);
        }
    }

    /**
     * Convert a namespace URI and DOM or SAX qualified name to a <code>QName</code>. The qualified name can have the
     * form
     * <code>prefix:localname</code> or <code>localName</code>.
     *
     * @param namespaceUri  the namespace URI
     * @param qualifiedName the qualified name
     * @return a QName
     */
    QName toQName(String namespaceUri, String qualifiedName) {
        int idx = qualifiedName.indexOf(':');
        if (idx == -1) {
            return new QName(namespaceUri, qualifiedName);
        } else {
            String prefix = qualifiedName.substring(0, idx);
            String localPart = qualifiedName.substring(idx + 1);
            return new QName(namespaceUri, localPart, prefix);
        }
    }

    protected abstract void skippedEntityInternal(String name) throws XMLStreamException;
}
