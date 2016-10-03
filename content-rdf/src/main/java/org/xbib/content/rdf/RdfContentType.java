package org.xbib.content.rdf;

/**
 *
 */
public interface RdfContentType {

    String contentType();

    String shortName();

    RdfContent<RdfContentParams> rdfContent();
}
