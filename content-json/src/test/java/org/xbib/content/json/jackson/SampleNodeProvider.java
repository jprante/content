package org.xbib.content.json.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class SampleNodeProvider {
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final Map<NodeType, JsonNode> SAMPLE_DATA;

    static {
        SAMPLE_DATA = new EnumMap<>(NodeType.class);

        SAMPLE_DATA.put(NodeType.ARRAY, FACTORY.arrayNode());
        SAMPLE_DATA.put(NodeType.BOOLEAN, FACTORY.booleanNode(true));
        SAMPLE_DATA.put(NodeType.INTEGER, FACTORY.numberNode(0));
        SAMPLE_DATA.put(NodeType.NULL, FACTORY.nullNode());
        SAMPLE_DATA.put(NodeType.NUMBER,
                FACTORY.numberNode(new BigDecimal("1.1")));
        SAMPLE_DATA.put(NodeType.OBJECT, FACTORY.objectNode());
        SAMPLE_DATA.put(NodeType.STRING, FACTORY.textNode(""));
    }

    private SampleNodeProvider() {
    }

    public static Iterator<Object[]> getSamples(final EnumSet<NodeType> types) {
        final Map<NodeType, JsonNode> map = new EnumMap<>(SAMPLE_DATA);
        map.keySet().retainAll(types);

        List<Object[]> list = new ArrayList<>();
        for (JsonNode jsonNode : map.values()) {
            list.add(new Object[]{jsonNode});
        }
        return list.iterator();
    }

    public static Iterator<Object[]> getSamplesExcept(
            final EnumSet<NodeType> types) {
        return getSamples(EnumSet.complementOf(types));
    }

    public static Iterator<Object[]> getSamples(final NodeType first,
                                                final NodeType... other) {
        return getSamples(EnumSet.of(first, other));
    }

    public static Iterator<Object[]> getSamplesExcept(final NodeType first,
                                                      final NodeType... other) {
        return getSamples(EnumSet.complementOf(EnumSet.of(first, other)));
    }
}
