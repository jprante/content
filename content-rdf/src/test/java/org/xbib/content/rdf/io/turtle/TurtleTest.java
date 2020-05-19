package org.xbib.content.rdf.io.turtle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xbib.content.rdf.RdfContentFactory.turtleBuilder;

import org.junit.jupiter.api.Test;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentFactory;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.content.resource.NamespaceContext;
import org.xbib.content.rdf.StreamTester;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class TurtleTest extends StreamTester {

    @Test
    public void testTurtleGND() throws Exception {
        NamespaceContext context = IRINamespaceContext.newInstance();
        context.addNamespace("gnd", "http://d-nb.info/gnd/");
        InputStream in = getClass().getResourceAsStream("GND.ttl");
        TurtleContentParser<TurtleContentParams> reader = new TurtleContentParser<TurtleContentParams>(in)
                .setBaseIRI(IRI.create("http://d-nb.info/gnd/"))
                .context(context);
        reader.parse();
        in.close();
    }

    @Test
    public void testTurtleGND2() throws Exception {
        IRINamespaceContext context = IRINamespaceContext.newInstance();
        InputStream in = getClass().getResourceAsStream("gnd2.ttl");
        TurtleContentParser<TurtleContentParams> reader = new TurtleContentParser<TurtleContentParams>(in)
                .setBaseIRI(IRI.create("http://d-nb.info/gnd/"))
                .context(context);
        reader.parse();
        in.close();
    }

    @Test
    public void testTurtleGND3() throws Exception {
        IRINamespaceContext context = IRINamespaceContext.newInstance();
        InputStream in = getClass().getResourceAsStream("gnd2.ttl");
        TurtleContentParser<TurtleContentParams> reader = new TurtleContentParser<TurtleContentParams>(in)
                .setBaseIRI(IRI.create("http://d-nb.info/gnd/"))
                .context(context);
        reader.setRdfContentBuilderProvider(RdfContentFactory::turtleBuilder);
        reader.setRdfContentBuilderHandler(b -> {
            //logger.info("doc id={} content={}", b.getSubject(), b.string());
        });
        reader.parse();
        in.close();
    }

    @Test
    public void testTurtle() throws Exception {
        StringBuilder sb = new StringBuilder();
        String filename = "turtle-demo.ttl";
        InputStream in = getClass().getResource(filename).openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        String s1 = sb.toString().trim();
        Resource resource = createResource();

        IRINamespaceContext context = IRINamespaceContext.newInstance();
        context.addNamespace("dc", "http://purl.org/dc/elements/1.1/");
        context.addNamespace("dcterms", "http://purl.org/dc/terms/");

        TurtleContentParams params = new TurtleContentParams(context, true);
        RdfContentBuilder<TurtleContentParams> builder = turtleBuilder(params);
        builder.receive(resource);
        String s2 = builder.string().trim();
        assertEquals(s2, s1);
        in.close();
    }

    private Resource createResource() {
        Resource resource = new DefaultResource(IRI.create("urn:doc1"));
        resource.add("dc:creator", "Smith");
        resource.add("dc:creator", "Jones");
        Resource r = resource.newResource("dcterms:hasPart")
                .add("dc:title", "This is a part")
                .add("dc:title", "of the sample title")
                .add("dc:creator", "Jörg Prante")
                .add("dc:date", "2009");
        resource.add("dc:title", "A sample title");
        resource.newResource("dcterms:isPartOf")
                .add("dc:title", "another")
                .add("dc:title", "title");
        return resource;
    }

    @Test
    public void testTurtleBuilder() throws Exception {
        Resource resource = createResource2();
        IRINamespaceContext context = IRINamespaceContext.getInstance();
        TurtleContentParams params = new TurtleContentParams(context, false);
        RdfContentBuilder<TurtleContentParams> builder = turtleBuilder(params);
        builder.receive(resource);
        assertStream(getClass().getResource("turtle-test.ttl").openStream(), builder.streamInput());
    }

    private Resource createResource2() {
        Resource r = new DefaultResource(IRI.create("urn:res"));
        r.add("dc:title", "Hello")
                .add("dc:title", "World")
                .add("xbib:person", "Jörg Prante")
                .add("dc:subject", "An")
                .add("dc:subject", "example")
                .add("dc:subject", "for")
                .add("dc:subject", "a")
                .add("dc:subject", "sequence")
                .add("http://purl.org/dc/terms/place", "Köln");
        r.newResource("urn:res1")
                .add("property1", "value1")
                .add("property2", "value2");
        r.newResource("urn:res2")
                .add("property3", "value3")
                .add("property4", "value4");
        r.newResource("urn:res3")
                .add("property5", "value5")
                .add("property6", "value6");
        return r;
    }

    @Test
    public void testTurtleResourceIndent() throws Exception {
        Resource resource = createNestedResources();
        TurtleContentParams params = new TurtleContentParams(IRINamespaceContext.getInstance(), false);
        RdfContentBuilder<TurtleContentParams> builder = turtleBuilder(params);
        builder.receive(resource);
        assertStream(getClass().getResourceAsStream("turtle-indent.ttl"),
                builder.streamInput());
    }

    private Resource createNestedResources() {
        Resource r = new DefaultResource(IRI.create("urn:res"));
        r.add("dc:title", "Hello")
                .add("dc:title", "World")
                .add("xbib:person", "Jörg Prante")
                .add("dc:subject", "An")
                .add("dc:subject", "example")
                .add("dc:subject", "for")
                .add("dc:subject", "a")
                .add("dc:subject", "sequence")
                .add("http://purl.org/dc/terms/place", "Köln");
        Resource r1 = r.newResource("urn:res1")
                .add("property1", "value1")
                .add("property2", "value2");
        r1.newResource("urn:res2")
                .add("property3", "value3")
                .add("property4", "value4");
        r.newResource("urn:res3")
                .add("property5", "value5")
                .add("property6", "value6");
        return r;
    }

    /**
     * Test un-indenting over three levels.
     *
     * @throws Exception if test fails
     */
    @Test
    public void testTurtleDeepNest() throws Exception {
        Resource resource = createDeepNestedResources();
        TurtleContentParams params = new TurtleContentParams(IRINamespaceContext.getInstance(), false);
        RdfContentBuilder<TurtleContentParams> builder = turtleBuilder(params);
        builder.receive(resource);
        assertStream(getClass().getResourceAsStream("deep-nested.ttl"),
                builder.streamInput());
    }

    private Resource createDeepNestedResources() {
        Resource r = new DefaultResource(IRI.create("urn:res"));
        r.add("dc:title", "Hello")
                .add("dc:title", "World")
                .add("xbib:person", "Jörg Prante")
                .add("http://purl.org/dc/terms/place", "Köln");
        Resource r1 = r.newResource("urn:res1")
                .add("property1", "value1")
                .add("property2", "value2");
        Resource r2 = r1.newResource("urn:res2")
                .add("property3", "value3")
                .add("property4", "value4");
        r2.newResource("urn:res3")
                .add("property5", "value5")
                .add("property6", "value6");
        r1.newResource("urn:res4")
                .add("property7", "value7")
                .add("property8", "value8");
        r.newResource("urn:res5")
                .add("property9", "value9")
                .add("property10", "value10");
        return r;
    }


}
