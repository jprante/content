package org.xbib.content.xml.json;

import org.xbib.content.xml.XmlNamespaceContext;
import org.xbib.content.xml.transform.StylesheetTransformer;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.XMLEventConsumer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;

/**
 * Transform JSON with stylesheets.
 */
public class JsonStylesheet {

    private QName root;

    private XmlNamespaceContext context;

    private StylesheetTransformer transformer;

    private String[] stylesheets;

    public JsonStylesheet() {
        this.root = new QName("root");
    }

    public JsonStylesheet root(QName root) {
        this.root = root;
        return this;
    }

    public JsonStylesheet context(XmlNamespaceContext context) {
        this.context = context;
        return this;
    }

    public JsonStylesheet setTransformer(StylesheetTransformer transformer) {
        this.transformer = transformer;
        return this;
    }

    public JsonStylesheet setStylesheets(String... stylesheets) {
        this.stylesheets = stylesheets;
        return this;
    }

    public JsonStylesheet transform(InputStream in, OutputStream out) throws IOException {
        return transform(in, new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    public JsonStylesheet transform(InputStream in, Writer out) throws IOException {
        if (root == null || context == null || in == null || out == null || transformer == null) {
            return this;
        }
        try {
            JsonXmlReader reader = new JsonXmlReader().root(root).context(context);
            if (stylesheets == null) {
                transformer.setSource(new SAXSource(reader, new InputSource(in)))
                        .setResult(out)
                        .transform();
            } else {
                transformer.setSource(new SAXSource(reader, new InputSource(in)))
                        .setResult(out)
                        .transform(Arrays.asList(stylesheets));
            }
            return this;
        } catch (TransformerException e) {
            throw new IOException(e);
        } finally {
            transformer.close();
        }
    }

    public JsonStylesheet toXML(InputStream in, OutputStream out) throws IOException {
        return toXML(in, new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    public JsonStylesheet toXML(InputStream in, Writer out) throws IOException {
        if (root == null || context == null || in == null || out == null) {
            return this;
        }
        try {
            JsonXmlStreamer jsonXml = new JsonXmlStreamer().root(root).context(context);
            jsonXml.toXML(in, out);
            out.flush();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
        return this;
    }

    public JsonStylesheet toXML(InputStream in, XMLEventConsumer out) throws IOException {
        if (root == null || context == null || in == null || out == null) {
            return this;
        }
        try {
            JsonXmlStreamer jsonXml = new JsonXmlStreamer().root(root).context(context);
            jsonXml.toXML(in, out);
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
        return this;
    }

}
