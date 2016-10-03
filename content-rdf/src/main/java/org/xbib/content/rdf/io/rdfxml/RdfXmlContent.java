package org.xbib.content.rdf.io.rdfxml;

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
public class RdfXmlContent implements RdfContent<RdfXmlContentParams> {

    private static final RdfXmlContent RDF_XML_CONTENT = new RdfXmlContent();

    private RdfXmlContent() {
    }

    public static RdfXmlContent rdfXmlContent() {
        return RDF_XML_CONTENT;
    }

    public static RdfContentBuilder<RdfXmlContentParams> contentBuilder(RdfXmlContentParams params) throws IOException {
        return new RdfContentBuilder<>(RDF_XML_CONTENT, params);
    }

    public static RdfContentBuilder<RdfXmlContentParams> contentBuilder(OutputStream out, RdfXmlContentParams params)
            throws IOException {
        return new RdfContentBuilder<>(RDF_XML_CONTENT, params, out);
    }

    @Override
    public StandardRdfContentType type() {
        return StandardRdfContentType.RDFXML;
    }

    @Override
    public RdfContentGenerator<RdfXmlContentParams> createGenerator(OutputStream os) throws IOException {
        return new RdfXmlContentGenerator(os);
    }

    @Override
    public RdfContentParser createParser(InputStream in) throws IOException {
        return new RdfXmlContentParser<RdfXmlContentParams>(in);
    }
}
