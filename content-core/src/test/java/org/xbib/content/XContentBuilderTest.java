package org.xbib.content;

import static org.xbib.content.json.JsonXContent.contentBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.xbib.content.json.JsonXContent;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 *
 */
public class XContentBuilderTest extends Assert {

    @Test
    public void testCopy() throws IOException {
        XContentBuilder builder = contentBuilder();
        builder.startObject().field("hello", "world").endObject();
        builder.close();

        XContentBuilder builder2 = contentBuilder();
        builder2.copy(builder);
        builder2.close();
        assertEquals(builder2.string(), "{\"hello\":\"world\"}");
    }

    @Test
    public void testCopyList() throws IOException {
        XContentBuilder builder1 = contentBuilder();
        builder1.startObject().field("hello", "world").endObject();
        builder1.close();
        XContentBuilder builder2 = contentBuilder();
        builder2.startObject().field("hello", "world").endObject();
        builder2.close();

        XContentBuilder builder = contentBuilder();
        builder.startObject().startArray("list");
        builder.copy(Arrays.asList(builder1, builder2));
        builder.endArray().endObject();
        builder.close();

        assertEquals(builder.string(), "{\"list\":[{\"hello\":\"world\"},{\"hello\":\"world\"}]}");
    }

    @Test
    public void testBuilderAsXContentList() throws IOException {
        XContentBuilder builder1 = contentBuilder();
        builder1.startObject().field("hello", "world").endObject();
        builder1.close();

        XContentBuilder builder2 = contentBuilder();
        builder2.startObject().field("hello", "world").endObject();
        builder2.close();

        XContentBuilder builder = contentBuilder();
        builder.startObject().array("list", builder1, builder2).endObject();
        builder.close();

        assertEquals(builder.string(), "{\"list\":[{\"hello\":\"world\"},{\"hello\":\"world\"}]}");
    }

    @Test
    public void testBigDecimal() throws IOException {
        XContentBuilder builder = contentBuilder();
        builder.startObject().field("value", new BigDecimal("57683974591503.00")).endObject();
        assertEquals(builder.string(), "{\"value\":57683974591503.00}");

        XContent content = XContentService.xContent(builder.string());
        Map<String, Object> map = content
                .createParser(builder.string())
                .losslessDecimals(true)
                .mapAndClose();
        assertEquals(map.toString(), "{value=57683974591503.00}");
        assertEquals(map.get("value").getClass().toString(), "class java.math.BigDecimal");

        map = content
                .createParser(builder.string())
                .losslessDecimals(false)
                .mapAndClose();
        assertEquals(map.toString(), "{value=5.7683974591503E13}");
        assertEquals(map.get("value").getClass().toString(), "class java.lang.Double");
    }

    @Test
    public void testBigInteger() throws IOException {
        XContentBuilder builder = contentBuilder();
        builder.startObject().field("value", new BigInteger("1234567891234567890123456789")).endObject();
        assertEquals(builder.string(), "{\"value\":1234567891234567890123456789}");

        XContent content = XContentService.xContent(builder.string());
        Map<String, Object> map = content
                .createParser(builder.string())
                .losslessDecimals(true)
                .mapAndClose();
        assertEquals(map.toString(), "{value=1234567891234567890123456789}");
        assertEquals(map.get("value").getClass().toString(), "class java.math.BigInteger");

        map = content
                .createParser(builder.string())
                .losslessDecimals(false)
                .mapAndClose();
        assertEquals(map.toString(), "{value=1234567891234567890123456789}");
        assertEquals(map.get("value").getClass().toString(), "class java.math.BigInteger");
    }

    @Test
    public void testDateFormatting() throws IOException {
        XContentBuilder builder = contentBuilder();
        Date d = new Date();
        d.setTime(1398175311488L);
        builder.startObject().field("value", d).endObject();
        Map<String, Object> map = JsonXContent.jsonContent()
                .createParser(builder.string())
                .losslessDecimals(false)
                .mapAndClose();
        assertEquals("{value=2014-04-22T14:01:51.488Z}", map.toString());
    }

    @Test
    public void testBase16() throws IOException {
        XContentBuilder builder = contentBuilder();
        builder.startObject().field("value", "4AC3B67267").endObject();
        assertEquals(builder.string(), "{\"value\":\"4AC3B67267\"}");

        XContent content = XContentService.xContent(builder.string());
        Map<String, Object> map = content
                .createParser(builder.string())
                .enableBase16Checks(true)
                .mapAndClose();
        assertEquals(new String((byte[]) map.get("value")), "Jörg");

        map = content.createParser(builder.string())
                .enableBase16Checks(false)
                .mapAndClose();
        assertEquals(map.toString(), "{value=4AC3B67267}");
    }

    @Test(expected = NullPointerException.class)
    public void testNullKey() throws IOException {
        XContentBuilder builder = contentBuilder();
        builder.field((String) null);
    }

}
