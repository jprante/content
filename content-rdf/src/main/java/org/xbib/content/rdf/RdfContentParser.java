package org.xbib.content.rdf;

import java.io.IOException;

/**
 *
 */
public interface RdfContentParser<R extends RdfContentParams> {

    RdfContentType contentType();

    RdfContentParser<R> parse() throws IOException;

}
