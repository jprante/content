package org.xbib.content.rdf.internal;

import org.junit.Assert;
import org.junit.Test;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.resource.IRI;

import java.util.Iterator;

/**
 *
 */
public class BlankNodeTest extends Assert {

    @Test
    public void testBlankNodeRenumbering() throws Exception {
        DefaultAnonymousResource.reset();
        Resource r = new DefaultResource(IRI.create("urn:meta1"));
        // test order of adding
        Resource r1 = r.newResource("urn:res1");
        r1.add("urn:has", "a first res");
        r.add("urn:has", "a first property");
        Resource q = new DefaultResource(IRI.create("urn:meta2"));
        Resource r2 = q.newResource("urn:res2");
        r2.add("urn:has", "a second res");
        q.add("urn:has", "a second property");
        // we test here resource adding
        r.add("a:res", q);
        Iterator<Triple> it = r.properties().iterator();
        assertEquals(it.next().toString(), "urn:meta1 urn:res1 _:b1");
        assertEquals(it.next().toString(), "urn:meta1 urn:has a first property");
        assertEquals(it.next().toString(), "urn:meta1 a:res urn:meta2");
    }

    @Test
    public void testIterator() throws Exception {
        DefaultAnonymousResource.reset();
        Resource r = new DefaultResource(IRI.create("res1"));
        r.add("p0", "l0")
                .newResource("res2")
                .add("p1", "l1")
                .add("p2", "l2")
                .newResource("res3")
                .add("p1", "l1")
                .add("p2", "l2")
                .newResource("res4")
                .add("p1", "l1")
                .add("p2", "l2");

        Iterator<Triple> it = r.triples().iterator();
        assertEquals(it.next().toString(), "res1 p0 l0");
        assertEquals(it.next().toString(), "res1 res2 _:b1");
        assertEquals(it.next().toString(), "_:b1 p1 l1");
        assertEquals(it.next().toString(), "_:b1 p2 l2");
        assertEquals(it.next().toString(), "_:b1 res3 _:b2");
        assertEquals(it.next().toString(), "_:b2 p1 l1");
        assertEquals(it.next().toString(), "_:b2 p2 l2");
        assertEquals(it.next().toString(), "_:b2 res4 _:b3");
        assertEquals(it.next().toString(), "_:b3 p1 l1");
        assertEquals(it.next().toString(), "_:b3 p2 l2");
    }

    @Test
    public void testResIterator() throws Exception {
        DefaultAnonymousResource.reset();
        Resource r = new DefaultResource(IRI.create("res0"));
        r.add("p0", "l0")
                .newResource("res")
                .add("p1", "l1")
                .add("p2", "l2")
                .newResource("res")
                .add("p1", "l1")
                .add("p2", "l2")
                .newResource("res")
                .add("p1", "l1")
                .add("p2", "l2");
        Iterator<Triple> it = r.triples().iterator();
        assertEquals(it.next().toString(), "res0 p0 l0");
        assertEquals(it.next().toString(), "res0 res _:b1");
        assertEquals(it.next().toString(), "_:b1 p1 l1");
        assertEquals(it.next().toString(), "_:b1 p2 l2");
        assertEquals(it.next().toString(), "_:b1 res _:b2");
        assertEquals(it.next().toString(), "_:b2 p1 l1");
        assertEquals(it.next().toString(), "_:b2 p2 l2");
        assertEquals(it.next().toString(), "_:b2 res _:b3");
        assertEquals(it.next().toString(), "_:b3 p1 l1");
        assertEquals(it.next().toString(), "_:b3 p2 l2");
    }
}
