package org.xbib.content.xml.json;

import org.junit.Test;
import org.xbib.content.resource.XmlNamespaceContext;
import org.xml.sax.InputSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 */
public class Json2XmlTest {

    private static final String PACKAGE = '/' + Json2XmlTest.class.getPackage().getName().replace('.', '/');

    private static final String[] jsons = {
            "dc",
            "elasticsearch-hit-example-1",
            "es-test-20130719",
            "glossary",
            "menu"
    };

    @Test
    public void testJSON() throws Exception {
        for (String s : jsons) {
            testJSONXmlReader(s);
            testJSONStreamer(s);
        }
    }

    private void testJSONXmlReader(String path) throws Exception {
        Reader r = getInput(path);
        InputSource in = new InputSource(r);
        JsonXmlReader parser = new JsonXmlReader().root(root()).context(context());
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        Writer w = getOutput("test-jsonxmlreader-" + path + ".xml");
        StreamResult stream = new StreamResult(w);
        transformer.transform(new SAXSource(parser, in), stream);
        w.close();
        r.close();
    }

    private void testJSONStreamer(String path) throws Exception {
        Reader r = getInput(path);
        Writer w = getOutput("test-jsonxmlstreamer-" + path + ".xml");
        JsonXmlStreamer jsonXml = new JsonXmlStreamer().root(root()).context(context());
        jsonXml.toXML(r, w);
        w.close();
        r.close();
    }

    private Reader getInput(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(PACKAGE + "/" + path + ".json");
        return new InputStreamReader(in, StandardCharsets.UTF_8);
    }

    private Writer getOutput(String path) throws IOException {
        File file = File.createTempFile(path, ".dat");
        return new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
    }

    private QName root() {
        return new QName("http://elasticsearch.org/ns/1.0/", "result", "es");
    }

    private XmlNamespaceContext context() {
        XmlNamespaceContext nsContext = XmlNamespaceContext.newDefaultInstance();
        nsContext.addNamespace("bib", "info:srw/cql-context-set/1/bib-v1/");
        nsContext.addNamespace("xbib", "http://xbib.org/");
        nsContext.addNamespace("abc", "http://localhost/");
        nsContext.addNamespace("lia", "http://xbib.org/namespaces/lia/");
        return nsContext;
    }

}
