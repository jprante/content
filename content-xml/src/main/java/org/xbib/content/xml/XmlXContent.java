package org.xbib.content.xml;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.core.DefaultXContentBuilder;
import org.xbib.content.XContentGenerator;
import org.xbib.content.XContentParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * A XML content implementation using Jackson XML data format.
 */
public class XmlXContent implements XContent {

    private static XmlXContent xmlXContent;

    private final XmlFactory xmlFactory;

    /**
     * Public constructor, used by {@link java.util.ServiceLoader}.
     */
    public XmlXContent() {
        this(XmlXParams.createXmlFactory(XmlXParams.createXMLInputFactory(), XmlXParams.createXMLOutputFactory()));
    }

    private XmlXContent(XmlFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
    }

    public static XContentBuilder contentBuilder() throws IOException {
        XContentBuilder builder = DefaultXContentBuilder.builder(xmlXContent());
        if (builder instanceof DefaultXContentBuilder) {
            DefaultXContentBuilder xContentBuilder = (DefaultXContentBuilder) builder;
            if (xContentBuilder.generator() instanceof XmlXContentGenerator) {
                ((XmlXContentGenerator) xContentBuilder.generator()).setParams(XmlXParams.getDefaultParams());
            }
        }
        return builder;
    }

    public static XContentBuilder contentBuilder(OutputStream outputStream) throws IOException {
        XContentBuilder builder = DefaultXContentBuilder.builder(xmlXContent(), outputStream);
        if (builder instanceof DefaultXContentBuilder) {
            DefaultXContentBuilder xContentBuilder = (DefaultXContentBuilder) builder;
            if (xContentBuilder.generator() instanceof XmlXContentGenerator) {
                ((XmlXContentGenerator) xContentBuilder.generator()).setParams(XmlXParams.getDefaultParams());
            }
        }
        return builder;
    }

    public static XContentBuilder contentBuilder(XmlXParams params) throws IOException {
        XContentBuilder builder = DefaultXContentBuilder.builder(xmlXContent(params.getXmlFactory()));
        if (builder instanceof DefaultXContentBuilder) {
            DefaultXContentBuilder xContentBuilder = (DefaultXContentBuilder) builder;
            if (xContentBuilder.generator() instanceof XmlXContentGenerator) {
                ((XmlXContentGenerator) xContentBuilder.generator()).setParams(params);
            }
        }
        return builder;
    }

    public static XContentBuilder contentBuilder(XmlXParams params, OutputStream outputStream) throws IOException {
        XContentBuilder builder = DefaultXContentBuilder.builder(xmlXContent(params.getXmlFactory()), outputStream);
        if (builder instanceof DefaultXContentBuilder) {
            DefaultXContentBuilder xContentBuilder = (DefaultXContentBuilder) builder;
            if (xContentBuilder.generator() instanceof XmlXContentGenerator) {
                ((XmlXContentGenerator) xContentBuilder.generator()).setParams(params);
            }
        }
        return builder;
    }

    public static XmlXContent xmlXContent() {
        if (xmlXContent == null) {
            xmlXContent = new XmlXContent(XmlXParams.createXmlFactory(XmlXParams.createXMLInputFactory(),
                    XmlXParams.createXMLOutputFactory()));
        }
        return xmlXContent;
    }

    public static XmlXContent xmlXContent(XmlFactory xmlFactory) {
        if (xmlXContent == null) {
            xmlXContent = new XmlXContent(xmlFactory);
        }
        return xmlXContent;
    }

    @Override
    public String name() {
        return "xml";
    }

    @Override
    public XContentGenerator createGenerator(OutputStream outputStream) throws IOException {
        return new XmlXContentGenerator(xmlFactory.createGenerator(outputStream, JsonEncoding.UTF8));
    }

    @Override
    public XContentGenerator createGenerator(Writer writer) throws IOException {
        return new XmlXContentGenerator(xmlFactory.createGenerator(writer));
    }

    @Override
    public XContentParser createParser(String content) throws IOException {
        return new XmlXContentParser(xmlFactory.createParser(content.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public XContentParser createParser(InputStream is) throws IOException {
        return new XmlXContentParser(xmlFactory.createParser(is));
    }

    @Override
    public XContentParser createParser(byte[] data) throws IOException {
        return new XmlXContentParser(xmlFactory.createParser(data));
    }

    @Override
    public XContentParser createParser(byte[] data, int offset, int length) throws IOException {
        return new XmlXContentParser(xmlFactory.createParser(data, offset, length));
    }

    @Override
    public XContentParser createParser(Reader reader) throws IOException {
        return new XmlXContentParser(xmlFactory.createParser(reader));
    }

    @Override
    public boolean isXContent(byte[] bytes, int offset, int len) {
        int length = Math.min(len, 20);
        if (length == 0) {
            return false;
        }
        byte first = bytes[offset];
        return length > 2 && first == '<' && bytes[offset + 1] == '?' && bytes[offset + 2] == 'x';
    }
}
