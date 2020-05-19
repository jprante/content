package org.xbib.content.xml.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.xbib.content.resource.NamespaceContext;
import org.xbib.content.xml.XmlNamespaceContext;
import org.xbib.content.xml.util.ToQName;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.XMLEventConsumer;

/**
 * Write JSON stream to XML stream. This is realized by transforming
 * Jackson stream events to StaX events. You need a root element to wrap
 * the JSON stream into.
 */
public class JsonXmlStreamer {

    private final JsonFactory jsonFactory;

    private final XMLEventFactory eventFactory;

    private final XMLOutputFactory outputFactory;

    private QName root;

    private NamespaceContext context;

    private final Deque<QName> elements;

    public JsonXmlStreamer() {
        context = XmlNamespaceContext.newInstance();
        root = new QName("root");
        eventFactory = XMLEventFactory.newInstance();
        outputFactory = XMLOutputFactory.newInstance();
        outputFactory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
        jsonFactory = new JsonFactory();
        elements = new ArrayDeque<>();
    }

    public JsonXmlStreamer root(QName root) {
        this.root = root;
        return this;
    }

    public JsonXmlStreamer context(NamespaceContext context) {
        this.context = context;
        return this;
    }

    public XMLEventWriter openWriter(OutputStream out) throws XMLStreamException {
        return openWriter(out, "UTF-8");
    }

    public XMLEventWriter openWriter(OutputStream out, String encoding)
            throws XMLStreamException {
        return outputFactory.createXMLEventWriter(out, encoding);
    }

    public XMLEventWriter openWriter(Writer writer) throws XMLStreamException {
        return outputFactory.createXMLEventWriter(writer);
    }

    public void writeXMLProcessingInstruction(XMLEventConsumer consumer, String encoding)
            throws XMLStreamException {
        // this encoding processing instruction also disables ISO-8859-1 entity generation of the event factory
        consumer.add(eventFactory.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"" + encoding + "\""));
    }

    public void writeStylesheetInstruction(XMLEventConsumer consumer, String stylesheet)
            throws XMLStreamException {
        consumer.add(eventFactory.createProcessingInstruction("xml-stylesheet",
                "type=\"text/xsl\" href=\"" + stylesheet + "\""));
    }

    public void toXML(InputStream in, Writer writer) throws XMLStreamException, IOException {
        toXML(in, openWriter(writer));
        writer.flush();
    }

    public void toXML(InputStream in, OutputStream out)
            throws XMLStreamException, IOException {
        toXML(in, openWriter(out));
        out.flush();
    }

    public void toXML(Reader in, Writer out)
            throws XMLStreamException, IOException {
        toXML(in, openWriter(out));
        out.flush();
    }

    public void toXML(InputStream in, XMLEventConsumer consumer)
            throws XMLStreamException, IOException {
        toXML(new InputStreamReader(in, StandardCharsets.UTF_8), consumer);
    }

    public void toXML(Reader in, XMLEventConsumer consumer)
            throws XMLStreamException, IOException {
        JsonParser parser = jsonFactory.createParser(in);
        JsonToken token = parser.nextToken();
        // first token must be a START_OBJECT token
        if (token != JsonToken.START_OBJECT) {
            throw new IOException("JSON first token is not START_OBJECT");
        }
        QName qname = root;
        boolean namespaceDecls = true;
        try {
            writeXMLProcessingInstruction(consumer, "UTF-8");
            while (token != null) {
                switch (token) {
                    case START_OBJECT:
                        consumer.add(eventFactory.createStartElement(qname, null, null));
                        if (namespaceDecls) {
                            if (!context.getNamespaces().containsKey(qname.getPrefix())) {
                                consumer.add(eventFactory.createNamespace(qname.getPrefix(), qname.getNamespaceURI()));
                            }
                            for (String prefix : context.getNamespaces().keySet()) {
                                String namespaceURI = context.getNamespaceURI(prefix);
                                consumer.add(eventFactory.createNamespace(prefix, namespaceURI));
                            }
                            namespaceDecls = false;
                        }
                        elements.push(qname);
                        break;
                    case END_OBJECT:
                        qname = elements.pop();
                        consumer.add(eventFactory.createEndElement(qname, null));
                        break;
                    case START_ARRAY:
                        elements.push(qname);
                        break;
                    case END_ARRAY:
                        qname = elements.pop();
                        break;
                    case FIELD_NAME:
                        qname = ToQName.toQName(root, context, parser.getCurrentName());
                        break;
                    case VALUE_STRING:
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT:
                    case VALUE_NULL:
                    case VALUE_TRUE:
                    case VALUE_FALSE:
                        if (parser.getCurrentName() != null) {
                            qname = ToQName.toQName(root, context, parser.getCurrentName());
                        }
                        String text = parser.getText();
                        int len = text.length();
                        if (len > 0) {
                            consumer.add(eventFactory.createStartElement(qname, null, null));
                            consumer.add(eventFactory.createCharacters(text));
                            consumer.add(eventFactory.createEndElement(qname, null));
                        }
                        break;
                    default:
                        throw new IOException("unknown JSON token: " + token);
                }
                token = parser.nextToken();
            }
        } catch (JsonParseException e) {
            // Illegal character ((CTRL-CHAR, code 0)): only regular white space (\r, \n, \t) is allowed between tokens
            throw new IOException(e);
        } finally {
            if (consumer instanceof XMLEventWriter) {
                ((XMLEventWriter) consumer).flush();
            }
        }
    }

}
