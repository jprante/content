package org.xbib.content;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 */
public abstract class AbstractXContentGenerator implements XContentGenerator {

    protected XContentGenerator generator;

    public AbstractXContentGenerator setGenerator(XContentGenerator generator) {
        this.generator = generator;
        return this;
    }

    @Override
    public void writeStartArray() throws IOException {
        generator.writeStartArray();
    }

    @Override
    public void writeEndArray() throws IOException {
        generator.writeEndArray();
    }

    @Override
    public void writeStartObject() throws IOException {
        generator.writeStartObject();
    }

    @Override
    public void writeEndObject() throws IOException {
        generator.writeEndObject();
    }

    @Override
    public void writeFieldName(String name) throws IOException {
        generator.writeFieldName(name);
    }

    @Override
    public void writeFieldName(XContentString name) throws IOException {
        generator.writeFieldName(name);
    }

    @Override
    public void writeString(String text) throws IOException {
        generator.writeString(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException {
        generator.writeString(text, offset, len);
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
        generator.writeUTF8String(text, offset, length);
    }

    @Override
    public void writeBinary(byte[] data, int offset, int len) throws IOException {
        generator.writeBinary(data, offset, len);
    }

    @Override
    public void writeBinary(byte[] data) throws IOException {
        generator.writeBinary(data);
    }

    @Override
    public void writeNumber(int v) throws IOException {
        generator.writeNumber(v);
    }

    @Override
    public void writeNumber(long v) throws IOException {
        generator.writeNumber(v);
    }

    @Override
    public void writeNumber(double d) throws IOException {
        generator.writeNumber(d);
    }

    @Override
    public void writeNumber(float f) throws IOException {
        generator.writeNumber(f);
    }

    @Override
    public void writeNumber(BigInteger bi) throws IOException {
        generator.writeNumber(bi);
    }

    @Override
    public void writeNumber(BigDecimal bd) throws IOException {
        generator.writeNumber(bd);
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        generator.writeBoolean(b);
    }

    @Override
    public void writeNull() throws IOException {
        generator.writeNull();
    }

    @Override
    public void writeStringField(String fieldName, String value) throws IOException {
        generator.writeStringField(fieldName, value);
    }

    @Override
    public void writeStringField(XContentString fieldName, String value) throws IOException {
        generator.writeFieldName(fieldName);
    }

    @Override
    public void writeBooleanField(String fieldName, boolean value) throws IOException {
        generator.writeBooleanField(fieldName, value);
    }

    @Override
    public void writeBooleanField(XContentString fieldName, boolean value) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeBoolean(value);
    }

    @Override
    public void writeNullField(String fieldName) throws IOException {
        generator.writeNullField(fieldName);
    }

    @Override
    public void writeNumberField(String fieldName, int value) throws IOException {
        generator.writeNumberField(fieldName, value);
    }

    @Override
    public void writeNumberField(XContentString fieldName, int value) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeNumber(value);
    }

    @Override
    public void writeNumberField(String fieldName, long value) throws IOException {
        generator.writeNumberField(fieldName, value);
    }

    @Override
    public void writeNumberField(XContentString fieldName, long value) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeNumber(value);
    }

    @Override
    public void writeNumberField(String fieldName, double value) throws IOException {
        generator.writeNumberField(fieldName, value);
    }

    @Override
    public void writeNumberField(XContentString fieldName, double value) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeNumber(value);
    }

    @Override
    public void writeNumberField(String fieldName, float value) throws IOException {
        generator.writeNumberField(fieldName, value);
    }

    @Override
    public void writeNumberField(XContentString fieldName, float value) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeNumber(value);
    }

    @Override
    public void writeNumberField(String fieldName, BigInteger value) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeNumber(value);
    }

    @Override
    public void writeNumberField(XContentString fieldName, BigInteger value) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeNumber(value);
    }

    @Override
    public void writeNumberField(String fieldName, BigDecimal value) throws IOException {
        generator.writeNumberField(fieldName, value);
    }

    @Override
    public void writeNumberField(XContentString fieldName, BigDecimal value) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeNumber(value);
    }

    @Override
    public void writeBinaryField(String fieldName, byte[] data) throws IOException {
        generator.writeBinaryField(fieldName, data);
    }

    @Override
    public void writeBinaryField(XContentString fieldName, byte[] data) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeBinary(data);
    }

    @Override
    public void writeArrayFieldStart(String fieldName) throws IOException {
        generator.writeArrayFieldStart(fieldName);
    }

    @Override
    public void writeArrayFieldStart(XContentString fieldName) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeStartArray();
    }

    @Override
    public void writeObjectFieldStart(String fieldName) throws IOException {
        generator.writeObjectFieldStart(fieldName);
    }

    @Override
    public void writeObjectFieldStart(XContentString fieldName) throws IOException {
        generator.writeFieldName(fieldName);
        generator.writeStartObject();
    }

    @Override
    public void copy(XContentBuilder builder, OutputStream outputStream) throws IOException {
        flush();
        builder.bytes().streamOutput(outputStream);
    }
}
