package org.xbib.content.resource.text;

/**
 * Base implementation of a CodepointIterator that filters the output of another CodpointIterator.
 */
public abstract class DelegatingCodepointIterator extends CodepointIterator {

    private CodepointIterator internal;

    protected DelegatingCodepointIterator(CodepointIterator internal) {
        this.internal = internal;
    }

    @Override
    protected char get() {
        return internal.get();
    }

    @Override
    protected char get(int index) {
        return internal.get(index);
    }

    @Override
    public boolean hasNext() {
        return internal.hasNext();
    }

    @Override
    public boolean isHigh(int index) {
        return internal.isHigh(index);
    }

    @Override
    public boolean isLow(int index) {
        return internal.isLow(index);
    }

    @Override
    public int limit() {
        return internal.limit();
    }

    @Override
    public Codepoint next() {
        return internal.next();
    }

    @Override
    public char[] nextChars() {
        return internal.nextChars();
    }

    @Override
    public Codepoint peek() {
        return internal.peek();
    }

    @Override
    public Codepoint peek(int index) {
        return internal.peek(index);
    }

    @Override
    public char[] peekChars() {
        return internal.peekChars();
    }

    @Override
    public int position() {
        return internal.position();
    }

    @Override
    public int remaining() {
        return internal.remaining();
    }

    @Override
    public void position(int position) {
        internal.position(position);
    }

}
