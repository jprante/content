package org.xbib.content.rdf.io.sink;

import java.io.IOException;

/**
 * Base sink interface.
 */
public interface Sink {

    /**
     * Sets document base URI. Must be called befor start stream event.
     *
     * @param baseUri base URI
     */
    void setBaseUri(String baseUri);

    /**
     * Callback for start stream event.
     *
     * @throws IOException if start of stream fails
     */
    void startStream() throws IOException;

    /**
     * Callback for end of stream event.
     *
     * @throws IOException if end of stream fails
     */
    void endStream() throws IOException;

    void beginDocument(String id) throws IOException;

    void endDocument(String id) throws IOException;

}
