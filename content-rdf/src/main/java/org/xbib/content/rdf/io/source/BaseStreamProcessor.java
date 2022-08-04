package org.xbib.content.rdf.io.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 */
public abstract class BaseStreamProcessor {

    public BaseStreamProcessor() {
    }

    protected abstract void startStream() throws IOException;

    protected abstract void endStream() throws IOException;

    protected abstract void processInternal(Reader reader, String mimeType, String baseUri) throws IOException;

    protected abstract void processInternal(InputStream inputStream, String mimeType, String baseUri) throws IOException;

    /**
     * Processes document pointed by specified URI.
     *
     * @param uri document's URI
     * @throws IOException if process fails
     */
    public final void process(String uri) throws IOException {
        process(uri, uri);
    }

    /**
     * Processes document pointed by specified URI. Uses specified URI as document's base.
     *
     * @param uri     document's URI
     * @param baseUri document's URI
     * @throws IOException if process fails
     */
    public final void process(String uri, String baseUri) throws IOException {
        URL url = new URL(uri);
        URLConnection urlConnection = url.openConnection();
        String mimeType = urlConnection.getContentType();
        try (InputStream inputStream = urlConnection.getInputStream()) {
            process(inputStream, mimeType, baseUri);
        }
    }

    public void process(InputStream inputStream) throws IOException {
        process(inputStream, null, null);
    }

    /**
     * Processes stream input for document.
     *
     * @param inputStream document's input stream
     * @param baseUri     document's base URI
     * @throws IOException if process fails
     */
    public void process(InputStream inputStream, String baseUri) throws IOException {
        process(inputStream, null, baseUri);
    }

    /**
     * Processes stream input for document.
     *
     * @param inputStream document's input stream
     * @param mimeType    document's MIME type
     * @param baseUri     document's base URI
     * @throws IOException if process fails
     */
    public final void process(InputStream inputStream, String mimeType, String baseUri) throws IOException {
        startStream();
        try {
            processInternal(inputStream, mimeType, baseUri);
        } finally {
            endStream();
        }
    }

    /**
     * Processes reader input for documents.
     *
     * @param reader document's reader
     * @throws IOException if process fails
     */
    public void process(Reader reader) throws IOException {
        process(reader, null, null);
    }

    /**
     * Processes reader input for documents.
     *
     * @param reader  document's reader
     * @param baseUri base URI
     * @throws IOException if process fails
     */
    public void process(Reader reader, String baseUri) throws IOException {
        process(reader, null, baseUri);
    }

    /**
     * Processes reader input for documents.
     *
     * @param reader   document's reader
     * @param mimeType document's MIME type
     * @param baseUri  document's base URI
     * @throws IOException if process fails
     */
    public final void process(Reader reader, String mimeType, String baseUri) throws IOException {
        startStream();
        try {
            processInternal(reader, mimeType, baseUri);
        } finally {
            endStream();
        }
    }
}
