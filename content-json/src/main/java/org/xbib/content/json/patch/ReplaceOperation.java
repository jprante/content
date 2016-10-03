package org.xbib.content.json.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.xbib.content.json.pointer.JsonPointer;

/**
 * JSON Patch {@code replace} operation
 * For this operation, {@code path} points to the value to replace, and
 * {@code value} is the replacement value.
 * It is an error condition if {@code path} does not point to an actual JSON
 * value.
 */
public final class ReplaceOperation
        extends PathValueOperation {
    @JsonCreator
    public ReplaceOperation(@JsonProperty("path") final JsonPointer path,
                            @JsonProperty("value") final JsonNode value) {
        super("replace", path, value);
    }

    @Override
    public JsonNode apply(final JsonNode node)
            throws JsonPatchException {
        if (path.path(node).isMissingNode()) {
            throw new JsonPatchException("no such path");
        }
        final JsonNode replacement = value.deepCopy();
        if (path.isEmpty()) {
            return replacement;
        }
        final JsonNode ret = node.deepCopy();
        final JsonNode parent = path.parent().get(ret);
        final String rawToken = path.getLast().getToken().getRaw();
        if (parent.isObject()) {
            ((ObjectNode) parent).set(rawToken, replacement);
        } else {
            ((ArrayNode) parent).set(Integer.parseInt(rawToken), replacement);
        }
        return ret;
    }
}
