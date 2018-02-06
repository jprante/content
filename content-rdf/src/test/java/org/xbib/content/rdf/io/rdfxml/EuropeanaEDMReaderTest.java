package org.xbib.content.rdf.io.rdfxml;

import static org.xbib.content.rdf.RdfContentFactory.ntripleBuilder;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xbib.content.rdf.RdfContent;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.internal.DefaultLiteral;
import org.xbib.content.rdf.internal.DefaultRdfGraph;
import org.xbib.content.rdf.internal.DefaultTriple;
import org.xbib.content.rdf.io.IOTests;
import org.xbib.content.rdf.io.ntriple.NTripleContent;
import org.xbib.content.rdf.io.ntriple.NTripleContentParams;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;
import org.xbib.helper.StreamTester;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 *
 */
@Category(IOTests.class)
public class EuropeanaEDMReaderTest extends StreamTester {

    private static final IRI GEO_LAT = IRI.create("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
    private static final IRI GEO_LON = IRI.create("http://www.w3.org/2003/01/geo/wgs84_pos#long");
    private static final IRI location = IRI.create("location");

    @SuppressWarnings("unchecked")
    @Test
    public void testEuropeana() throws Exception {
        String filename = "oai_edm.xml";
        InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            throw new IOException("file " + filename + " not found");
        }
        DefaultRdfGraph graph = new DefaultRdfGraph();
        RdfXmlContentParser<NTripleContentParams> reader = new RdfXmlContentParser<>(in);
        reader.setRdfContentBuilderProvider(() -> new GeoJSONFilter(NTripleContent.nTripleContent(),
                NTripleContentParams.N_TRIPLE_CONTENT_PARAMS, graph));
        reader.parse();
        RdfContentBuilder<NTripleContentParams> builder = ntripleBuilder();
        Iterator<Resource> resourceIterator = graph.getResources();
        while (resourceIterator.hasNext()) {
            Resource resource = resourceIterator.next();
            builder.receive(resource);
        }
        //System.err.println(builder.string());
        assertStream(getClass().getResource("edm.nt").openStream(),
                builder.streamInput());
    }

    private class GeoJSONFilter extends RdfContentBuilder<NTripleContentParams> {

        DefaultRdfGraph graph;

        Node lat = null;

        Node lon = null;

        GeoJSONFilter(RdfContent<NTripleContentParams> content, NTripleContentParams params, DefaultRdfGraph graph)
                throws IOException {
            super(content, params);
            this.graph = graph;
        }

        @Override
        public GeoJSONFilter receive(Triple triple) {
            graph.receive(triple);
            if (triple.predicate().equals(GEO_LAT)) {
                lat = triple.object();
            }
            if (triple.predicate().equals(GEO_LON)) {
                lon = triple.object();
            }
            if (lat != null && lon != null) {
                // create location string for Elasticsearch
                graph.receive(new DefaultTriple(triple.subject(), location, new DefaultLiteral(lat + "," + lon)));
                lon = null;
                lat = null;
            }
            return this;
        }
    }
}
