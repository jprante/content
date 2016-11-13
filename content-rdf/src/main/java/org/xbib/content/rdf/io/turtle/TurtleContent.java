package org.xbib.content.rdf.io.turtle;

import org.xbib.content.rdf.RdfContent;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentGenerator;
import org.xbib.content.rdf.RdfContentParser;
import org.xbib.content.rdf.StandardRdfContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class TurtleContent implements RdfContent<TurtleContentParams> {

    private static final TurtleContent TURTLE_CONTENT = new TurtleContent();

    private TurtleContent() {
    }

    public static TurtleContent turtleContent() {
        return TURTLE_CONTENT;
    }

    public static RdfContentBuilder<TurtleContentParams> contentBuilder(TurtleContentParams params) throws IOException {
        return new RdfContentBuilder<>(TURTLE_CONTENT, params);
    }

    public static RdfContentBuilder<TurtleContentParams> contentBuilder(OutputStream out, TurtleContentParams params)
            throws IOException {
        return new RdfContentBuilder<>(TURTLE_CONTENT, params, out);
    }

    @Override
    public StandardRdfContentType type() {
        return StandardRdfContentType.TURTLE;
    }

    @Override
    public RdfContentGenerator<TurtleContentParams> createGenerator(OutputStream outputStream) throws IOException {
        return new TurtleContentGenerator(outputStream);
    }

    @Override
    public RdfContentParser<TurtleContentParams> createParser(InputStream inputStream) throws IOException {
        return new TurtleContentParser<>(inputStream);
    }

}
