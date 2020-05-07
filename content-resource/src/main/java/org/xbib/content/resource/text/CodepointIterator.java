package org.xbib.content.resource.text;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides an iterator over Unicode Codepoints.
 */
public abstract class CodepointIterator implements Iterator<Codepoint> {

    protected int position = -1;
    protected int limit = -1;

    /**
     * Get a CodepointIterator for the specified char array.
     * @param array char array
     * @return code point iterator
     */
    public static CodepointIterator forCharArray(char[] array) {
        return new CharArrayCodepointIterator(array);
    }

    /**
     * Get a CodepointIterator for the specified CharSequence.
     * @param seq char sequence
     * @return code point iterator
     */
    public static CodepointIterator forCharSequence(CharSequence seq) {
        return new CharSequenceCodepointIterator(seq);
    }


    public static CodepointIterator restrict(CodepointIterator ci, Filter filter) {
        return new RestrictedCodepointIterator(ci, filter, false);
    }

    public static CodepointIterator restrict(CodepointIterator ci, Filter filter, boolean scanning) {
        return new RestrictedCodepointIterator(ci, filter, scanning);
    }

    public static CodepointIterator restrict(CodepointIterator ci, Filter filter, boolean scanning, boolean invert) {
        return new RestrictedCodepointIterator(ci, filter, scanning, invert);
    }

    public CodepointIterator restrict(Filter filter) {
        return restrict(this, filter);
    }

    public CodepointIterator restrict(Filter filter, boolean scanning) {
        return restrict(this, filter, scanning);
    }

    public CodepointIterator restrict(Filter filter, boolean scanning, boolean invert) {
        return restrict(this, filter, scanning, invert);
    }

    /**
     * Get the next char.
     * @return char
     */
    protected abstract char get();

    /**
     * Get the specified char.
     * @param index index
     * @return char
     */
    protected abstract char get(int index);

    /**
     * Checks if there are codepoints remaining.
     * @return true if there are codepoints remaining
     */
    @Override
    public boolean hasNext() {
        return remaining() > 0;
    }

    /**
     * Return the final index position.
     * @return final index position
     */
    public int lastPosition() {
        int p = position();
        return (p > -1) ? (p >= limit()) ? p : p - 1 : -1;
    }

    /**
     * Return the next chars. If the codepoint is not supplemental, the char array will have a single member. If the
     * codepoint is supplemental, the char array will have two members, representing the high and low surrogate chars.
     * @return next chars
     */
    public char[] nextChars(){
        if (hasNext()) {
            if (isNextSurrogate()) {
                char c1 = get();
                if (CharUtils.isHighSurrogate(c1) && position() < limit()) {
                    char c2 = get();
                    if (CharUtils.isLowSurrogate(c2)) {
                        return new char[]{c1, c2};
                    } else {
                        throw new InvalidCharacterException(c2);
                    }
                } else if (CharUtils.isLowSurrogate(c1) && position() > 0) {
                    char c2 = get(position() - 2);
                    if (CharUtils.isHighSurrogate(c2)) {
                        return new char[]{c1, c2};
                    } else {
                        throw new InvalidCharacterException(c2);
                    }
                }
            }
            return new char[]{get()};
        }
        return null;
    }

    /**
     * Peek the next chars in the iterator. If the codepoint is not supplemental, the char array will have a single
     * member. If the codepoint is supplemental, the char array will have two members, representing the high and low
     * surrogate chars.
     * @return chars
     */
    public char[] peekChars() {
        return peekChars(position());
    }

    /**
     * Peek the specified chars in the iterator. If the codepoint is not supplemental, the char array will have a single
     * member. If the codepoint is supplemental, the char array will have two members, representing the high and low
     * surrogate chars.
     * @return chars
     */
    private char[] peekChars(int pos) {
        if (pos < 0 || pos >= limit()) {
            return null;
        }
        char c1 = get(pos);
        if (CharUtils.isHighSurrogate(c1) && pos < limit()) {
            char c2 = get(pos + 1);
            if (CharUtils.isLowSurrogate(c2)) {
                return new char[]{c1, c2};
            } else {
                throw new InvalidCharacterException(c2);
            }
        } else if (CharUtils.isLowSurrogate(c1) && pos > 1) {
            char c2 = get(pos - 1);
            if (CharUtils.isHighSurrogate(c2)) {
                return new char[]{c2, c1};
            } else {
                throw new InvalidCharacterException(c2);
            }
        } else {
            return new char[]{c1};
        }
    }

    /**
     * Return the next codepoint.
     * @return code point
     */
    @Override
    public Codepoint next() {
        if (remaining() > 0) {
            return toCodepoint(nextChars());
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Peek the next codepoint.
     * @return code point
     */
    public Codepoint peek() {
        return toCodepoint(peekChars());
    }

    /**
     * Peek the specified codepoint.
     * @param index index
     * @return code point
     */
    public Codepoint peek(int index) {
        return toCodepoint(peekChars(index));
    }

    private Codepoint toCodepoint(char[] chars) {
        return (chars == null) ? null : (chars.length == 1) ? new Codepoint(chars[0]) : CharUtils
                .toSupplementary(chars[0], chars[1]);
    }

    /**
     * Set the iterator position.
     * @param n iterator position
     */
    public void position(int n) {
        if (n < 0 || n > limit()) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        position = n;
    }

    /**
     * Get the iterator position.
     * @return position
     */
    public int position() {
        return position;
    }

    /**
     * Return the iterator limit.
     * @return limit
     */
    public int limit() {
        return limit;
    }

    /**
     * Return the remaining iterator size.
     * @return remaining size
     */
    public int remaining() {
        return limit - position();
    }

    private boolean isNextSurrogate() {
        if (!hasNext()) {
            return false;
        }
        char c = get(position());
        return CharUtils.isHighSurrogate(c) || CharUtils.isLowSurrogate(c);
    }

    /**
     * Returns true if the char at the specified index is a high surrogate.
     * @param index index
     * @return  true if the char at the specified index is a high surrogate
     */
    public boolean isHigh(int index) {
        if (index < 0 || index > limit()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return CharUtils.isHighSurrogate(get(index));
    }

    /**
     * Returns true if the char at the specified index is a low surrogate.
     * @param index index
     * @return true if the char at the specified index is a low surrogate
     */
    public boolean isLow(int index) {
        if (index < 0 || index > limit()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return CharUtils.isLowSurrogate(get(index));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private static class CharArrayCodepointIterator extends CodepointIterator {
        protected char[] buffer;

        CharArrayCodepointIterator(char[] buffer) {
            this(buffer, 0, buffer.length);
        }

        CharArrayCodepointIterator(char[] buffer, int n, int e) {
            this.buffer = buffer;
            this.position = n;
            this.limit = Math.min(buffer.length - n, e);
        }

        @Override
        protected char get() {
            return (position < limit) ? buffer[position++] : (char) -1;
        }

        @Override
        protected char get(int index) {
            if (index < 0 || index >= limit) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return buffer[index];
        }
    }

    private static class CharSequenceCodepointIterator extends CodepointIterator {
        private CharSequence buffer;

        CharSequenceCodepointIterator(CharSequence buffer) {
            this(buffer, 0, buffer.length());
        }

        CharSequenceCodepointIterator(CharSequence buffer, int n, int e) {
            this.buffer = buffer;
            this.position = n;
            this.limit = Math.min(buffer.length() - n, e);
        }

        @Override
        protected char get() {
            return buffer.charAt(position++);
        }

        @Override
        protected char get(int index) {
            return buffer.charAt(index);
        }
    }

    private static class RestrictedCodepointIterator extends DelegatingCodepointIterator {

        private final Filter filter;
        private final boolean scanningOnly;
        private final boolean notset;

        RestrictedCodepointIterator(CodepointIterator internal, Filter filter, boolean scanningOnly) {
            this(internal, filter, scanningOnly, false);
        }

        RestrictedCodepointIterator(CodepointIterator internal,
                                    Filter filter,
                                    boolean scanningOnly,
                                    boolean notset) {
            super(internal);
            this.filter = filter;
            this.scanningOnly = scanningOnly;
            this.notset = notset;
        }

        @Override
        public boolean hasNext() {
            boolean b = super.hasNext();
            if (scanningOnly) {
                try {
                    int cp = super.peek(super.position()).getValue();
                    if (b && cp != -1 && check(cp)) {
                        return false;
                    }
                } catch (InvalidCharacterException e) {
                    return false;
                }
            }
            return b;
        }

        @Override
        public Codepoint next() {
            Codepoint cp = super.next();
            int v = cp.getValue();
            if (v != -1 && check(v)) {
                if (scanningOnly) {
                    super.position(super.position() - 1);
                    return null;
                } else {
                    throw new InvalidCharacterException(v);
                }
            }
            return cp;
        }

        private boolean check(int cp) {
            return notset == !filter.accept(cp);
        }

        @Override
        public char[] nextChars() {
            char[] chars = super.nextChars();
            if (chars != null && chars.length > 0) {
                if (chars.length == 1 && check(chars[0])) {
                    if (scanningOnly) {
                        super.position(super.position() - 1);
                        return null;
                    } else {
                        throw new InvalidCharacterException(chars[0]);
                    }
                } else if (chars.length == 2) {
                    int cp = CharUtils.toSupplementary(chars[0], chars[1]).getValue();
                    if (check(cp)) {
                        if (scanningOnly) {
                            super.position(super.position() - 2);
                            return null;
                        } else {
                            throw new InvalidCharacterException(cp);
                        }
                    }
                }
            }
            return chars;
        }
    }
}
