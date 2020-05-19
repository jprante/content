package org.xbib.content.rdf.io.ntriple;

import org.xbib.content.rdf.RDF;
import org.xbib.content.rdf.io.sink.CharSink;
import org.xbib.content.rdf.io.sink.TripleSink;

import java.io.IOException;
import java.util.BitSet;

/**
 * Implementation of {@link org.xbib.content.rdf.io.sink.TripleSink} which serializes triples to
 * {@link org.xbib.content.rdf.io.sink.CharSink} using NTriples syntax. *
 */
public class NTriplesSerializer implements TripleSink, RDF {

    protected static final String DOT_EOL = ".\n";
    protected static final char SPACE = ' ';
    private static final char QUOTE = '"';
    private static final char URI_START = '<';
    private static final char URI_END = '>';

    private static final BitSet ESCAPABLE_CONTENT_CHARS = new BitSet();
    private static final BitSet ESCAPABLE_URI_CHARS = new BitSet();

    static {
        ESCAPABLE_CONTENT_CHARS.set('\\');
        ESCAPABLE_CONTENT_CHARS.set('\"');
        ESCAPABLE_CONTENT_CHARS.set('\b');
        ESCAPABLE_CONTENT_CHARS.set('\f');
        ESCAPABLE_CONTENT_CHARS.set('\n');
        ESCAPABLE_CONTENT_CHARS.set('\r');
        ESCAPABLE_CONTENT_CHARS.set('\t');

        for (char ch = 0; ch <= 0x20; ch++) {
            ESCAPABLE_URI_CHARS.set(ch);
        }
        ESCAPABLE_URI_CHARS.set('\\');
        ESCAPABLE_URI_CHARS.set('<');
        ESCAPABLE_URI_CHARS.set('>');
        ESCAPABLE_URI_CHARS.set('{');
        ESCAPABLE_URI_CHARS.set('}');
        ESCAPABLE_URI_CHARS.set('"');
        ESCAPABLE_URI_CHARS.set('`');
        ESCAPABLE_URI_CHARS.set('|');
        ESCAPABLE_URI_CHARS.set('^');
    }

    private final CharSink sink;

    protected NTriplesSerializer(CharSink sink) {
        this.sink = sink;
    }

    public static TripleSink connect(CharSink sink) {
        return new NTriplesSerializer(sink);
    }

    private static String escapeContent(String str) {
        int limit = str.length();
        int pos = 0;
        for (; pos < limit; pos++) {
            char ch = str.charAt(pos);
            if (ch > 0x80 || ESCAPABLE_CONTENT_CHARS.get(ch)) {
                break;
            }
        }
        if (pos == limit) {
            return str;
        }
        StringBuilder result = new StringBuilder(limit);
        result.append(str.substring(0, pos));
        for (; pos < limit; pos++) {
            char ch = str.charAt(pos);
            if (ch < 0x80) {
                switch (ch) {
                    case '\\':
                    case '\"':
                        result.append('\\').append(ch);
                        break;
                    case '\b':
                        result.append("\\b");
                        break;
                    case '\f':
                        result.append("\\f");
                        break;
                    case '\n':
                        result.append("\\n");
                        break;
                    case '\r':
                        result.append("\\r");
                        break;
                    case '\t':
                        result.append("\\t");
                        break;
                    default:
                        result.append(ch);
                }
            } else {
                result.append("\\u").append(String.format("%04X", (int) ch));
            }
        }
        return result.toString();
    }

    private static String escapeUri(String str) {
        int limit = str.length();
        int pos = 0;
        for (; pos < limit; pos++) {
            char ch = str.charAt(pos);
            if (ch > 0x80 || ESCAPABLE_URI_CHARS.get(ch)) {
                break;
            }
        }
        if (pos == limit) {
            return str;
        }
        StringBuilder result = new StringBuilder(limit);
        result.append(str, 0, pos);
        for (; pos < limit; pos++) {
            char ch = str.charAt(pos);
            if (ch < 0x80) {
                result.append(ch);
            } else {
                result.append("\\u").append(String.format("%04X", (int) ch));
            }
        }
        return result.toString();
    }

    @Override
    public void startStream() throws IOException {
        sink.startStream();
    }

    @Override
    public void endStream() throws IOException {
        sink.endStream();
    }

    @Override
    public void beginDocument(String id) {
        // nothing to do
    }

    @Override
    public void endDocument(String id) {
        // nothing to do
    }

    @Override
    public void addNonLiteral(String subj, String pred, String obj) throws IOException {
        startTriple(subj, pred);
        serializeBnodeOrUri(obj);
        sink.process(DOT_EOL);
    }

    @Override
    public void addPlainLiteral(String subj, String pred, String content, String lang) throws IOException {
        startTriple(subj, pred);
        addContent(content);
        if (lang != null) {
            sink.process('@').process(lang);
        }
        sink.process(SPACE).process(DOT_EOL);
    }

    @Override
    public void addTypedLiteral(String subj, String pred, String content, String type) throws IOException {
        startTriple(subj, pred);
        addContent(content);
        sink.process("^^");
        serializeUri(type);
        sink.process(DOT_EOL);
    }

    @Override
    public void setBaseUri(String baseUri) {
        // ignore
    }

    protected void startTriple(String subj, String pred) throws IOException {
        serializeBnodeOrUri(subj);
        serializeBnodeOrUri(pred);
    }

    protected void serializeBnodeOrUri(String value) throws IOException {
        if (value.startsWith(BNODE_PREFIX)) {
            sink.process(value).process(SPACE);
        } else {
            serializeUri(value);
        }
    }

    protected void serializeUri(String uri) throws IOException {
        String escapedUri = escapeUri(uri);
        sink.process(URI_START).process(escapedUri).process(URI_END).process(SPACE);
    }

    protected void addContent(String content) throws IOException {
        String escapedContent = escapeContent(content);
        sink.process(QUOTE).process(escapedContent).process(QUOTE);
    }
}
