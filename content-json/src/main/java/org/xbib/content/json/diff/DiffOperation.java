package org.xbib.content.json.diff;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.xbib.content.json.jackson.JacksonUtils;
import org.xbib.content.json.pointer.JsonPointer;

/**
 * Difference operation types. Add, remove, and replace operations
 * are directly generated by node comparison. Move operations are
 * the result of factorized add and remove operations.
 */
enum DiffOperation {
    ADD("add"),
    REMOVE("remove"),
    REPLACE("replace"),
    MOVE("move"),
    COPY("copy");

    private final String opName;

    DiffOperation(final String opName) {
        this.opName = opName;
    }

    ObjectNode newOp(final JsonPointer ptr) {
        final ObjectNode ret = JacksonUtils.nodeFactory().objectNode();
        ret.put("op", opName);
        ret.put("path", ptr.toString());
        return ret;
    }

    @Override
    public String toString() {
        return opName;
    }
}
