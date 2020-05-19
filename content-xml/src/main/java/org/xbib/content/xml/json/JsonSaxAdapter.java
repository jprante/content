package org.xbib.content.xml.json;

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.FIELD_NAME;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.START_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.VALUE_NULL;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.xbib.content.resource.NamespaceContext;
import org.xbib.content.xml.util.ToQName;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

/**
 * Converts JSON to SAX events. It can be used either directly
 * <code>
 * ContentHandler ch = ...;
 * JsonSaxAdapter service = new JsonSaxAdapter("{\"name\":\"value\"}", ch);
 * service.parse();
 * </code>
 * <code>
 * Transformer transformer = TransformerFactory.newInstance().newTransformer();
 * InputSource source = new InputSource(...);
 * Result result = ...;
 * transformer.transform(new SAXSource(new JsonXmlReader(),source), result);
 * </code>
 */
public class JsonSaxAdapter {

    private static final AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();

    private static final JsonFactory factory = new JsonFactory();

    private final JsonParser jsonParser;

    private final ContentHandler contentHandler;

    private QName root = new QName("root");

    private NamespaceContext context;

    public JsonSaxAdapter(Reader reader, ContentHandler contentHandler) throws IOException {
        this(factory.createParser(reader), contentHandler);
    }

    public JsonSaxAdapter(JsonParser jsonParser, ContentHandler contentHandler) {
        this.jsonParser = jsonParser;
        this.contentHandler = contentHandler;
        contentHandler.setDocumentLocator(new DocumentLocator());
    }

    public JsonSaxAdapter root(QName root) {
        this.root = root;
        return this;
    }

    public JsonSaxAdapter context(NamespaceContext context) {
        this.context = context;
        return this;
    }

    /**
     * Method parses JSON and emits SAX events.
     *
     * @throws IOException  if parse input/output fails
     * @throws SAXException if parse fails
     */
    public void parse() throws IOException, SAXException {
        jsonParser.nextToken();
        contentHandler.startDocument();
        writeNamespaceDeclarations(context);
        if (root != null) {
            contentHandler.startElement(root.getNamespaceURI(), root.getLocalPart(),
                    root.getPrefix() + ":" + root.getLocalPart(), EMPTY_ATTRIBUTES);
        }
        parseObject();
        if (root != null) {
            contentHandler.endElement(root.getNamespaceURI(), root.getLocalPart(),
                    root.getPrefix() + ":" + root.getLocalPart());
        }
        contentHandler.endDocument();
    }

    /**
     * Parses generic object.
     *
     * @return number of elements written
     * @throws IOException  if parse input/output fails
     * @throws SAXException if parse object fails
     */
    private int parseObject() throws SAXException, IOException {
        int elementsWritten = 0;
        while (jsonParser.nextToken() != null && jsonParser.getCurrentToken() != END_OBJECT) {
            if (FIELD_NAME.equals(jsonParser.getCurrentToken())) {
                String elementName = jsonParser.getCurrentName();
                jsonParser.nextToken();
                parseElement(elementName);
                elementsWritten++;
            } else {
                throw new JsonParseException(jsonParser, "expected field name, but got " + jsonParser.getCurrentToken(),
                        jsonParser.getCurrentLocation());
            }
        }
        return elementsWritten;
    }

    private void parseElement(final String elementName) throws SAXException, IOException {
        JsonToken currentToken = jsonParser.getCurrentToken();
        if (START_OBJECT.equals(currentToken)) {
            startElement(elementName);
            parseObject();
            endElement(elementName);
        } else if (START_ARRAY.equals(currentToken)) {
            parseArray(elementName);
        } else if (currentToken.isScalarValue() && !isEmptyValue()) {
            startElement(elementName);
            parseValue();
            endElement(elementName);
        }
    }

    private boolean isEmptyValue() throws IOException {
        return (jsonParser.getCurrentToken() == VALUE_NULL) || jsonParser.getText().isEmpty();
    }

    private void parseArray(final String elementName) throws IOException, SAXException {
        while (jsonParser.nextToken() != END_ARRAY && jsonParser.getCurrentToken() != null) {
            parseElement(elementName);
        }
    }

    private void parseValue() throws IOException, SAXException {
        if (jsonParser.getCurrentToken() != VALUE_NULL) {
            String text = jsonParser.getText();
            contentHandler.characters(text.toCharArray(), 0, text.length());
        }
    }

    private void startElement(final String elementName) throws SAXException {
        QName qname = ToQName.toQName(root, context, elementName);
        contentHandler.startElement(qname.getNamespaceURI(),
                qname.getLocalPart(), qname.getPrefix() + ":" + qname.getLocalPart(),
                EMPTY_ATTRIBUTES);
    }

    private void endElement(final String elementName) throws SAXException {
        QName qname = ToQName.toQName(root, context, elementName);
        contentHandler.endElement(qname.getNamespaceURI(),
                qname.getLocalPart(), qname.getPrefix() + ":" + qname.getLocalPart());
    }

    private void writeNamespaceDeclarations(NamespaceContext context) throws SAXException {
        Set<String> keys = new TreeSet<>(context.getNamespaces().keySet());
        if (root != null && !keys.contains(root.getPrefix())) {
            contentHandler.startPrefixMapping(root.getPrefix(), root.getNamespaceURI());
        }
        for (String prefix : keys) {
            contentHandler.startPrefixMapping(prefix, context.getNamespaceURI(prefix));
        }
    }

    private class DocumentLocator implements Locator {

        @Override
        public String getPublicId() {
            Object sourceRef = jsonParser.getCurrentLocation().getSourceRef();
            if (sourceRef != null) {
                return sourceRef.toString();
            } else {
                return "";
            }
        }

        @Override
        public String getSystemId() {
            return getPublicId();
        }

        @Override
        public int getLineNumber() {
            return jsonParser.getCurrentLocation() != null ? jsonParser.getCurrentLocation().getLineNr() : -1;
        }

        @Override
        public int getColumnNumber() {
            return jsonParser.getCurrentLocation() != null ? jsonParser.getCurrentLocation().getColumnNr() : -1;
        }
    }

}
