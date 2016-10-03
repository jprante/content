package org.xbib.content.language.enums;

import org.xbib.content.language.Subtag;

import java.util.Locale;

/**
 * Enum constants used to validate language tags.
 */
public enum Singleton {
    A("Undefined", -1, null, null), B("Undefined", -1, null, null), C("Undefined", -1, null, null), D("Undefined", -1,
    null, null), E("Undefined", -1, null, null), F("Undefined", -1, null, null), G("Undefined", -1, null, null), H(
    "Undefined", -1, null, null), I("Undefined", -1, null, null), J("Undefined", -1, null, null), K("Undefined",
    -1, null, null), L("Undefined", -1, null, null), M("Undefined", -1, null, null), N("Undefined", -1, null, null), O(
    "Undefined", -1, null, null), P("Undefined", -1, null, null), Q("Undefined", -1, null, null), R("Undefined",
    -1, null, null), S("Undefined", -1, null, null), T("Undefined", -1, null, null), U("Undefined", -1, null, null), V(
    "Undefined", -1, null, null), W("Undefined", -1, null, null), X("Private Use", 4646, null, null), Y(
    "Undefined", -1, null, null), Z("Undefined", -1, null, null);

    private final String description;
    private final int rfc;
    private final String deprecated;
    private final String preferred;

    private Singleton(String description, int rfc, String deprecated, String preferred) {
        this.description = description;
        this.rfc = rfc;
        this.deprecated = deprecated;
        this.preferred = preferred;
    }

    public static Singleton valueOf(Subtag subtag) {
        if (subtag == null) {
            return null;
        }
        if (subtag.getType() == Subtag.Type.SINGLETON) {
            return valueOf(subtag.getName().toUpperCase(Locale.US));
        } else {
            throw new IllegalArgumentException("Wrong subtag type");
        }
    }

    public String getDescription() {
        return description;
    }

    public int getRFC() {
        return rfc;
    }

    public Subtag newSubtag() {
        return new Subtag(this);
    }

    public String getDeprecated() {
        return deprecated;
    }

    public boolean isDeprecated() {
        return deprecated != null;
    }

    public String getPreferredValue() {
        return preferred;
    }

    public Singleton getPreferred() {
        return preferred != null ? valueOf(preferred.toUpperCase(Locale.US)) : this;
    }
}
