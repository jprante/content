package org.xbib.content.core;

import org.xbib.content.ToXContent;
import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentGenerator;
import org.xbib.content.XContentParser;
import org.xbib.content.io.BytesReference;
import org.xbib.content.io.BytesStreamOutput;
import org.xbib.content.util.geo.GeoPoint;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DefaultXContentBuilder implements XContentBuilder {

    private final OutputStream outputStream;
    private final XContentGenerator generator;

    /**
     * Constructs a new builder using the provided xcontent and an OutputStream. Make sure
     * to call {@link #close()} when the builder is done with.
     * @param xContent content
     * @param outputStream output stream
     * @throws IOException if construction fails
     */
    public DefaultXContentBuilder(XContent xContent, OutputStream outputStream) throws IOException {
        this.outputStream = outputStream;
        this.generator = xContent.createGenerator(outputStream);
    }

    /**
     * Constructs a new builder using a fresh {@link org.xbib.content.io.BytesStreamOutput}.
     * @param xContent the content
     * @return content builder
     * @throws IOException exception
     */
    public static XContentBuilder builder(XContent xContent) throws IOException {
        return new DefaultXContentBuilder(xContent, new BytesStreamOutput());
    }

    /**
     * Constructs a new content builder.
     * @param xContent the content
     * @param out out
     * @return content builder
     * @throws IOException if build fails
     */
    public static XContentBuilder builder(XContent xContent, OutputStream out) throws IOException {
        return new DefaultXContentBuilder(xContent, out);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        return builder.copy(this);
    }

    @Override
    public XContentBuilder prettyPrint() {
        generator.usePrettyPrint();
        return this;
    }

    @Override
    public XContentBuilder rawField(String fieldName, byte[] content, int offset, int length) throws IOException {
        generator.writeRawField(fieldName, content, offset, length, outputStream);
        return this;
    }

    @Override
    public XContentBuilder copy(XContentBuilder builder) throws IOException {
        generator.copy(builder, outputStream);
        return this;
    }

    @Override
    public XContentBuilder copy(List<XContentBuilder> builder) throws IOException {
        for (int i = 0; i < builder.size(); i++) {
            if (i > 0) {
                outputStream.write(',');
            }
            generator.copy(builder.get(i), outputStream);
        }
        return this;
    }

    @Override
    public void copyCurrentStructure(XContentParser parser) throws IOException {
        generator.copyCurrentStructure(parser);
    }

    @Override
    public XContentBuilder field(String name, ToXContent xContent) throws IOException {
        field(name);
        xContent.toXContent(this, ToXContent.EMPTY_PARAMS);
        return this;
    }

    @Override
    public XContentBuilder field(String name, ToXContent xContent, ToXContent.Params params) throws IOException {
        field(name);
        xContent.toXContent(this, params);
        return this;
    }

    @Override
    public XContentBuilder field(String name) throws IOException {
        Objects.requireNonNull(name);
        generator.writeFieldName(name);
        return this;
    }

    @Override
    public XContentBuilder field(String name, boolean value) throws IOException {
        field(name);
        generator.writeBoolean(value);
        return this;
    }

    @Override
    public XContentBuilder field(String name, Boolean value) throws IOException {
        field(name);
        generator.writeBoolean(value);
        return this;
    }

    @Override
    public XContentBuilder field(String name, Integer value) throws IOException {
        field(name);
        if (value == null) {
            generator.writeNull();
        } else {
            generator.writeNumber(value);
        }
        return this;
    }

    @Override
    public XContentBuilder field(String name, int value) throws IOException {
        field(name);
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder field(String name, Long value) throws IOException {
        field(name);
        if (value == null) {
            generator.writeNull();
        } else {
            generator.writeNumber(value);
        }
        return this;
    }

    @Override
    public XContentBuilder field(String name, long value) throws IOException {
        field(name);
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder field(String name, Float value) throws IOException {
        field(name);
        if (value == null) {
            generator.writeNull();
        } else {
            generator.writeNumber(value);
        }
        return this;
    }

    @Override
    public XContentBuilder field(String name, float value) throws IOException {
        field(name);
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder field(String name, Double value) throws IOException {
        field(name);
        if (value == null) {
            generator.writeNull();
        } else {
            generator.writeNumber(value);
        }
        return this;
    }

    @Override
    public XContentBuilder field(String name, double value) throws IOException {
        field(name);
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder field(String name, BigInteger value) throws IOException {
        field(name);
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder field(String name, BigDecimal value) throws IOException {
        field(name);
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder field(String name, String value) throws IOException {
        field(name);
        if (value == null) {
            generator.writeNull();
        } else {
            generator.writeString(value);
        }
        return this;
    }

    @Override
    public XContentBuilder field(String name, Object value) throws IOException {
        field(name);
        writeValue(value);
        return this;
    }

    @Override
    public XContentBuilder field(String name, byte[] value, int offset, int length) throws IOException {
        field(name);
        generator.writeBinary(value, offset, length);
        return this;
    }

    @Override
    public XContentBuilder field(String name, char[] value, int offset, int length) throws IOException {
        field(name);
        if (value == null) {
            generator.writeNull();
        } else {
            generator.writeString(value, offset, length);
        }
        return this;
    }

    public XContentBuilder field(String name, Map<String, Object> value) throws IOException {
        field(name);
        value(value);
        return this;
    }

    public XContentBuilder field(String name, Iterable<?> value) throws IOException {
        startArray(name);
        for (Object o : value) {
            value(o);
        }
        endArray();
        return this;
    }

    public XContentBuilder field(String name, String... value) throws IOException {
        startArray(name);
        for (String o : value) {
            value(o);
        }
        endArray();
        return this;
    }

    public XContentBuilder field(String name, Object... value) throws IOException {
        startArray(name);
        for (Object o : value) {
            value(o);
        }
        endArray();
        return this;
    }

    @Override
    public XContentBuilder field(String name, int... value) throws IOException {
        startArray(name);
        for (Object o : value) {
            value(o);
        }
        endArray();
        return this;
    }

    @Override
    public XContentBuilder field(String name, long... value) throws IOException {
        startArray(name);
        for (Object o : value) {
            value(o);
        }
        endArray();
        return this;
    }

    @Override
    public XContentBuilder field(String name, float... value) throws IOException {
        startArray(name);
        for (Object o : value) {
            value(o);
        }
        endArray();
        return this;
    }

    @Override
    public XContentBuilder field(String name, double... value) throws IOException {
        startArray(name);
        for (Object o : value) {
            value(o);
        }
        endArray();
        return this;
    }

    @Override
    public XContentBuilder fieldIfNotNull(String name, Boolean value) throws IOException {
        if (value != null) {
            field(name);
            generator.writeBoolean(value);
        }
        return this;
    }

    @Override
    public XContentBuilder fieldIfNotNull(String name, String value) throws IOException {
        if (value != null) {
            field(name);
            generator.writeString(value);
        }
        return this;
    }

    @Override
    public XContentBuilder fieldIfNotNull(String name, Integer value) throws IOException {
        if (value != null) {
            field(name);
            generator.writeNumber(value);
        }
        return this;
    }

    @Override
    public XContentBuilder fieldIfNotNull(String name, Object value) throws IOException {
        if (value != null) {
            return field(name, value);
        }
        return this;
    }

    @Override
    public XContentBuilder fieldIfNotNull(String name, Long value) throws IOException {
        if (value != null) {
            field(name);
            generator.writeNumber(value);
        }
        return this;
    }

    @Override
    public XContentBuilder fieldIfNotNull(String name, Float value) throws IOException {
        if (value != null) {
            field(name);
            generator.writeNumber(value);
        }
        return this;
    }

    @Override
    public XContentBuilder fieldIfNotNull(String name, Double value) throws IOException {
        if (value != null) {
            field(name);
            generator.writeNumber(value);
        }
        return this;
    }

    @Override
    public XContentBuilder nullField(String name) throws IOException {
        generator.writeNullField(name);
        return this;
    }

    @Override
    public XContentBuilder nullValue() throws IOException {
        generator.writeNull();
        return this;
    }

    @Override
    public XContentBuilder value(boolean value) throws IOException {
        generator.writeBoolean(value);
        return this;
    }

    @Override
    public XContentBuilder value(Boolean value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        return value(value.booleanValue());
    }

    @Override
    public XContentBuilder value(int value) throws IOException {
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder value(Integer value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        return value(value.intValue());
    }

    @Override
    public XContentBuilder value(long value) throws IOException {
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder value(Long value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        return value(value.longValue());
    }

    @Override
    public XContentBuilder value(Float value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        return value(value.floatValue());
    }

    @Override
    public XContentBuilder value(float value) throws IOException {
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder value(double value) throws IOException {
        generator.writeNumber(value);
        return this;
    }

    @Override
    public XContentBuilder value(Double value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        return value(value.doubleValue());
    }

    @Override
    public XContentBuilder value(BigInteger bi) throws IOException {
        generator.writeNumber(bi);
        return this;
    }

    @Override
    public XContentBuilder value(BigDecimal bd) throws IOException {
        generator.writeNumber(bd);
        return this;
    }

    @Override
    public XContentBuilder value(String value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        generator.writeString(value);
        return this;
    }

    @Override
    public XContentBuilder value(XContentBuilder builder) throws IOException {
        generator.writeValue(builder);
        return this;
    }

    @Override
    public XContentBuilder value(Object value) throws IOException {
        writeValue(value);
        return this;
    }

    @Override
    public XContentBuilder value(byte[] value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        generator.writeBinary(value);
        return this;
    }

    @Override
    public XContentBuilder value(byte[] value, int offset, int length) throws IOException {
        if (value == null) {
            return nullValue();
        }
        generator.writeBinary(value, offset, length);
        return this;
    }

    @Override
    public XContentBuilder value(Map<String, Object> map) throws IOException {
        if (map == null) {
            return nullValue();
        }
        writeMap(map);
        return this;
    }

    @Override
    public XContentBuilder value(Iterable<?> value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        startArray();
        for (Object o : value) {
            value(o);
        }
        endArray();
        return this;
    }

    @Override
    public XContentBuilder startObject(String name) throws IOException {
        field(name);
        startObject();
        return this;
    }

    @Override
    public XContentBuilder startObject() throws IOException {
        generator.writeStartObject();
        return this;
    }

    @Override
    public XContentBuilder endObject() throws IOException {
        generator.writeEndObject();
        return this;
    }

    @Override
    public XContentBuilder array(String name, Collection<?> values) throws IOException {
        startArray(name);
        for (Object value : values) {
            value(value);
        }
        endArray();
        return this;
    }

    @Override
    public XContentBuilder array(String name, String... values) throws IOException {
        startArray(name);
        for (String value : values) {
            value(value);
        }
        endArray();
        return this;
    }

    @Override
    public XContentBuilder array(String name, Object... values) throws IOException {
        startArray(name);
        for (Object value : values) {
            value(value);
        }
        endArray();
        return this;
    }

    @Override
    public XContentBuilder startArray(String name) throws IOException {
        field(name);
        startArray();
        return this;
    }

    @Override
    public XContentBuilder startArray() throws IOException {
        generator.writeStartArray();
        return this;
    }

    @Override
    public XContentBuilder endArray() throws IOException {
        generator.writeEndArray();
        return this;
    }

    @Override
    public XContentBuilder map(Map<String, Object> map) throws IOException {
        if (map == null) {
            return nullValue();
        }
        writeMap(map);
        return this;
    }

    @Override
    public XContentBuilder timeseriesMap(Map<Instant, Object> map) throws IOException {
        if (map == null) {
            return nullValue();
        }
        writeInstantMap(map);
        return this;
    }

    @Override
    public XContentBuilder flatMap(Map<String, String> map) throws IOException {
        if (map == null) {
            return nullValue();
        }
        writeFlatMap(map);
        return this;
    }

    public XContentBuilder field(String name, BytesReference value) throws IOException {
        return field(name, value.toBytes(), 0, value.length());
    }

    public XContentBuilder value(BytesReference value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        byte[] b = value.toBytes();
        generator.writeBinary(b, 0, b.length);
        return this;
    }

    @Override
    public String string() throws IOException {
        return bytes().toUtf8();
    }

    @Override
    public void flush() throws IOException {
        generator.flush();
    }

    @Override
    public void close() throws IOException {
        generator.close();
    }

    public XContent content() {
        return generator.content();
    }

    public XContentGenerator generator() {
        return generator;
    }

    public BytesReference bytes() throws IOException {
        generator.close();
        return ((BytesStreamOutput) outputStream).bytes();
    }

    private void writeMap(Map<String, Object> map) throws IOException {
        generator.writeStartObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            field(entry.getKey());
            Object value = entry.getValue();
            if (value == null) {
                generator.writeNull();
            } else {
                writeValue(value);
            }
        }
        generator.writeEndObject();
    }

    private void writeInstantMap(Map<Instant, Object> map) throws IOException {
        generator.writeStartObject();
        for (Map.Entry<Instant, Object> entry : map.entrySet()) {
            Instant instant = entry.getKey();
            ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            field(zdt.format(DateTimeFormatter.ISO_INSTANT));
            Object value = entry.getValue();
            if (value == null) {
                generator.writeNull();
            } else {
                writeValue(value);
            }
        }
        generator.writeEndObject();
    }

    private void writeFlatMap(Map<String, String> map) throws IOException {
        generator.writeStartObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            field(entry.getKey());
            Object value = entry.getValue();
            if (value == null) {
                generator.writeNull();
            } else {
                writeValue(value);
            }
        }
        generator.writeEndObject();
    }

    @SuppressWarnings("unchecked")
    private void writeValue(Object value) throws IOException {
        if (value == null) {
            generator.writeNull();
            return;
        }
        Class<?> type = value.getClass();
        if (type == String.class) {
            generator.writeString((String) value);
        } else if (type == Integer.class) {
            generator.writeNumber((Integer) value);
        } else if (type == Long.class) {
            generator.writeNumber((Long) value);
        } else if (type == Float.class) {
            generator.writeNumber((Float) value);
        } else if (type == Double.class) {
            generator.writeNumber((Double) value);
        } else if (type == Short.class) {
            generator.writeNumber((Short) value);
        } else if (type == Boolean.class) {
            generator.writeBoolean((Boolean) value);
        } else if (type == GeoPoint.class) {
            generator.writeStartObject();
            generator.writeNumberField("lat", ((GeoPoint) value).lat());
            generator.writeNumberField("lon", ((GeoPoint) value).lon());
            generator.writeEndObject();
        } else if (value instanceof Map) {
            writeMap((Map<String, Object>) value);
        } else if (value instanceof Iterable) {
            generator.writeStartArray();
            for (Object v : (Iterable<?>) value) {
                if (v != value) {
                    writeValue(v);
                }
            }
            generator.writeEndArray();
        } else if (value instanceof Object[]) {
            generator.writeStartArray();
            for (Object v : (Object[]) value) {
                if (v != value) {
                    writeValue(v);
                }
            }
            generator.writeEndArray();
        } else if (type == byte[].class) {
            generator.writeBinary((byte[]) value);
        } else if (value instanceof Instant) {
            Instant instant = (Instant) value;
            ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            generator.writeString(zdt.format(DateTimeFormatter.ISO_INSTANT));
        } else if (value instanceof Date) {
            Date date = (Date) value;
            Instant instant = Instant.ofEpochMilli(date.getTime());
            ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            generator.writeString(zdt.format(DateTimeFormatter.ISO_INSTANT));
        } else if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            Instant instant = Instant.ofEpochMilli(calendar.getTime().getTime());
            ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            generator.writeString(zdt.format(DateTimeFormatter.ISO_INSTANT));
        } else if (value instanceof BytesReference) {
            BytesReference bytes = (BytesReference) value;
            byte[] b = bytes.toBytes();
            generator.writeBinary(b, 0, b.length);
        } else if (value instanceof DefaultXContentBuilder) {
            value((DefaultXContentBuilder) value);
        } else if (value instanceof ToXContent) {
            ((ToXContent) value).toXContent(this, ToXContent.EMPTY_PARAMS);
        } else if (value instanceof double[]) {
            generator.writeStartArray();
            for (double v : (double[]) value) {
                generator.writeNumber(v);
            }
            generator.writeEndArray();
        } else if (value instanceof long[]) {
            generator.writeStartArray();
            for (long v : (long[]) value) {
                generator.writeNumber(v);
            }
            generator.writeEndArray();
        } else if (value instanceof int[]) {
            generator.writeStartArray();
            for (int v : (int[]) value) {
                generator.writeNumber(v);
            }
            generator.writeEndArray();
        } else if (value instanceof float[]) {
            generator.writeStartArray();
            for (float v : (float[]) value) {
                generator.writeNumber(v);
            }
            generator.writeEndArray();
        } else if (value instanceof short[]) {
            generator.writeStartArray();
            for (float v : (short[]) value) {
                generator.writeNumber(v);
            }
            generator.writeEndArray();
        } else {
            // if this is a "value" object, like enum, DistanceUnit, ..., just toString it
            // yea, it can be misleading when toString a Java class, but really, jackson should be used in that case
            generator.writeString(value.toString());
        }
    }
}
