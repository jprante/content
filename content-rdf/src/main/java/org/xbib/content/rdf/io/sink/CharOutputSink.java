package org.xbib.content.rdf.io.sink;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Implementation of {@link org.xbib.content.rdf.io.sink.CharSink}. Provides bridging to Java IO APIs
 * ({@link java.io.Writer}, {@link java.io.OutputStream}, {@link java.io.File}).
 */
public final class CharOutputSink implements CharSink {

    private static final short BATCH_SIZE = 256;

    private final Charset charset;

    private Writer writer;

    private OutputStream outputStream;

    private boolean closeOnEndStream;

    private StringBuilder buffer;

    private long bufferSize;

    /**
     * Creates class instance with default charset encoding.
     */
    public CharOutputSink() {
        this(Charset.defaultCharset());
    }

    /**
     * Creates class instance with specified charset encoding.
     *
     * @param charset charset
     */
    public CharOutputSink(Charset charset) {
        this.charset = charset;
    }


    /**
     * Redirects output to specified writer.
     *
     * @param writer output writer
     */
    public void connect(Writer writer) {
        this.writer = writer;
        this.outputStream = null;
        this.closeOnEndStream = false;
    }

    /**
     * Redirects output to specified stream.
     *
     * @param outputStream output stream
     */
    public void connect(OutputStream outputStream) {
        this.writer = null;
        this.outputStream = outputStream;
        this.closeOnEndStream = false;
    }

    @Override
    public CharOutputSink process(String str) throws IOException {
        buffer.append(str);
        bufferSize += str.length();
        writeBuffer();
        return this;
    }

    @Override
    public CharOutputSink process(char ch) throws IOException {
        buffer.append(ch);
        bufferSize++;
        writeBuffer();
        return this;
    }

    @Override
    public CharOutputSink process(char[] buffer, int start, int count) throws IOException {
        this.buffer.append(buffer, start, count);
        bufferSize += count;
        writeBuffer();
        return this;
    }

    private void writeBuffer() throws IOException {
        if (bufferSize >= BATCH_SIZE) {
            writer.write(buffer.toString());
            buffer = new StringBuilder(BATCH_SIZE);
            bufferSize = 0;
        }
    }

    @Override
    public void setBaseUri(String baseUri) {
        // we don't have a base URI
    }

    @Override
    public void startStream() throws IOException {
        buffer = new StringBuilder();
        bufferSize = 0;
        if (writer == null) {
            writer = new OutputStreamWriter(outputStream, charset);
        }
    }

    @Override
    public void endStream() throws IOException {
        buffer.append("\n");
        bufferSize = BATCH_SIZE;
        writeBuffer();
        writer.flush();
        if (closeOnEndStream) {
            writer.close();
            writer = null;
        } else if (outputStream != null) {
            outputStream.close();
            outputStream = null;
        }
    }

    @Override
    public void beginDocument(String id) throws IOException {
        // nothing to do
    }

    @Override
    public void endDocument(String id) throws IOException {
        // nothing to do
    }
}
