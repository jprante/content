package org.xbib.content.rdf.io.source;

import org.xbib.content.rdf.io.sink.XmlSink;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

final class XmlSource extends AbstractSource<XmlSink> {

    private XMLReader xmlReader = null;

    XmlSource(XmlSink sink) {
        super(sink);
    }

    public static XMLReader getDefaultXmlReader() throws SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        SAXParser parser = factory.newSAXParser();
        return parser.getXMLReader();
    }

    @Override
    public void process(Reader reader, String mimeType, String baseUri) throws IOException {
        try {
            initXmlReader();
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException("can not instantinate XMLReader", e);
        }
        try {
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
        try (Reader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"))) {
            process(reader, mimeType, baseUri);
        }
    }

    private void initXmlReader() throws SAXException, ParserConfigurationException {
        if (xmlReader == null) {
            xmlReader = getDefaultXmlReader();
        }
        xmlReader.setContentHandler(sink);
        xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", sink);
    }

    public void setXmlReader(XMLReader xmlReader) throws SAXException, ParserConfigurationException {
        if (xmlReader == null) {
            this.xmlReader = getDefaultXmlReader();
        } else {
            this.xmlReader = xmlReader;
        }
    }
}
