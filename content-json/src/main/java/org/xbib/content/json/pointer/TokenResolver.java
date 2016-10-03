package org.xbib.content.json.pointer;

import com.fasterxml.jackson.core.TreeNode;

/**
 * Reference token traversal class.
 *
 * This class is meant to be extended and implemented for all types of trees
 * inheriting {@link com.fasterxml.jackson.core.TreeNode}.
 * This package contains one implementation of this class for {@link
 * com.fasterxml.jackson.databind.JsonNode}.
 * Note that its {@link #equals(Object)}, {@link #hashCode()} and {@link
 * #toString()} are final.
 *
 * @param <T> the type of tree to traverse
 * @see JsonNodeResolver
 */
public abstract class TokenResolver<T extends TreeNode> {
    /**
     * The associated reference token.
     */
    protected final ReferenceToken token;

    /**
     * The only constructor.
     *
     * @param token the reference token
     */
    TokenResolver(final ReferenceToken token) {
        this.token = token;
    }

    /**
     * Advance one level into the tree
     * Note: it is required that this method return null on
     * traversal failure.
     * Note 2: handling {@code null} itself is up to implementations.
     *
     * @param node the node to traverse
     * @return the other node, or {@code null} if no such node exists for that
     * token
     */
    public abstract T get(final T node);

    public final ReferenceToken getToken() {
        return token;
    }

    @Override
    public final int hashCode() {
        return token.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        return obj != null && (this == obj || getClass() == obj.getClass()
                && token.equals(((TokenResolver<?>) obj).token));
    }

    @Override
    public final String toString() {
        return token.toString();
    }
}
