package org.xbib.content.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentGenerator;
import org.xbib.content.XContentParser;
import org.xbib.content.io.BytesReference;
import org.xbib.content.io.BytesStreamInput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * A JSON content implementation using Jackson.
 */
public class JsonXContent implements XContent {

    private static final JsonXContent jsonXContent;
    private static final JsonFactory jsonFactory;

    static {
        jsonFactory = new JsonFactory();
        jsonFactory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        jsonXContent = new JsonXContent();
    }

    public JsonXContent() {
        // nothing to do
    }

    public static JsonXContent jsonContent() {
        return jsonXContent;
    }

    public static XContentBuilder contentBuilder() throws IOException {
        return XContentBuilder.builder(jsonXContent);
    }

    public static XContentBuilder contentBuilder(OutputStream outputStream) throws IOException {
        return XContentBuilder.builder(jsonXContent, outputStream);
    }

    @Override
    public String name() {
        return "json";
    }

    @Override
    public XContentGenerator createGenerator(OutputStream outputStream) throws IOException {
        return new JsonXContentGenerator(jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8));
    }

    @Override
    public XContentGenerator createGenerator(Writer writer) throws IOException {
        return new JsonXContentGenerator(jsonFactory.createGenerator(writer));
    }

    @Override
    public XContentParser createParser(String content) throws IOException {
        return new JsonXContentParser(jsonFactory
                .createParser(new BytesStreamInput(content.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public XContentParser createParser(InputStream inputStream) throws IOException {
        return new JsonXContentParser(jsonFactory.createParser(inputStream));
    }

    @Override
    public XContentParser createParser(byte[] data) throws IOException {
        return new JsonXContentParser(jsonFactory.createParser(data));
    }

    @Override
    public XContentParser createParser(byte[] data, int offset, int length) throws IOException {
        return new JsonXContentParser(jsonFactory.createParser(data, offset, length));
    }

    @Override
    public XContentParser createParser(BytesReference bytes) throws IOException {
        return createParser(bytes.streamInput());
    }

    @Override
    public XContentParser createParser(Reader reader) throws IOException {
        return new JsonXContentParser(jsonFactory.createParser(reader));
    }

    @Override
    public boolean isXContent(BytesReference bytes) {
        int length = Math.min(bytes.length(), 20);
        if (length == 0) {
            return false;
        }
        byte first = bytes.get(0);
        if (first == '{') {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (bytes.get(i) == '{') {
                return true;
            }
        }
        return false;
    }
}
