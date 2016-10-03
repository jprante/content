package org.xbib.content;

import org.xbib.content.io.BytesReference;
import org.xbib.content.json.JsonXContent;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class XContentHelper {

    private static final Logger logger = Logger.getLogger(XContentHelper.class.getName());

    private static final String UNKNOWN_FORMAT = "unknown format";

    private XContentHelper() {
    }

    public static XContentParser createParser(BytesReference bytes) throws IOException {
        XContent content = XContentService.xContent(bytes);
        if (content == null) {
            throw new IOException(UNKNOWN_FORMAT);
        }
        return content.createParser(bytes.streamInput());
    }

    public static XContentParser createParser(byte[] data, int offset, int length) throws IOException {
        return XContentService.xContent(data, offset, length).createParser(data, offset, length);
    }

    public static Map<String, Object> convertFromJsonToMap(Reader reader) {
        try {
            return JsonXContent.jsonContent().createParser(reader).mapOrderedAndClose();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse content to map", e);
        }
    }

    public static Map<String, Object> convertToMap(String data) {
        try {
            XContent content = XContentService.xContent(data);
            return content.createParser(data).mapOrderedAndClose();
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to parse content to map", e);
        }
    }

    public static Map<String, Object> convertToMap(BytesReference bytes, boolean ordered) {
        XContent content = XContentService.xContent(bytes);
        if (content == null) {
            throw new IllegalArgumentException(UNKNOWN_FORMAT);
        }
        try {
            XContentParser parser = content.createParser(bytes.streamInput());
            if (ordered) {
                return parser.mapOrderedAndClose();
            } else {
                return parser.mapAndClose();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to parse content to map", e);
        }
    }

    public static Map<String, Object> convertToMap(byte[] data, boolean ordered) throws IOException {
        return convertToMap(data, 0, data.length, ordered);
    }

    public static Map<String, Object> convertToMap(byte[] data, int offset, int length, boolean ordered) throws IOException {
        XContent content = XContentService.xContent(data, offset, length);
        XContentParser parser = content.createParser(data, offset, length);
        if (ordered) {
            return parser.mapOrderedAndClose();
        } else {
            return parser.mapAndClose();
        }
    }

    public static String convertToJson(BytesReference bytes, boolean reformatJson, boolean prettyPrint) throws IOException {
        XContent xContent = XContentService.xContent(bytes);
        if (xContent == null) {
            throw new IOException(UNKNOWN_FORMAT);
        }
        if (xContent == JsonXContent.jsonContent() && !reformatJson) {
            return bytes.toUtf8();
        }
        try (XContentParser parser = xContent.createParser(bytes.streamInput());
             XContentBuilder builder = XContentBuilder.builder(JsonXContent.jsonContent())) {
            parser.nextToken();
            if (prettyPrint) {
                builder.prettyPrint();
            }
            builder.copyCurrentStructure(parser);
            return builder.string();
        }
    }

    public static String convertToJson(byte[] data, int offset, int length, boolean reformatJson, boolean prettyPrint)
            throws IOException {
        XContent xContent = XContentService.xContent(data, offset, length);
        if (xContent == JsonXContent.jsonContent() && !reformatJson) {
            return new String(data, offset, length, StandardCharsets.UTF_8);
        }
        try (XContentParser parser = xContent.createParser(data, offset, length);
             XContentBuilder builder = XContentBuilder.builder(JsonXContent.jsonContent())) {
            parser.nextToken();
            if (prettyPrint) {
                builder.prettyPrint();
            }
            builder.copyCurrentStructure(parser);
            return builder.string();
        }
    }

    public static void copyCurrentStructure(XContentGenerator generator, XContentParser parser) throws IOException {
        XContentParser.Token t = parser.currentToken();
        if (t == XContentParser.Token.FIELD_NAME) {
            generator.writeFieldName(parser.currentName());
            t = parser.nextToken();
        }
        switch (t) {
            case START_ARRAY:
                generator.writeStartArray();
                while (parser.nextToken() != XContentParser.Token.END_ARRAY) {
                    copyCurrentStructure(generator, parser);
                }
                generator.writeEndArray();
                break;
            case START_OBJECT:
                generator.writeStartObject();
                while (parser.nextToken() != XContentParser.Token.END_OBJECT) {
                    copyCurrentStructure(generator, parser);
                }
                generator.writeEndObject();
                break;
            default:
                copyCurrentEvent(generator, parser);
        }
    }

    private static void copyCurrentEvent(XContentGenerator generator, XContentParser parser) throws IOException {
        switch (parser.currentToken()) {
            case START_OBJECT:
                generator.writeStartObject();
                break;
            case END_OBJECT:
                generator.writeEndObject();
                break;
            case START_ARRAY:
                generator.writeStartArray();
                break;
            case END_ARRAY:
                generator.writeEndArray();
                break;
            case FIELD_NAME:
                generator.writeFieldName(parser.currentName());
                break;
            case VALUE_STRING:
                if (parser.hasTextCharacters()) {
                    generator.writeString(parser.textCharacters(), parser.textOffset(), parser.textLength());
                } else {
                    if (parser.isBase16Checks()) {
                        try {
                            generator.writeBinary(parseBase16(parser.text()));
                        } catch (IllegalArgumentException e) {
                            logger.log(Level.FINE, e.getMessage(), e);
                            generator.writeString(parser.text());
                        }
                    } else {
                        generator.writeString(parser.text());
                    }
                }
                break;
            case VALUE_NUMBER:
                switch (parser.numberType()) {
                    case INT:
                        generator.writeNumber(parser.intValue());
                        break;
                    case LONG:
                        generator.writeNumber(parser.longValue());
                        break;
                    case FLOAT:
                        generator.writeNumber(parser.floatValue());
                        break;
                    case DOUBLE:
                        if (parser.isLosslessDecimals()) {
                            generator.writeNumber(parser.bigDecimalValue());
                        } else {
                            generator.writeNumber(parser.doubleValue());
                        }
                        break;
                    case BIG_INTEGER:
                        generator.writeNumber(parser.bigIntegerValue());
                        break;
                    case BIG_DECIMAL:
                        generator.writeNumber(parser.bigDecimalValue());
                        break;
                    default:
                        break;
                }
                break;
            case VALUE_BOOLEAN:
                generator.writeBoolean(parser.booleanValue());
                break;
            case VALUE_NULL:
                generator.writeNull();
                break;
            case VALUE_EMBEDDED_OBJECT:
                generator.writeBinary(parser.binaryValue());
                break;
            default:
                break;
        }
    }

    static byte[] parseBase16(String s) {
        final int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hex string needs to be of even length: " + s);
        }
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hex string: " + s);
            }
            out[i / 2] = (byte) (h * 16 + l);
        }
        return out;
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }

}
