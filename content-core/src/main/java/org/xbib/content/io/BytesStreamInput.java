package org.xbib.content.io;

import java.io.IOException;
import java.io.InputStream;

public class BytesStreamInput extends InputStream {

    private final byte[] buf;

    private final int count;

    private long pos;

    public BytesStreamInput(byte[] buf) {
        this(buf, 0, buf.length);
    }

    public BytesStreamInput(byte[] buf, int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min(offset + length, buf.length);
    }

    @Override
    public long skip(long n) throws IOException {
        long res = n;
        if (pos + res > count) {
            res = (long) count - pos;
        }
        if (res < 0) {
            return 0;
        }
        pos += res;
        return res;
    }

    @Override
    public int read() throws IOException {
        int i = (int) pos++;
        return pos < count ? buf[i] & 0xff : -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (pos >= count) {
            return -1;
        }
        long l = len;
        if (pos + l > count) {
            l = count - pos;
        }
        if (l <= 0) {
            return 0;
        }
        System.arraycopy(buf, (int) pos, b, off, (int) l);
        pos += l;
        return (int) l;
    }

    @Override
    public void reset() throws IOException {
        pos = 0L;
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }
}
