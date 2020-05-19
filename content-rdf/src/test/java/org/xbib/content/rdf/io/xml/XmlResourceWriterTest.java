package org.xbib.content.rdf.io.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xbib.content.rdf.RdfContentFactory.xmlBuilder;

import org.junit.jupiter.api.Test;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.resource.IRI;

/**
 *
 */
public class XmlResourceWriterTest {

    @Test
    public void testXMLResourceWriter() throws Exception {
        DefaultAnonymousResource.reset();
        Resource root = new DefaultResource(IRI.create("urn:root"));
        Resource resource = root.newResource("urn:res");
        resource.add("urn:property", "value");
        Resource nestedResource = resource.newResource("urn:nestedresource");
        nestedResource.add("urn:nestedproperty", "nestedvalue");
        RdfContentBuilder<?> builder = xmlBuilder();
        builder.receive(root);
        assertEquals("<urn:root><urn:res><urn:property>value</urn:property><urn:nestedresource>"
           + "<urn:nestedproperty>nestedvalue</urn:nestedproperty></urn:nestedresource></urn:res></urn:root>",
           builder.string());
    }

    @Test
    public void testResourceXml() throws Exception {
        DefaultAnonymousResource.reset();
        Resource parent = new DefaultResource(IRI.create("urn:doc3"));
        Resource child = parent.newResource("urn:res");
        child.add("urn:property", "value");
        RdfContentBuilder<?> builder = xmlBuilder();
        builder.receive(parent);
        assertEquals(
                builder.string(),
                "<urn:doc3><urn:res><urn:property>value</urn:property></urn:res></urn:doc3>");
    }
}
