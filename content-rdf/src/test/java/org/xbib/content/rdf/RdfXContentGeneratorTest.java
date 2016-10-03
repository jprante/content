package org.xbib.content.rdf;

import static org.xbib.content.rdf.RdfXContentFactory.rdfXContentBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.xbib.content.rdf.internal.DefaultLiteral;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.resource.IRI;

/**
 *
 */
public class RdfXContentGeneratorTest extends Assert {

    @Test
    public void testContentBuilder() throws Exception {
        Resource resource = new DefaultResource(IRI.create("urn:res"));
        DefaultLiteral l = new DefaultLiteral("2013")
                .type(IRI.create("xsd:gYear"));
        resource.add("urn:property", "Hello World")
                .add("urn:date", l)
                .add("urn:link", IRI.create("urn:pointer"));
        RdfXContentParams params = new RdfXContentParams();
        RdfContentBuilder<RdfXContentParams> builder = rdfXContentBuilder(params);
        builder.receive(resource);
        String result = params.getGenerator().get();
        assertEquals(result,
                "{\"urn:property\":\"Hello World\",\"urn:date\":2013,\"urn:link\":\"urn:pointer\"}");
    }

    @Test
    public void testContentBuilderSingleEmbedded() throws Exception {
        Resource resource = new DefaultResource(IRI.create("urn:res"));
        DefaultLiteral l = new DefaultLiteral("2013")
                .type(IRI.create("xsd:gYear"));
        resource.add("urn:property", "Hello World")
                .add("urn:date", l)
                .add("rdf:type", IRI.create("urn:type1"))
                .newResource("urn:embedded")
                .add("rdf:type", IRI.create("urn:type2"));
        RdfXContentParams params = new RdfXContentParams();
        RdfContentBuilder<RdfXContentParams> builder = rdfXContentBuilder(params);
        builder.receive(resource);
        String result = params.getGenerator().get();
        assertEquals("{\"urn:property\":\"Hello World\",\"urn:date\":2013,\"rdf:type\":\"urn:type1\","
                + "\"urn:embedded\":{\"rdf:type\":\"urn:type2\"}}", result);
    }

    @Test
    public void testContentBuilderDoubleEmbedded() throws Exception {
        Resource resource = new DefaultResource(IRI.create("urn:res"));
        DefaultLiteral l = new DefaultLiteral("2013")
                .type(IRI.create("xsd:gYear"));
        resource.add("urn:property", "Hello World")
                .add("urn:date", l)
                .add("rdf:type", IRI.create("urn:type1"))
                .newResource("urn:embedded")
                .add("rdf:type", IRI.create("urn:type2"));
        resource.newResource("urn:embedded2")
                .add("rdf:type", IRI.create("urn:type3"));
        RdfXContentParams params = new RdfXContentParams();
        RdfContentBuilder<RdfXContentParams> builder = rdfXContentBuilder(params);
        builder.receive(resource);
        String result = params.getGenerator().get();
        assertEquals("{\"urn:property\":\"Hello World\",\"urn:date\":2013,\"rdf:type\":\"urn:type1\","
                + "\"urn:embedded\":{\"rdf:type\":\"urn:type2\"},\"urn:embedded2\":{\"rdf:type\":\"urn:type3\"}}", result);
    }

    @Test
    public void testContentBuilderEmptyEmbedded() throws Exception {
        Resource resource = new DefaultResource(IRI.create("urn:res"));
        DefaultLiteral l = new DefaultLiteral("2013")
                .type(IRI.create("xsd:gYear"));
        resource.add("urn:property", "Hello World")
                .add("urn:date", l)
                .add("rdf:type", IRI.create("urn:type1"))
                .newResource("urn:embedded"); // empty resource, do not copy
        RdfXContentParams params = new RdfXContentParams();
        RdfContentBuilder<RdfXContentParams> builder = rdfXContentBuilder(params);
        builder.receive(resource);
        String result = params.getGenerator().get();
        assertEquals(result,
                "{\"urn:property\":\"Hello World\",\"urn:date\":2013,\"rdf:type\":\"urn:type1\"}");
    }
}
