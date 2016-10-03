package org.xbib.content.rdf.io.rdfxml;

import static org.xbib.content.rdf.RdfContentFactory.turtleBuilder;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xbib.content.rdf.RdfContentFactory;
import org.xbib.content.rdf.io.IOTests;
import org.xbib.content.rdf.io.ntriple.NTripleContentParams;
import org.xbib.content.rdf.io.turtle.TurtleContentParams;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.helper.StreamTester;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.logging.Logger;

/**
 *
 */
@Category(IOTests.class)
public class GNDRdfXmlReaderTest extends StreamTester {

    private static final Logger logger = Logger.getLogger(GNDRdfXmlReaderTest.class.getName());

    @Test
    public void testGNDfromRdfXmltoTurtle() throws Exception {
        String filename = "GND.rdf";
        InputStream in = getClass().getResourceAsStream(filename);
        TurtleContentParams params = new TurtleContentParams(IRINamespaceContext.newInstance(), false);
        RdfXmlContentParser<TurtleContentParams> reader = new RdfXmlContentParser<>(in);
        StringBuilder sb = new StringBuilder();
        reader.setRdfContentBuilderProvider(() -> turtleBuilder(params));
        reader.setRdfContentBuilderHandler(builder -> sb.append(builder.string()));
        reader.parse();
        assertStream(new InputStreamReader(getClass().getResourceAsStream("gnd.ttl")),
                new StringReader(sb.toString()));
    }

    @Test
    public void testAnotherGNDfromRdfXmltoTurtle() throws Exception {
        String filename = "GND.rdf";
        InputStream in = getClass().getResourceAsStream(filename);
        TurtleContentParams params = new TurtleContentParams(IRINamespaceContext.newInstance(), false);
        RdfXmlContentParser<TurtleContentParams> reader = new RdfXmlContentParser<>(in);
        StringBuilder sb = new StringBuilder();
        reader.setRdfContentBuilderProvider(() -> turtleBuilder(params));
        reader.setRdfContentBuilderHandler(builder -> sb.append(builder.string()));
        reader.parse();
        assertStream(new InputStreamReader(getClass().getResourceAsStream("gnd.ttl")),
                new StringReader(sb.toString()));
    }

    @Test
    public void testGNDtoNtriple() throws Exception {
        String filename = "GND.rdf";
        InputStream in = getClass().getResourceAsStream(filename);
        RdfXmlContentParser<NTripleContentParams> reader = new RdfXmlContentParser<>(in);
        StringBuilder sb = new StringBuilder();
        reader.setRdfContentBuilderProvider(RdfContentFactory::ntripleBuilder);
        reader.setRdfContentBuilderHandler(builder -> sb.append(builder.string()));
        reader.parse();
        assertStream(new InputStreamReader(getClass().getResourceAsStream("GND.nt")),
                new StringReader(sb.toString()));
    }

}
