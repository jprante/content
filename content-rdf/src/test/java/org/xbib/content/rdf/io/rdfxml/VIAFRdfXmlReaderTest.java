package org.xbib.content.rdf.io.rdfxml;

import static org.xbib.content.rdf.RdfContentFactory.turtleBuilder;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xbib.content.rdf.io.IOTests;
import org.xbib.content.rdf.io.turtle.TurtleContentParams;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.helper.StreamTester;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 *
 */
@Category(IOTests.class)
public class VIAFRdfXmlReaderTest extends StreamTester {

    @Test
    public void testVIAF() throws Exception {
        InputStream in = getClass().getResource("VIAF.rdf").openStream();
        if (in == null) {
            throw new IOException("VIAF.rdf not found");
        }
        TurtleContentParams params = new TurtleContentParams(IRINamespaceContext.newInstance(), false);
        StringBuilder sb = new StringBuilder();
        new RdfXmlContentParser<TurtleContentParams>(in)
                .setRdfContentBuilderProvider(() -> turtleBuilder(params))
                .setRdfContentBuilderHandler(builder -> sb.append(builder.string()))
                .parse();
        assertStream(new InputStreamReader(getClass().getResource("viaf.ttl").openStream()),
                new StringReader(sb.toString()));
    }
}
