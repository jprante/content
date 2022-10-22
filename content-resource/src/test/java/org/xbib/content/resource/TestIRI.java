package org.xbib.content.resource;

import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestIRI {

    @Test
    public void testSimple() throws URISyntaxException {
        IRI iri = IRI.create("http://validator.w3.org/check?uri=http%3A%2F%2Fr\u00E9sum\u00E9.example.org");
        assertEquals("http", iri.getScheme());
        assertEquals("validator.w3.org", iri.getHost());
        assertEquals("/check", iri.getPath());
        assertEquals("//validator.w3.org/check?uri=http%3A%2F%2Frésumé.example.org", iri.getSchemeSpecificPart());
        //assertEquals("http://validator.w3.org/check?uri=http%3A%2F%2Fr%C3%A9sum%C3%A9.example.org", iri.toURI().toString());
    }
}
