package org.xbib.content.rdf.io.source;

import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import org.xbib.content.rdf.io.sink.XmlSink;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

final class XmlSource extends AbstractSource<XmlSink> {

    private XMLReader xmlReader;

    XmlSource(XmlSink sink) {
        super(sink);
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setFeature("http://xml.org/sax/features/namespaces", true);
            parserFactory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            parserFactory.setFeature("http://xml.org/sax/features/validation", false);
            parserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            parserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            parserFactory.setXIncludeAware(false);
            SAXParser saxParser = parserFactory.newSAXParser();
            setXmlReader(saxParser.getXMLReader());
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void process(Reader reader, String mimeType, String baseUri) throws IOException {
        try {
            xmlReader.setContentHandler(sink);
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", sink);
            sink.setBaseUri(baseUri);
            xmlReader.parse(new InputSource(reader));
        } catch (SAXException e) {
            try {
                sink.endDocument();
            } catch (SAXException e2) {
                throw new IOException(e2);
            }
            throw new IOException(e);
        }
    }

    @Override
    public void process(InputStream inputStream, String mimeType, String baseUri) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            process(reader, mimeType, baseUri);
        }
    }

    public void setXmlReader(XMLReader xmlReader) throws SAXException, ParserConfigurationException {
        this.xmlReader = xmlReader;
    }
}
