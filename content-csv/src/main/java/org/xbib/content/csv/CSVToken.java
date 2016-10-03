package org.xbib.content.csv;

/**
 *
 */
final class CSVToken {

    private static final int INITIAL_TOKEN_LENGTH = 50;

    enum Type {
        INVALID,
        TOKEN,
        EOF,
        EORECORD,
        COMMENT
    }

    CSVToken.Type type = Type.INVALID;

    StringBuilder content = new StringBuilder(INITIAL_TOKEN_LENGTH);

    boolean isReady;

    void reset() {
        content.setLength(0);
        type = Type.INVALID;
        isReady = false;
    }

}
