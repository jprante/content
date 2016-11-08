package org.xbib.content.xml.stream;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

/**
 * SAX <code>ContentHandler</code> that transforms callback calls to <code>XMLEvent</code>s and writes them to a
 * <code>XMLEventConsumer</code>.
 */
public class StaxEventContentHandler extends AbstractStaxContentHandler {

    private final XMLEventFactory eventFactory;

    private final XMLEventConsumer eventConsumer;

    private Locator locator;

    /**
     * Constructs a new instance of the <code>StaxEventContentHandler</code> that writes to the given
     * <code>XMLEventConsumer</code>. A default <code>XMLEventFactory</code> will be created.
     *
     * @param consumer the consumer to write events to
     */
    public StaxEventContentHandler(XMLEventConsumer consumer) {
        eventFactory = XMLEventFactory.newInstance();
        eventConsumer = consumer;
    }

    /**
     * Constructs a new instance of the <code>StaxEventContentHandler</code> that uses the given event factory to create
     * events and writes to the given <code>XMLEventConsumer</code>.
     *
     * @param consumer the consumer to write events to
     * @param factory  the factory used to create events
     */
    public StaxEventContentHandler(XMLEventConsumer consumer, XMLEventFactory factory) {
        eventFactory = factory;
        eventConsumer = consumer;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    protected void startDocumentInternal() throws XMLStreamException {
        consumeEvent(eventFactory.createStartDocument());
    }

    @Override
    protected void endDocumentInternal() throws XMLStreamException {
        consumeEvent(eventFactory.createEndDocument());
    }

    @Override
    protected void startElementInternal(QName name, Attributes atts, SimpleNamespaceContext namespaceContext)
            throws XMLStreamException {
        List<?> attributes = getAttributes(atts);
        List<?> namespaces = createNamespaces(namespaceContext);
        consumeEvent(eventFactory.createStartElement(name, attributes.iterator(), namespaces.iterator()));
    }

    @Override
    protected void endElementInternal(QName name, SimpleNamespaceContext namespaceContext) throws XMLStreamException {
        List<?> namespaces = createNamespaces(namespaceContext);
        consumeEvent(eventFactory.createEndElement(name, namespaces.iterator()));
    }

    @Override
    protected void charactersInternal(char[] ch, int start, int length) throws XMLStreamException {
        consumeEvent(eventFactory.createCharacters(new String(ch, start, length)));
    }

    @Override
    protected void ignorableWhitespaceInternal(char[] ch, int start, int length) throws XMLStreamException {
        consumeEvent(eventFactory.createIgnorableSpace(new String(ch, start, length)));
    }

    @Override
    protected void processingInstructionInternal(String target, String data) throws XMLStreamException {
        consumeEvent(eventFactory.createProcessingInstruction(target, data));
    }

    private void consumeEvent(XMLEvent event) throws XMLStreamException {
        if (locator != null) {
            eventFactory.setLocation(new SaxLocation(locator));
        }
        eventConsumer.add(event);
    }

    /**
     * Creates and returns a list of <code>NameSpace</code> objects from the <code>NamespaceContext</code>.
     */
    @SuppressWarnings("unchecked")
    private List<Namespace> createNamespaces(SimpleNamespaceContext namespaceContext) {
        List<Namespace> namespaces = new ArrayList<>();
        String defaultNamespaceUri = namespaceContext.getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX);
        if (defaultNamespaceUri != null && defaultNamespaceUri.length() > 0) {
            namespaces.add(eventFactory.createNamespace(defaultNamespaceUri));
        }
        for (Iterator<String> iterator = namespaceContext.getBoundPrefixes(); iterator.hasNext(); ) {
            String prefix = iterator.next();
            String namespaceUri = namespaceContext.getNamespaceURI(prefix);
            namespaces.add(eventFactory.createNamespace(prefix, namespaceUri));
        }
        return namespaces;
    }

    private List<Attribute> getAttributes(Attributes attributes) {
        List<Attribute> list = new ArrayList<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            QName name = toQName(attributes.getURI(i), attributes.getQName(i));
            if (!("xmlns".equals(name.getLocalPart()) || "xmlns".equals(name.getPrefix()))) {
                list.add(eventFactory.createAttribute(name, attributes.getValue(i)));
            }
        }
        return list;
    }

    @Override
    protected void skippedEntityInternal(String name) throws XMLStreamException {
        // not used
    }

    private static class SaxLocation implements Location {

        private Locator locator;

        private SaxLocation(Locator locator) {
            this.locator = locator;
        }

        @Override
        public int getLineNumber() {
            return locator.getLineNumber();
        }

        @Override
        public int getColumnNumber() {
            return locator.getColumnNumber();
        }

        @Override
        public int getCharacterOffset() {
            return -1;
        }

        @Override
        public String getPublicId() {
            return locator.getPublicId();
        }

        @Override
        public String getSystemId() {
            return locator.getSystemId();
        }
    }
}
