package org.xbib.content.xml;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.xbib.content.XContent;
import org.xbib.content.XContentBuilder;
import org.xbib.content.XContentGenerator;
import org.xbib.content.XContentParser;
import org.xbib.content.io.BytesReference;

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

    private XmlFactory xmlFactory;

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
        XContentBuilder builder = XContentBuilder.builder(xmlXContent());
        if (builder.generator() instanceof XmlXContentGenerator) {
            ((XmlXContentGenerator) builder.generator()).setParams(XmlXParams.getDefaultParams());
        }
        return builder;
    }

    public static XContentBuilder contentBuilder(OutputStream outputStream) throws IOException {
        XContentBuilder builder = XContentBuilder.builder(xmlXContent(), outputStream);
        if (builder.generator() instanceof XmlXContentGenerator) {
            ((XmlXContentGenerator) builder.generator()).setParams(XmlXParams.getDefaultParams());
        }
        return builder;
    }

    public static XContentBuilder contentBuilder(XmlXParams params) throws IOException {
        XContentBuilder builder = XContentBuilder.builder(xmlXContent(params.getXmlFactory()));
        if (builder.generator() instanceof XmlXContentGenerator) {
            ((XmlXContentGenerator) builder.generator()).setParams(params);
        }
        return builder;
    }

    public static XContentBuilder contentBuilder(XmlXParams params, OutputStream outputStream) throws IOException {
        XContentBuilder builder = XContentBuilder.builder(xmlXContent(params.getXmlFactory()), outputStream);
        if (builder.generator() instanceof XmlXContentGenerator) {
            ((XmlXContentGenerator) builder.generator()).setParams(params);
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
    public XContentParser createParser(BytesReference bytes) throws IOException {
        return createParser(bytes.streamInput());
    }

    @Override
    public boolean isXContent(BytesReference bytes) {
        int length = bytes.length() < 20 ? bytes.length() : 20;
        if (length == 0) {
            return false;
        }
        byte first = bytes.get(0);
        return length > 2 && first == '<' && bytes.get(1) == '?' && bytes.get(2) == 'x';
    }
}
