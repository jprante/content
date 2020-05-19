package org.xbib.content.rdf.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.content.rdf.Resource;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

/**
 *
 */
public class TripleTest {

    @Test
    public void testSimpleTriple() {
        Resource s = new DefaultResource(IRI.create("urn:1"));
        IRI p = IRI.create("urn:2");
        Node o = new DefaultLiteral("Hello World");
        DefaultTriple triple = new DefaultTriple(s, p, o);
        assertEquals(triple.subject().id(), s.id());
        assertEquals(triple.predicate(), p);
        assertEquals(triple.object(), o);
    }
}
