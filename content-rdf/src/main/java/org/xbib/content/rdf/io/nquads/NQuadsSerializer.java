package org.xbib.content.rdf.io.nquads;

import org.xbib.content.rdf.io.ntriple.NTriplesSerializer;
import org.xbib.content.rdf.io.sink.CharSink;
import org.xbib.content.rdf.io.sink.QuadSink;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link org.xbib.content.rdf.io.sink.TripleSink} which serializes triples to
 * {@link org.xbib.content.rdf.io.sink.CharSink} using NTriples syntax. *
 */
public class NQuadsSerializer extends NTriplesSerializer implements QuadSink {

    private static final Logger logger = Logger.getLogger(NQuadsSerializer.class.getName());

    private final CharSink sink;

    private NQuadsSerializer(CharSink sink) {
        super(sink);
        this.sink = sink;
    }

    /**
     * Creates instance of TurtleSerializer connected to specified sink.
     *
     * @param sink sink to be connected to
     * @return instance of TurtleSerializer
     */
    public static QuadSink connect(CharSink sink) {
        return new NQuadsSerializer(sink);
    }

    @Override
    public void addNonLiteral(String subj, String pred, String obj, String graph) {
        try {
            startTriple(subj, pred);
            serializeBnodeOrUri(obj);
            if (graph != null) {
                serializeBnodeOrUri(graph);
            }
            sink.process(DOT_EOL);
        } catch (IOException e) {
            logger.log(Level.FINE, e.getMessage(), e);
        }
    }

    @Override
    public void addPlainLiteral(String subj, String pred, String content, String lang, String graph) {
        try {
            startTriple(subj, pred);
            addContent(content);
            if (lang != null) {
                sink.process('@').process(lang);
            }
            sink.process(SPACE);
            if (graph != null) {
                serializeBnodeOrUri(graph);
            }
            sink.process(DOT_EOL);
        } catch (IOException e) {
            logger.log(Level.FINE, e.getMessage(), e);
        }
    }

    @Override
    public void addTypedLiteral(String subj, String pred, String content, String type, String graph) {
        try {
            startTriple(subj, pred);
            addContent(content);
            sink.process("^^");
            serializeUri(type);
            if (graph != null) {
                serializeBnodeOrUri(graph);
            }
            sink.process(DOT_EOL);
        } catch (IOException e) {
            logger.log(Level.FINE, e.getMessage(), e);
        }
    }
}
