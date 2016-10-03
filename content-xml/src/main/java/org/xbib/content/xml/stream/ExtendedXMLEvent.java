package org.xbib.content.xml.stream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

/**
 * Extended {@link XMLEvent} interface that provides additional functionality.
 */
public interface ExtendedXMLEvent extends XMLEvent {

    /**
     * Determines if this event matches another event, irrespective of document
     * location.
     *
     * @param event The event to match against.
     * @return <code>true</code> if the two events match, <code>false</code>
     * otherwise.
     */
    boolean matches(XMLEvent event);

    /**
     * Writes the event to the provided {@link XMLStreamWriter}.
     *
     * @param writer The destination stream.
     * @throws XMLStreamException If an error occurs writing to the destination
     *                            stream.
     */
    void writeEvent(XMLStreamWriter writer) throws XMLStreamException;
}
