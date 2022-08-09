package org.xbib.content.json.pointer;

import com.fasterxml.jackson.core.TreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A pointer into a {@link com.fasterxml.jackson.core.TreeNode}.
 * Note that all pointers are absolute: they start from the root of
 * the tree. This is to mirror the behaviour of JSON Pointer proper.
 * The class does not decode a JSON Pointer representation itself; however
 * it provides all the necessary methods for implementations to achieve this.
 * This class has two traversal methods: {@link #get(com.fasterxml.jackson.core.TreeNode)} and {@link
 * #path(com.fasterxml.jackson.core.TreeNode)}. The difference between both is that {@code path()} may
 * return another node than {@code null} if the tree representation has such
 * a node. This is the case, for instance, for {@link com.fasterxml.jackson.databind.JsonNode}, which has a
 * {@link com.fasterxml.jackson.databind.node.MissingNode}.
 * At the core, this class is essentially a(n ordered) {@link java.util.List} of
 * {@link TokenResolver}s (which is iterable via the class itself).
 * Note that this class' {@link #hashCode()}, {@link #equals(Object)} and
 * {@link #toString()} are final.
 *
 * @param <T> the type of the tree
 */
public abstract class TreePointer<T extends TreeNode> implements Iterable<TokenResolver<T>> {
    /**
     * The reference token separator.
     */
    private static final char SLASH = '/';
    /**
     * The list of token resolvers.
     */
    final List<TokenResolver<T>> tokenResolvers;
    /**
     * What this tree can see as a missing node (may be {@code null}).
     */
    private final T missing;

    /**
     * Main protected constructor. This constructor makes an immutable copy of the list it receives as
     * an argument.
     *
     * @param missing        the representation of a missing node (may be null)
     * @param tokenResolvers the list of reference token resolvers
     */
    TreePointer(final T missing,
                final List<TokenResolver<T>> tokenResolvers) {
        this.missing = missing;
        this.tokenResolvers = new ArrayList<>(tokenResolvers);
    }

    /**
     * Alternate constructor.
     * This is the same as calling {@link #TreePointer(com.fasterxml.jackson.core.TreeNode, java.util.List)} with
     * {@code null} as the missing node.
     *
     * @param tokenResolvers the list of token resolvers
     */
    protected TreePointer(final List<TokenResolver<T>> tokenResolvers) {
        this(null, tokenResolvers);
    }

    /**
     * Decode an input into a list of reference tokens.
     *
     * @param input the input
     * @return the list of reference tokens
     * @throws JsonPointerException input is not a valid JSON Pointer
     * @throws NullPointerException input is null
     */
    protected static List<ReferenceToken> tokensFromInput(final String input)
            throws JsonPointerException {
        String s = input;
        final List<ReferenceToken> ret = new ArrayList<>();
        String cooked;
        int index;
        char c;

        while (!s.isEmpty()) {
            c = s.charAt(0);
            if (c != SLASH) {
                throw new JsonPointerException("not a slash");
            }
            s = s.substring(1);
            index = s.indexOf(SLASH);
            cooked = index == -1 ? s : s.substring(0, index);
            ret.add(ReferenceToken.fromCooked(cooked));
            if (index == -1) {
                break;
            }
            s = s.substring(index);
        }

        return ret;
    }

    /**
     * Traverse a node and return the result.
     * Note that this method shortcuts: it stops at the first node it cannot
     * traverse.
     *
     * @param node the node to traverse
     * @return the resulting node, {@code null} if not found
     */
    public final T get(final T node) {
        T ret = node;
        for (final TokenResolver<T> tokenResolver : tokenResolvers) {
            if (ret == null) {
                break;
            }
            ret = tokenResolver.get(ret);
        }

        return ret;
    }

    public final TokenResolver<T> getLast() {
        return tokenResolvers.get(tokenResolvers.size() - 1);
    }

    /**
     * Traverse a node and return the result.
     * <p>This is like {@link #get(com.fasterxml.jackson.core.TreeNode)}, but it will return the missing
     * node if traversal fails.</p>
     *
     * @param node the node to traverse
     * @return the result, or the missing node
     * @see #TreePointer(com.fasterxml.jackson.core.TreeNode, java.util.List)
     */
    public final T path(final T node) {
        final T ret = get(node);
        return ret == null ? missing : ret;
    }

    /**
     * Tell whether this pointer is empty.
     *
     * @return true if the reference token list is empty
     */
    public final boolean isEmpty() {
        return tokenResolvers.isEmpty();
    }

    @Override
    public final Iterator<TokenResolver<T>> iterator() {
        return tokenResolvers.iterator();
    }

    @Override
    public int hashCode() {
        return tokenResolvers.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TreePointer<?> other = (TreePointer<?>) obj;
        return tokenResolvers.equals(other.tokenResolvers);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final TokenResolver<T> tokenResolver : tokenResolvers) {
            sb.append('/').append(tokenResolver);
        }

        return sb.toString();
    }
}
