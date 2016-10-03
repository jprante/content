package org.xbib.content.json.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.xbib.content.json.pointer.JsonPointer;

/**
 * JSON Patch {@code copy} operation.
 * For this operation, {@code from} is the JSON Pointer of the value to copy,
 * and {@code path} is the destination where the value should be copied.
 * As for {@code add}:
 *
 * <ul>
 * <li>the value at the destination path is either created or replaced;</li>
 * <li>it is created only if the immediate parent exists;</li>
 * <li>{@code -} appends at the end of an array.</li>
 * </ul>
 *
 * It is an error if {@code from} fails to resolve to a JSON value.
 */
public final class CopyOperation extends DualPathOperation {
    @JsonCreator
    public CopyOperation(@JsonProperty("from") final JsonPointer from,
                         @JsonProperty("path") final JsonPointer path) {
        super("copy", from, path);
    }

    @Override
    public JsonNode apply(final JsonNode node)
            throws JsonPatchException {
        final JsonNode dupData = from.path(node).deepCopy();
        if (dupData.isMissingNode()) {
            throw new JsonPatchException("no such path");
        }
        return new AddOperation(path, dupData).apply(node);
    }
}
