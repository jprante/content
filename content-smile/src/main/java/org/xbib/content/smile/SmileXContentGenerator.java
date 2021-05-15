package org.xbib.content.smile;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.fasterxml.jackson.dataformat.smile.SmileParser;
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

public class SmileXContentGenerator extends AbstractXContentGenerator {

    private final SmileGeneratorDelegate delegate;

    public SmileXContentGenerator(SmileGenerator generator) {
        this.delegate = new SmileGeneratorDelegate(generator);
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
        try (SmileParser parser = SmileXContent.smileFactory().createParser(content)) {
            parser.nextToken();
            ((JsonGenerator) generator).copyCurrentStructure(parser);
        }
    }

    @Override
    public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream bos) throws IOException {
        writeFieldName(fieldName);
        try (SmileParser parser = SmileXContent.smileFactory().createParser(content, offset, length)) {
            parser.nextToken();
            ((JsonGenerator) generator).copyCurrentStructure(parser);
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

    private static class SmileGeneratorDelegate implements XContentGenerator {

        private final SmileGenerator smileGenerator;

        private SmileGeneratorDelegate(SmileGenerator smileGenerator) {
            this.smileGenerator = smileGenerator;
        }

        @Override
        public XContent content() {
            return SmileXContent.smileContent();
        }

        @Override
        public void usePrettyPrint() {
            smileGenerator.useDefaultPrettyPrinter();
        }

        @Override
        public void writeStartArray() throws IOException {
            smileGenerator.writeStartArray();
        }

        @Override
        public void writeEndArray() throws IOException {
            smileGenerator.writeEndArray();
        }

        @Override
        public void writeStartObject() throws IOException {
            smileGenerator.writeStartObject();
        }

        @Override
        public void writeEndObject() throws IOException {
            smileGenerator.writeEndObject();
        }

        @Override
        public void writeFieldName(String name) throws IOException {
            smileGenerator.writeFieldName(name);
        }

        @Override
        public void writeString(String text) throws IOException {
            smileGenerator.writeString(text);
        }

        @Override
        public void writeString(char[] text, int offset, int len) throws IOException {
            smileGenerator.writeString(text, offset, len);
        }

        @Override
        public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
            smileGenerator.writeUTF8String(text, offset, length);
        }

        @Override
        public void writeBinary(byte[] data, int offset, int len) throws IOException {
            smileGenerator.writeBinary(data, offset, len);
        }

        @Override
        public void writeBinary(byte[] data) throws IOException {
            smileGenerator.writeBinary(data);
        }

        @Override
        public void writeNumber(int v) throws IOException {
            smileGenerator.writeNumber(v);
        }

        @Override
        public void writeNumber(long v) throws IOException {
            smileGenerator.writeNumber(v);
        }

        @Override
        public void writeNumber(double d) throws IOException {
            smileGenerator.writeNumber(d);
        }

        @Override
        public void writeNumber(float f) throws IOException {
            smileGenerator.writeNumber(f);
        }

        @Override
        public void writeNumber(BigInteger bi) throws IOException {
            smileGenerator.writeNumber(bi);
        }

        @Override
        public void writeNumber(BigDecimal bd) throws IOException {
            smileGenerator.writeNumber(bd);
        }

        @Override
        public void writeBoolean(boolean b) throws IOException {
            smileGenerator.writeBoolean(b);
        }

        @Override
        public void writeNull() throws IOException {
            smileGenerator.writeNull();
        }

        @Override
        public void writeStringField(String fieldName, String value) throws IOException {
            smileGenerator.writeStringField(fieldName, value);
        }

        @Override
        public void writeBooleanField(String fieldName, boolean value) throws IOException {
            smileGenerator.writeBooleanField(fieldName, value);
        }

        @Override
        public void writeNullField(String fieldName) throws IOException {
            smileGenerator.writeNullField(fieldName);
        }

        @Override
        public void writeNumberField(String fieldName, int value) throws IOException {
            smileGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, long value) throws IOException {
            smileGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, double value) throws IOException {
            smileGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, float value) throws IOException {
            smileGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, BigInteger value) throws IOException {
            smileGenerator.writeFieldName(fieldName);
            smileGenerator.writeNumber(value);
        }

        @Override
        public void writeNumberField(String fieldName, BigDecimal value) throws IOException {
            smileGenerator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeBinaryField(String fieldName, byte[] data) throws IOException {
            smileGenerator.writeBinaryField(fieldName, data);
        }

        @Override
        public void writeArrayFieldStart(String fieldName) throws IOException {
            smileGenerator.writeArrayFieldStart(fieldName);
        }

        @Override
        public void writeObjectFieldStart(String fieldName) throws IOException {
            smileGenerator.writeObjectFieldStart(fieldName);
        }

        @Override
        public void writeRawField(String fieldName, byte[] content, OutputStream outputStream) throws IOException {
            smileGenerator.writeRaw(",\"");
            smileGenerator.writeRaw(fieldName);
            smileGenerator.writeRaw("\":");
            flush();
            outputStream.write(content);
        }

        @Override
        public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream outputStream)
                throws IOException {
            smileGenerator.writeRaw(",\"");
            smileGenerator.writeRaw(fieldName);
            smileGenerator.writeRaw("\":");
            flush();
            outputStream.write(content, offset, length);
        }

        public void writeRawField(String fieldName, BytesReference content, OutputStream outputStream) throws IOException {
            smileGenerator.writeRaw(",\"");
            smileGenerator.writeRaw(fieldName);
            smileGenerator.writeRaw("\":");
            flush();
            content.streamOutput(outputStream);
        }

        @Override
        public void writeValue(XContentBuilder builder) throws IOException {
            smileGenerator.writeRawValue(builder.string());
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
            smileGenerator.flush();
        }

        @Override
        public void close() throws IOException {
            if (smileGenerator.isClosed()) {
                return;
            }
            smileGenerator.close();
        }
    }
}
