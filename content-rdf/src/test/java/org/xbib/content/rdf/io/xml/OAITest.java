package org.xbib.content.rdf.io.xml;

import static org.xbib.content.rdf.RdfContentFactory.turtleBuilder;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.io.IOTests;
import org.xbib.content.rdf.io.turtle.TurtleContentParams;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.content.resource.text.CharUtils;
import org.xbib.content.resource.url.UrlEncoding;
import org.xbib.helper.StreamTester;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

/**
 *
 */
public class OAITest extends StreamTester {

    private static final Logger logger = Logger.getLogger(OAITest.class.getName());

    @Test
    @Category(IOTests.class)
    public void testOAIListRecordsToTurtle() throws Exception {
        String filename = "oai-listrecords.xml";
        InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            throw new IOException("file " + filename + " not found");
        }

        IRINamespaceContext context = IRINamespaceContext.newInstance();
        XmlContentParams params = new XmlContentParams(context);
        XmlHandler<TurtleContentParams> xmlHandler = new AbstractXmlResourceHandler<TurtleContentParams>(params) {

            @Override
            public boolean isResourceDelimiter(QName name) {
                return "oai_dc".equals(name.getLocalPart());
            }

            @Override
            public void identify(QName name, String value, IRI identifier) {
                if ("identifier".equals(name.getLocalPart())) {
                    // make sure we can build an opaque IRI, whatever is out there
                    try {
                        getResource().setId(IRI.create("id:"
                                + UrlEncoding.encode(value, CharUtils.Profile.SCHEMESPECIFICPART.filter())));
                    } catch (IOException e) {
                        logger.log(Level.FINE, e.getMessage(), e);
                    }
                }
            }

            @Override
            public boolean skip(QName name) {
                return name.getLocalPart().startsWith("@");
            }

            @Override
            public XmlHandler<TurtleContentParams> setNamespaceContext(IRINamespaceContext namespaceContext) {
                return this;
            }

            @Override
            public IRINamespaceContext getNamespaceContext() {
                return context;
            }
        };
        TurtleContentParams turtleParams = new TurtleContentParams(context, true);
        RdfContentBuilder<TurtleContentParams> builder = turtleBuilder(turtleParams);
        xmlHandler.setBuilder(builder)
                .setNamespaceContext(context)
                .setDefaultNamespace("oai", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        XmlContentParser<TurtleContentParams> parser = new XmlContentParser<>(in);
        parser.builder(builder);
        parser.setHandler(xmlHandler)
                .parse();
        assertStream(getClass().getResourceAsStream("oai.ttl"), builder.streamInput());
    }
}
