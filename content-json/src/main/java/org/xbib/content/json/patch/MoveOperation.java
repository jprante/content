package org.xbib.content.json.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.xbib.content.json.pointer.JsonPointer;

/**
 * JSON Patch {@code move} operation.
 * For this operation, {@code from} points to the value to move, and {@code
 * path} points to the new location of the moved value.
 * As for {@code add}:
 * <ul>
 * <li>the value at the destination path is either created or replaced;</li>
 * <li>it is created only if the immediate parent exists;</li>
 * <li>{@code -} appends at the end of an array.</li>
 * </ul>
 * It is an error condition if {@code from} does not point to a JSON value.
 * The specification adds another rule that the {@code from} path must not be
 * an immediate parent of {@code path}. Unfortunately, that doesn't really work.
 * Consider this patch:
 * <pre>
 *     { "op": "move", "from": "/0", "path": "/0/x" }
 * </pre>
 * Even though {@code /0} is an immediate parent of {@code /0/x}, when this
 * patch is applied to:
 * <pre>
 *     [ "victim", {} ]
 * </pre>
 * it actually succeeds and results in the patched value:
 * <pre>
 *     [ { "x": "victim" } ]
 * </pre>
 */
public final class MoveOperation extends DualPathOperation {

    @JsonCreator
    public MoveOperation(@JsonProperty("from") final JsonPointer from,
                         @JsonProperty("path") final JsonPointer path) {
        super("move", from, path);
    }

    @Override
    public JsonNode apply(final JsonNode node) throws JsonPatchException {
        if (from.equals(path)) {
            return node.deepCopy();
        }
        final JsonNode movedNode = from.path(node);
        if (movedNode.isMissingNode()) {
            throw new JsonPatchException("no such path: " + path);
        }
        final JsonPatchOperation remove = new RemoveOperation(from);
        final JsonPatchOperation add = new AddOperation(path, movedNode);
        return add.apply(remove.apply(node));
    }
}
