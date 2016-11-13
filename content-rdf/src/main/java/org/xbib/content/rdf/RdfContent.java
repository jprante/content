package org.xbib.content.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @param <P> the parameter type
 */
public interface RdfContent<P extends RdfContentParams> {

    StandardRdfContentType type();

    RdfContentGenerator<P> createGenerator(OutputStream out) throws IOException;

    RdfContentParser<P> createParser(InputStream in) throws IOException;
}
