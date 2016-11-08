package org.xbib.content.resource.url;

import org.xbib.content.resource.text.CharUtils;
import org.xbib.content.resource.text.Filter;

import java.io.EOFException;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Performs URL Percent Encoding.
 */
public final class UrlEncoding {

    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private UrlEncoding() {
    }

    private static void encode(Appendable sb, byte... bytes) throws IOException {
        encode(sb, 0, bytes.length, bytes);
    }

    private static void encode(Appendable sb, int offset, int length, byte... bytes) throws IOException {
        for (int n = offset, i = 0; n < bytes.length && i < length; n++, i++) {
            byte c = bytes[n];
            sb.append("%");
            sb.append(HEX[(c >> 4) & 0x0f]);
            sb.append(HEX[c & 0x0f]);
        }
    }

    public static String encode(CharSequence s, Filter filter) throws IOException {
        return encode(s, new Filter[]{filter});
    }

    public static String encode(CharSequence s, Filter... filters) throws IOException {
        if (s == null) {
            return null;
        }
        return encode(s, "utf-8", filters);
    }

    private static boolean check(int codepoint, Filter... filters) {
        for (Filter filter : filters) {
            if (filter.accept(codepoint)) {
                return true;
            }
        }
        return false;
    }

    public static String encode(CharSequence s, String enc, Filter... filters) throws IOException {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < s.length(); n++) {
            char c = s.charAt(n);
            if (!CharUtils.isHighSurrogate(c) && check(c, filters)) {
                encode(sb, String.valueOf(c).getBytes(enc));
            } else if (CharUtils.isHighSurrogate(c)) {
                if (check(c, filters)) {
                    String buf = String.valueOf(c) + s.charAt(++n);
                    byte[] b = buf.getBytes(enc);
                    encode(sb, b);
                } else {
                    sb.append(c);
                    sb.append(s.charAt(++n));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String decode(String string) throws IOException {
        String e = string;
        char[] buf = new char[e.length()];
        try (DecodingReader r = new DecodingReader(new StringReader(e))) {
            int l = r.read(buf);
            e = new String(buf, 0, l);
        }
        return e;
    }

    /**
     *
     */
    private static class DecodingReader extends FilterReader {

        DecodingReader(Reader in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            int c = super.read();
            if (c == '%') {
                int c1 = super.read();
                int c2 = super.read();
                return decode((char) c1, (char) c2);
            } else {
                return c;
            }
        }

        @Override
        public int read(char[] b, int off, int len) throws IOException {
            int n = off;
            int i;
            while ((i = read()) != -1 && n < off + len) {
                b[n++] = (char) i;
            }
            return n - off;
        }

        @Override
        public int read(char[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public long skip(long n) throws IOException {
            long i = 0;
            int c;
            for (; i < n; i++) {
                c = read();
                if (c == -1) {
                    throw new EOFException();
                }
            }
            return i;
        }

        private static byte decode(char c, int shift) {
            return (byte) ((((c >= '0' && c <= '9') ? c - '0' : (c >= 'A' && c <= 'F') ? c - 'A' + 10
                    : (c >= 'a' && c <= 'f') ? c - 'a' + 10 : -1) & 0xf) << shift);
        }

        private static byte decode(char c1, char c2) {
            return (byte) (decode(c1, 4) | decode(c2, 0));
        }
    }
}
