package org.xbib.content.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A reference to bytes.
 */
public interface BytesReference {

    /**
     * Returns the byte at the specified index. Need to be between 0 and length.
     *
     * @param index index
     * @return byte at specified index
     */
    byte get(int index);

    /**
     * The length.
     *
     * @return length
     */
    int length();

    /**
     * Find the index of a given byte, in the given area.
     * @param b the byte
     * @param offset offset
     * @param len len
     * @return -1 if not found, otherwise the position, counting from offset
     */
    int indexOf(byte b, int offset, int len);

    /**
     * Slice the bytes from the <tt>from</tt> index up to <tt>length</tt>.
     *
     * @param from   from
     * @param length length
     * @return bytes reference
     */
    BytesReference slice(int from, int length);

    /**
     * Returns the bytes as a single byte array.
     *
     * @return bytes
     */
    byte[] toBytes();

    /**
     * Converts to a string based on utf8.
     *
     * @return UTF-8 encoded string
     */
    String toUtf8();

    BytesStreamInput streamInput();

    void streamOutput(OutputStream outputStream) throws IOException;
}
