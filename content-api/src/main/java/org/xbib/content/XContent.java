package org.xbib.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * A generic abstraction on top of handling content, inspired by JSON and pull parsing.
 */
public interface XContent {

    String name();

    boolean isXContent(byte[] data, int offset, int length);

    /**
     * Creates a new generator using the provided output stream.
     *
     * @param outputStream output stream
     * @return content generator
     * @throws IOException if creation fails
     */
    XContentGenerator createGenerator(OutputStream outputStream) throws IOException;

    /**
     * Creates a new generator using the provided writer.
     *
     * @param writer  writer
     * @return content generator
     * @throws IOException if creation fails
     */
    XContentGenerator createGenerator(Writer writer) throws IOException;

    /**
     * Creates a parser over the provided input stream.
     *
     * @param inputStream input stream
     * @return content parser
     * @throws IOException if creation fails
     */
    XContentParser createParser(InputStream inputStream) throws IOException;

    /**
     * Creates a parser over the provided reader.
     *
     * @param reader reader
     * @return content parser
     * @throws IOException if creation fails
     */
    XContentParser createParser(Reader reader) throws IOException;

    /**
     * Creates a parser over the provided string content.
     *
     * @param content string
     * @return content parser
     * @throws IOException if creation fails
     */
    XContentParser createParser(String content) throws IOException;

    /**
     * Creates a parser over the provided bytes.
     *
     * @param bytes bytes
     * @return content parser
     * @throws IOException if creation fails
     */
    XContentParser createParser(byte[] bytes) throws IOException;

    /**
     * Creates a parser over the provided bytes.
     *
     * @param bytes bytes
     * @param offset offset
     * @param length length
     * @return content parser
     * @throws IOException if creation fails
     */
    XContentParser createParser(byte[] bytes, int offset, int length) throws IOException;

}
