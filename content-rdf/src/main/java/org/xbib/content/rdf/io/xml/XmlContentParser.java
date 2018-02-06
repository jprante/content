package org.xbib.content.rdf.io.xml;

import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.rdf.RdfContentParser;
import org.xbib.content.rdf.RdfContentType;
import org.xbib.content.rdf.StandardRdfContentType;
import org.xbib.content.rdf.util.NormalizeEolFilter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * An XML reader for parsing XML into triples.
 * @param <P> parameter type
 */
public class XmlContentParser<P extends RdfContentParams> implements RdfContentParser<P> {

    private final Reader reader;

    private RdfContentBuilder<P> builder;

    private XmlHandler<P> handler;

    private boolean namespaces = true;

    private boolean validate = false;

    public XmlContentParser(InputStream in) {
        this(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public XmlContentParser(Reader reader) {
        this.reader = new NormalizeEolFilter(reader, System.getProperty("line.separator"), true);
    }

    @Override
    public RdfContentType contentType() {
        return StandardRdfContentType.XML;
    }

    public XmlHandler<P> getHandler() {
        return handler;
    }

    public XmlContentParser<P> setHandler(XmlHandler<P> handler) {
        this.handler = handler;
        return this;
    }

    public XmlContentParser<P> setValidate(boolean validate) {
        this.validate = validate;
        return this;
    }

    public XmlContentParser<P> setNamespaces(boolean namespaces) {
        this.namespaces = namespaces;
        return this;
    }

    public XmlContentParser<P> builder(RdfContentBuilder<P> builder) {
        this.builder = builder;
        return this;
    }

    @Override
    public XmlContentParser<P> parse() throws IOException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            parse(xmlReader, new InputSource(reader));
        } catch (SAXException | ParserConfigurationException ex) {
            throw new IOException(ex);
        }
        return this;
    }

    private XmlContentParser<P> parse(XMLReader reader, InputSource source) throws IOException, SAXException {
        if (handler != null) {
            if (builder != null) {
                handler.setBuilder(builder);
            }
            reader.setContentHandler(handler);
        }
        reader.parse(source);
        return this;
    }
}
