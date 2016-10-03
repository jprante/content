package org.xbib.content.rdf;

import java.io.IOException;

/**
 * @param <P> the parameter type
 */
@FunctionalInterface
public interface RdfContentBuilderProvider<P extends RdfContentParams> {

    RdfContentBuilder<P> newContentBuilder() throws IOException;
}
