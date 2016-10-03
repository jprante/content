package org.xbib.content.rdf;

import org.xbib.content.resource.IRI;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 * RDF content generator interface.
 *
 * @param <P> the RDF content parameters
 */
public interface RdfContentGenerator<P extends RdfContentParams> extends Flushable, Closeable {

    P getParams();

    RdfContentGenerator<P> setParams(P rdfContentParams);

    RdfContentGenerator<P> startStream() throws IOException;

    RdfContentGenerator<P> setBaseUri(String baseUri);

    RdfContentGenerator<P> startPrefixMapping(String prefix, String uri);

    RdfContentGenerator<P> endPrefixMapping(String prefix);

    RdfContentGenerator<P> receive(IRI identifier) throws IOException;

    RdfContentGenerator<P> receive(Triple triple) throws IOException;

    RdfContentGenerator<P> receive(Resource resource) throws IOException;

    RdfContentGenerator<P> endStream() throws IOException;

}
