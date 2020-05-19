package org.xbib.content.rdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class IRINamespaceCompactionTest {

    @Test
    public void testCompaction() throws Exception {
        IRINamespaceContext context = IRINamespaceContext.getInstance();
        assertEquals(context.getNamespaceURI("dc"), "http://purl.org/dc/elements/1.1/");
        assertEquals(context.getPrefix("http://purl.org/dc/elements/1.1/"), "dc");
        IRI dc = IRI.create("http://purl.org/dc/elements/1.1/creator");
        assertEquals(context.compact(dc), "dc:creator");
    }

}
