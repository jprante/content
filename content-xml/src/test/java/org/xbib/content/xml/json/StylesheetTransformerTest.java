package org.xbib.content.xml.json;

import org.junit.Test;
import org.xbib.content.resource.XmlNamespaceContext;
import org.xbib.content.xml.transform.StylesheetTransformer;
import org.xml.sax.InputSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.transform.sax.SAXSource;

/**
 *
 */
public class StylesheetTransformerTest {

    private final QName root = new QName("http://example.org", "result", "ex");

    private final XmlNamespaceContext context = XmlNamespaceContext.newDefaultInstance();

    @Test
    public void testJsonAsXML() throws Exception {
        InputStream in = getClass().getResourceAsStream("dc.json");
        if (in == null) {
            throw new IOException("dc.json not found");
        }
        context.addNamespace("xbib", "http://xbib.org/");
        context.addNamespace("bib", "info:bib");
        context.addNamespace("lia", "http://xbib.org/lia/");

        JsonXmlReader reader = new JsonXmlReader()
                .root(root)
                .context(context);
        File file = File.createTempFile("dc.", ".xml");
        FileWriter out = new FileWriter(file);
        StylesheetTransformer transformer = new StylesheetTransformer().setPath(
                "src/main/resources",
                "src/main/resources/xsl");
        // no style sheet, just a simple copy
        transformer.setSource(new SAXSource(reader, new InputSource(in)))
                .setResult(out)
                .transform();
        out.close();
        transformer.close();
    }
}
