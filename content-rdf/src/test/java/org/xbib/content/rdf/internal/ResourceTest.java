package org.xbib.content.rdf.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.xbib.content.rdf.RdfContentFactory.ntripleBuilder;

import org.junit.jupiter.api.Test;
import org.xbib.content.rdf.Literal;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.io.ntriple.NTripleContentParams;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.content.resource.Node;

import java.io.IOException;
import java.util.Iterator;

/**
 *
 */
public class ResourceTest {

    @Test
    public void deleted() throws Exception {
        Resource r = new DefaultAnonymousResource();
        assertFalse(r.isDeleted());
        r.setDeleted(true);
        assertTrue(r.isDeleted());
        r.setDeleted(false);
        assertFalse(r.isDeleted());
    }

    @Test
    public void testResourceId() throws Exception {
        IRI iri = IRI.create("http://index?type#id");
        Resource r = new DefaultResource(iri);
        assertEquals("http", r.id().getScheme());
        assertEquals("index", r.id().getHost());
        assertEquals("type", r.id().getQuery());
        assertEquals("id", r.id().getFragment());
    }

    @Test
    public void testEmptyResources() throws Exception {
        Resource r = new DefaultResource(IRI.create("urn:root"));
        assertTrue(r.isEmpty());
        assertEquals(r.toString(), "urn:root");
    }

    @Test
    public void testEmptyProperty() throws Exception {
        Resource r = new DefaultResource(IRI.create("urn:root"));
        r.add("urn:property", (String) null);
        assertTrue(r.isEmpty());
    }

    @Test
    public void testStringLiteral() throws Exception {
        Resource r = new DefaultResource(IRI.create("urn:root"));
        r.add("urn:property", "Hello World");
        assertFalse(r.isEmpty());
        assertEquals(r.triples().get(0).object().toString(), "Hello World");
    }

    @Test
    public void testIntegerLiteral() throws Exception {
        Resource r = new DefaultResource(IRI.create("urn:root"));
        DefaultLiteral literal = new DefaultLiteral(123).type(Literal.INT);
        r.add("urn:property", literal);
        assertFalse(r.isEmpty());
        assertEquals(r.triples().get(0).object().toString(), "123^^xsd:int");
    }

    @Test
    public void testPredicateSet() throws Exception {
        Resource r = new DefaultResource(IRI.create("urn:doc1"))
                .add("urn:valueURI", "Hello World")
                .add("urn:creator", "Smith")
                .add("urn:creator", "Jones");
        Iterator<IRI> it = r.predicates().iterator();
        assertEquals("urn:valueURI", it.next().toString());
        assertEquals("urn:creator", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testUniqueObjects() throws Exception {
        Resource r = new DefaultResource(IRI.create("urn:doc4"));
        r.add("urn:hasAttribute", "a")
                .add("urn:hasAttribute", "b")
                .add("urn:hasAttribute", "a") // another a, must be suppressed
                .add("urn:hasAttribute", "c");
        StringBuilder sb = new StringBuilder();
        r.objects("urn:hasAttribute").forEach(sb::append);
        assertEquals(sb.toString(), "abc");
    }

    @Test
    public void testPropertyIterator() throws Exception {
        Resource r = new DefaultResource(IRI.create("urn:doc2"));
        r.add("urn:valueURI", "Hello World")
                .add("urn:name", "Smith")
                .add("urn:name", "Jones");
        Iterator<Triple> it = r.properties().iterator();
        assertEquals("urn:doc2 urn:valueURI Hello World", it.next().toString());
        assertEquals("urn:doc2 urn:name Smith", it.next().toString());
        assertEquals("urn:doc2 urn:name Jones", it.next().toString());
    }

    @Test
    public void testIterator() throws Exception {
        DefaultAnonymousResource.reset(); // for blank node counter
        Resource r = new DefaultResource(IRI.create("urn:doc1"));
        r.add("urn:valueURI", "Hello World")
                .add("urn:name", "Smith")
                .add("urn:name", "Jones");
        // the first resource adds a resource value
        Resource r1 = r.newResource("urn:res1");
        r1.add("urn:has", "a first res value");
        // the second resource adds another resource value
        Resource r2 = r.newResource("urn:res1");
        r2.add("urn:has", "a second res value");
        assertEquals(r.predicates().size(), 3);
        Iterator<Triple> it = r.triples().iterator();
        assertEquals("urn:doc1 urn:valueURI Hello World", it.next().toString());
        assertEquals("urn:doc1 urn:name Smith", it.next().toString());
        assertEquals("urn:doc1 urn:name Jones", it.next().toString());
        assertEquals("urn:doc1 urn:res1 _:b1", it.next().toString());
        assertEquals("_:b1 urn:has a first res value", it.next().toString());
        assertEquals("urn:doc1 urn:res1 _:b2", it.next().toString());
        assertEquals("_:b2 urn:has a second res value", it.next().toString());
        assertFalse(it.hasNext());

        Iterator<IRI> itp = r.predicates().iterator();
        IRI pred = itp.next();
        assertEquals("urn:valueURI", pred.toString());
        Iterator<Node> values = r.objects(pred).iterator();
        assertEquals("Hello World", values.next().toString());
        assertFalse(values.hasNext());
        pred = itp.next();
        assertEquals("urn:name", pred.toString());
        values = r.objects(pred).iterator();
        assertEquals("Smith", values.next().toString());
        assertEquals("Jones", values.next().toString());
        assertFalse(values.hasNext());
        pred = itp.next();
        assertEquals("urn:res1", pred.toString());
        values = r.objects(pred).iterator();
        assertEquals("_:b1", values.next().toString());
        assertEquals("_:b2", values.next().toString());
        assertFalse(values.hasNext());
        assertFalse(itp.hasNext());
    }

    @Test
    public void testCompactPredicate() throws Exception {
        Resource r = new DefaultResource(IRI.create("urn:doc"));
        r.add("urn:value1", "Hello World");
        IRI predicate = IRI.create("urn:pred");
        Resource r1 = r.newResource(predicate);
        r1.add(predicate, "a value");
        Iterator<Triple> it = r.triples().iterator();
        int cnt = 0;
        while (it.hasNext()) {
            it.next();
            cnt++;
        }
        assertEquals(cnt, 3);
        r.compactPredicate(predicate);
        it = r.triples().iterator();
        assertEquals("urn:doc urn:value1 Hello World", it.next().toString());
        assertEquals("urn:doc urn:pred a value", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testAddResource() throws Exception {
        DefaultAnonymousResource.reset(); // for blank node counter
        Resource r = new DefaultResource(IRI.create("urn:res1"));
        r.add("urn:value", "Foo bar");
        Resource s = new DefaultResource(IRI.create("urn:res2"));
        s.add("urn:value", "Baz bar");
        r.add("urn:myres", s);
        assertEquals("[urn:res1 urn:value Foo bar, urn:res1 urn:myres urn:res2, urn:res2 urn:value Baz bar]",
                r.triples().toString());
    }

    @Test
    public void testAddingResources() throws Exception {
        DefaultAnonymousResource.reset(); // for blank node counter
        Resource r = new DefaultResource(IRI.create("urn:r"));
        r.add("urn:value", "Hello R");
        // named ID
        Resource s = new DefaultResource(IRI.create("urn:s"));
        s.add("urn:value", "Hello S");
        // another named ID
        Resource t = new DefaultResource(IRI.create("urn:t"));
        t.add("urn:value", "Hello T");
        // a blank node resource ID
        IRI blank1 = new DefaultAnonymousResource().id();
        Resource u = new DefaultResource(blank1);
        u.add("urn:value", "Hello U");
        // another blank node resource ID
        IRI blank2 = new DefaultAnonymousResource().id();
        Resource v = new DefaultResource(blank2);
        v.add("urn:value", "Hello V");
        IRI predicate = IRI.create("dc:subject");
        r.add(predicate, s);
        r.add(predicate, t);
        r.add(predicate, u);
        r.add(predicate, v);
        Iterator<Triple> it = r.triples().iterator();
        assertEquals("urn:r urn:value Hello R", it.next().toString());
        assertEquals("urn:r dc:subject urn:s", it.next().toString());
        assertEquals("urn:s urn:value Hello S", it.next().toString());
        assertEquals("urn:r dc:subject urn:t", it.next().toString());
        assertEquals("urn:t urn:value Hello T", it.next().toString());
        assertEquals("urn:r dc:subject _:b1", it.next().toString());
        assertEquals("_:b1 urn:value Hello U", it.next().toString());
        assertEquals("urn:r dc:subject _:b2", it.next().toString());
        assertEquals("_:b2 urn:value Hello V", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testTripleAdder() throws IOException {
        IRINamespaceContext context = IRINamespaceContext.newInstance();
        context.addNamespace("vcard", "http://www.w3.org/2006/vcard/ns#");
        context.addNamespace("owl", "http://www.w3.org/2002/07/owl#");
        // ID with compact IRI, will be expanded
        Resource r = DefaultResource.create(context, "vcard:value");
        // triples with expanded IRIs
        Triple t1 = new DefaultTriple(DefaultResource.create("http://www.w3.org/2006/vcard/ns#value"),
                IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                IRI.create("http://www.w3.org/2002/07/owl#DatatypeProperty"));
        Triple t2 = new DefaultTriple(DefaultResource.create("http://www.w3.org/2006/vcard/ns#value"),
                IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#label"),
                new DefaultLiteral("value@en"));
        r.add(t1).add(t2);
        NTripleContentParams params = new NTripleContentParams(context);
        RdfContentBuilder<NTripleContentParams> builder = ntripleBuilder(params);
        builder.receive(r);
        assertEquals("<http://www.w3.org/2006/vcard/ns#value> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#DatatypeProperty> .\n" +
                        "<http://www.w3.org/2006/vcard/ns#value> <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> \"value@en\" .",
                builder.string().trim());
    }
}
