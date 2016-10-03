package org.xbib.content.json.jackson;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An equivalence strategy for JSON Schema equality
 * {@link com.fasterxml.jackson.databind.JsonNode} does a pretty good job of obeying the  {@link
 * Object#equals(Object) equals()}/{@link Object#hashCode() hashCode()}
 * contract. And in fact, it does it too well for JSON Schema.
 *
 * For instance, it considers numeric nodes {@code 1} and {@code 1.0} to be
 * different nodes, which is true. But some IETF RFCs and drafts (among  them,
 * JSON Schema and JSON Patch) mandate that numeric JSON values be considered
 * equal if their mathematical value is the same. This class implements this
 * kind of equality.
 */
public final class JsonNumEquals implements Equivalence<JsonNode> {
    private static final Equivalence<JsonNode> INSTANCE = new JsonNumEquals();

    private JsonNumEquals() {
    }

    public static Equivalence<JsonNode> getInstance() {
        return INSTANCE;
    }

    private static boolean numEquals(final JsonNode a, final JsonNode b) {
        /*
         * If both numbers are integers, delegate to JsonNode.
         */
        if (a.isIntegralNumber() && b.isIntegralNumber()) {
            return a.equals(b);
        }

        /*
         * Otherwise, compare decimal values.
         */
        return a.decimalValue().compareTo(b.decimalValue()) == 0;
    }

    @Override
    public boolean equivalent(final JsonNode a, final JsonNode b) {
        /*
         * If both are numbers, delegate to the helper method
         */
        if (a.isNumber() && b.isNumber()) {
            return numEquals(a, b);
        }

        final NodeType typeA = NodeType.getNodeType(a);
        final NodeType typeB = NodeType.getNodeType(b);

        /*
         * If they are of different types, no dice
         */
        if (typeA != typeB) {
            return false;
        }

        /*
         * For all other primitive types than numbers, trust JsonNode
         */
        if (!a.isContainerNode()) {
            return a.equals(b);
        }

        /*
         * OK, so they are containers (either both arrays or objects due to the
         * test on types above). They are obviously not equal if they do not
         * have the same number of elements/members.
         */
        if (a.size() != b.size()) {
            return false;
        }

        /*
         * Delegate to the appropriate method according to their type.
         */
        return typeA == NodeType.ARRAY ? arrayEquals(a, b) : objectEquals(a, b);
    }

    @Override
    public int hash(final JsonNode t) {
        /*
         * If this is a numeric node, we want the same hashcode for the same
         * mathematical values. Go with double, its range is good enough for
         * 99+% of use cases.
         */
        if (t.isNumber()) {
            return Double.valueOf(t.doubleValue()).hashCode();
        }

        /*
         * If this is a primitive type (other than numbers, handled above),
         * delegate to JsonNode.
         */
        if (!t.isContainerNode()) {
            return t.hashCode();
        }

        /*
         * The following hash calculations work, yes, but they are poor at best.
         * And probably slow, too.
         */
        int ret = 0;

        /*
         * If the container is empty, just return
         */
        if (t.size() == 0) {
            return ret;
        }

        /*
         * Array
         */
        if (t.isArray()) {
            for (final JsonNode element : t) {
                ret = 31 * ret + hash(element);
            }
            return ret;
        }

        /*
         * Not an array? An object.
         */
        final Iterator<Map.Entry<String, JsonNode>> iterator = t.fields();

        Map.Entry<String, JsonNode> entry;

        while (iterator.hasNext()) {
            entry = iterator.next();
            ret = 31 * ret + (entry.getKey().hashCode() ^ hash(entry.getValue()));
        }

        return ret;
    }

    private boolean arrayEquals(final JsonNode a, final JsonNode b) {
        /*
         * We are guaranteed here that arrays are the same size.
         */
        final int size = a.size();

        for (int i = 0; i < size; i++) {
            if (!equivalent(a.get(i), b.get(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean objectEquals(final JsonNode a, final JsonNode b) {
        /*
         * Grab the key set from the first node
         */
        final Set<String> keys = new HashSet<>();
        Iterator<String> it = a.fieldNames();
        while (it.hasNext()) {
            keys.add(it.next());
        }

        /*
         * Grab the key set from the second node, and see if both sets are the
         * same. If not, objects are not equal, no need to check for children.
         */
        final Set<String> set = new HashSet<>();
        it = b.fieldNames();
        while (it.hasNext()) {
            set.add(it.next());
        }
        if (!set.equals(keys)) {
            return false;
        }

        /*
         * Test each member individually.
         */
        for (final String key : keys) {
            if (!equivalent(a.get(key), b.get(key))) {
                return false;
            }
        }

        return true;
    }
}
