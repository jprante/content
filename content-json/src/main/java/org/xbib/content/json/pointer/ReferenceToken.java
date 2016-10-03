package org.xbib.content.json.pointer;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;


/**
 * One JSON Pointer reference token.
 * This class represents one reference token. It has no publicly available
 * constructor; instead, it has static factory methods used to generate tokens
 * depending on whether the input is a decoded (raw) token or an encoded
 * (cooked) one, or even an integer.
 * The only characters to encode in a raw token are {@code /} (which becomes
 * {@code ~1}) and {@code ~} (which becomes {@code ~0}).
 * Note that a reference token may be empty and empty object member names
 * are legal.
 */
public final class ReferenceToken {
    /**
     * The escape character in a cooked token.
     */
    private static final char ESCAPE = '~';

    /**
     * List of encoding characters in a cooked token.
     */
    private static final List<Character> ENCODED = Arrays.asList('0', '1');

    /**
     * List of sequences to encode in a raw token.
     * This list and {@link #ENCODED} have matching indices on purpose.
     */
    private static final List<Character> DECODED = Arrays.asList('~', '/');

    /**
     * The cooked representation of that token.
     *
     * @see #toString()
     */
    private final String cooked;

    /**
     * The raw representation of that token.
     *
     * @see #hashCode()
     * @see #equals(Object)
     */
    private final String raw;

    /**
     * The only constructor, private by design.
     *
     * @param cooked the cooked representation of that token
     * @param raw    the raw representation of that token
     */
    private ReferenceToken(final String cooked, final String raw) {
        this.cooked = cooked;
        this.raw = raw;
    }

    /**
     * Generate a reference token from an encoded (cooked) representation.
     *
     * @param cooked the input
     * @return a token
     * @throws JsonPointerException illegal token (bad encode sequence)
     * @throws NullPointerException null input
     */
    public static ReferenceToken fromCooked(final String cooked)
            throws JsonPointerException {
        return new ReferenceToken(cooked, asRaw(cooked));
    }

    /**
     * Generate a reference token from a decoded (raw) representation.
     *
     * @param raw the input
     * @return a token
     * @throws NullPointerException null input
     */
    public static ReferenceToken fromRaw(final String raw) {
        return new ReferenceToken(asCooked(raw), raw);
    }

    /**
     * Generate a reference token from an integer.
     *
     * @param index the integer
     * @return a token
     */
    public static ReferenceToken fromInt(final int index) {
        final String s = Integer.toString(index);
        return new ReferenceToken(s, s);
    }

    /**
     * Decode an encoded token.
     *
     * @param cooked the encoded token
     * @return the decoded token
     * @throws JsonPointerException bad encoded representation
     */
    private static String asRaw(final String cooked)
            throws JsonPointerException {
        final StringBuilder raw = new StringBuilder(cooked.length());

        final CharBuffer buffer = CharBuffer.wrap(cooked);
        boolean inEscape = false;
        char c;

        while (buffer.hasRemaining()) {
            c = buffer.get();
            if (inEscape) {
                appendEscaped(raw, c);
                inEscape = false;
                continue;
            }
            if (c == ESCAPE) {
                inEscape = true;
                continue;
            }
            raw.append(c);
        }

        if (inEscape) {
            throw new JsonPointerException("empty escape");
        }

        return raw.toString();
    }

    /**
     * Append a decoded sequence to a {@link StringBuilder}.
     *
     * @param sb the string builder to append to
     * @param c  the escaped character
     * @throws JsonPointerException illegal escaped character
     */
    private static void appendEscaped(final StringBuilder sb, final char c)
            throws JsonPointerException {
        final int index = ENCODED.indexOf(c);
        if (index == -1) {
            throw new JsonPointerException("illegal escape");
        }

        sb.append(DECODED.get(index));
    }

    /**
     * Encode a raw token.
     *
     * @param raw the raw representation
     * @return the cooked, encoded representation
     */
    private static String asCooked(final String raw) {
        final StringBuilder cooked = new StringBuilder(raw.length());

        final CharBuffer buffer = CharBuffer.wrap(raw);
        char c;
        int index;

        while (buffer.hasRemaining()) {
            c = buffer.get();
            index = DECODED.indexOf(c);
            if (index != -1) {
                cooked.append('~').append(ENCODED.get(index));
            } else {
                cooked.append(c);
            }
        }

        return cooked.toString();
    }

    /**
     * Get the raw representation of that token as a string.
     *
     * @return the raw representation (for traversing purposes)
     */
    public String getRaw() {
        return raw;
    }

    @Override
    public int hashCode() {
        return raw.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReferenceToken other = (ReferenceToken) obj;
        return raw.equals(other.raw);
    }

    @Override
    public String toString() {
        return cooked;
    }
}
