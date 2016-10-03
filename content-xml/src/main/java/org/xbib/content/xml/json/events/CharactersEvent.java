package org.xbib.content.xml.json.events;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;

/**
 *
 */
public class CharactersEvent extends JsonReaderXmlEvent {

    public CharactersEvent(String text, Location location) {
        setText(text);
        setLocation(location);
    }

    @Override
    public boolean isCharacters() {
        return true;
    }

    @Override
    public int getEventType() {
        return XMLStreamConstants.CHARACTERS;
    }

    @Override
    public String toString() {
        return "CharactersEvent(" + getText() + ")";
    }
}
