package org.xbib.content.json.pointer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link TreePointer} for {@link com.fasterxml.jackson.databind.JsonNode}.
 * This is the "original" JSON Pointer in that it addresses JSON documents.
 * It also has a lot of utility methods covering several usage scenarios.
 */
public final class JsonPointer extends TreePointer<JsonNode> {

    @SuppressWarnings("unchecked")
    private static final List<TokenResolver<JsonNode>> EMPTY_LIST = Collections.emptyList();

    /**
     * The empty JSON Pointer.
     */
    private static final JsonPointer EMPTY = new JsonPointer(EMPTY_LIST);

    /**
     * The main constructor.
     *
     * @param input the input string
     * @throws JsonPointerException malformed JSON Pointer
     * @throws NullPointerException null input
     */
    public JsonPointer(final String input) throws JsonPointerException {
        this(fromTokens(tokensFromInput(input)));
    }

    /**
     * Alternate constructor.
     * This calls {@link TreePointer#TreePointer(com.fasterxml.jackson.core.TreeNode, java.util.List)} with a
     * {@link com.fasterxml.jackson.databind.node.MissingNode} as the missing tree node.
     *
     * @param tokenResolvers the list of token resolvers
     */
    public JsonPointer(final List<TokenResolver<JsonNode>> tokenResolvers) {
        super(MissingNode.getInstance(), tokenResolvers);
    }

    /**
     * Return an empty JSON Pointer.
     *
     * @return an empty, statically allocated JSON Pointer
     */
    public static JsonPointer empty() {
        return EMPTY;
    }

    /**
     * Build a JSON Pointer out of a series of reference tokens.
     * These tokens can be everything; be sure however that they implement
     * {@link Object#toString()} correctly.
     * Each of these tokens are treated as raw tokens (ie, not
     * encoded).
     *
     * @param first the first token
     * @param other other tokens
     * @return a JSON Pointer
     * @throws NullPointerException one input token is null
     */
    public static JsonPointer of(final Object first, final Object... other) {
        final List<ReferenceToken> tokens = new ArrayList<>();
        tokens.add(ReferenceToken.fromRaw(first.toString()));
        for (final Object o : other) {
            tokens.add(ReferenceToken.fromRaw(o.toString()));
        }
        return new JsonPointer(fromTokens(tokens));
    }

    /**
     * Build a list of token resolvers from a list of reference tokens
     * Here, the token resolvers are {@link JsonNodeResolver}s.
     *
     * @param tokens the token list
     * @return a (mutable) list of token resolvers
     */
    private static List<TokenResolver<JsonNode>> fromTokens(
            final List<ReferenceToken> tokens) {
        final List<TokenResolver<JsonNode>> list = new ArrayList<>();
        for (final ReferenceToken token : tokens) {
            list.add(new JsonNodeResolver(token));
        }
        return list;
    }

    /**
     * Return a new pointer with a new token appended.
     *
     * @param raw the raw token to append
     * @return a new pointer
     * @throws NullPointerException input is null
     */
    public JsonPointer append(final String raw) {
        final ReferenceToken refToken = ReferenceToken.fromRaw(raw);
        final JsonNodeResolver resolver = new JsonNodeResolver(refToken);
        final List<TokenResolver<JsonNode>> list = new ArrayList<>(tokenResolvers);
        list.add(resolver);
        return new JsonPointer(list);
    }

    /**
     * Return a new pointer with a new integer token appended.
     *
     * @param index the integer token to append
     * @return a new pointer
     */
    public JsonPointer append(final int index) {
        return append(Integer.toString(index));
    }

    /**
     * Return a new pointer with another pointer appended.
     *
     * @param other the other pointer
     * @return a new pointer
     * @throws NullPointerException other pointer is null
     */
    public JsonPointer append(final JsonPointer other) {
        final List<TokenResolver<JsonNode>> list = new ArrayList<>(tokenResolvers);
        list.addAll(other.tokenResolvers);
        return new JsonPointer(list);
    }

    /**
     * Return the immediate parent of this JSON Pointer.
     * The parent of the empty pointer is itself.
     *
     * @return a new JSON Pointer representing the parent of the current one
     */
    public JsonPointer parent() {
        final int size = tokenResolvers.size();
        return size <= 1 ? EMPTY
                : new JsonPointer(tokenResolvers.subList(0, size - 1));
    }
}
