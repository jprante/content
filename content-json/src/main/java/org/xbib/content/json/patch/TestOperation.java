package org.xbib.content.json.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.xbib.content.json.jackson.Equivalence;
import org.xbib.content.json.jackson.JsonNumEquals;
import org.xbib.content.json.pointer.JsonPointer;

/**
 * JSON Patch {@code test} operation.
 * The two arguments for this operation are the pointer containing the value
 * to test ({@code path}) and the value to test equality against ({@code
 * value}).
 * It is an error if no value exists at the given path.
 * Also note that equality as defined by JSON Patch is exactly the same as it
 * is defined by JSON Schema itself. As such, this operation reuses {@link
 * JsonNumEquals} for testing equality.
 */
public final class TestOperation
        extends PathValueOperation {
    private static final Equivalence<JsonNode> EQUIVALENCE
            = JsonNumEquals.getInstance();

    @JsonCreator
    public TestOperation(@JsonProperty("path") final JsonPointer path,
                         @JsonProperty("value") final JsonNode value) {
        super("test", path, value);
    }

    @Override
    public JsonNode apply(final JsonNode node)
            throws JsonPatchException {
        final JsonNode tested = path.path(node);
        if (tested.isMissingNode()) {
            throw new JsonPatchException("no such path");
        }
        if (!EQUIVALENCE.equivalent(tested, value)) {
            throw new JsonPatchException("value test failure");
        }
        return node.deepCopy();
    }
}
