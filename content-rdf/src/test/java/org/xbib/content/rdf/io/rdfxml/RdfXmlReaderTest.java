package org.xbib.content.rdf.io.rdfxml;

import static org.xbib.content.rdf.RdfContentFactory.turtleBuilder;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.io.IOTests;
import org.xbib.content.rdf.io.turtle.TurtleContentParams;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.helper.StreamTester;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 *
 */
@Category(IOTests.class)
public class RdfXmlReaderTest extends StreamTester {

    private static final Logger logger = Logger.getLogger(RdfXmlReaderTest.class.getName());

    @Test
    public void testReader() throws Exception {
        String filename = "118540238.xml";
        InputStream in = getClass().getResourceAsStream(filename);
        DefaultAnonymousResource.reset();
        TurtleContentParams params = new TurtleContentParams(IRINamespaceContext.getInstance(), false);
        RdfXmlContentParser<TurtleContentParams> reader = new RdfXmlContentParser<>(in);
        StringBuilder sb = new StringBuilder();
        reader.setRdfContentBuilderProvider(() -> turtleBuilder(params));
        reader.setRdfContentBuilderHandler(builder -> sb.append(builder.string()));
        reader.parse();
        assertStream(new InputStreamReader(getClass().getResourceAsStream("118540238.ttl"), StandardCharsets.UTF_8),
                new StringReader(sb.toString()));
    }

}
