package org.xbib.content.xml.stream;

import javax.xml.stream.events.Characters;

/**
 * Abstract base class for various {@link Characters} implementations.
 */
public abstract class AbstractCharactersEvent extends AbstractXMLEvent implements Characters {

    protected String data;

    public AbstractCharactersEvent(String data) {
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public boolean isCharacters() {
        return true;
    }

    @Override
    public boolean isWhiteSpace() {
        String s1 = getData();
        for (int i = 0, s = s1.length(); i < s; i++) {
            char ch = s1.charAt(i);
            if (ch != ' ' && ch != '\n' && ch != '\t' && ch != '\r') {
                return false;
            }
        }
        return true;

    }
}
