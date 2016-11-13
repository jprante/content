package org.xbib.content.rdf.io.ntriple;

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
public class NTripleContent implements RdfContent<NTripleContentParams> {

    private static final NTripleContent N_TRIPLE_CONTENT = new NTripleContent();

    private NTripleContent() {
    }

    public static NTripleContent nTripleContent() {
        return N_TRIPLE_CONTENT;
    }

    public static RdfContentBuilder<NTripleContentParams> contentBuilder(NTripleContentParams params) throws IOException {
        return new RdfContentBuilder<>(N_TRIPLE_CONTENT, params);
    }

    public static RdfContentBuilder<NTripleContentParams> contentBuilder(OutputStream out, NTripleContentParams params)
            throws IOException {
        return new RdfContentBuilder<>(N_TRIPLE_CONTENT, params, out);
    }

    @Override
    public StandardRdfContentType type() {
        return StandardRdfContentType.NTRIPLE;
    }

    @Override
    public RdfContentGenerator<NTripleContentParams> createGenerator(OutputStream out) throws IOException {
        return new NTripleContentGenerator(out);
    }

    @Override
    public RdfContentParser<NTripleContentParams> createParser(InputStream in) throws IOException {
        return new NTripleContentParser<>(in);
    }
}
