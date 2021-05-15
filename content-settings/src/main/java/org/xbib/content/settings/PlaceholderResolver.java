package org.xbib.content.settings;

/**
 * Strategy interface used to resolve replacement values for placeholders contained in Strings.
 */
@FunctionalInterface
public interface PlaceholderResolver {

    /**
     * Resolves the supplied placeholder name into the replacement value.
     *
     * @param placeholderName the name of the placeholder to resolve.
     * @return the replacement value or <code>null</code> if no replacement is to be made.
     */
    String resolvePlaceholder(String placeholderName);
}
