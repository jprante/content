package org.xbib.settings;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class PropertyPlaceholder {

    private final String placeholderPrefix;

    private final String placeholderSuffix;

    private final boolean ignoreUnresolvablePlaceholders;

    /**
     * Creates a new <code>PropertyPlaceholderHelper</code> that uses the supplied prefix and suffix.
     *
     * @param placeholderPrefix              the prefix that denotes the start of a placeholder.
     * @param placeholderSuffix              the suffix that denotes the end of a placeholder.
     * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should be ignored
     *                                       (<code>true</code>) or cause an exception (<code>false</code>).
     */
    public PropertyPlaceholder(String placeholderPrefix,
                               String placeholderSuffix,
                               boolean ignoreUnresolvablePlaceholders) {
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    /**
     * Replaces all placeholders of format <code>${name}</code> with the value returned from the supplied {@link
     * PlaceholderResolver}.
     *
     * @param value               the value containing the placeholders to be replaced.
     * @param placeholderResolver the <code>PlaceholderResolver</code> to use for replacement.
     * @return the supplied value with placeholders replaced inline.
     */
    public String replacePlaceholders(String value,
                                      PlaceholderResolver placeholderResolver) {
        return parseStringValue(value, placeholderResolver, new HashSet<>());
    }

    protected String parseStringValue(String value,
                                      PlaceholderResolver placeholderResolver,
                                      Set<String> visitedPlaceholders) {
        StringBuilder sb = new StringBuilder(value);
        int startIndex = value.indexOf(this.placeholderPrefix);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(sb, startIndex);
            if (endIndex != -1) {
                String placeholder = sb.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                if (!visitedPlaceholders.add(placeholder)) {
                    throw new IllegalArgumentException(
                            "Circular placeholder reference '" + placeholder + "' in property definitions");
                }
                placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
                int defaultValueIdx = placeholder.indexOf(':');
                String defaultValue = null;
                if (defaultValueIdx != -1) {
                    defaultValue = placeholder.substring(defaultValueIdx + 1);
                    placeholder = placeholder.substring(0, defaultValueIdx);
                }
                String propVal = placeholderResolver.resolvePlaceholder(placeholder);
                if (propVal == null) {
                    propVal = defaultValue;
                }
                if (propVal != null) {
                    propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
                    sb.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
                    startIndex = sb.indexOf(this.placeholderPrefix, startIndex + propVal.length());
                } else if (this.ignoreUnresolvablePlaceholders) {
                    // Proceed with unprocessed value.
                    startIndex = sb.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
                } else {
                    throw new IllegalArgumentException("could not resolve placeholder '" + placeholder + "'");
                }
                visitedPlaceholders.remove(placeholder);
            } else {
                startIndex = -1;
            }
        }
        return sb.toString();
    }

    private int findPlaceholderEndIndex(CharSequence charSequence, int startIndex) {
        int index = startIndex + this.placeholderPrefix.length();
        int withinNestedPlaceholder = 0;
        while (index < charSequence.length()) {
            if (substringMatch(charSequence, index, this.placeholderSuffix)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + this.placeholderPrefix.length() - 1;
                } else {
                    return index;
                }
            } else if (substringMatch(charSequence, index, this.placeholderPrefix)) {
                withinNestedPlaceholder++;
                index = index + this.placeholderPrefix.length();
            } else {
                index++;
            }
        }
        return -1;
    }

    private boolean substringMatch(CharSequence charSequence, int index, CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= charSequence.length() || charSequence.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }
}
