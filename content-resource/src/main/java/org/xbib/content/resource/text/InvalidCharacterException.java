package org.xbib.content.resource.text;

/**
 *
 */
public class InvalidCharacterException extends RuntimeException {

    private static final long serialVersionUID = -3037013255350562940L;
    private final int input;

    public InvalidCharacterException(int input) {
        this.input = input;
    }

    @Override
    public String getMessage() {
        return "Invalid Character 0x" + Integer.toHexString(input) + "(" + (char) input + ")";
    }

}
