package org.xbib.content.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.xbib.content.AbstractXContentGenerator;
import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentGenerator;
import org.xbib.content.XContentHelper;
import org.xbib.content.XContentParser;
import org.xbib.content.XContentString;
import org.xbib.content.io.BytesReference;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 */
public class JsonXContentGenerator extends AbstractXContentGenerator {

    private final JsonGeneratorDelegate delegate;

    public JsonXContentGenerator(JsonGenerator generator) {
        this.delegate = new JsonGeneratorDelegate(generator);
        super.setGenerator(delegate);
    }

    @Override
    public XContent content() {
        return delegate.content();
    }

    @Override
    public void usePrettyPrint() {
        delegate.usePrettyPrint();
    }

    @Override
    public void writeRawField(String fieldName, byte[] content, OutputStream outputStream) throws IOException {
        delegate.writeRawField(fieldName, content, outputStream);
    }

    @Override
    public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream outputStream)
            throws IOException {
        delegate.writeRawField(fieldName, content, offset, length, outputStream);
    }

    @Override
    public void writeRawField(String fieldName, BytesReference content, OutputStream outputStream) throws IOException {
        delegate.writeRawField(fieldName, content, outputStream);
    }

    @Override
    public void writeValue(XContentBuilder builder) throws IOException {
        delegate.writeValue(builder);
    }

    @Override
    public void copyCurrentStructure(XContentParser parser) throws IOException {
        delegate.copyCurrentStructure(parser);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    private static class JsonGeneratorDelegate implements XContentGenerator {

        final JsonGenerator jsonGenerator;

        JsonGeneratorDelegate(JsonGenerator jsonGenerator) {
            this.jsonGenerator = jsonGenerator;
        }

        @Override
        public XContent content() {
            return JsonXContent.jsonContent();
        }

        @Override
        public void usePrettyPrint() {
            jsonGenerator.useDefaultPrettyPrinter();
        }


        @Override
        public void writeStartArray() throws IOException {
            jsonGenerator.writeStartArray();
        }

        @Override
        public void writeEndArray() throws IOException {
            jsonGenerator.writeEndArray();
        }

        @Override
        public void writeStartObject() throws IOException {
            jsonGenerator.writeStartObject();
        }

        @Override
        public void writeEndObject() throws IOException {
            jsonGenerator.writeEndObject();
        }

        @Override
        public void writeFieldName(String name) throws IOException {
            jsonGenerator.writeFieldName(name);
        }

        @Override
        public void writeFieldName(XContentString name) throws IOException {
            jsonGenerator.writeFieldName(name);
        }

        @Override
        public void writeString(String text) throws IOException {
            jsonGenerator.writeString(text);
        }

        @Override
        public void writeString(char[] text, int offset, int len) throws IOException {
            jsonGenerator.writeString(text, offset, len);
        }

        @Override
        public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
            jsonGenerator.writeUTF8String(text, offset, length);
        }

        @Override
        public void writeBinary(byte[] data, int offset, int len) throws IOException {
            jsonGenerator.writeBinary(data, offset, len);
        }

        @Override
        public void writeBinary(byte[] data) throws IOException {
            jsonGenerator.writeBinary(data);
        }

        @Override
        public void writeNumber(int v) throws IOException {
            jsonGenerator.writeNumber(v);
        }

        @Override
        public void writeNumber(long v) throws IOException {
            jsonGenerator.writeNumber(v);
        }

        @Override
        public void writeNumber(double d) throws IOException {
            jsonGenerator.writeNumber(d);
        }

        @Override
        public void writeNumber(float f) throws IOException {
            jsonGenerator.writeNumber(f);
        }

        @Override
        public void writeNumber(BigInteger bi) throws IOException {
            jsonGenerator.writeNumber(bi);
        }

        @Override
        public void writeNumber(BigDecimal bd) throws IOException {
            jsonGenerator.writeNumber(bd);
        }

        @Override
        public void writeBoolean(boolean b) throws IOException {
            jsonGenerator.writeBoolean(b);
        }

        @Override
        public void writeNull() throws IOException {
            jsonGenerator.writeNull();
        }

        @Override
        public void writeStringField(String fieldName, String value) throws IOException {
            jsonGenerator.writeStringField(fieldName, value);
        }

        @Override
        public void writeStringField(XContentString fieldName, String value) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
        }

        @Override
        public void writeBooleanField(String fieldName, boolean value) throws IOException {
            jsonGenerator.writeBooleanField(fieldName, value);
        }

        @Override
        public void writeBooleanField(XContentString fieldName, boolean value) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeBoolean(value);
        }

        @Override
        public void writeNullField(String fieldName) throws IOException {
            jsonGenerator.writeNullField(fieldName);
        }

        @Override
        public void writeNumberField(String fieldName, int value) throws IOException {
            jsonGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(XContentString fieldName, int value) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeNumber(value);
        }

        @Override
        public void writeNumberField(String fieldName, long value) throws IOException {
            jsonGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(XContentString fieldName, long value) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeNumber(value);
        }

        @Override
        public void writeNumberField(String fieldName, double value) throws IOException {
            jsonGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(XContentString fieldName, double value) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeNumber(value);
        }

        @Override
        public void writeNumberField(String fieldName, float value) throws IOException {
            jsonGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(XContentString fieldName, float value) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeNumber(value);
        }

        @Override
        public void writeNumberField(String fieldName, BigInteger value) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeNumber(value);
        }

        @Override
        public void writeNumberField(XContentString fieldName, BigInteger value) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeNumber(value);
        }

        @Override
        public void writeNumberField(String fieldName, BigDecimal value) throws IOException {
            jsonGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(XContentString fieldName, BigDecimal value) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeNumber(value);
        }

        @Override
        public void writeBinaryField(String fieldName, byte[] data) throws IOException {
            jsonGenerator.writeBinaryField(fieldName, data);
        }

        @Override
        public void writeBinaryField(XContentString fieldName, byte[] data) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeBinary(data);
        }

        @Override
        public void writeArrayFieldStart(String fieldName) throws IOException {
            jsonGenerator.writeArrayFieldStart(fieldName);
        }

        @Override
        public void writeArrayFieldStart(XContentString fieldName) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeStartArray();
        }

        @Override
        public void writeObjectFieldStart(String fieldName) throws IOException {
            jsonGenerator.writeObjectFieldStart(fieldName);
        }

        @Override
        public void writeObjectFieldStart(XContentString fieldName) throws IOException {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeStartObject();
        }

        @Override
        public void writeRawField(String fieldName, byte[] content, OutputStream outputStream) throws IOException {
            jsonGenerator.writeRaw(",\"");
            jsonGenerator.writeRaw(fieldName);
            jsonGenerator.writeRaw("\":");
            flush();
            outputStream.write(content);
        }

        @Override
        public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream outputStream)
                throws IOException {
            jsonGenerator.writeRaw(",\"");
            jsonGenerator.writeRaw(fieldName);
            jsonGenerator.writeRaw("\":");
            flush();
            outputStream.write(content, offset, length);
        }

        @Override
        public void writeRawField(String fieldName, BytesReference content, OutputStream outputStream) throws IOException {
            jsonGenerator.writeRaw(",\"");
            jsonGenerator.writeRaw(fieldName);
            jsonGenerator.writeRaw("\":");
            flush();
            content.streamOutput(outputStream);
        }

        @Override
        public void writeValue(XContentBuilder builder) throws IOException {
            jsonGenerator.writeRawValue(builder.string());
        }

        @Override
        public void copy(XContentBuilder builder, OutputStream outputStream) throws IOException {
            flush();
            builder.bytes().streamOutput(outputStream);
        }

        @Override
        public void copyCurrentStructure(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser instanceof JsonXContentParser) {
                jsonGenerator.copyCurrentStructure(((JsonXContentParser) parser).parser);
            } else {
                XContentHelper.copyCurrentStructure(this, parser);
            }
        }

        @Override
        public void flush() throws IOException {
            jsonGenerator.flush();
        }

        @Override
        public void close() throws IOException {
            if (jsonGenerator.isClosed()) {
                return;
            }
            jsonGenerator.close();
        }
    }
}
