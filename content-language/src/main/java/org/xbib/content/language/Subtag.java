package org.xbib.content.language;

import org.xbib.content.language.enums.Extlang;
import org.xbib.content.language.enums.Language;
import org.xbib.content.language.enums.Region;
import org.xbib.content.language.enums.Script;
import org.xbib.content.language.enums.Singleton;
import org.xbib.content.language.enums.Variant;

import java.util.Locale;

/**
 * A Lang tag subtag.
 */
public final class Subtag implements Comparable<Subtag> {

    private final Type type;
    private final String name;
    private Subtag prev;
    private Subtag next;

    /**
     * Create a Subtag.
     * @param language language
     */
    public Subtag(Language language) {
        this(Type.PRIMARY, language.name().toLowerCase(Locale.US));
    }

    /**
     * Create a Subtag.
     * @param script script
     */
    public Subtag(Script script) {
        this(Type.SCRIPT, toTitleCase(script.name()));
    }

    /**
     * Create a Subtag.
     * @param region region
     */
    public Subtag(Region region) {
        this(Type.REGION, getRegionName(region.name()));
    }

    /**
     * Create a Subtag.
     * @param variant variant
     */
    public Subtag(Variant variant) {
        this(Type.VARIANT, getVariantName(variant.name().toLowerCase(Locale.US)));
    }

    /**
     * Create a Subtag.
     * @param extlang ext lang
     */
    public Subtag(Extlang extlang) {
        this(Type.EXTLANG, extlang.name().toLowerCase(Locale.US));
    }

    /**
     * Create a Subtag.
     * @param singleton singleton
     */
    public Subtag(Singleton singleton) {
        this(Type.SINGLETON, singleton.name().toLowerCase(Locale.US));
    }

    /**
     * Create a Subtag.
     * @param type type
     * @param name name
     */
    public Subtag(Type type, String name) {
        this(type, name, null);
    }

    private Subtag() {
        this(Type.WILDCARD, "*");
    }

    /**
     * Create a Subtag.
     * @param type type
     * @param name name
     * @param prev prev
     */
    public Subtag(Type type, String name, Subtag prev) {
        this.type = type;
        this.name = name;
        this.prev = prev;
        if (prev != null) {
            prev.setNext(this);
        }
    }

    Subtag(Type type, String name, Subtag prev, Subtag next) {
        this.type = type;
        this.name = name;
        this.prev = prev;
        this.next = next;
    }

    private static String getRegionName(String name) {
        return name.startsWith("UN") && name.length() == 5 ? name.substring(2) : name;
    }

    private static String getVariantName(String name) {
        return name.startsWith("_") ? name.substring(1) : name;
    }

    private static String toTitleCase(String string) {
        if (string == null) {
            return null;
        }
        if (string.length() == 0) {
            return string;
        }
        char[] chars = string.toLowerCase(Locale.US).toCharArray();
        chars[0] = (char) (chars[0] - 32);
        return new String(chars);
    }

    /**
     * Create a new wildcard subtag.
     * @return sub tag
     */
    public static Subtag newWildcard() {
        return new Subtag();
    }

    /**
     * Get the subtag type.
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the subtag value.
     * @return sub tag
     */
    public String getName() {
        return toString();
    }

    /**
     * Get the previous subtag.
     * @return sub tag
     */
    public Subtag getPrevious() {
        return prev;
    }

    void setPrevious(Subtag prev) {
        this.prev = prev;
    }

    /**
     * Get the next subtag.
     * @return sub tag
     */
    public Subtag getNext() {
        return next;
    }

    void setNext(Subtag next) {
        this.next = next;
        if (next != null) {
            next.setPrevious(this);
        }
    }

    @Override
    public String toString() {
        switch (type) {
            case REGION:
                return name.toUpperCase(Locale.US);
            case SCRIPT:
                return toTitleCase(name);
            default:
                return name.toLowerCase(Locale.US);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.toLowerCase(Locale.US).hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Subtag other = (Subtag) obj;
        if (other.getType() == Type.WILDCARD || getType() == Type.WILDCARD) {
            return true;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equalsIgnoreCase(other.name)) {
            return false;
        }
        if (other.getType() == Type.SIMPLE || getType() == Type.SIMPLE) {
            return true;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    /**
     * True if this subtag has been deprecated.
     * @return true if this subtag has been deprecated.
     */
    public boolean isDeprecated() {
        switch (type) {
            case PRIMARY: {
                Language e = getEnum();
                return e != null && e.isDeprecated();
            }
            case SCRIPT: {
                Script e = getEnum();
                return e != null && e.isDeprecated();
            }
            case REGION: {
                Region e = getEnum();
                return e != null && e.isDeprecated();
            }
            case VARIANT: {
                Variant e = getEnum();
                return e != null && e.isDeprecated();
            }
            case EXTLANG: {
                Extlang e = getEnum();
                return e != null && e.isDeprecated();
            }
            case EXTENSION: {
                Singleton e = getEnum();
                return e != null && e.isDeprecated();
            }
            default:
                return false;
        }
    }

    /**
     * Get this subtags Enum, allowing the subtag to be verified.
     * @param <T> type parameter
     * @return subtags enum
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> T getEnum() {
        switch (type) {
            case PRIMARY:
                return (T) Language.valueOf(this);
            case SCRIPT:
                return (T) Script.valueOf(this);
            case REGION:
                return (T) Region.valueOf(this);
            case VARIANT:
                return (T) Variant.valueOf(this);
            case EXTLANG:
                return (T) Extlang.valueOf(this);
            case EXTENSION:
                return (T) Singleton.valueOf(this);
            default:
                return null;
        }
    }

    /**
     * True if this subtag is valid.
     * @return true if valid
     */
    public boolean isValid() {
        switch (type) {
            case PRIMARY:
            case SCRIPT:
            case REGION:
            case VARIANT:
            case EXTLANG:
                getEnum();
                return true;
            case EXTENSION:
                return name.matches("[A-Za-z0-9]{2,8}");
            case GRANDFATHERED:
                return name.matches("[A-Za-z]{1,3}(?:-[A-Za-z0-9]{2,8}){1,2}");
            case PRIVATEUSE:
            case SIMPLE:
                return name.matches("[A-Za-z0-9]{1,8}");
            case SINGLETON:
                return name.matches("[A-Za-z]");
            case WILDCARD:
                return "*".equals(name);
            default:
                return false;
        }
    }

    /**
     * Return the canonicalized version of this subtag.
     * @return sub tag
     */
    public Subtag canonicalize() {
        switch (type) {
            case REGION:
                Region region = getEnum();
                return region != null ? region.getPreferred().newSubtag() : null;
            case PRIMARY:
                Language language = getEnum();
                return language != null ? language.getPreferred().newSubtag() : null;
            case SCRIPT:
                Script script = getEnum();
                return script != null ? script.getPreferred().newSubtag() : null;
            case VARIANT:
                Variant variant = getEnum();
                return variant != null ? variant.getPreferred().newSubtag() : null;
            case EXTLANG:
                Extlang extlang = getEnum();
                return extlang != null ? extlang.getPreferred().newSubtag() : null;
            case EXTENSION:
            case GRANDFATHERED:
            case PRIVATEUSE:
            case SINGLETON:
            default:
                return this;
        }
    }

    @Override
    public int compareTo(Subtag o) {
        int c = o.type.compareTo(type);
        return c != 0 ? c : o.name.compareTo(name);
    }

    /**
     *
     */
    public enum Type {
        /**
         * Primary language subtag.
         */
        PRIMARY,
        /**
         * Extended Language subtag.
         */
        EXTLANG,
        /**
         * Script subtag.
         */
        SCRIPT,
        /**
         * Region subtag.
         */
        REGION,
        /**
         * Variant subtag.
         */
        VARIANT,
        /**
         * Singleton subtag.
         */
        SINGLETON,
        /**
         * Extension subtag.
         */
        EXTENSION,
        /**
         * Primary-use subtag.
         */
        PRIVATEUSE,
        /**
         * Grandfathered subtag.
         */
        GRANDFATHERED,
        /**
         * Wildcard subtag ("*").
         */
        WILDCARD,
        /**
         * Simple subtag (ranges).
         */
        SIMPLE
    }
}
