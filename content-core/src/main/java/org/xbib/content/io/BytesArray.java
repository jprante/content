package org.xbib.content.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A byte array, wrapped in a {@link BytesReference}.
 */
public class BytesArray implements BytesReference {

    private static final String EMPTY_STRING = "";

    private byte[] bytes;

    private int offset;

    private int length;

    /**
     * Create {@link BytesArray} from a byte array.
     * @param bytes the byte array
     */
    public BytesArray(byte[] bytes) {
        this.bytes = bytes;
        this.offset = 0;
        this.length = bytes.length;
    }

    /**
     * Create {@link BytesArray} from a part of a byte array.
     * @param bytes the byte array
     * @param offset the offset
     * @param length the length
     */
    public BytesArray(byte[] bytes, int offset, int length) {
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }

    public void write(byte[] b) {
        byte[] c = new byte[length + b.length];
        System.arraycopy(bytes, 0, c, 0, length);
        System.arraycopy(b, 0, c, bytes.length, b.length);
        this.bytes = c;
        this.offset = 0;
        this.length = c.length;
    }

    @Override
    public byte get(int index) {
        return bytes[offset + index];
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public int indexOf(byte b, int offset, int len) {
        if (offset < 0 || (offset + length) > this.length) {
            throw new IllegalArgumentException();
        }
        for (int i = offset; i < offset + len; i++) {
            if (bytes[i] == b) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public BytesReference slice(int from, int length) {
        if (from < 0 || (from + length) > this.length) {
            throw new IllegalArgumentException("can't slice a buffer with length [" + this.length +
                    "], with slice parameters from [" + from + "], length [" + length + "]");
        }
        return new BytesArray(bytes, offset + from, length);
    }

    @Override
    public byte[] toBytes() {
        if (offset == 0 && bytes.length == length) {
            return bytes;
        }
        return Arrays.copyOfRange(bytes, offset, offset + length);
    }

    @Override
    public String toUtf8() {
        if (length == 0) {
            return EMPTY_STRING;
        }
        return new String(bytes, offset, length, StandardCharsets.UTF_8);
    }

    @Override
    public BytesStreamInput streamInput() {
        return new BytesStreamInput(bytes, offset, length);
    }

    @Override
    public void streamOutput(OutputStream os) throws IOException {
        os.write(bytes, offset, length);
    }
}
