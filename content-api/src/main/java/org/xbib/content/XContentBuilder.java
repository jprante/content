package org.xbib.content;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface XContentBuilder extends ToXContent, Flushable, Closeable {

    XContentBuilder prettyPrint();

    XContentBuilder rawField(String fieldName, byte[] content, int offset, int length) throws IOException;

    XContentBuilder copy(XContentBuilder builder) throws IOException;

    XContentBuilder copy(List<XContentBuilder> builder) throws IOException;

    void copyCurrentStructure(XContentParser parser) throws IOException;

    XContentBuilder field(String name) throws IOException;

    XContentBuilder field(String name, ToXContent xContent) throws IOException;

    XContentBuilder field(String name, ToXContent xContent, ToXContent.Params params) throws IOException;

    XContentBuilder field(String name, boolean value) throws IOException;

    XContentBuilder field(String name, Boolean value) throws IOException;

    XContentBuilder field(String name, int value) throws IOException;

    XContentBuilder field(String name, Integer value) throws IOException;

    XContentBuilder field(String name, long value) throws IOException;

    XContentBuilder field(String name, Long value) throws IOException;

    XContentBuilder field(String name, float value) throws IOException;

    XContentBuilder field(String name, Float value) throws IOException;

    XContentBuilder field(String name, double value) throws IOException;

    XContentBuilder field(String name, Double value) throws IOException;

    XContentBuilder field(String name, BigInteger value) throws IOException;

    XContentBuilder field(String name, BigDecimal value) throws IOException;

    XContentBuilder field(String name, String value) throws IOException;

    XContentBuilder field(String name, Object value) throws IOException;

    XContentBuilder field(String name, int... value) throws IOException;

    XContentBuilder field(String name, long... value) throws IOException;

    XContentBuilder field(String name, double... value) throws IOException;

    XContentBuilder field(String name, float... value) throws IOException;

    XContentBuilder field(String name, byte[] value, int offset, int length) throws IOException;

    XContentBuilder field(String name, char[] value, int offset, int length) throws IOException;

    XContentBuilder nullField(String name) throws IOException;

    XContentBuilder fieldIfNotNull(String name, Boolean value) throws IOException;

    XContentBuilder fieldIfNotNull(String name, Integer value) throws IOException;

    XContentBuilder fieldIfNotNull(String name, Long value) throws IOException;

    XContentBuilder fieldIfNotNull(String name, Float value) throws IOException;

    XContentBuilder fieldIfNotNull(String name, Double value) throws IOException;

    XContentBuilder fieldIfNotNull(String name, String value) throws IOException;

    XContentBuilder fieldIfNotNull(String name, Object value) throws IOException;

    XContentBuilder value(boolean value) throws IOException;

    XContentBuilder value(Boolean value) throws IOException;

    XContentBuilder value(int value) throws IOException;

    XContentBuilder value(Integer value) throws IOException;

    XContentBuilder value(long value) throws IOException;

    XContentBuilder value(Long value) throws IOException;

    XContentBuilder value(float value) throws IOException;

    XContentBuilder value(Float value) throws IOException;

    XContentBuilder value(double value) throws IOException;

    XContentBuilder value(Double value) throws IOException;

    XContentBuilder value(BigInteger value) throws IOException;

    XContentBuilder value(BigDecimal value) throws IOException;

    XContentBuilder value(String value) throws IOException;

    XContentBuilder value(XContentBuilder builder) throws IOException;

    XContentBuilder value(Object value) throws IOException;

    XContentBuilder value(byte[] value) throws IOException;

    XContentBuilder value(byte[] value, int offset, int length) throws IOException;

    XContentBuilder value(Iterable<?> value) throws IOException;

    XContentBuilder value(Map<String, Object> map) throws IOException;

    XContentBuilder nullValue() throws IOException;

    XContentBuilder array(String name, Collection<?> values) throws IOException;

    XContentBuilder array(String name, String... values) throws IOException;

    XContentBuilder array(String name, Object... values) throws IOException;

    XContentBuilder startArray() throws IOException;

    XContentBuilder startArray(String name) throws IOException;

    XContentBuilder endArray() throws IOException;

    XContentBuilder startObject() throws IOException;

    XContentBuilder startObject(String name) throws IOException;

    XContentBuilder endObject() throws IOException;

    XContentBuilder map(Map<String, Object> map) throws IOException;

    XContentBuilder flatMap(Map<String, String> map) throws IOException;

    XContentBuilder timeseriesMap(Map<Instant, Object> map) throws IOException;

    String string() throws IOException;
}
