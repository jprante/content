package org.xbib.content.json.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for Jackson.
 *
 * This class provides utility methods to get a {@link com.fasterxml.jackson.databind.node.JsonNodeFactory} and
 * a preconfigured {@link com.fasterxml.jackson.databind.ObjectReader}. It can also be used to return
 * preconfigured instances of {@link com.fasterxml.jackson.databind.ObjectMapper} (see {@link #newMapper()}.
 *
 */
public final class JacksonUtils {

    private static final Logger logger = Logger.getLogger(JacksonUtils.class.getName());

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    private static final ObjectReader READER;
    private static final ObjectWriter WRITER;

    static {
        final ObjectMapper mapper = newMapper();
        READER = mapper.reader();
        WRITER = mapper.writer();
    }

    private JacksonUtils() {
    }

    /**
     * Return a preconfigured {@link com.fasterxml.jackson.databind.ObjectReader} to read JSON inputs.
     *
     * @return the reader
     * @see #newMapper()
     */
    public static ObjectReader getReader() {
        return READER;
    }

    /**
     * Return a preconfigured {@link com.fasterxml.jackson.databind.node.JsonNodeFactory} to generate JSON data as
     * {@link com.fasterxml.jackson.databind.JsonNode}s.
     *
     * @return the factory
     */
    public static JsonNodeFactory nodeFactory() {
        return FACTORY;
    }

    /**
     * Return a map out of an object's members.
     * If the node given as an argument is not a map, an empty map is
     * returned.
     *
     * @param node the node
     * @return a map
     */
    public static Map<String, JsonNode> asMap(final JsonNode node) {
        if (!node.isObject()) {
            return Collections.emptyMap();
        }
        final Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        final Map<String, JsonNode> ret = new HashMap<>();
        Map.Entry<String, JsonNode> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            ret.put(entry.getKey(), entry.getValue());
        }

        return ret;
    }

    /**
     * Pretty print a JSON value.
     *
     * @param node the JSON value to print
     * @return the pretty printed value as a string
     * @see #newMapper()
     */
    public static String prettyPrint(final JsonNode node) {
        final StringWriter writer = new StringWriter();

        try {
            WRITER.writeValue(writer, node);
            writer.flush();
        } catch (IOException e) {
            logger.log(Level.FINE, e.getMessage(), e);
        }
        return writer.toString();
    }

    /**
     * Return a preconfigured {@link com.fasterxml.jackson.databind.ObjectMapper}.
     * The returned mapper will have the following features enabled:
     * <ul>
     * <li>{@link com.fasterxml.jackson.databind.DeserializationFeature#USE_BIG_DECIMAL_FOR_FLOATS};</li>
     * <li>{@link com.fasterxml.jackson.databind.SerializationFeature#INDENT_OUTPUT}.</li>
     * </ul>
     * This returns a new instance each time.
     *
     * @return an {@link com.fasterxml.jackson.databind.ObjectMapper}
     */
    public static ObjectMapper newMapper() {
        return new ObjectMapper().setNodeFactory(FACTORY)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }
}
