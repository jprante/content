package org.xbib.content.rdf;

import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
@FunctionalInterface
public interface RdfContentParams {

    IRINamespaceContext getNamespaceContext();
}
