package org.xbib.content.resource.scheme;

/**
 *
 */
public class FtpScheme extends HttpScheme {

    static final String FTP_SCHEME_NAME = "ftp";

    private static final int DEFAULT_PORT = 21;

    public FtpScheme() {
        super(FTP_SCHEME_NAME, DEFAULT_PORT);
    }

}
