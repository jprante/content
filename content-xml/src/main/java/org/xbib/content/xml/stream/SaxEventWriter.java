package org.xbib.content.xml.stream;

import org.xml.sax.ContentHandler;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

/**
 * Receive and convert StAX events to SAX events.
 * Extends {@link SaxEventConsumer} with {@link XMLEventWriter} methods.
 */
public class SaxEventWriter extends SaxEventConsumer implements XMLEventWriter {
    /**
     * @param handler the content handler
     */
    public SaxEventWriter(ContentHandler handler) {
        super(handler);
    }

    @Override
    public void add(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            add(reader.nextEvent());
        }
    }

    // Not supported by SAX

    @Override
    public void flush() throws XMLStreamException {
        // Not supported by ContentHandler
    }

    @Override
    public void close() throws XMLStreamException {
        // Not supported by ContentHandler
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        // Not supported by ContentHandler

        return null;
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        // Not supported by ContentHandler
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        // Not supported by ContentHandler
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        // Not supported by ContentHandler

        return null;
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        // Not supported by ContentHandler
    }
}
