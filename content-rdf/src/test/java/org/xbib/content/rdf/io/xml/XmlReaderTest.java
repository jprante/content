package org.xbib.content.rdf.io.xml;

import static org.xbib.content.rdf.RdfContentFactory.turtleBuilder;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.rdf.io.IOTests;
import org.xbib.content.rdf.io.ntriple.NTripleContent;
import org.xbib.content.rdf.io.ntriple.NTripleContentParams;
import org.xbib.content.rdf.io.turtle.TurtleContentParams;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.content.resource.text.CharUtils.Profile;
import org.xbib.content.resource.url.UrlEncoding;
import org.xbib.helper.StreamTester;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

/**
 *
 */
@Category(IOTests.class)
public class XmlReaderTest extends StreamTester {

    private static final Logger logger = Logger.getLogger(XmlReaderTest.class.getName());

    @Test
    public void testOAIDC() throws Exception {
        String filename = "oro-eprint-25656.xml";
        InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            throw new IOException("file " + filename + " not found");
        }

        IRINamespaceContext namespaceContext = IRINamespaceContext.newInstance();
        namespaceContext.addNamespace("oaidc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        namespaceContext.addNamespace("dc", "http://purl.org/dc/elements/1.1/");

        XmlContentParams params = new XmlContentParams(namespaceContext);
        XmlHandler<TurtleContentParams> xmlHandler = new AbstractXmlResourceHandler<TurtleContentParams>(params) {

            @Override
            public boolean isResourceDelimiter(QName name) {
                return "oai_dc".equals(name.getLocalPart());
            }

            @Override
            public void identify(QName name, String value, IRI identifier) {
                if ("identifier".equals(name.getLocalPart()) && DefaultResource.isBlank(getResource())) {
                    try {
                        // make sure we can build an opaque IRI, whatever is out there
                        String s = UrlEncoding.encode(value, Profile.SCHEMESPECIFICPART.filter());
                        getResource().setId(IRI.create("id:" + s));
                    } catch (IOException e) {
                        logger.log(Level.FINE, e.getMessage(), e);
                    }
                }
            }

            @Override
            public boolean skip(QName name) {
                // skip dc:dc element
                return "dc".equals(name.getLocalPart());
            }

            @Override
            public XmlHandler<TurtleContentParams> setNamespaceContext(IRINamespaceContext namespaceContext) {
                return this;
            }

            @Override
            public IRINamespaceContext getNamespaceContext() {
                return namespaceContext;
            }
        };
        TurtleContentParams turtleParams = new TurtleContentParams(namespaceContext, true);
        RdfContentBuilder<TurtleContentParams> builder = turtleBuilder(turtleParams);
        xmlHandler.setBuilder(builder);
        new XmlContentParser<TurtleContentParams>(in)
                .setHandler(xmlHandler)
                .parse();
        assertStream(getClass().getResource("dc.ttl").openStream(),
                builder.streamInput());
    }

    @Test
    public void testXmlArray() throws Exception {
        String filename = "array.xml";
        InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            throw new IOException("file " + filename + " not found");
        }
        IRINamespaceContext namespaceContext = IRINamespaceContext.newInstance();
        XmlContentParams params = new XmlContentParams(namespaceContext);
        AbstractXmlHandler<NTripleContentParams> xmlHandler = new AbstractXmlResourceHandler<NTripleContentParams>(params) {

            @Override
            public boolean isResourceDelimiter(QName name) {
                return false;
            }

            @Override
            public void identify(QName name, String value, IRI identifier) {
                getResource().setId(IRI.create("id:1"));
            }

            @Override
            public boolean skip(QName name) {
                return false;
            }

            @Override
            public XmlHandler<NTripleContentParams> setNamespaceContext(IRINamespaceContext namespaceContext) {
                return this;
            }

            @Override
            public IRINamespaceContext getNamespaceContext() {
                return namespaceContext;
            }
        };

        MyBuilder builder = new MyBuilder();
        xmlHandler.setDefaultNamespace("xml", "http://xmltest")
                .setBuilder(builder);
        DefaultAnonymousResource.reset();
        new XmlContentParser<NTripleContentParams>(in)
                .setHandler(xmlHandler)
                .parse();
        assertEquals("[id:1 xml:dates _:b2, _:b2 xml:date 2001, _:b2 xml:date 2002, _:b2 xml:date 2003]",
                builder.getTriples().toString()
        );
    }

    @Test
    public void testXmlAttribute() throws Exception {
        String filename = "attribute.xml";
        InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            throw new IOException("file " + filename + " not found");
        }
        IRINamespaceContext namespaceContext = IRINamespaceContext.newInstance();
        XmlContentParams params = new XmlContentParams(namespaceContext);
        AbstractXmlHandler<NTripleContentParams> xmlHandler = new AbstractXmlResourceHandler<NTripleContentParams>(params) {
            @Override
            public boolean isResourceDelimiter(QName name) {
                return false;
            }

            @Override
            public void identify(QName name, String value, IRI identifier) {
                getResource().setId(IRI.create("id:1"));
            }

            @Override
            public boolean skip(QName name) {
                return false;
            }

            @Override
            public XmlHandler<NTripleContentParams> setNamespaceContext(IRINamespaceContext namespaceContext) {
                return this;
            }

            @Override
            public IRINamespaceContext getNamespaceContext() {
                return namespaceContext;
            }
        };

        MyBuilder builder = new MyBuilder();

        xmlHandler.setDefaultNamespace("xml", "http://localhost")
                .setBuilder(builder);
        DefaultAnonymousResource.reset();
        new XmlContentParser<NTripleContentParams>(in)
                .setHandler(xmlHandler)
                .parse();
        assertEquals("[id:1 xml:dates _:b2, _:b2 xml:date _:b3, _:b3 xml:@href 1, _:b2 xml:date _:b5, "
            + "_:b5 xml:@href 2, _:b2 xml:date _:b7, _:b7 xml:@href 3, _:b2 xml:date _:b9, _:b9 xml:hello World]",
            builder.getTriples().toString()
        );
    }

    private class MyBuilder extends RdfContentBuilder<NTripleContentParams> {

        final List<Triple> triples = new LinkedList<>();

        MyBuilder() throws IOException {
            super(NTripleContent.nTripleContent(), NTripleContentParams.N_TRIPLE_CONTENT_PARAMS);
        }

        @Override
        public MyBuilder receive(Triple triple) {
            triples.add(triple);
            return this;
        }

        @Override
        public MyBuilder receive(Resource resource) throws IOException {
            resource.triples().forEach(triples::add);
            return this;
        }

        List<Triple> getTriples() {
            return triples;
        }
    }

}
