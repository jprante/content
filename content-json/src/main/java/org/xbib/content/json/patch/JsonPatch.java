package org.xbib.content.json.patch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.xbib.content.json.jackson.JacksonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of JSON Patch.
 * <a href="http://tools.ietf.org/html/draft-ietf-appsawg-json-patch-10">JSON
 * Patch</a>, as its name implies, is an IETF draft describing a mechanism to
 * apply a patch to any JSON value. This implementation covers all operations
 * according to the specification; however, there are some subtle differences
 * with regards to some operations which are covered in these operations'
 * respective documentation.
 * An example of a JSON Patch is as follows:
 * <code>
 *     [
 *         {
 *             "op": "add",
 *             "path": "/-",
 *             "value": {
 *                 "productId": 19,
 *                 "name": "Duvel",
 *                 "type": "beer"
 *             }
 *         }
 *     ]
 * </code>
 * This patch contains a single operation which adds an item at the end of
 * an array. A JSON Patch can contain more than one operation; in this case, all
 * operations are applied to the input JSON value in their order of appearance,
 * until all operations are applied or an error condition is encountered.
 * The main point where this implementation differs from the specification
 * is initial JSON parsing. The draft says:
 *
 * <pre>
 *     Operation objects MUST have exactly one "op" member
 * </pre>
 *
 * and
 *
 * <pre>
 *     Additionally, operation objects MUST have exactly one "path" member.
 * </pre>
 *
 * However, obeying these to the letter forces constraints on the JSON
 * parser. Here, these constraints are not enforced, which means:
 *
 * <pre>
 *     [ { "op": "add", "op": "remove", "path": "/x" } ]
 * </pre>
 *
 * is parsed (as a {@code remove} operation, since it appears last).
 * IMPORTANT NOTE: the JSON Patch is supposed to be VALID when the
 * constructor for this class ({@link JsonPatch#fromJson(com.fasterxml.jackson.databind.JsonNode)} is used.
 */
public final class JsonPatch
        implements JsonSerializable {
    /**
     * List of operations.
     */
    private final List<JsonPatchOperation> operations;

    /**
     * Package-visible constructor.
     *
     * Visible only for testing purposes. Also used for deserialization.
     *
     * @param operations the list of operations for this patch
     * @see JsonPatchOperation
     */
    @JsonCreator
    JsonPatch(final List<JsonPatchOperation> operations) {
        this.operations = new ArrayList<>(operations);
    }

    /**
     * Static factory method to build a JSON Patch out of a JSON representation.
     *
     * @param node the JSON representation of the generated JSON Patch
     * @return a JSON Patch
     * @throws java.io.IOException  input is not a valid JSON patch
     */
    public static JsonPatch fromJson(final JsonNode node) throws IOException {
        return JacksonUtils.getReader().forType(JsonPatch.class)
                .readValue(node);
    }

    /**
     * Apply this patch to a JSON value.
     *
     * @param node the value to apply the patch to
     * @return the patched JSON value
     * @throws JsonPatchException if patch fails
     */
    public JsonNode apply(final JsonNode node) throws JsonPatchException {
        JsonNode ret = node;
        for (final JsonPatchOperation operation : operations) {
            ret = operation.apply(ret);
        }

        return ret;
    }

    @Override
    public String toString() {
        return operations.toString();
    }

    @Override
    public void serialize(final JsonGenerator jgen,
                          final SerializerProvider provider)
            throws IOException {
        jgen.writeStartArray();
        for (final JsonPatchOperation op : operations) {
            op.serialize(jgen, provider);
        }
        jgen.writeEndArray();
    }

    @Override
    public void serializeWithType(final JsonGenerator jgen,
                                  final SerializerProvider provider, final TypeSerializer typeSer)
            throws IOException {
        serialize(jgen, provider);
    }
}
