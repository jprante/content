package org.xbib.content.rdf;

import java.io.IOException;

/**
 *
 * @param <R> parameter type
 */
public interface RdfContentParser<R extends RdfContentParams> {

    RdfContentType contentType();

    RdfContentParser<R> parse() throws IOException;

}
