package org.xbib.content.resource.text;

/**
 * Filters are used in a variety of ways to filter or verify unicode codepoints.
 */
@FunctionalInterface
public interface Filter {

    boolean accept(int c);
}
