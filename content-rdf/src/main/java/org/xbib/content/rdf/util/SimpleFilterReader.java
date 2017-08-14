package org.xbib.content.rdf.util;

import java.io.IOException;
import java.io.Reader;

/**
 * This filter reader redirects all read I/O methods through its own read() method.
 */
public class SimpleFilterReader extends Reader {

    private static final int PREEMPT_BUFFER_LENGTH = 16;

    private Reader in;

    private int[] preempt = new int[PREEMPT_BUFFER_LENGTH];

    private int preemptIndex = 0;

    public SimpleFilterReader(Reader in) {
        this.in = in;
    }

    public void push(char c) {
        push((int) c);
    }

    public void push(int c) {
        try {
            preempt[preemptIndex++] = c;
        } catch (ArrayIndexOutOfBoundsException e) {
            int[] p2 = new int[preempt.length * 2];
            System.arraycopy(preempt, 0, p2, 0, preempt.length);
            preempt = p2;
            push(c);
        }
    }

    public void push(char[] cs, int start, int length) {
        for (int i = start + length - 1; i >= start;) {
            push(cs[i--]);
        }
    }

    public void push(char[] cs) {
        push(cs, 0, cs.length);
    }

    @Override
    public int read() throws IOException {
        return preemptIndex > 0 ? preempt[--preemptIndex] : in.read();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    @Override
    public boolean ready() throws IOException {
        return in.ready();
    }

    @Override
    public void mark(int i) throws IOException {
        in.mark(i);
    }

    @Override
    public long skip(long i) throws IOException {
        return in.skip(i);
    }

    @Override
    public int read(char[] buf) throws IOException {
        return read(buf, 0, buf.length);
    }

    @Override
    public int read(char[] buf, int start, int length) throws IOException {
        int count = 0;
        int c = 0;
        while (length-- > 0 && (c = this.read()) != -1) {
            buf[start++] = (char) c;
            count++;
        }
        return (count == 0 && c == -1) ? -1 : count;
    }
}
