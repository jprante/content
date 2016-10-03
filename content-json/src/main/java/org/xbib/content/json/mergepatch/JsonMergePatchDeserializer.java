package org.xbib.content.json.mergepatch;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Custom {@link com.fasterxml.jackson.databind.JsonDeserializer} for {@link JsonMergePatch} instances
 * Unlike "real" JSON Patches (ie, as defined by RFC 6902), JSON merge patch
 * instances are "free form", they can be either JSON arrays or JSON objects
 * without any restriction on the contents; only the content itself may guide
 * the patching process (null elements in arrays, null values in objects).
 * Jackson does not provide a deserializer for such a case; we therefore
 * write our own here.
 */
public final class JsonMergePatchDeserializer extends JsonDeserializer<JsonMergePatch> {
    @Override
    public JsonMergePatch deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
        final JsonNode node = jp.readValueAs(JsonNode.class);
        if (!node.isContainerNode()) {
            throw new IOException("expected either an array or an object");
        }
        return node.isArray() ? new ArrayMergePatch(node)
                : new ObjectMergePatch(node);
    }
}
