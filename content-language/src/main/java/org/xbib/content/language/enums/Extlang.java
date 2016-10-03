package org.xbib.content.language.enums;

import org.xbib.content.language.Subtag;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Enum constants used to validate language tags.
 */
public enum Extlang {
    EXT_LANG(null, null, null);

    private final String deprecated;
    private final String preferred;
    private final String prefix;
    private final List<String> descriptions;

    Extlang(String dep, String pref, String prefix, String... desc) {
        this.deprecated = dep;
        this.preferred = pref;
        this.prefix = prefix;
        this.descriptions = Arrays.asList(desc);
    }

    public static Extlang valueOf(Subtag subtag) {
        if (subtag != null && subtag.getType() == Subtag.Type.PRIMARY) {
            return valueOf(subtag.getName().toUpperCase(Locale.US));
        }
        throw new IllegalArgumentException("wrong subtag type");
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

    public Extlang getPreferred() {
        return preferred != null ? valueOf(preferred.toUpperCase(Locale.US)) : this;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDescription() {
        return descriptions != null && !descriptions.isEmpty() ? descriptions.get(0) : null;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public Subtag newSubtag() {
        return new Subtag(this);
    }

}
