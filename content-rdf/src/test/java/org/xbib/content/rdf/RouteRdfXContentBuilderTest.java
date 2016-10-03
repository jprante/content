package org.xbib.content.rdf;

import static org.xbib.content.rdf.RdfXContentFactory.routeRdfXContentBuilder;

import org.junit.Test;
import org.xbib.content.rdf.internal.DefaultLiteral;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.rdf.io.rdfxml.RdfXmlContentParser;
import org.xbib.content.resource.IRI;
import org.xbib.helper.StreamTester;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class RouteRdfXContentBuilderTest extends StreamTester {

    @Test
    public void testRoute() throws Exception {
        Resource resource = new DefaultResource(IRI.create("urn:res"));
        DefaultLiteral l = new DefaultLiteral("2013")
                .type(IRI.create("xsd:gYear"));
        resource.add("urn:property", "Hello World")
                .add("urn:date", l)
                .add("urn:link", IRI.create("urn:pointer"));
        RouteRdfXContentParams params = new RouteRdfXContentParams("index", "type");
        params.setHandler((content, p) -> assertEquals(p.getIndex() + " " + p.getType() + " 1 " + content,
                "index type 1 {\"urn:property\":\"Hello World\",\"urn:date\":2013,\"urn:link\":\"urn:pointer\"}"
        ));
        RdfContentBuilder<RouteRdfXContentParams> builder = routeRdfXContentBuilder(params);
        builder.receive(resource);
    }

    @Test
    public void testVIAF() throws Exception {
        InputStream in = getClass().getResourceAsStream("VIAF.rdf");
        if (in == null) {
            throw new IOException("VIAF.rdf not found");
        }
        StringBuilder sb = new StringBuilder();
        RouteRdfXContentParams params = new RouteRdfXContentParams("index", "type");
        params.setHandler((content, p) -> {
            //logger.info("handle: {} {} {} {}", p.getIndex(), p.getType(), p.getId(), content);
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
        assertStream(new InputStreamReader(getClass().getResourceAsStream("viaf.json"), StandardCharsets.UTF_8),
                new StringReader(sb.toString()));
    }
}
