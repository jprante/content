package org.xbib.content.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class RouteRdfXContent implements RdfContent<RouteRdfXContentParams> {

    private static final RouteRdfXContent ROUTE_RDF_X_CONTENT = new RouteRdfXContent();

    private RouteRdfXContent() {
    }

    public static RouteRdfXContent routeRdfXContent() {
        return ROUTE_RDF_X_CONTENT;
    }

    public static RdfContentBuilder<RouteRdfXContentParams> contentBuilder(RouteRdfXContentParams params) throws IOException {
        return new RdfContentBuilder<>(ROUTE_RDF_X_CONTENT, params);
    }

    @Override
    public StandardRdfContentType type() {
        return null;
    }

    @Override
    public RdfContentGenerator<RouteRdfXContentParams> createGenerator(OutputStream out) {
        return new RouteRdfXContentGenerator<>(out);
    }

    @Override
    public RdfContentParser<RouteRdfXContentParams> createParser(InputStream in) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     */
    @FunctionalInterface
    public interface RouteHandler {
        void complete(String content, RouteRdfXContentParams params) throws IOException;
    }
}
