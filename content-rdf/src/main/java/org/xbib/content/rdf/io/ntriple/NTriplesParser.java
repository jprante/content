package org.xbib.content.rdf.io.ntriple;

import org.xbib.content.rdf.io.sink.CharSink;
import org.xbib.content.rdf.io.sink.TripleSink;

import java.io.IOException;
import java.util.BitSet;

/**
 * Implementation of streaming <a href="http://www.w3.org/2001/sw/RDFCore/ntriples/">NTriples</a> parser.
 */
public final class NTriplesParser implements CharSink {

    private static final short PARSING_OUTSIDE = 0;
    private static final short PARSING_URI = 1;
    private static final short PARSING_BNODE = 2;
    private static final short PARSING_LITERAL = 3;
    private static final short PARSING_AFTER_LITERAL = 4;
    private static final short PARSING_LITERAL_TYPE = 5;
    private static final short PARSING_COMMENT = 6;

    private static final char SENTENCE_END = '.';

    private static final BitSet WHITESPACE = new BitSet() {
        {
            set('\t');
            set(' ');
            set('\r');
            set('\n');
        }
    };

    private final TripleSink sink;

    private String subj = null;
    private String pred = null;
    private String literalObj = null;

    private boolean skipSentence = false;

    private short parsingState;

    private int tokenStartPos;
    private short charsToEscape = 0;
    private boolean waitingForSentenceEnd = false;
    private StringBuilder addBuffer = null;

    private NTriplesParser(TripleSink sink) {
        this.sink = sink;
    }

    /**
     * Creates instance of NTriplesParser connected to specified sink.
     *
     * @param sink sink to be connected to
     * @return instance of NTriplesParser
     */
    public static CharSink connect(TripleSink sink) {
        return new NTriplesParser(sink);
    }

    @Override
    public NTriplesParser process(String str) throws IOException {
        return process(str.toCharArray(), 0, str.length());
    }

    @Override
    public NTriplesParser process(char ch) throws IOException {
        char[] buffer = new char[1];
        buffer[0] = ch;
        return process(buffer, 0, 1);
    }

    @Override
    public NTriplesParser process(char[] buffer, int start, int count) throws IOException {
        if (tokenStartPos != -1) {
            tokenStartPos = start;
        }
        int end = start + count;

        for (int pos = start; pos < end; pos++) {
            if (skipSentence && buffer[pos] != SENTENCE_END) {
                continue;
            } else {
                skipSentence = false;
            }

            if (parsingState == PARSING_OUTSIDE) {
                processOutsideChar(buffer, pos);
            } else if (parsingState == PARSING_COMMENT) {
                if (buffer[pos] == '\n' || buffer[pos] == '\r') {
                    parsingState = PARSING_OUTSIDE;
                }
            } else if (parsingState == PARSING_URI) {
                if (buffer[pos] == '>') {
                    onNonLiteral(unescape(extractToken(buffer, pos, 1)));
                    parsingState = PARSING_OUTSIDE;
                }
            } else if (parsingState == PARSING_BNODE) {
                if (WHITESPACE.get(buffer[pos]) || buffer[pos] == SENTENCE_END) {
                    onNonLiteral(extractToken(buffer, pos - 1, 0));
                    parsingState = PARSING_OUTSIDE;
                }
            } else if (parsingState == PARSING_LITERAL) {
                processLiteralChar(buffer, pos);
            } else if (parsingState == PARSING_AFTER_LITERAL) {
                if (buffer[pos] == '@' || buffer[pos] == '^') {
                    tokenStartPos = pos;
                    parsingState = PARSING_LITERAL_TYPE;
                } else if (WHITESPACE.get(buffer[pos]) || buffer[pos] == SENTENCE_END) {
                    onPlainLiteral(literalObj, null);
                    parsingState = PARSING_OUTSIDE;
                    processOutsideChar(buffer, pos);
                } else {
                    throw new IOException("unexpected character '" + buffer[pos] + "' after literal");
                }
            } else if (parsingState == PARSING_LITERAL_TYPE) {
                processLiteralTypeChar(buffer, pos);
            }
        }
        if (tokenStartPos != -1) {
            if (addBuffer == null) {
                addBuffer = new StringBuilder();
            }
            addBuffer.append(buffer, tokenStartPos, end - tokenStartPos);
        }
        return this;
    }

    private void processLiteralChar(char[] buffer, int pos) throws IOException {
        if (charsToEscape == 9 && buffer[pos] == 'u') {
            charsToEscape -= 5;
        } else if (charsToEscape == 9 && buffer[pos] != 'U') {
            charsToEscape = 0;
        } else if (charsToEscape > 0) {
            charsToEscape--;
        } else {
            if (buffer[pos] == '\"') {
                literalObj = unescape(extractToken(buffer, pos, 1));
                parsingState = PARSING_AFTER_LITERAL;
            } else if (buffer[pos] == '\\') {
                charsToEscape = 9;
            }
        }
    }

    private void processLiteralTypeChar(char[] buffer, int pos) throws IOException {
        if (WHITESPACE.get(buffer[pos])) {
            String type = extractToken(buffer, pos, 0);
            int trimSize = type.charAt(type.length() - 1) == SENTENCE_END ? 1 : 0;
            if (type.charAt(0) == '@') {
                onPlainLiteral(literalObj, type.substring(1, type.length() - 1 - trimSize));
            } else if (type.startsWith("^^<") && type.charAt(type.length() - 2) == '>') {
                onTypedLiteral(literalObj, type.substring(3, type.length() - 2 - trimSize));
            } else {
                throw new IOException("literal type '" + type + "' can not be parsed");
            }
            parsingState = PARSING_OUTSIDE;
            if (trimSize > 0) {
                finishSentence();
            }
        }
    }

    private void processOutsideChar(char[] buffer, int pos) throws IOException {
        switch (buffer[pos]) {
            case '\"':
                parsingState = PARSING_LITERAL;
                tokenStartPos = pos;
                break;
            case '<':
                parsingState = PARSING_URI;
                tokenStartPos = pos;
                break;
            case '_':
                parsingState = PARSING_BNODE;
                tokenStartPos = pos;
                break;
            case '#':
                parsingState = PARSING_COMMENT;
                break;
            case SENTENCE_END:
                finishSentence();
                break;
            default:
                if (!WHITESPACE.get(buffer[pos])) {
                    throw new IOException("unexpected character '" + buffer[pos] + "'");
                }
        }
    }

    private void finishSentence() throws IOException {
        if (waitingForSentenceEnd) {
            waitingForSentenceEnd = false;
        } else {
            throw new IOException("unexpected end of sentence");
        }
    }

    private void onNonLiteral(String uri) throws IOException {
        if (waitingForSentenceEnd) {
            throw new IOException("endStream of sentence expected");
        }
        if (subj == null) {
            subj = uri;
        } else if (pred == null) {
            pred = uri;
        } else {
            sink.addNonLiteral(subj, pred, uri);
            resetTriple();
        }
    }

    private void onPlainLiteral(String value, String lang) throws IOException {
        if (subj == null || pred == null) {
            if (waitingForSentenceEnd) {
                throw new IOException("end of sentence expected");
            } else {
                throw new IOException("literal is not an object");
            }
        }
        sink.addPlainLiteral(subj, pred, value, lang);
        resetTriple();
    }

    private void onTypedLiteral(String value, String type) throws IOException {
        if (subj == null || pred == null) {
            if (waitingForSentenceEnd) {
                throw new IOException("end of sentence expected");
            } else {
                throw new IOException("literal is not an object");
            }
        }
        sink.addTypedLiteral(subj, pred, value, type);
        resetTriple();
    }

    @Override
    public void setBaseUri(String baseUri) {
        // we don't have a base URI
    }

    private String extractToken(char[] buffer, int tokenEndPos, int trimSize) throws IOException {
        String saved;
        if (addBuffer != null) {
            if (tokenEndPos - trimSize >= tokenStartPos) {
                addBuffer.append(buffer, tokenStartPos, tokenEndPos - tokenStartPos - trimSize + 1);
            }
            addBuffer.delete(0, trimSize);
            saved = addBuffer.toString();
            addBuffer = null;
        } else {
            saved = String.valueOf(buffer, tokenStartPos + trimSize, tokenEndPos - tokenStartPos + 1 - 2 * trimSize);
        }
        tokenStartPos = -1;
        return saved;
    }

    @Override
    public void startStream() throws IOException {
        sink.startStream();
        resetTriple();
        waitingForSentenceEnd = false;
        parsingState = PARSING_OUTSIDE;
    }

    private void resetTriple() {
        addBuffer = null;
        tokenStartPos = -1;
        subj = null;
        pred = null;
        waitingForSentenceEnd = true;
    }

    @Override
    public void endStream() throws IOException {
        if (tokenStartPos != -1 || waitingForSentenceEnd) {
            throw new IOException("unexpected end of stream");
        }
        sink.endStream();
    }

    @Override
    public void beginDocument(String id) throws IOException {
        sink.beginDocument(id);
    }

    @Override
    public void endDocument(String id) throws IOException {
        sink.endDocument(id);
    }

    private String unescape(String str) throws IOException {
        int limit = str.length();
        StringBuilder result = new StringBuilder(limit);

        for (int i = 0; i < limit; i++) {
            char ch = str.charAt(i);
            if (ch != '\\') {
                result.append(ch);
                continue;
            }
            i++;
            if (i == limit) {
                break;
            }
            ch = str.charAt(i);
            switch (ch) {
                case 'b':
                    result.append('\b');
                    break;
                case 'f':
                    result.append('\f');
                    break;
                case 'n':
                    result.append('\n');
                    break;
                case 'r':
                    result.append('\r');
                    break;
                case 't':
                    result.append('\t');
                    break;
                case 'u':
                case 'U':
                    int sequenceLength = ch == 'u' ? 4 : 8;
                    if (i + sequenceLength >= limit) {
                        throw new IOException("error parsing escape sequence '\\" + ch + "'");
                    }
                    String code = str.substring(i + 1, i + 1 + sequenceLength);
                    i += sequenceLength;

                    try {
                        int value = Integer.parseInt(code, 16);
                        result.append((char) value);
                    } catch (NumberFormatException nfe) {
                        throw new IOException("error parsing escape sequence '\\" + ch + "'");
                    }
                    break;
                default:
                    result.append(ch);
                    break;
            }
        }
        return result.toString();
    }

}
