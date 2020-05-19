package org.xbib.content.rdf.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.content.resource.IRI;

/**
 *
 */
public class LiteralTest {

    @Test
    public void testLiteral() {
        DefaultLiteral l = new DefaultLiteral("2013")
                .type(IRI.create("xsd:gYear"));
        assertEquals(l.toString(), "2013^^xsd:gYear");
        assertEquals(l.object(), 2013);
    }
}
