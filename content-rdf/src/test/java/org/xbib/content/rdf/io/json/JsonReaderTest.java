package org.xbib.content.rdf.io.json;

import static org.xbib.content.rdf.RdfContentFactory.jsonBuilder;

import org.junit.Test;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.io.xml.XmlHandler;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.helper.StreamTester;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

/**
 *
 */
public class JsonReaderTest extends StreamTester {

    @Test
    public void testGenericJsonReader() throws Exception {
        String filename = "dc.json";
        InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            throw new IOException("file " + filename + " not found");
        }

        IRINamespaceContext namespaceContext = IRINamespaceContext.newInstance();
        namespaceContext.addNamespace("dc", "http://purl.org/dc/elements/1.1/");
        namespaceContext.addNamespace("dcterms", "http://purl.org/dc/terms/");
        namespaceContext.addNamespace("bib", "info:srw/cql-context-set/1/bib-v1/");
        namespaceContext.addNamespace("xbib", "http://xbib.org/");
        namespaceContext.addNamespace("lia", "http://xbib.org/lia/");

        JsonContentParams params = new JsonContentParams(namespaceContext);
        JsonResourceHandler jsonHandler = new JsonResourceHandler(params) {

            @Override
            public boolean isResourceDelimiter(QName name) {
                return false;
            }

            @Override
            public boolean skip(QName name) {
                return false;
            }

            @Override
            public void identify(QName name, String value, IRI identifier) {
                if (identifier == null) {
                    getResource().setId(IRI.create("id:doc1"));
                }
            }

            @Override
            public XmlHandler<JsonContentParams> setNamespaceContext(IRINamespaceContext namespaceContext) {
                return this;
            }

            @Override
            public IRINamespaceContext getNamespaceContext() {
                return namespaceContext;
            }
        };
        RdfContentBuilder<JsonContentParams> builder = jsonBuilder();
        jsonHandler.setBuilder(builder);
        new JsonContentParser<JsonContentParams>(in)
                .setHandler(jsonHandler)
                .root(new QName("http://purl.org/dc/elements/1.1/", "root", "dc"))
                .parse();
    }

}
