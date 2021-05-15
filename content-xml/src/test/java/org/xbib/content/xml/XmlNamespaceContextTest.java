package org.xbib.content.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xbib.content.xml.XmlXContent.contentBuilder;
import org.junit.jupiter.api.Test;
import org.xbib.content.XContentBuilder;
import org.xbib.content.xml.transform.StylesheetTransformer;
import java.util.Locale;
import javax.xml.namespace.QName;

/**
 *
 */
public class XmlNamespaceContextTest {

    @Test
    public void testDefaultNamespace() {
        XmlNamespaceContext context = XmlNamespaceContext.newInstance();
        assertEquals(0, context.getNamespaces().size());
    }

    @Test
    public void testDefaultNamespaces() throws Exception {
        XmlNamespaceContext context =
                XmlNamespaceContext.newInstance("org/xbib/content/resource/namespace",
                        Locale.getDefault(), StylesheetTransformer.class.getClassLoader());
        XmlXParams params = new XmlXParams(context);
        XContentBuilder builder = contentBuilder(params);
        builder.startObject()
                .field("dc:creator", "John Doe")
                .endObject();
        assertEquals("<root xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
                        + "xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" "
                        + "xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
                        + " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:xalan=\"http://xml.apache.org/xslt\" "
                        + "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><dc:creator>John Doe</dc:creator></root>",
                builder.string());
    }

    @Test
    public void testCustomNamespaces() throws Exception {
        QName root = new QName("result");
        XmlNamespaceContext context = XmlNamespaceContext.newInstance();
        context.addNamespace("abc", "http://localhost");
        XmlXParams params = new XmlXParams(root, context);
        XContentBuilder builder = contentBuilder(params);
        builder.startObject()
                .field("abc:creator", "John Doe")
                .endObject();
        assertEquals("<result xmlns:abc=\"http://localhost\"><abc:creator>John Doe</abc:creator></result>",
                builder.string());
    }

    @Test
    public void testRootNamespace() throws Exception {
        QName root = new QName("http://content", "root", "abc");
        XmlNamespaceContext context = XmlNamespaceContext.newInstance();
        context.addNamespace("", "http://localhost");
        context.addNamespace("abc", "http://content");
        XmlXParams params = new XmlXParams(root, context);
        XContentBuilder builder = contentBuilder(params);
        builder.startObject()
                .field("creator", "John Doe")
                .endObject();
        assertEquals("<abc:root xmlns:abc=\"http://content\" xmlns=\"http://localhost\">"
                + "<creator>John Doe</creator></abc:root>", builder.string());
    }
}
