package org.xbib.content.rdf;

import org.junit.Assert;
import org.junit.Test;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class IRINamespaceCompactionTest extends Assert {

    @Test
    public void testCompaction() throws Exception {
        IRINamespaceContext context = IRINamespaceContext.getInstance();
        assertEquals(context.getNamespaceURI("dc"), "http://purl.org/dc/elements/1.1/");
        assertEquals(context.getPrefix("http://purl.org/dc/elements/1.1/"), "dc");
        IRI dc = IRI.create("http://purl.org/dc/elements/1.1/creator");
        assertEquals(context.compact(dc), "dc:creator");
    }

}
