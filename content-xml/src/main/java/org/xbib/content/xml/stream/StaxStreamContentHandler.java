package org.xbib.content.xml.stream;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * SAX <code>ContentHandler</code> that writes to a <code>XMLStreamWriter</code>.
 */
public class StaxStreamContentHandler extends AbstractStaxContentHandler {

    private final XMLStreamWriter streamWriter;

    /**
     * Constructs a new instance of the <code>StaxStreamContentHandler</code> that writes to the given
     * <code>XMLStreamWriter</code>.
     *
     * @param streamWriter the stream writer to write to
     */
    public StaxStreamContentHandler(XMLStreamWriter streamWriter) {
        this.streamWriter = streamWriter;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        // not used
    }

    @Override
    protected void charactersInternal(char[] ch, int start, int length) throws XMLStreamException {
        streamWriter.writeCharacters(ch, start, length);
    }

    @Override
    protected void endDocumentInternal() throws XMLStreamException {
        streamWriter.writeEndDocument();
    }

    @Override
    protected void endElementInternal(QName name, SimpleNamespaceContext namespaceContext) throws XMLStreamException {
        streamWriter.writeEndElement();
    }

    @Override
    protected void ignorableWhitespaceInternal(char[] ch, int start, int length) throws XMLStreamException {
        streamWriter.writeCharacters(ch, start, length);
    }

    @Override
    protected void processingInstructionInternal(String target, String data) throws XMLStreamException {
        streamWriter.writeProcessingInstruction(target, data);
    }

    @Override
    protected void skippedEntityInternal(String name) {
        // not used
    }

    @Override
    protected void startDocumentInternal() throws XMLStreamException {
        streamWriter.writeStartDocument();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void startElementInternal(QName name, Attributes attributes, SimpleNamespaceContext namespaceContext)
            throws XMLStreamException {
        streamWriter.writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
        String defaultNamespaceUri = namespaceContext.getNamespaceURI("");
        if (defaultNamespaceUri != null && defaultNamespaceUri.length() > 0) {
            streamWriter.writeNamespace("", defaultNamespaceUri);
            streamWriter.setDefaultNamespace(defaultNamespaceUri);
        }
        for (Iterator<String> iterator = namespaceContext.getBoundPrefixes(); iterator.hasNext(); ) {
            String prefix = iterator.next();
            streamWriter.writeNamespace(prefix, namespaceContext.getNamespaceURI(prefix));
            streamWriter.setPrefix(prefix, namespaceContext.getNamespaceURI(prefix));
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            QName attrName = toQName(attributes.getURI(i), attributes.getQName(i));
            if (!("xmlns".equals(attrName.getLocalPart()) || "xmlns".equals(attrName.getPrefix()))) {
                streamWriter.writeAttribute(attrName.getPrefix(), attrName.getNamespaceURI(), attrName.getLocalPart(),
                        attributes.getValue(i));
            }
        }
    }
}
