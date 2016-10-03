package org.xbib.content.csv;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 */
class CSVLexer implements CSVConstants, Closeable {

    private final CSVLookAheadReader reader;

    private final char delimiter;

    private final char escape;

    private final char quoteChar;

    private final char commentStart;

    private final boolean ignoreSurroundingSpaces;

    private final boolean ignoreEmptyLines;

    CSVLexer(CSVLookAheadReader reader, char delimiter, char escape, char quoteChar, char commentStart,
             boolean ignoreSurroundingSpaces, boolean ignoreEmptyLines) {
        this.reader = reader;
        this.delimiter = delimiter;
        this.escape = escape;
        this.quoteChar = quoteChar;
        this.commentStart = commentStart;
        this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
        this.ignoreEmptyLines = ignoreEmptyLines;
    }

    CSVToken nextToken(final CSVToken csvToken) throws IOException {
        int lastChar = reader.getLastChar();
        int c = reader.read();
        boolean eol = readEndOfLine(c);
        if (ignoreEmptyLines) {
            while (eol && isStartOfLine(lastChar)) {
                lastChar = c;
                c = reader.read();
                eol = readEndOfLine(c);
                if (isEndOfFile(c)) {
                    csvToken.type = CSVToken.Type.EOF;
                    return csvToken;
                }
            }
        }
        if (isEndOfFile(lastChar) || (!isDelimiter(lastChar) && isEndOfFile(c))) {
            csvToken.type = CSVToken.Type.EOF;
            return csvToken;
        }
        if (isStartOfLine(lastChar) && isCommentStart(c)) {
            final String line = reader.readLine();
            if (line == null) {
                csvToken.type = CSVToken.Type.EOF;
                return csvToken;
            }
            final String comment = line.trim();
            csvToken.content.append(comment);
            csvToken.type = CSVToken.Type.COMMENT;
            return csvToken;
        }
        while (csvToken.type == CSVToken.Type.INVALID) {
            if (ignoreSurroundingSpaces) {
                while (isWhitespace(c) && !eol) {
                    c = reader.read();
                    eol = readEndOfLine(c);
                }
            }
            if (isDelimiter(c)) {
                csvToken.type = CSVToken.Type.TOKEN;
            } else if (eol) {
                csvToken.type = CSVToken.Type.EORECORD;
            } else if (isQuoteChar(c)) {
                parseEncapsulatedToken(csvToken);
            } else if (isEndOfFile(c)) {
                csvToken.type = CSVToken.Type.EOF;
                csvToken.isReady = true;
            } else {
                parseSimpleToken(csvToken, c);
            }
        }
        return csvToken;
    }

    private CSVToken parseSimpleToken(final CSVToken csvToken, int c) throws IOException {
        int ch = c;
        while (true) {
            if (readEndOfLine(ch)) {
                csvToken.type = CSVToken.Type.EORECORD;
                break;
            } else if (isEndOfFile(ch)) {
                csvToken.type = CSVToken.Type.EOF;
                csvToken.isReady = true;
                break;
            } else if (isDelimiter(ch)) {
                csvToken.type = CSVToken.Type.TOKEN;
                break;
            } else if (isEscape(ch)) {
                final int unescaped = readEscape();
                if (unescaped == -1) {
                    csvToken.content.append((char) ch).append((char) reader.getLastChar());
                } else {
                    csvToken.content.append((char) unescaped);
                }
                ch = reader.read();
            } else {
                csvToken.content.append((char) ch);
                ch = reader.read();
            }
        }
        if (ignoreSurroundingSpaces) {
            trimTrailingSpaces(csvToken.content);
        }
        return csvToken;
    }

    private CSVToken parseEncapsulatedToken(final CSVToken csvToken) throws IOException {
        final long startLineNumber = getCurrentLineNumber();
        int c;
        while (true) {
            c = reader.read();
            if (isEscape(c)) {
                final int unescaped = readEscape();
                if (unescaped == -1) {
                    csvToken.content.append((char) c).append((char) reader.getLastChar());
                } else {
                    csvToken.content.append((char) unescaped);
                }
            } else if (isQuoteChar(c)) {
                if (isQuoteChar(reader.lookAhead())) {
                    c = reader.read();
                    csvToken.content.append((char) c);
                } else {
                    while (true) {
                        c = reader.read();
                        if (isDelimiter(c)) {
                            csvToken.type = CSVToken.Type.TOKEN;
                            return csvToken;
                        } else if (isEndOfFile(c)) {
                            csvToken.type = CSVToken.Type.EOF;
                            csvToken.isReady = true;
                            return csvToken;
                        } else if (readEndOfLine(c)) {
                            csvToken.type = CSVToken.Type.EORECORD;
                            return csvToken;
                        } else if (!isWhitespace(c)) {
                            throw new IOException("(line "
                                    + getCurrentLineNumber()
                                    + ") invalid char between encapsulated token and delimiter");
                        }
                    }
                }
            } else if (isEndOfFile(c)) {
                throw new IOException("(startline "
                        + startLineNumber
                        + ") EOF reached before encapsulated token finished");
            } else {
                csvToken.content.append((char) c);
            }
        }
    }

    long getCurrentLineNumber() {
        return reader.getCurrentLineNumber();
    }

    private int readEscape() throws IOException {
        final int ch = reader.read();
        switch (ch) {
            case 'r':
                return CR;
            case 'n':
                return LF;
            case 't':
                return TAB;
            case 'b':
                return BACKSPACE;
            case 'f':
                return FF;
            case CR:
            case LF:
            case FF:
            case TAB:
            case BACKSPACE:
                return ch;
            case -1:
                throw new IOException("EOF whilst processing escape sequence");
            default:
                if (isMetaChar(ch)) {
                    return ch;
                }
                return -1;
        }
    }

    private void trimTrailingSpaces(final StringBuilder buffer) {
        int length = buffer.length();
        while (length > 0 && Character.isWhitespace(buffer.charAt(length - 1))) {
            length = length - 1;
        }
        if (length != buffer.length()) {
            buffer.setLength(length);
        }
    }

    private boolean readEndOfLine(final int ch) throws IOException {
        int c = ch;
        if (c == CR && reader.lookAhead() == LF) {
            c = reader.read();
        }
        return c == LF || c == CR;
    }

    private boolean isWhitespace(final int ch) {
        return !isDelimiter(ch) && Character.isWhitespace((char) ch);
    }

    private boolean isStartOfLine(final int ch) {
        return ch == LF || ch == CR || ch == -2;
    }

    private boolean isEndOfFile(final int ch) {
        return ch == -1;
    }

    private boolean isDelimiter(final int ch) {
        return ch == delimiter;
    }

    private boolean isEscape(final int ch) {
        return ch == escape;
    }

    private boolean isQuoteChar(final int ch) {
        return ch == quoteChar;
    }

    private boolean isCommentStart(final int ch) {
        return ch == commentStart;
    }

    private boolean isMetaChar(final int ch) {
        return ch == delimiter ||
                ch == escape ||
                ch == quoteChar ||
                ch == commentStart;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
