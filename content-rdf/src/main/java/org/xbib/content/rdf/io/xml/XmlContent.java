package org.xbib.content.rdf.io.xml;

import org.xbib.content.rdf.RdfContent;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentGenerator;
import org.xbib.content.rdf.RdfContentParser;
import org.xbib.content.rdf.StandardRdfContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class XmlContent implements RdfContent<XmlContentParams> {

    private static final XmlContent XML_CONTENT = new XmlContent();

    private XmlContent() {
    }

    public static XmlContent xmlContent() {
        return XML_CONTENT;
    }

    public static RdfContentBuilder<XmlContentParams> contentBuilder(XmlContentParams params) throws IOException {
        return new RdfContentBuilder<>(XML_CONTENT, params);
    }

    public static RdfContentBuilder<XmlContentParams> contentBuilder(OutputStream out, XmlContentParams params)
            throws IOException {
        return new RdfContentBuilder<>(XML_CONTENT, params, out);
    }

    @Override
    public StandardRdfContentType type() {
        return StandardRdfContentType.XML;
    }

    @Override
    public RdfContentGenerator<XmlContentParams> createGenerator(OutputStream outputStream) throws IOException {
        return new XmlContentGenerator(outputStream);
    }

    @Override
    public RdfContentParser createParser(InputStream inputStream) throws IOException {
        return new XmlContentParser<>(inputStream);
    }
}
