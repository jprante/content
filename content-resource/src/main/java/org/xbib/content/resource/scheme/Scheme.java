package org.xbib.content.resource.scheme;

import org.xbib.content.resource.IRI;

/**
 * Interface implemented by custom IRI scheme parsers.
 */
public interface Scheme {

    String getName();

    IRI normalize(IRI iri);

    String normalizePath(String path);

    int getDefaultPort();
}
