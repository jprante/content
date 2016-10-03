package org.xbib.content.xml.stream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * SAX <code>XMLReader</code> that reads from a StAX <code>XMLEventReader</code>.
 * Consumes <code>XMLEvents</code> from
 * an <code>XMLEventReader</code>, and calls the corresponding methods on the SAX callback interfaces.
 */
public class StaxEventXMLReader extends AbstractStaxXMLReader {

    private static final Logger logger = Logger.getLogger(StaxEventXMLReader.class.getName());

    private final XMLEventReader reader;

    /**
     * Constructs a new instance of the <code>StaxEventXmlReader</code> that reads from the given
     * <code>XMLEventReader</code>. The supplied event reader must be in <code>XMLStreamConstants.START_DOCUMENT</code>
     * or
     * <code>XMLStreamConstants.START_ELEMENT</code> state.
     *
     * @param reader the <code>XMLEventReader</code> to read from
     * @throws IllegalStateException if the reader is not at the start of a document or element
     */
    public StaxEventXMLReader(XMLEventReader reader) {
        try {
            XMLEvent event = reader.peek();
            if (event == null || !(event.isStartDocument() || event.isStartElement())) {
                throw new IllegalStateException("XMLEventReader not at start of document or element");
            }
        } catch (XMLStreamException ex) {
            logger.log(Level.FINE, ex.getMessage(), ex);
            throw new IllegalStateException("Could not read first element: " + ex.getMessage());
        }
        this.reader = reader;
    }

    @Override
    protected void parseInternal() throws SAXException, XMLStreamException {
        boolean documentStarted = false;
        boolean documentEnded = false;
        int elementDepth = 0;
        while (reader.hasNext() && elementDepth >= 0) {
            XMLEvent event = reader.nextEvent();
            if (!event.isStartDocument() && !event.isEndDocument() && !documentStarted) {
                handleStartDocument();
                documentStarted = true;
            }
            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    elementDepth++;
                    handleStartElement(event.asStartElement());
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementDepth--;
                    if (elementDepth >= 0) {
                        handleEndElement(event.asEndElement());
                    }
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    handleProcessingInstruction((ProcessingInstruction) event);
                    break;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.SPACE:
                case XMLStreamConstants.CDATA:
                    handleCharacters(event.asCharacters());
                    break;
                case XMLStreamConstants.START_DOCUMENT:
                    setLocator(event.getLocation());
                    handleStartDocument();
                    documentStarted = true;
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    handleEndDocument();
                    documentEnded = true;
                    break;
                case XMLStreamConstants.NOTATION_DECLARATION:
                    handleNotationDeclaration((NotationDeclaration) event);
                    break;
                case XMLStreamConstants.ENTITY_DECLARATION:
                    handleEntityDeclaration((EntityDeclaration) event);
                    break;
                case XMLStreamConstants.COMMENT:
                    handleComment((Comment) event);
                    break;
                case XMLStreamConstants.DTD:
                    handleDtd((DTD) event);
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    handleEntityReference((EntityReference) event);
                    break;
                default:
                    break;
            }
        }
        if (!documentEnded) {
            handleEndDocument();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleStartElement(StartElement startElement) throws SAXException {
        if (getContentHandler() != null) {
            QName qName = startElement.getName();
            if (hasNamespacesFeature()) {
                for (Iterator<Namespace> i = startElement.getNamespaces(); i.hasNext(); ) {
                    Namespace namespace = i.next();
                    getContentHandler().startPrefixMapping(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                getContentHandler().startElement(qName.getNamespaceURI(), qName.getLocalPart(), toQualifiedName(qName),
                        getAttributes(startElement));
            } else {
                getContentHandler().startElement("", "", toQualifiedName(qName), getAttributes(startElement));
            }
        }
    }

    private void handleCharacters(Characters characters) throws SAXException {
        char[] data = characters.getData().toCharArray();
        if (getContentHandler() != null && characters.isIgnorableWhiteSpace()) {
            getContentHandler().ignorableWhitespace(data, 0, data.length);
            return;
        }
        if (characters.isCData() && getLexicalHandler() != null) {
            getLexicalHandler().startCDATA();
        }
        if (getContentHandler() != null) {
            getContentHandler().characters(data, 0, data.length);
        }
        if (characters.isCData() && getLexicalHandler() != null) {
            getLexicalHandler().endCDATA();
        }
    }

    private void handleEndDocument() throws SAXException {
        if (getContentHandler() != null) {
            getContentHandler().endDocument();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleEndElement(EndElement endElement) throws SAXException {
        if (getContentHandler() != null) {
            QName qName = endElement.getName();
            if (hasNamespacesFeature()) {
                getContentHandler().endElement(qName.getNamespaceURI(), qName.getLocalPart(), toQualifiedName(qName));
                for (Iterator<Namespace> i = endElement.getNamespaces(); i.hasNext(); ) {
                    Namespace namespace = i.next();
                    getContentHandler().endPrefixMapping(namespace.getPrefix());
                }
            } else {
                getContentHandler().endElement("", "", toQualifiedName(qName));
            }
        }
    }

    private void handleNotationDeclaration(NotationDeclaration declaration) throws SAXException {
        if (getDTDHandler() != null) {
            getDTDHandler().notationDecl(declaration.getName(), declaration.getPublicId(), declaration.getSystemId());
        }
    }

    private void handleEntityDeclaration(EntityDeclaration entityDeclaration) throws SAXException {
        if (getDTDHandler() != null) {
            getDTDHandler().unparsedEntityDecl(entityDeclaration.getName(), entityDeclaration.getPublicId(),
                    entityDeclaration.getSystemId(), entityDeclaration.getNotationName());
        }
    }

    private void handleProcessingInstruction(ProcessingInstruction pi) throws SAXException {
        if (getContentHandler() != null) {
            getContentHandler().processingInstruction(pi.getTarget(), pi.getData());
        }
    }

    private void handleStartDocument() throws SAXException {
        if (getContentHandler() != null) {
            getContentHandler().startDocument();
        }
    }

    private void handleComment(Comment comment) throws SAXException {
        if (getLexicalHandler() != null) {
            char[] ch = comment.getText().toCharArray();
            getLexicalHandler().comment(ch, 0, ch.length);
        }
    }

    private void handleDtd(DTD dtd) throws SAXException {
        if (getLexicalHandler() != null) {
            javax.xml.stream.Location location = dtd.getLocation();
            getLexicalHandler().startDTD(null, location.getPublicId(), location.getSystemId());
        }
        if (getLexicalHandler() != null) {
            getLexicalHandler().endDTD();
        }
    }

    private void handleEntityReference(EntityReference reference) throws SAXException {
        if (getLexicalHandler() != null) {
            getLexicalHandler().startEntity(reference.getName());
        }
        if (getLexicalHandler() != null) {
            getLexicalHandler().endEntity(reference.getName());
        }
    }

    @SuppressWarnings("unchecked")
    private Attributes getAttributes(StartElement event) {
        AttributesImpl attributes = new AttributesImpl();
        for (Iterator<Attribute> i = event.getAttributes(); i.hasNext(); ) {
            Attribute attribute = i.next();
            QName qName = attribute.getName();
            String namespace = qName.getNamespaceURI();
            if (namespace == null || !hasNamespacesFeature()) {
                namespace = "";
            }
            String type = attribute.getDTDType();
            if (type == null) {
                type = "CDATA";
            }
            attributes.addAttribute(namespace, qName.getLocalPart(), toQualifiedName(qName), type, attribute.getValue());
        }
        if (hasNamespacePrefixesFeature()) {
            for (Iterator<Namespace> i = event.getNamespaces(); i.hasNext(); ) {
                Namespace namespace = i.next();
                String prefix = namespace.getPrefix();
                String namespaceUri = namespace.getNamespaceURI();
                String qName;
                if (!prefix.isEmpty()) {
                    qName = "xmlns:" + prefix;
                } else {
                    qName = "xmlns";
                }
                attributes.addAttribute("", "", qName, "CDATA", namespaceUri);
            }
        }
        return attributes;
    }
}
