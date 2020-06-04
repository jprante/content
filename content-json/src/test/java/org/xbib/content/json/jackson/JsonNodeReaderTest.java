package org.xbib.content.json.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public final class JsonNodeReaderTest {

    @Test
    public void streamIsClosedOnRead()
            throws IOException {
        final InputStream in = spy(new ByteArrayInputStream("[]".getBytes(StandardCharsets.UTF_8)));
        final JsonNode node = new JsonNodeReader().fromInputStream(in);
        verify(in).close();
        assertEquals(node, new ObjectMapper().readTree(new ByteArrayInputStream("[]".getBytes("UTF-8"))));
    }

    @Test
    public void readerIsClosedOnRead()
            throws IOException {
        final Reader reader = spy(new StringReader("[]"));
        final JsonNode node = new JsonNodeReader().fromReader(reader);
        assertEquals(node, new ObjectMapper().readTree(new StringReader("[]")));
        verify(reader).close();
    }

    @Test
    public void malformedDataThrowsExpectedException() {
        String[] inputs = new String[]{
                "", "[]{}", "[]]"
        };
        final JsonNodeReader reader = new JsonNodeReader();
        for (String input : inputs) {
            Assertions.assertThrows(JsonParseException.class, () ->
                reader.fromInputStream(new ByteArrayInputStream(input.getBytes())));
        }
    }
}
