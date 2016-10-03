package org.xbib.content.xml.json.events;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;

/**
 *
 */
public class EndElementEvent extends JsonReaderXmlEvent {

    public EndElementEvent(QName name, Location location) {
        setQName(name);
        setLocation(location);
    }

    @Override
    public boolean isEndElement() {
        return true;
    }

    @Override
    public int getEventType() {
        return XMLStreamConstants.END_ELEMENT;
    }

    @Override
    public String toString() {
        return "EndElementEvent(" + getName() + ")";
    }
}
