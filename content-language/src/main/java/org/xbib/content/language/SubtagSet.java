package org.xbib.content.language;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

abstract class SubtagSet implements Cloneable, Iterable<Subtag>, Comparable<SubtagSet> {

    protected final Subtag primary;

    protected SubtagSet(Subtag primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Subtag subtag : this) {
            if (buf.length() > 0) {
                buf.append('-');
            }
            buf.append(subtag.getName());
        }
        return buf.toString();
    }

    @Override
    public Iterator<Subtag> iterator() {
        return new SubtagIterator(primary);
    }

    public boolean contains(Subtag subtag) {
        for (Subtag tag : this) {
            if (tag.equals(subtag)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String tag) {
        return contains(tag, Subtag.Type.SIMPLE);
    }

    public boolean contains(String tag, Subtag.Type type) {
        return contains(new Subtag(type, tag));
    }

    public int length() {
        return toString().length();
    }

    public boolean isValid() {
        for (Subtag subtag : this) {
            if (!subtag.isValid()) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unused")
    public int count() {
        int n = 0;
        for (Subtag tag : this) {
            n++;
        }
        return n;
    }

    public Subtag get(int index) {
        if (index < 0 || index > count()) {
            throw new IndexOutOfBoundsException();
        }
        Subtag tag = primary;
        for (int n = 1; n <= index; n++) {
            tag = tag.getNext();
        }
        return tag;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (Subtag tag : this) {
            result = prime * result + tag.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }

    public Subtag[] toArray() {
        List<Subtag> tags = new LinkedList<>();
        for (Subtag tag : this) {
            tags.add(tag);
        }
        return tags.toArray(new Subtag[tags.size()]);
    }

    public List<Subtag> asList() {
        return Arrays.asList(toArray());
    }

    @Override
    public int compareTo(SubtagSet o) {
        Iterator<Subtag> i = iterator();
        Iterator<Subtag> e = o.iterator();
        while (i.hasNext() && e.hasNext()) {
            Subtag inext = i.next();
            Subtag enext = e.next();
            int c = inext.compareTo(enext);
            if (c != 0) {
                return c;
            }
        }
        if (e.hasNext() && !i.hasNext()) {
            return -1;
        }
        if (i.hasNext() && !e.hasNext()) {
            return 1;
        }
        return 0;
    }

    /**
     *
     */
    private static class SubtagIterator implements Iterator<Subtag> {
        private Subtag current;

        SubtagIterator(Subtag current) {
            this.current = current;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Subtag next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            Subtag tag = current;
            current = tag.getNext();
            return tag;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
