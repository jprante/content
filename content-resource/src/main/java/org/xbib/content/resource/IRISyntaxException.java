package org.xbib.content.resource;

/**
 *
 */
@SuppressWarnings("serial")
public class IRISyntaxException extends RuntimeException {

    IRISyntaxException(String message) {
        super(message);
    }

    IRISyntaxException(Throwable cause) {
        super(cause);
    }

}
