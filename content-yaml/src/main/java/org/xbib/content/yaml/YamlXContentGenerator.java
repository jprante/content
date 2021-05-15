package org.xbib.content.yaml;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentGenerator;
import org.xbib.content.XContentParser;
import org.xbib.content.core.AbstractXContentGenerator;
import org.xbib.content.core.DefaultXContentBuilder;
import org.xbib.content.core.XContentHelper;
import org.xbib.content.io.BytesReference;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class YamlXContentGenerator extends AbstractXContentGenerator {

    private final YAMLGeneratorDelegate delegate;

    public YamlXContentGenerator(YAMLGenerator generator) {
        this.delegate = new YAMLGeneratorDelegate(generator);
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
        writeFieldName(fieldName);
        try (YAMLParser parser = YamlXContent.yamlFactory().createParser(content)) {
            parser.nextToken();
            YamlXContent.yamlFactory().createGenerator(outputStream).copyCurrentStructure(parser);
        }
    }

    @Override
    public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream outputStream)
            throws IOException {
        writeFieldName(fieldName);
        try (YAMLParser parser = YamlXContent.yamlFactory().createParser(content, offset, length)) {
            parser.nextToken();
            YamlXContent.yamlFactory().createGenerator(outputStream).copyCurrentStructure(parser);
        }
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
    public void copy(XContentBuilder builder, OutputStream outputStream) throws IOException {
        flush();
        if (builder instanceof DefaultXContentBuilder) {
            DefaultXContentBuilder xContentBuilder = (DefaultXContentBuilder) builder;
            xContentBuilder.bytes().streamOutput(outputStream);
        }
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    private static class YAMLGeneratorDelegate implements XContentGenerator {

        final YAMLGenerator yamlGenerator;

        YAMLGeneratorDelegate(YAMLGenerator yamlGenerator) {
            this.yamlGenerator = yamlGenerator;
        }

        @Override
        public XContent content() {
            return YamlXContent.yamlContent();
        }

        @Override
        public void usePrettyPrint() {
            yamlGenerator.useDefaultPrettyPrinter();
        }

        @Override
        public void writeStartArray() throws IOException {
            yamlGenerator.writeStartArray();
        }

        @Override
        public void writeEndArray() throws IOException {
            yamlGenerator.writeEndArray();
        }

        @Override
        public void writeStartObject() throws IOException {
            yamlGenerator.writeStartObject();
        }

        @Override
        public void writeEndObject() throws IOException {
            yamlGenerator.writeEndObject();
        }

        @Override
        public void writeFieldName(String name) throws IOException {
            yamlGenerator.writeFieldName(name);
        }

        @Override
        public void writeString(String text) throws IOException {
            yamlGenerator.writeString(text);
        }

        @Override
        public void writeString(char[] text, int offset, int len) throws IOException {
            yamlGenerator.writeString(text, offset, len);
        }

        @Override
        public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
            yamlGenerator.writeUTF8String(text, offset, length);
        }

        @Override
        public void writeBinary(byte[] data, int offset, int len) throws IOException {
            yamlGenerator.writeBinary(data, offset, len);
        }

        @Override
        public void writeBinary(byte[] data) throws IOException {
            yamlGenerator.writeBinary(data);
        }

        @Override
        public void writeNumber(int v) throws IOException {
            yamlGenerator.writeNumber(v);
        }

        @Override
        public void writeNumber(long v) throws IOException {
            yamlGenerator.writeNumber(v);
        }

        @Override
        public void writeNumber(double d) throws IOException {
            yamlGenerator.writeNumber(d);
        }

        @Override
        public void writeNumber(float f) throws IOException {
            yamlGenerator.writeNumber(f);
        }

        @Override
        public void writeNumber(BigInteger bi) throws IOException {
            yamlGenerator.writeNumber(bi);
        }

        @Override
        public void writeNumber(BigDecimal bd) throws IOException {
            yamlGenerator.writeNumber(bd);
        }

        @Override
        public void writeBoolean(boolean b) throws IOException {
            yamlGenerator.writeBoolean(b);
        }

        @Override
        public void writeNull() throws IOException {
            yamlGenerator.writeNull();
        }

        @Override
        public void writeStringField(String fieldName, String value) throws IOException {
            yamlGenerator.writeStringField(fieldName, value);
        }

        @Override
        public void writeBooleanField(String fieldName, boolean value) throws IOException {
            yamlGenerator.writeBooleanField(fieldName, value);
        }

        @Override
        public void writeNullField(String fieldName) throws IOException {
            yamlGenerator.writeNullField(fieldName);
        }

        @Override
        public void writeNumberField(String fieldName, int value) throws IOException {
            yamlGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, long value) throws IOException {
            yamlGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, double value) throws IOException {
            yamlGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, float value) throws IOException {
            yamlGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, BigInteger value) throws IOException {
            yamlGenerator.writeFieldName(fieldName);
            yamlGenerator.writeNumber(value);
        }

        @Override
        public void writeNumberField(String fieldName, BigDecimal value) throws IOException {
            yamlGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeBinaryField(String fieldName, byte[] data) throws IOException {
            yamlGenerator.writeBinaryField(fieldName, data);
        }

        @Override
        public void writeArrayFieldStart(String fieldName) throws IOException {
            yamlGenerator.writeArrayFieldStart(fieldName);
        }

        @Override
        public void writeObjectFieldStart(String fieldName) throws IOException {
            yamlGenerator.writeObjectFieldStart(fieldName);
        }

        @Override
        public void writeRawField(String fieldName, byte[] content, OutputStream outputStream) throws IOException {
            yamlGenerator.writeRaw(",\"");
            yamlGenerator.writeRaw(fieldName);
            yamlGenerator.writeRaw("\":");
            flush();
            outputStream.write(content);
        }

        @Override
        public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream outputStream)
                throws IOException {
            yamlGenerator.writeRaw(",\"");
            yamlGenerator.writeRaw(fieldName);
            yamlGenerator.writeRaw("\":");
            flush();
            outputStream.write(content, offset, length);
        }

        public void writeRawField(String fieldName, BytesReference content, OutputStream outputStream) throws IOException {
            yamlGenerator.writeRaw(",\"");
            yamlGenerator.writeRaw(fieldName);
            yamlGenerator.writeRaw("\":");
            flush();
            content.streamOutput(outputStream);
        }

        @Override
        public void writeValue(XContentBuilder builder) throws IOException {
            yamlGenerator.writeRawValue(builder.string());
        }

        @Override
        public void copy(XContentBuilder builder, OutputStream outputStream) throws IOException {
            flush();
            if (builder instanceof DefaultXContentBuilder) {
                DefaultXContentBuilder xContentBuilder = (DefaultXContentBuilder) builder;
                xContentBuilder.bytes().streamOutput(outputStream);
            }
        }

        @Override
        public void copyCurrentStructure(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            XContentHelper.copyCurrentStructure(this, parser);
        }

        @Override
        public void flush() throws IOException {
            yamlGenerator.flush();
        }

        @Override
        public void close() throws IOException {
            if (yamlGenerator.isClosed()) {
                return;
            }
            yamlGenerator.close();
        }
    }
}
