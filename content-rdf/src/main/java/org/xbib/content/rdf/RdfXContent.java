package org.xbib.content.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class RdfXContent implements RdfContent<RdfXContentParams> {

    private static final RdfXContent RDF_X_CONTENT = new RdfXContent();

    private RdfXContent() {
    }

    public static RdfXContent rdfXContent() {
        return RDF_X_CONTENT;
    }

    public static RdfContentBuilder<RdfXContentParams> contentBuilder(RdfXContentParams params) throws IOException {
        return new RdfContentBuilder<>(RDF_X_CONTENT, params);
    }

    @Override
    public StandardRdfContentType type() {
        return null;
    }

    @Override
    public RdfContentGenerator<RdfXContentParams> createGenerator(OutputStream out) throws IOException {
        return new RdfXContentGenerator<>(out);
    }

    @Override
    public RdfContentParser<RdfXContentParams> createParser(InputStream in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
