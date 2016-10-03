package org.xbib.content.xml;

import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentParser;
import org.xbib.content.XContentService;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 *
 */
public class XmlXContentHelper {

    public static Map<String, Object> convertFromXmlToMap(Reader reader) {
        try {
            return XmlXContent.xmlXContent().createParser(reader).mapOrderedAndClose();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse content to map", e);
        }
    }

    public static Map<String, Object> convertFromXmlToMap(String data) {
        try {
            return XmlXContent.xmlXContent().createParser(data).mapOrderedAndClose();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse content to map", e);
        }
    }

    public static String convertToXml(byte[] data, int offset, int length) throws IOException {
        return convertToXml(XmlXParams.getDefaultParams(), data, offset, length, false);
    }

    public static String convertToXml(byte[] data, int offset, int length, boolean prettyprint) throws IOException {
        return convertToXml(XmlXParams.getDefaultParams(), data, offset, length, prettyprint);
    }

    public static String convertToXml(XmlXParams params, byte[] data, int offset, int length) throws IOException {
        return convertToXml(params, data, offset, length, false);
    }

    public static String convertToXml(XmlXParams params, byte[] data, int offset, int length, boolean prettyPrint)
            throws IOException {
        XContent xContent = XContentService.xContent(data, offset, length);
        try (XContentParser parser = xContent.createParser(data, offset, length);
             XContentBuilder builder = XmlXContent.contentBuilder(params)) {
            parser.nextToken();
            if (prettyPrint) {
                builder.prettyPrint();
            }
            builder.copyCurrentStructure(parser);
            return builder.string();
        }
    }
}
