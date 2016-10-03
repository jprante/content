package org.xbib.content.resource.scheme;

import org.xbib.content.resource.IRI;
import org.xbib.content.resource.text.CharUtils.Profile;
import org.xbib.content.resource.url.UrlEncoding;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class HttpScheme extends AbstractScheme {

    static final String HTTP_SCHEME_NAME = "http";
    private static final Logger logger = Logger.getLogger(HttpScheme.class.getName());
    private static final int DEFAULT_PORT = 80;

    HttpScheme() {
        super(HTTP_SCHEME_NAME, DEFAULT_PORT);
    }

    HttpScheme(String name, int port) {
        super(name, port);
    }

    @Override
    public IRI normalize(IRI iri) {
        int port = (iri.getPort() == getDefaultPort()) ? -1 : iri.getPort();
        String host = iri.getHost();
        if (host != null) {
            host = host.toLowerCase();
        }
        try {
            return IRI.builder()
                    .scheme(iri.getScheme())
                    .userinfo(iri.getUserInfo())
                    .host(host)
                    .port(port)
                    .path(iri.getPath())
                    .query(UrlEncoding.encode(UrlEncoding.decode(iri.getQuery()), Profile.IQUERY.filter()))
                    .fragment(UrlEncoding.encode(UrlEncoding.decode(iri.getFragment()), Profile.IFRAGMENT.filter()))
                    .build();
        } catch (IOException e) {
            logger.log(Level.FINE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String normalizePath(String path) {
        return null;
    }

}
