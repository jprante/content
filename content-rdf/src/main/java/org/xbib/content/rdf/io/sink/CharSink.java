package org.xbib.content.rdf.io.sink;

import org.xbib.content.rdf.io.source.CharSource;

import java.io.IOException;

/**
 * Interface for handling events from {@link CharSource}.
 */
public interface CharSink extends Sink {

    /**
     * Callback for string processing.
     *
     * @param str string for processing
     * @return char sink
     * @throws IOException exception
     */
    CharSink process(String str) throws IOException;

    /**
     * Callback for char processing.
     *
     * @param ch char for processing
     * @return char sink
     * @throws IOException exception
     */
    CharSink process(char ch) throws IOException;

    /**
     * Callback for buffer processing.
     *
     * @param buffer char buffer for processing
     * @param start  position to start
     * @param count  count of chars to process
     * @return char sink
     * @throws IOException exception
     */
    CharSink process(char[] buffer, int start, int count) throws IOException;
}
