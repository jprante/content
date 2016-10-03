package org.xbib.content.resource.scheme;

import org.xbib.content.resource.IRI;

/**
 * Base implementation for IRI scheme providers.
 */
public abstract class AbstractScheme implements Scheme {

    protected final String name;
    protected final int port;

    protected AbstractScheme(String name, int port) {
        this.name = name;
        this.port = port;
    }

    @Override
    public int getDefaultPort() {
        return port;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Default return unmodified.
     */
    @Override
    public IRI normalize(IRI iri) {
        return iri;
    }

    /**
     * Default return unmodified.
     */
    @Override
    public String normalizePath(String path) {
        return path;
    }
}
