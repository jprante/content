package org.xbib.content.rdf;

import java.io.IOException;

/**
 * @param <P> the parameter type
 */
@FunctionalInterface
public interface RdfContentBuilderHandler<P extends RdfContentParams> {

    void build(RdfContentBuilder<P> builder) throws IOException;
}
