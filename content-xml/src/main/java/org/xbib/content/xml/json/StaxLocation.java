package org.xbib.content.xml.json;

import com.fasterxml.jackson.core.JsonLocation;

import javax.xml.stream.Location;

class StaxLocation implements Location {

    private final int charOffset;
    private final int column;
    private final int line;

    StaxLocation(final int charOffset, final int column, final int line) {
        super();
        this.charOffset = charOffset;
        this.column = column;
        this.line = line;
    }

    StaxLocation(final JsonLocation location) {
        this((int) location.getCharOffset(), location.getColumnNr(), location.getLineNr());
    }

    @Override
    public int getCharacterOffset() {
        return charOffset;
    }

    @Override
    public int getColumnNumber() {
        return column;
    }

    @Override
    public int getLineNumber() {
        return line;
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public String getSystemId() {
        return null;
    }
}
