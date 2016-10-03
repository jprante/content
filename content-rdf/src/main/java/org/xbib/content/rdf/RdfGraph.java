package org.xbib.content.rdf;

import org.xbib.content.resource.IRI;

import java.util.Iterator;

/**
 * RDF graph interface.
 *
 * @param <P> type parameter
 */
public interface RdfGraph<P extends RdfGraphParams> extends RdfContentGenerator<P> {

    Iterator<Resource> getResources();

    RdfGraph<P> putResource(IRI predicate, Resource resource);

    Resource getResource(IRI predicate);

    Resource removeResource(IRI predicate);

    boolean hasResource(IRI predicate);

}
