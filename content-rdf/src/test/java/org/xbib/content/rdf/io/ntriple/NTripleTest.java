package org.xbib.content.rdf.io.ntriple;

import static org.xbib.content.rdf.RdfContentFactory.ntripleBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.XSDResourceIdentifiers;
import org.xbib.content.rdf.internal.DefaultLiteral;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.resource.IRI;

/**
 *
 */
public class NTripleTest extends Assert {

    @Test
    public void testNTripleBuilder() throws Exception {
        RdfContentBuilder<NTripleContentParams> builder = ntripleBuilder();
        Resource resource = createResource();
        builder.receive(resource);
        assertTrue(builder.string().length() > 0);
    }

    @Test
    public void testNTripleWriteInt() throws Exception {
        Resource resource = new DefaultResource(IRI.create("urn:doc1"));
        resource.add("http://purl.org/dc/elements/1.1/date", new DefaultLiteral("2010").type(XSDResourceIdentifiers.INTEGER));
        RdfContentBuilder<NTripleContentParams> builder = ntripleBuilder();
        builder.receive(resource);
        assertEquals("<urn:doc1> <http://purl.org/dc/elements/1.1/date> \"2010\"^^<xsd:integer> .\n", builder.string());
    }

    private Resource createResource() {
        Resource resource = new DefaultResource(IRI.create("urn:doc1"));
        resource.add("http://purl.org/dc/elements/1.1/creator", "Smith");
        resource.add("http://purl.org/dc/elements/1.1/creator", "Jones");
        Resource r = resource.newResource("dcterms:hasPart");
        r.add("http://purl.org/dc/elements/1.1/title", "This is a part")
          .add("http://purl.org/dc/elements/1.1/title", "of a title")
          .add("http://purl.org/dc/elements/1.1/creator", "Jörg Prante")
          .add("http://purl.org/dc/elements/1.1/date", "2009");
        resource.add("http://purl.org/dc/elements/1.1/title", "A sample title");
        r = resource.newResource("http://purl.org/dc/terms/isPartOf");
        r.add("http://purl.org/dc/elements/1.1/title", "another");
        r.add("http://purl.org/dc/elements/1.1/title", "title");
        return resource;
    }
}
