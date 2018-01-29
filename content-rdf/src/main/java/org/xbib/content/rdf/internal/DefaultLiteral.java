package org.xbib.content.rdf.internal;

import org.xbib.content.rdf.Literal;
import org.xbib.content.resource.IRI;

/**
 * A simple Literal is a value of object type.
 */
public class DefaultLiteral implements Literal, Comparable<Literal> {

    private Object value;

    private IRI type;

    private String lang;

    public DefaultLiteral(Object value) {
        this.value = value;
    }

    private static boolean equalObject(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    @Override
    public DefaultLiteral object(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public DefaultLiteral type(IRI type) {
        this.type = type;
        return this;
    }

    @Override
    public IRI type() {
        return type;
    }

    @Override
    public DefaultLiteral lang(String lang) {
        this.lang = lang;
        return this;
    }

    @Override
    public String lang() {
        return lang;
    }

    @Override
    public int compareTo(Literal that) {
        if (this == that) {
            return 0;
        }
        return toString().compareTo(that.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Literal)) {
            return false;
        }
        final Literal that = (Literal) obj;
        return equalObject(this.value, that.object())
                && equalObject(this.lang, that.lang())
                && equalObject(this.type, that.type());
    }

    @Override
    public int hashCode() {
        return (value + lang + type).hashCode();
    }

    @Override
    public String toString() {
        return lexicalValue();
    }

    public String lexicalValue() {
        return (value != null ? value : "")
                + (lang != null ? "@" + lang : "")
                + (type != null ? "^^" + type : "");
    }

    @Override
    public Object object() {
        if (type == null || value == null) {
            return value;
        }
        String s = value.toString();
        try {
            switch (type.toString()) {
                case "xsd:long":
                    return Long.parseLong(s);
                case "xsd:int":
                    return Integer.parseInt(s);
                case "xsd:gYear":
                    return Integer.parseInt(s);
                case "xsd:boolean":
                    return Boolean.parseBoolean(s);
                case "xsd:float":
                    return Float.parseFloat(s);
                case "xsd:double":
                    return Double.parseDouble(s);
                default:
                    return s;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean isEmbedded() {
        return false;
    }
}
