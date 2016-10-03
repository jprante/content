package org.xbib.content.rdf;

import org.xbib.content.rdf.io.json.JsonContent;
import org.xbib.content.rdf.io.json.JsonContentParams;
import org.xbib.content.rdf.io.ntriple.NTripleContent;
import org.xbib.content.rdf.io.ntriple.NTripleContentParams;
import org.xbib.content.rdf.io.rdfxml.RdfXmlContent;
import org.xbib.content.rdf.io.rdfxml.RdfXmlContentParams;
import org.xbib.content.rdf.io.turtle.TurtleContent;
import org.xbib.content.rdf.io.turtle.TurtleContentParams;
import org.xbib.content.rdf.io.xml.XmlContent;
import org.xbib.content.rdf.io.xml.XmlContentParams;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class RdfContentFactory {

    public static RdfContentBuilder<NTripleContentParams> ntripleBuilder() throws IOException {
        return NTripleContent.contentBuilder(NTripleContentParams.N_TRIPLE_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<NTripleContentParams> ntripleBuilder(NTripleContentParams params) throws IOException {
        return NTripleContent.contentBuilder(params);
    }

    public static RdfContentBuilder<NTripleContentParams> ntripleBuilder(OutputStream out) throws IOException {
        return NTripleContent.contentBuilder(out, NTripleContentParams.N_TRIPLE_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<NTripleContentParams> ntripleBuilder(OutputStream out, NTripleContentParams params)
            throws IOException {
        return NTripleContent.contentBuilder(out, params);
    }

    public static RdfContentBuilder<RdfXmlContentParams> rdfXmlBuilder() throws IOException {
        return RdfXmlContent.contentBuilder(RdfXmlContentParams.RDF_XML_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<RdfXmlContentParams> rdfXmlBuilder(RdfXmlContentParams params) throws IOException {
        return RdfXmlContent.contentBuilder(params);
    }

    public static RdfContentBuilder<RdfXmlContentParams> rdfXmlBuilder(OutputStream out) throws IOException {
        return RdfXmlContent.contentBuilder(out, RdfXmlContentParams.RDF_XML_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<RdfXmlContentParams> rdfXmlBuilder(OutputStream out, RdfXmlContentParams params)
            throws IOException {
        return RdfXmlContent.contentBuilder(out, params);
    }

    public static RdfContentBuilder<TurtleContentParams> turtleBuilder() throws IOException {
        return TurtleContent.contentBuilder(TurtleContentParams.TURTLE_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<TurtleContentParams> turtleBuilder(TurtleContentParams params) throws IOException {
        return TurtleContent.contentBuilder(params);
    }

    public static RdfContentBuilder<TurtleContentParams> turtleBuilder(OutputStream out) throws IOException {
        return TurtleContent.contentBuilder(out, TurtleContentParams.TURTLE_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<TurtleContentParams> turtleBuilder(OutputStream out, TurtleContentParams params)
            throws IOException {
        return TurtleContent.contentBuilder(out, TurtleContentParams.TURTLE_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<XmlContentParams> xmlBuilder() throws IOException {
        return XmlContent.contentBuilder(XmlContentParams.XML_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<XmlContentParams> xmlBuilder(XmlContentParams params) throws IOException {
        return XmlContent.contentBuilder(params);
    }

    public static RdfContentBuilder<JsonContentParams> jsonBuilder() throws IOException {
        return JsonContent.contentBuilder(JsonContentParams.JSON_CONTENT_PARAMS);
    }
}
