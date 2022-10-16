package org.xbib.content.rdf.io.rdfxml;

import static org.xbib.content.rdf.RdfContentFactory.turtleBuilder;

import org.junit.jupiter.api.Test;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.io.turtle.TurtleContentParams;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.content.rdf.StreamTester;

import java.io.InputStream;

public class RdfXmlReaderTest extends StreamTester {

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
        assertStream("118540238.ttl", getClass().getResourceAsStream("118540238.ttl"), sb.toString());
    }
}
