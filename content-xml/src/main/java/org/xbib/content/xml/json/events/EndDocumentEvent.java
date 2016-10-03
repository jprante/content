package org.xbib.content.xml.json.events;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;

/**
 *
 */
public class EndDocumentEvent extends JsonReaderXmlEvent {

    public EndDocumentEvent(Location location) {
        setLocation(location);
    }

    @Override
    public boolean isEndDocument() {
        return true;
    }

    @Override
    public int getEventType() {
        return XMLStreamConstants.END_DOCUMENT;
    }

    @Override
    public String toString() {
        return "EndDocumentEvent()";
    }
}
