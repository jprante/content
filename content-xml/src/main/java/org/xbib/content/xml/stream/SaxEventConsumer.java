package org.xbib.content.xml.stream;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

/**
 * Receive and convert StAX events to SAX events.
 * <p>
 * It's a modification of the code from XMLEventReaderToContentHandler that can be used as
 * {@link XMLEventConsumer} since SAX result is not supported by standard {@link javax.xml.stream.XMLOutputFactory}.
 */
public class SaxEventConsumer implements XMLEventConsumer {

    /**
     * The SAX filter.
     */
    private XMLFilterImplEx filter;

    /**
     * The depth of XML elements.
     */
    private int depth;

    /**
     * @param handler the content handler
     */
    public SaxEventConsumer(ContentHandler handler) {
        this.filter = new XMLFilterImplEx();
        this.filter.setContentHandler(handler);
        if (handler instanceof LexicalHandler) {
            this.filter.setLexicalHandler((LexicalHandler) handler);
        }
        if (handler instanceof ErrorHandler) {
            this.filter.setErrorHandler((ErrorHandler) handler);
        }
    }

    @Override
    public void add(XMLEvent event) throws XMLStreamException {
        convertEvent(event);
    }

    /**
     * @param event the XML event to convert
     * @throws XMLStreamException if conversion fails
     */
    private void convertEvent(XMLEvent event) throws XMLStreamException {
        try {
            if (event.isStartDocument()) {
                this.handleStartDocument(event);
            } else if (event.isEndDocument()) {
                this.handleEndDocument();
            } else {
                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        this.depth++;
                        this.handleStartElement(event.asStartElement());
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        this.handleEndElement(event.asEndElement());
                        this.depth--;
                        if (this.depth == 0) {
                            break;
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        this.handleCharacters(event.asCharacters());
                        break;
                    case XMLStreamConstants.ENTITY_REFERENCE:
                        this.handleEntityReference();
                        break;
                    case XMLStreamConstants.PROCESSING_INSTRUCTION:
                        this.handlePI((ProcessingInstruction) event);
                        break;
                    case XMLStreamConstants.COMMENT:
                        this.handleComment((Comment) event);
                        break;
                    case XMLStreamConstants.DTD:
                        this.handleDTD();
                        break;
                    case XMLStreamConstants.ATTRIBUTE:
                        this.handleAttribute();
                        break;
                    case XMLStreamConstants.NAMESPACE:
                        this.handleNamespace();
                        break;
                    case XMLStreamConstants.CDATA:
                        this.handleCDATA();
                        break;
                    case XMLStreamConstants.ENTITY_DECLARATION:
                        this.handleEntityDecl();
                        break;
                    case XMLStreamConstants.NOTATION_DECLARATION:
                        this.handleNotationDecl();
                        break;
                    case XMLStreamConstants.SPACE:
                        this.handleSpace();
                        break;
                    default:
                        throw new InternalError("processing event: " + event);
                }
            }
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleEndDocument() throws SAXException {
        this.filter.endDocument();
    }

    private void handleStartDocument(final XMLEvent event) throws SAXException {
        final Location location = event.getLocation();
        if (location != null) {
            this.filter.setDocumentLocator(new Locator() {
                @Override
                public int getColumnNumber() {
                    return location.getColumnNumber();
                }

                @Override
                public int getLineNumber() {
                    return location.getLineNumber();
                }

                @Override
                public String getPublicId() {
                    return location.getPublicId();
                }

                @Override
                public String getSystemId() {
                    return location.getSystemId();
                }
            });
        }
        this.filter.startDocument();
    }

    private void handlePI(ProcessingInstruction event) throws XMLStreamException {
        try {
            this.filter.processingInstruction(event.getTarget(), event.getData());
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleCharacters(Characters event) throws XMLStreamException {
        try {
            this.filter.characters(event.getData().toCharArray(), 0, event.getData().length());
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleEndElement(EndElement event) throws XMLStreamException {
        QName qName = event.getName();
        try {
            String prefix = qName.getPrefix();
            String rawname;
            if (prefix == null || prefix.length() == 0) {
                rawname = qName.getLocalPart();
            } else {
                rawname = prefix + ':' + qName.getLocalPart();
            }
            this.filter.endElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname);
            for (Iterator<Namespace> i = event.getNamespaces(); i.hasNext(); ) {
                String nsprefix = i.next().getPrefix();
                if (nsprefix == null) { // true for default namespace
                    nsprefix = "";
                }
                this.filter.endPrefixMapping(nsprefix);
            }
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleStartElement(StartElement event) throws XMLStreamException {
        try {
            for (Iterator<Namespace> i = event.getNamespaces(); i.hasNext(); ) {
                String prefix = i.next().getPrefix();
                if (prefix == null) { // true for default namespace
                    prefix = "";
                }
                this.filter.startPrefixMapping(prefix, event.getNamespaceURI(prefix));
            }
            QName qName = event.getName();
            String prefix = qName.getPrefix();
            String rawname;
            if (prefix == null || prefix.length() == 0) {
                rawname = qName.getLocalPart();
            } else {
                rawname = prefix + ':' + qName.getLocalPart();
            }
            Attributes saxAttrs = getAttributes(event);
            this.filter.startElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname, saxAttrs);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     * Get the attributes associated with the given START_ELEMENT StAXevent.
     *
     * @param event the StAX start element event
     * @return the StAX attributes converted to an org.xml.sax.Attributes
     */
    @SuppressWarnings("unchecked")
    private Attributes getAttributes(StartElement event) {
        AttributesImpl attrs = new AttributesImpl();
        if (!event.isStartElement()) {
            throw new InternalError("getAttributes() attempting to process: " + event);
        }
        if (this.filter.getNamespacePrefixes()) {
            for (Iterator<Namespace> i = event.getNamespaces(); i.hasNext(); ) {
                Namespace staxNamespace = i.next();
                String uri = staxNamespace.getNamespaceURI();
                if (uri == null) {
                    uri = "";
                }
                String prefix = staxNamespace.getPrefix();
                if (prefix == null) {
                    prefix = "";
                }
                String qName = "xmlns";
                if (prefix.length() == 0) {
                    prefix = qName;
                } else {
                    qName = qName + ':' + prefix;
                }
                attrs.addAttribute("http://www.w3.org/2000/xmlns/", prefix, qName, "CDATA", uri);
            }
        }
        for (Iterator<Attribute> i = event.getAttributes(); i.hasNext(); ) {
            Attribute staxAttr = i.next();
            String uri = staxAttr.getName().getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            String localName = staxAttr.getName().getLocalPart();
            String prefix = staxAttr.getName().getPrefix();
            String qName;
            if (prefix == null || prefix.length() == 0) {
                qName = localName;
            } else {
                qName = prefix + ':' + localName;
            }
            String type = staxAttr.getDTDType();
            String value = staxAttr.getValue();
            attrs.addAttribute(uri, localName, qName, type, value);
        }
        return attrs;
    }

    private void handleNamespace() {
        // not used
    }

    private void handleAttribute() {
        // not used
    }

    private void handleDTD() {
        // not used
    }

    private void handleComment(Comment comment) throws XMLStreamException {
        try {
            String text = comment.getText();
            this.filter.comment(text.toCharArray(), 0, text.length());
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleEntityReference() {
        // not used
    }

    private void handleSpace() {
        // not used
    }

    private void handleNotationDecl() {
        // not used
    }

    private void handleEntityDecl() {
        // not used
    }

    private void handleCDATA() {
        // not used
    }
}
