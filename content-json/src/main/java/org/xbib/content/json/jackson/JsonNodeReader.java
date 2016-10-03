package org.xbib.content.json.jackson;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class dedicated to reading JSON values from {@link java.io.InputStream}s and {@link
 * java.io.Reader}s.
 * This class wraps a Jackson {@link com.fasterxml.jackson.databind.ObjectMapper} so that it read one, and
 * only one, JSON text from a source. By default, when you read and map an
 * input source, Jackson will stop after it has read the first valid JSON text;
 * this means, for instance, that with this as an input:
 * <pre>
 *     []]]
 * </pre>
 * it will read the initial empty array ({@code []}) and stop there. This
 * class, instead, will peek to see whether anything is after the initial array,
 * and throw an exception if it finds anything.
 * Note: the input sources are closed by the read methods.
 *
 * @see com.fasterxml.jackson.databind.ObjectMapper#readValues(com.fasterxml.jackson.core.JsonParser, Class)
 */
public final class JsonNodeReader {

    private static final Logger logger = Logger.getLogger(JsonNodeReader.class.getName());

    private final ObjectReader reader;

    public JsonNodeReader(final ObjectMapper mapper) {
        reader = mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true)
                .readerFor(JsonNode.class);
    }

    public JsonNodeReader() {
        this(JacksonUtils.newMapper());
    }

    private static JsonNode readNode(final MappingIterator<JsonNode> iterator) throws IOException {
        final Object source = iterator.getParser().getInputSource();
        final JsonParseExceptionBuilder builder = new JsonParseExceptionBuilder(null, source);
        if (!iterator.hasNextValue()) {
            throw builder.build();
        }
        final JsonNode ret = iterator.nextValue();
        builder.setLocation(iterator.getCurrentLocation());
        try {
            if (iterator.hasNextValue()) {
                throw builder.build();
            }
        } catch (JsonParseException e) {
            logger.log(Level.FINE, e.getMessage(), e);
            throw builder.setLocation(e.getLocation()).build();
        }
        return ret;
    }

    /**
     * Read a JSON value from an {@link java.io.InputStream}.
     *
     * @param in the input stream
     * @return the value
     * @throws java.io.IOException malformed input, or problem encountered when reading
     *                             from the stream
     */
    public JsonNode fromInputStream(final InputStream in) throws IOException {
        JsonParser parser = null;
        MappingIterator<JsonNode> iterator = null;

        try {
            parser = reader.getFactory().createParser(in);
            iterator = reader.readValues(parser);
            return readNode(iterator);
        } finally {
            if (parser != null) {
                parser.close();
            }
            if (iterator != null) {
                iterator.close();
            }
        }
    }

    /**
     * Read a JSON value from a {@link java.io.Reader}.
     *
     * @param r the reader
     * @return the value
     * @throws java.io.IOException malformed input, or problem encountered when reading
     *                             from the reader
     */
    public JsonNode fromReader(final Reader r)
            throws IOException {
        JsonParser parser = null;
        MappingIterator<JsonNode> iterator = null;

        try {
            parser = reader.getFactory().createParser(r);
            iterator = reader.readValues(parser);
            return readNode(iterator);
        } finally {
            if (parser != null) {
                parser.close();
            }
            if (iterator != null) {
                iterator.close();
            }
        }
    }

    /**
     *
     */
    private static final class JsonParseExceptionBuilder {
        private JsonParser jsonParser;
        private JsonLocation location;

        private JsonParseExceptionBuilder(final JsonParser jsonParser, final Object source) {
            this.jsonParser = jsonParser;
            location = new JsonLocation(source, 0L, 1, 1);
        }

        private JsonParseExceptionBuilder setLocation(final JsonLocation location) {
            this.location = location;
            return this;
        }

        public JsonParseException build() {
            return new JsonParseException(jsonParser, "", location);
        }
    }
}
