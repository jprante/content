package org.xbib.content.xml.stream;

import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Abstract base class for {@link XMLEvent} implementations.
 */
public abstract class AbstractXMLEvent implements XMLEvent {

    /**
     * The event location.
     */
    protected Location location;

    /**
     * The schema type.
     */
    protected QName schemaType;

    public AbstractXMLEvent() {
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public QName getSchemaType() {
        return schemaType;
    }

    @Override
    public Characters asCharacters() {
        return (Characters) this;
    }

    @Override
    public EndElement asEndElement() {
        return (EndElement) this;
    }

    @Override
    public StartElement asStartElement() {
        return (StartElement) this;
    }

    @Override
    public boolean isAttribute() {
        return getEventType() == ATTRIBUTE;
    }

    @Override
    public boolean isCharacters() {
        switch (getEventType()) {
            case CHARACTERS:
            case SPACE:
            case CDATA:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isEndDocument() {
        return getEventType() == END_DOCUMENT;
    }

    @Override
    public boolean isEndElement() {
        return getEventType() == END_ELEMENT;
    }

    @Override
    public boolean isEntityReference() {
        return getEventType() == ENTITY_REFERENCE;
    }

    @Override
    public boolean isNamespace() {
        return getEventType() == NAMESPACE;
    }

    @Override
    public boolean isProcessingInstruction() {
        return getEventType() == PROCESSING_INSTRUCTION;
    }

    @Override
    public boolean isStartDocument() {
        return getEventType() == START_DOCUMENT;
    }

    @Override
    public boolean isStartElement() {
        return getEventType() == START_ELEMENT;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            this.writeAsEncodedUnicode(writer);
        } catch (XMLStreamException e) {
            // ignore
        }
        return writer.toString();
    }
}
