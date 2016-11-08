package org.xbib.content.json.diff;

import com.fasterxml.jackson.databind.JsonNode;
import org.xbib.content.json.jackson.Equivalence;
import org.xbib.content.json.jackson.JsonNumEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Longest common subsequence algorithm implementation
 * <p>
 * <p>This is an adaptation of the code found at <a
 * href="http://rosettacode.org/wiki/Longest_common_subsequence#Dynamic_Programming_2">Rosetta
 * Code</a> for {@link com.fasterxml.jackson.databind.node.ArrayNode} instances.</p>
 * <p>
 * <p>For instance, given these two arrays:</p>
 * <p>
 * <ul>
 * <li>{@code [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]},</li>
 * <li>{@code [ 1, 2, 10, 11, 5, 12, 8, 9 ]}</li>
 * </ul>
 * <p>
 * <p>this code will return {@code [ 1, 2, 5, 8, 9 ]}.</p>
 */
final class LeastCommonSubsequence {

    private static final Equivalence<JsonNode> EQUIVALENCE = JsonNumEquals.getInstance();

    private LeastCommonSubsequence() {
    }

    /**
     * Get the longest common subsequence of elements of two array nodes
     * <p>
     * <p>This is an implementation of the classic 'diff' algorithm often used
     * to compare text files line by line.</p>
     *
     * @param first  first array node to compare
     * @param second second array node to compare
     */
    static List<JsonNode> getLCS(final JsonNode first, final JsonNode second) {
        final int minSize = Math.min(first.size(), second.size());

        List<JsonNode> l1 = Arrays.asList(first);
        List<JsonNode> l2 = Arrays.asList(second);

        final List<JsonNode> ret = head(l1, l2);
        final int headSize = ret.size();

        l1 = l1.subList(headSize, l1.size());
        l2 = l2.subList(headSize, l2.size());

        final List<JsonNode> tail = tail(l1, l2);
        final int trim = tail.size();

        l1 = l1.subList(0, l1.size() - trim);
        l2 = l2.subList(0, l2.size() - trim);

        if (headSize < minSize) {
            ret.addAll(doLCS(l1, l2));
        }
        ret.addAll(tail);
        return ret;
    }

    static IndexedJsonArray doLCS(final JsonNode first, final JsonNode second) {
        return new IndexedJsonArray(getLCS(first, second));
    }

    /**
     * Compute longest common subsequence out of two lists
     * <p>
     * <p>When entering this function, both lists are trimmed from their
     * common leading and trailing nodes.</p>
     *
     * @param l1 the first list
     * @param l2 the second list
     * @return the longest common subsequence
     */
    private static List<JsonNode> doLCS(final List<JsonNode> l1,
                                        final List<JsonNode> l2) {
        final List<JsonNode> lcs = new ArrayList<>();
        // construct LCS lengths matrix
        final int size1 = l1.size();
        final int size2 = l2.size();
        final int[][] lengths = new int[size1 + 1][size2 + 1];

        JsonNode node1;
        JsonNode node2;
        int len;

        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                node1 = l1.get(i);
                node2 = l2.get(j);
                len = EQUIVALENCE.equivalent(node1, node2) ? lengths[i][j] + 1
                        : Math.max(lengths[i + 1][j], lengths[i][j + 1]);
                lengths[i + 1][j + 1] = len;
            }
        }

        // return result out of the LCS lengths matrix
        int x = size1;
        int y = size2;
        while (x > 0 && y > 0) {
            if (lengths[x][y] == lengths[x - 1][y]) {
                x--;
            } else if (lengths[x][y] == lengths[x][y - 1]) {
                y--;
            } else {
                lcs.add(l1.get(x - 1));
                x--;
                y--;
            }
        }
        Collections.reverse(lcs);
        return lcs;
    }

    /**
     * Return a list with common head elements of two lists
     * <p>
     * <p>Note that the arguments are NOT altered.</p>
     *
     * @param l1 first list
     * @param l2 second list
     * @return a list of common head elements
     */
    private static List<JsonNode> head(final List<JsonNode> l1,
                                       final List<JsonNode> l2) {
        final List<JsonNode> ret = new ArrayList<>();
        final int len = Math.min(l1.size(), l2.size());

        JsonNode node;

        for (int index = 0; index < len; index++) {
            node = l1.get(index);
            if (!EQUIVALENCE.equivalent(node, l2.get(index))) {
                break;
            }
            ret.add(node);
        }

        return ret;
    }

    /**
     * Return the list of common tail elements of two lists
     * <p>
     * <p>Note that the arguments are NOT altered. Elements are returned in
     * their order of appearance.</p>
     *
     * @param l1 first list
     * @param l2 second list
     * @return a list of common tail elements
     */
    private static List<JsonNode> tail(final List<JsonNode> l1,
                                       final List<JsonNode> l2) {
        Collections.reverse(l1);
        Collections.reverse(l2);
        final List<JsonNode> l = head(l1, l2);
        Collections.reverse(l);
        return l;
    }
}
