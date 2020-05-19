package org.xbib.content.rdf.io.turtle;

import org.xbib.content.rdf.RDF;
import org.xbib.content.rdf.io.sink.CharSink;
import org.xbib.content.rdf.io.sink.TripleSink;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Implementation of {@link org.xbib.content.rdf.io.sink.TripleSink} which serializes triples to {@link
 * org.xbib.content.rdf.io.sink.CharSink} using
 * <a href="http://www.w3.org/TR/2012/WD-turtle-20120710/">Turtle</a> syntax. *
 */
public final class TurtleSerializer implements TripleSink, RDF {

    private static final String DOT_EOL = " .\n";
    private static final String COMMA_EOL = " ,\n";
    private static final String SEMICOLON_EOL = " ;\n";
    private static final String EOL = "\n";

    private static final String MULTILINE_QUOTE = "\"\"\"";
    private static final char SINGLE_LINE_QUOTE = '"';
    private static final char BNODE_START = '[';
    private static final char BNODE_END = ']';
    private static final char URI_START = '<';
    private static final char URI_END = '>';

    private static final char SPACE = ' ';
    private static final char RDF_TYPE_ABBR = 'a';
    private static final String INDENT = "    ";

    private final CharSink sink;
    private final LinkedList<String> bnodeStack = new LinkedList<>();
    private final Set<String> namedBnodes = new HashSet<>();
    private String prevSubj;
    private String prevPred;
    private String baseUri;

    private TurtleSerializer(CharSink sink) {
        this.sink = sink;
    }

    /**
     * Creates instance of TurtleSerializer connected to specified sink.
     *
     * @param sink sink to be connected to
     * @return instance of TurtleSerializer
     */
    public static TripleSink connect(CharSink sink) {
        return new TurtleSerializer(sink);
    }

    @Override
    public void addNonLiteral(String subj, String pred, String obj) throws IOException {
        startTriple(subj, pred);
        if (obj.startsWith(BNODE_PREFIX)) {
            if (!namedBnodes.contains(obj) && obj.endsWith(SHORTENABLE_BNODE_SUFFIX)) {
                openBnode(obj);
            } else {
                sink.process(obj);
            }
        } else {
            serializeUri(obj);
        }
    }

    @Override
    public void addPlainLiteral(String subj, String pred, String content, String lang) throws IOException {
        startTriple(subj, pred);
        addContent(content);
        if (lang != null) {
            sink.process('@').process(lang);
        }
    }

    @Override
    public void addTypedLiteral(String subj, String pred, String content, String type) throws IOException {
        startTriple(subj, pred);
        addContent(content);
        sink.process("^^");
        serializeUri(type);
}

    @Override
    public void startStream() throws IOException {
        sink.startStream();
        prevSubj = null;
        prevPred = null;
        if (baseUri != null) {
            sink.process("@base ").process(URI_START).process(baseUri).process(URI_END).process(DOT_EOL);
        }
        sink.process("@prefix rdf: ").process(URI_START).process(NS).process(URI_END).process(DOT_EOL);
        bnodeStack.clear();
        namedBnodes.clear();
    }

    @Override
    public void endStream() throws IOException {
        while (!bnodeStack.isEmpty()) {
            closeBnode();
        }
        if (prevPred != null) {
            sink.process(DOT_EOL);
        } else {
            sink.process(EOL);
        }
        baseUri = null;
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

    @Override
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri.substring(0, baseUri.length() - 1);
    }

    private void startTriple(String subj, String pred) throws IOException {
        if (subj.equals(prevSubj)) {
            if (pred.equals(prevPred)) {
                sink.process(COMMA_EOL);
                indent(2);
            } else if (prevPred != null) {
                sink.process(SEMICOLON_EOL);
                indent(1);
                serializePredicate(pred);
            } else {
                indent(0);
                serializePredicate(pred);
            }
        } else {
            if (!bnodeStack.isEmpty()) {
                closeBnode();
                startTriple(subj, pred);
                return;
            } else if (prevSubj != null) {
                sink.process(DOT_EOL);
            }
            if (subj.startsWith(BNODE_PREFIX)) {
                if (subj.endsWith(SHORTENABLE_BNODE_SUFFIX)) {
                    openBnode(subj);
                } else {
                    sink.process(subj).process(SPACE);
                    namedBnodes.add(subj);
                }
            } else {
                serializeUri(subj);
            }
            serializePredicate(pred);
        }
        prevSubj = subj;
        prevPred = pred;
    }

    private void serializePredicate(String pred) throws IOException {
        if (TYPE.equals(pred)) {
            sink.process(RDF_TYPE_ABBR).process(SPACE);
        } else {
            serializeUri(pred);
        }
    }

    private void serializeUri(String uri) throws IOException {
        String escapedUri = uri.replace("\\", "\\\\").replace(">", "\\u003E");
        if (escapedUri.startsWith(NS)) {
            sink.process("rdf:").process(escapedUri.substring(NS.length()));
        } else if (baseUri != null && escapedUri.startsWith(baseUri)) {
            sink.process(URI_START).process(escapedUri.substring(baseUri.length())).process(URI_END);
        } else {
            sink.process(URI_START).process(escapedUri).process(URI_END);
        }
        sink.process(SPACE);
    }

    private void indent(int additionalIndent) throws IOException {
        for (int i = 0; i < bnodeStack.size() + additionalIndent; i++) {
            sink.process(INDENT);
        }
    }

    private void addContent(String content) throws IOException {
        String escapedContent = content.replace("\\", "\\\\").replace("\"", "\\\"");
        if (escapedContent.contains(EOL)) {
            sink.process(MULTILINE_QUOTE).process(escapedContent).process(MULTILINE_QUOTE);
        } else {
            sink.process(SINGLE_LINE_QUOTE).process(escapedContent).process(SINGLE_LINE_QUOTE);
        }
    }

    private void openBnode(String obj) throws IOException {
        sink.process(BNODE_START);
        bnodeStack.push(obj);
        prevSubj = obj;
        prevPred = null;
    }

    private void closeBnode() throws IOException {
        sink.process(BNODE_END);
        prevSubj = bnodeStack.pop();
        prevPred = null;
        if (prevSubj == null) {
            sink.process(DOT_EOL);
        }
    }

}
