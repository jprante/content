package org.xbib.content.rdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.xbib.content.rdf.RdfXContentFactory.routeRdfXContentBuilder;
import static org.xbib.content.rdf.StreamTester.assertStream;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.xbib.content.rdf.internal.DefaultLiteral;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.rdf.io.rdfxml.RdfXmlContentParser;
import org.xbib.content.resource.IRI;

import java.io.IOException;
import java.io.InputStream;

public class RouteRdfXContentBuilderTest {

    private static final Logger logger = Logger.getLogger(RouteRdfXContentBuilderTest.class.getName());

    @Test
    public void testRoute() throws Exception {
        Resource resource = new DefaultResource(IRI.create("urn:res"));
        DefaultLiteral l = new DefaultLiteral("2013")
                .type(IRI.create("xsd:gYear"));
        resource.add("urn:property", "Hello World")
                .add("urn:date", l)
                .add("urn:link", IRI.create("urn:pointer"));
        RouteRdfXContentParams params = new RouteRdfXContentParams("index", "type");
        AtomicBoolean found = new AtomicBoolean();
        params.setHandler((content, p) -> {
            assertEquals(p.getIndex() + " " + p.getType() + " 1 " + content,
                    "index type 1 {\"urn:property\":\"Hello World\",\"urn:date\":2013,\"urn:link\":\"urn:pointer\"}");
            found.set(true);
        });
        try (RdfContentBuilder<RouteRdfXContentParams> builder = routeRdfXContentBuilder(params)) {
            builder.receive(resource);
        }
        assertTrue(found.get());
    }

    @Test
    public void testVIAF() throws Exception {
        InputStream in = getClass().getResourceAsStream("VIAF.rdf");
        if (in == null) {
            throw new IOException("VIAF.rdf not found");
        }
        StringBuilder sb = new StringBuilder();
        RouteRdfXContentParams params = new RouteRdfXContentParams("index", "type");
        AtomicInteger counter = new AtomicInteger();
        params.setHandler((content, p) -> {
            logger.log(Level.INFO, MessageFormat.format("handle: {0} {1} {2} {3}",
                    p.getIndex(), p.getType(), p.getId(), content));
            counter.incrementAndGet();
        });
        new RdfXmlContentParser<RouteRdfXContentParams>(in)
                .setRdfContentBuilderProvider(() -> routeRdfXContentBuilder(params))
                .setRdfContentBuilderHandler(builder -> {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(builder.string());
                })
                .parse();
        assertStream("viaf.json", getClass().getResourceAsStream("viaf.json"),
                sb.toString());
        assertEquals(5, counter.get());
    }
}
