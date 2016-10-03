package org.xbib.content.rdf;

import org.xbib.content.rdf.io.ntriple.NTripleContent;
import org.xbib.content.rdf.io.ntriple.NTripleContentParams;
import org.xbib.content.rdf.io.rdfxml.RdfXmlContent;
import org.xbib.content.rdf.io.rdfxml.RdfXmlContentParams;
import org.xbib.content.rdf.io.turtle.TurtleContent;
import org.xbib.content.rdf.io.turtle.TurtleContentParams;
import org.xbib.content.rdf.io.xml.XmlContent;
import org.xbib.content.rdf.io.xml.XmlContentParams;

import java.io.IOException;

/**
 *
 */
public class RdfXContentFactory {

    private RdfXContentFactory() {
    }

    public static RdfContentBuilder<NTripleContentParams> ntripleBuilder() throws IOException {
        return NTripleContent.contentBuilder(NTripleContentParams.N_TRIPLE_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<NTripleContentParams> ntripleBuilder(NTripleContentParams params) throws IOException {
        return NTripleContent.contentBuilder(params);
    }

    public static RdfContentBuilder<RdfXmlContentParams> rdfXmlBuilder() throws IOException {
        return RdfXmlContent.contentBuilder(RdfXmlContentParams.RDF_XML_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<RdfXmlContentParams> rdfXmlBuilder(RdfXmlContentParams params) throws IOException {
        return RdfXmlContent.contentBuilder(params);
    }

    public static RdfContentBuilder<TurtleContentParams> turtleBuilder() throws IOException {
        return TurtleContent.contentBuilder(TurtleContentParams.TURTLE_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<TurtleContentParams> turtleBuilder(TurtleContentParams params) throws IOException {
        return TurtleContent.contentBuilder(params);
    }

    public static RdfContentBuilder<XmlContentParams> xmlBuilder() throws IOException {
        return XmlContent.contentBuilder(XmlContentParams.XML_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<XmlContentParams> xmlBuilder(XmlContentParams params) throws IOException {
        return XmlContent.contentBuilder(params);
    }

    public static RdfContentBuilder<RdfXContentParams> rdfXContentBuilder() throws IOException {
        return RdfXContent.contentBuilder(RdfXContentParams.RDF_X_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<RdfXContentParams> rdfXContentBuilder(RdfXContentParams params) throws IOException {
        return RdfXContent.contentBuilder(params);
    }

    public static RdfContentBuilder<RouteRdfXContentParams> routeRdfXContentBuilder() throws IOException {
        return RouteRdfXContent.contentBuilder(RouteRdfXContentParams.ROUTE_RDF_X_CONTENT_PARAMS);
    }

    public static RdfContentBuilder<RouteRdfXContentParams> routeRdfXContentBuilder(RouteRdfXContentParams params)
            throws IOException {
        return RouteRdfXContent.contentBuilder(params);
    }

}
