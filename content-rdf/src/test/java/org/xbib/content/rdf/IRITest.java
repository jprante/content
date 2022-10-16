package org.xbib.content.rdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRISyntaxException;

public class IRITest {

    @Test
    public void testUserInfo() {
        IRI iri = IRI.create("http://localhost");
        assertNull(iri.getUserInfo());
        assertEquals("localhost", iri.getHost());
        assertEquals(-1, iri.getPort());
        iri = IRI.create("http://user@localhost");
        assertEquals("user", iri.getUserInfo());
        assertEquals("localhost", iri.getHost());
        assertEquals(-1, iri.getPort());
        iri = IRI.create("http://user:password@localhost");
        assertEquals("user:password", iri.getUserInfo());
        assertEquals("localhost", iri.getHost());
        assertEquals(-1, iri.getPort());
        iri = IRI.create("http://user:password@localhost:1234");
        assertEquals("user:password", iri.getUserInfo());
        assertEquals("localhost", iri.getHost());
        assertEquals(1234, iri.getPort());
    }

    @Test
    public void testJsonLd() {
        IRI iri = IRI.create("@context");
        assertNull(iri.getScheme());
        assertEquals("@context", iri.getSchemeSpecificPart());
    }

    @Test
    public void testIllegalBlankNodeIRI() {
        Assertions.assertThrows(IRISyntaxException.class, () -> {
            IRI iri = IRI.create("_:a1");
            assertEquals("_", iri.getScheme());
            assertEquals("a1", iri.getSchemeSpecificPart());

        });
    }

    @Test
    public void testRoutingByIRI() {
        IRI iri = IRI.create("http://index?type#id");
        assertEquals("http", iri.getScheme());
        assertEquals("index", iri.getHost());
        assertEquals("type", iri.getQuery());
        assertEquals("id", iri.getFragment());
    }

    @Test
    public void testCuri() {
        IRI curi = IRI.builder().curie("dc:creator").build();
        assertEquals("dc", curi.getScheme());
        assertEquals("creator", curi.getPath());
        curi = IRI.builder().curie("creator").build();
        assertNull(curi.getScheme());
        assertEquals("creator", curi.getPath());
    }

    @Test
    public void testSchemeSpecificPart() {
        IRI curi = IRI.builder().curie("dc:creator").build();
        assertEquals("dc", curi.getScheme());
        assertEquals("creator", curi.getSchemeSpecificPart());
        assertEquals("dc:creator", curi.toString());
        curi = IRI.builder().curie("creator").build();
        assertNull(curi.getScheme());
        assertEquals("creator", curi.getSchemeSpecificPart());
        assertEquals("creator", curi.toString());
    }

    @Test
    public void testIdentity() {
        String s = "urn:a";
        IRI i1 = IRI.builder().curie(s).build();
        IRI i2 = IRI.create(s);
        assertEquals(i1, i2);
    }
}
