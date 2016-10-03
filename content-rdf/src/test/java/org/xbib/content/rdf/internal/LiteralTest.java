package org.xbib.content.rdf.internal;

import org.junit.Assert;
import org.junit.Test;
import org.xbib.content.resource.IRI;

/**
 *
 */
public class LiteralTest extends Assert {

    @Test
    public void testLiteral() {
        DefaultLiteral l = new DefaultLiteral("2013")
                .type(IRI.create("xsd:gYear"));
        assertEquals(l.toString(), "2013^^xsd:gYear");
        assertEquals(l.object(), 2013);
    }
}
