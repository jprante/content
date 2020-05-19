package org.xbib.content.xml.json;

import org.xbib.content.xml.XmlNamespaceContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Read JSON like SaX.
 *
 * Helper class that can be used for JSON to XML transformation.
 * <pre>
 * Transformer transformer = TransformerFactory.newInstance().newTransformer();
 * InputSource source = new InputSource(...);
 * Result result = ...;
 * transformer.transform(new SAXSource(new JsonXmlReader(namespace),source), result);
 * </pre>
 */
public class JsonXmlReader implements XMLReader {

    private QName root = new QName("root");

    private XmlNamespaceContext context;

    private final Map<String, Boolean> map;

    private ContentHandler contentHandler;

    private EntityResolver entityResolver;

    private DTDHandler dtdHandler;

    private ErrorHandler errorHandler;

    public JsonXmlReader() {
        this.map = new HashMap<>();
    }

    public JsonXmlReader root(QName root) {
        this.root = root;
        return this;
    }

    public JsonXmlReader context(XmlNamespaceContext context) {
        this.context = context;
        return this;
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        map.put(name, value);
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return map.get(name);
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return null;
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        //ignore
    }

    @Override
    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        this.dtdHandler = handler;
    }

    @Override
    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        if (input.getCharacterStream() != null) {
            new JsonSaxAdapter(input.getCharacterStream(), contentHandler)
                    .root(root)
                    .context(context)
                    .parse();
        } else if (input.getByteStream() != null) {
            String encoding = input.getEncoding() != null ? input.getEncoding() : System.getProperty("file.encoding");
            new JsonSaxAdapter(new InputStreamReader(input.getByteStream(), encoding), contentHandler)
                    .root(root)
                    .context(context)
                    .parse();
        }
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        throw new UnsupportedOperationException();
    }
}
