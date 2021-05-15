package org.xbib.content.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.xbib.content.XContentBuilder;
import org.xbib.content.core.AbstractXContentGenerator;
import org.xbib.content.XContent;
import org.xbib.content.core.DefaultXContentBuilder;
import org.xbib.content.XContentGenerator;
import org.xbib.content.core.XContentHelper;
import org.xbib.content.XContentParser;
import org.xbib.content.io.BytesReference;
import org.xbib.content.xml.util.ISO9075;
import org.xbib.content.xml.util.XMLUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

/**
 * Content generator for XML formatted content.
 */
public class XmlXContentGenerator extends AbstractXContentGenerator {

    private final XmlXContentGeneratorDelegate delegate;

    private final XmlXParams params;

    public XmlXContentGenerator(ToXmlGenerator generator) {
        this(generator, XmlXParams.getDefaultParams());
    }

    public XmlXContentGenerator(ToXmlGenerator generator, XmlXParams params) {
        this.params = params;
        generator.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
        this.delegate = new XmlXContentGeneratorDelegate(generator, params);
        super.setGenerator(delegate);
    }

    public XmlXParams getParams() {
        return params;
    }

    public XmlXContentGenerator setParams(XmlXParams params) throws IOException {
        delegate.setParams(params);
        return this;
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
    public void writeStartObject() throws IOException {
        delegate.writeStartObject();
    }

    @Override
    public void writeFieldName(String name) throws IOException {
        delegate.writeFieldName(name);
    }

    @Override
    public void writeString(String text) throws IOException {
        delegate.writeString(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException {
        delegate.writeString(text, offset, len);
    }
    @Override
    public void writeRawField(String fieldName, byte[] content, OutputStream outputStream) throws IOException {
        delegate.writeRawField(fieldName, content, outputStream);
    }

    @Override
    public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream outputStream)
            throws IOException {
        delegate.writeRawField(fieldName, content, outputStream);
    }

    @Override
    public void writeValue(XContentBuilder builder) throws IOException {
        delegate.writeValue(builder);
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
        delegate.copyCurrentStructure(parser);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    private static class XmlXContentGeneratorDelegate implements XContentGenerator {

        final ToXmlGenerator generator;

        private XmlXParams params;

        private boolean rootUsed = false;

        XmlXContentGeneratorDelegate(ToXmlGenerator xmlGenerator, XmlXParams params) {
            this.generator = xmlGenerator;
            this.params = params;
        }

        public void setParams(XmlXParams params) throws IOException {
            this.params = params;
            try {
                generator.getStaxWriter().setPrefix(params.getRoot().getPrefix(), params.getRoot().getNamespaceURI());
            } catch (XMLStreamException e) {
                throw new IOException(e);
            }
        }

        @Override
        public XContent content() {
            return XmlXContent.xmlXContent();
        }

        @Override
        public void usePrettyPrint() {
            generator.useDefaultPrettyPrinter();
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
            if (!rootUsed) {
                generator.setNextName(params.getRoot());
            }
            generator.writeStartObject();
            if (!rootUsed) {
                try {
                    for (String prefix : params.getNamespaceContext().getNamespaces().keySet()) {
                        String uri = params.getNamespaceContext().getNamespaceURI(prefix);
                        if (uri == null || uri.isEmpty()) {
                            continue;
                        }
                        generator.getStaxWriter().writeNamespace(prefix, uri);
                    }
                } catch (XMLStreamException e) {
                    throw new IOException(e);
                }
                rootUsed = true;
            }
        }

        @Override
        public void writeEndObject() throws IOException {
            generator.writeEndObject();
        }

        @Override
        public void writeFieldName(String name) throws IOException {
            writeFieldNameWithNamespace(name);
        }

        @Override
        public void writeString(String text) throws IOException {
            generator.writeString(XMLUtil.sanitize(text));
        }

        @Override
        public void writeString(char[] text, int offset, int len) throws IOException {
            generator.writeString(XMLUtil.sanitize(new String(text, offset, len)));
        }

        @Override
        public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
            generator.writeUTF8String(text, offset, length);
        }

        @Override
        public void writeBinary(byte[] data, int offset, int len) throws IOException {
            // write base64
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
        public void writeBoolean(boolean state) throws IOException {
            generator.writeBoolean(state);
        }

        @Override
        public void writeNull() throws IOException {
            generator.writeNull();
        }

        @Override
        public void writeStringField(String fieldName, String value) throws IOException {
            generator.writeStringField(fieldName, value);
        }

        public void writeBooleanField(String fieldName, boolean value) throws IOException {
            generator.writeBooleanField(fieldName, value);
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
        public void writeNumberField(String fieldName, long value) throws IOException {
            generator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, double value) throws IOException {
            generator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, float value) throws IOException {
            generator.writeNumberField(fieldName, value);
        }

        @Override
        public void writeNumberField(String fieldName, BigInteger value) throws IOException {
            generator.writeFieldName(fieldName);
            generator.writeNumber(value);
        }

        @Override
        public void writeNumberField(String fieldName, BigDecimal value) throws IOException {
            generator.writeFieldName(fieldName);
            generator.writeNumber(value);
        }

        @Override
        public void writeBinaryField(String fieldName, byte[] data) throws IOException {
            generator.writeBinaryField(fieldName, data);
        }

        @Override
        public void writeArrayFieldStart(String fieldName) throws IOException {
            generator.writeArrayFieldStart(fieldName);
        }

        @Override
        public void writeObjectFieldStart(String fieldName) throws IOException {
            generator.writeObjectFieldStart(fieldName);
        }

        @Override
        public void writeRawField(String fieldName, byte[] content, OutputStream outputStream) throws IOException {
            writeFieldNameWithNamespace(fieldName);
            try (JsonParser parser = params.getXmlFactory().createParser(content)) {
                parser.nextToken();
                params.getXmlFactory().createGenerator(outputStream).copyCurrentStructure(parser);
            }
        }

        public void writeRawField(String fieldName, BytesReference content, OutputStream outputStream) throws IOException {
            writeFieldNameWithNamespace(fieldName);
            try (JsonParser parser = params.getXmlFactory().createParser(content.streamInput())) {
                parser.nextToken();
                params.getXmlFactory().createGenerator(outputStream).copyCurrentStructure(parser);
            }
        }

        @Override
        public void writeRawField(String fieldName, byte[] content, int offset, int length, OutputStream outputStream)
                throws IOException {
            writeFieldNameWithNamespace(fieldName);
            try (JsonParser parser = params.getXmlFactory().createParser(content, offset, length)) {
                parser.nextToken();
                params.getXmlFactory().createGenerator(outputStream).copyCurrentStructure(parser);
            }
        }

        @Override
        public void writeValue(XContentBuilder builder) throws IOException {
            generator.writeRawValue(builder.string());
        }

        @Override
        public void copy(XContentBuilder builder, OutputStream bos) throws IOException {
            flush();
            if (builder instanceof DefaultXContentBuilder) {
                DefaultXContentBuilder xContentBuilder = (DefaultXContentBuilder) builder;
                xContentBuilder.bytes().streamOutput(bos);
            }
        }

        @Override
        public void copyCurrentStructure(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser instanceof XmlXContentParser) {
                generator.copyCurrentStructure(((XmlXContentParser) parser).parser);
            } else {
                XContentHelper.copyCurrentStructure(this, parser);
            }
        }

        @Override
        public void flush() throws IOException {
            generator.flush();
        }

        @Override
        public void close() throws IOException {
            if (generator.isClosed()) {
                return;
            }
            generator.close();
        }

        private void writeFieldNameWithNamespace(String name) throws IOException {
            QName qname = toQualifiedName(params.getNamespaceContext(), name);
            try {
                generator.getStaxWriter().setPrefix(qname.getPrefix(), qname.getNamespaceURI());
            } catch (XMLStreamException e) {
                throw new IOException(e);
            }
            generator.setNextName(qname);
            generator.writeFieldName(qname.getLocalPart());
        }

        private QName toQualifiedName(NamespaceContext context, String string) {
            String name = string;
            if (name.startsWith("_") || name.startsWith("@")) {
                name = name.substring(1);
            }
            name = ISO9075.encode(name);
            int pos = name.indexOf(':');
            String nsPrefix = "";
            String nsURI = context.getNamespaceURI("");
            if (pos > 0) {
                nsPrefix = name.substring(0, pos);
                nsURI = context.getNamespaceURI(nsPrefix);
                if (nsURI == null) {
                    if (params.isFatalNamespaceErrors()) {
                        throw new IllegalArgumentException("unknown namespace prefix: " + nsPrefix);
                    } else {
                        nsURI = "xbib:namespace/" + nsPrefix;
                    }
                }
                name = name.substring(pos + 1);
            }
            return new QName(nsURI, name, nsPrefix);
        }
    }
}
