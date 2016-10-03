package org.xbib.content.resource.scheme;

class HttpsScheme extends HttpScheme {

    static final String HTTPS_SCHEME_NAME = "https";
    private static final int DEFAULT_PORT = 443;

    public HttpsScheme() {
        super(HTTPS_SCHEME_NAME, DEFAULT_PORT);
    }

}
