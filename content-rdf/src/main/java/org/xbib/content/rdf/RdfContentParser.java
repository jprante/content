package org.xbib.content.rdf;

import java.io.IOException;

/**
 *
 */
public interface RdfContentParser {

    RdfContentType contentType();

    RdfContentParser parse() throws IOException;

}
