package org.xbib.content.rdf.io.xml;

import org.xbib.content.rdf.Literal;
import org.xbib.content.rdf.RdfContentGenerator;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.XMLEventConsumer;

/**
 * Write resource as XML to stream.
 */
public class XmlContentGenerator implements RdfContentGenerator<XmlContentParams>, Flushable, XmlConstants {

    private static final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    private static final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    private final Writer writer;
    private Resource resource;

    private XmlContentParams params = XmlContentParams.XML_CONTENT_PARAMS;

    public XmlContentGenerator(OutputStream out) throws IOException {
        this(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    public XmlContentGenerator(Writer writer) throws IOException {
        this.writer = writer;
    }

    public XmlContentParams getParams() {
        return params;
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public XmlContentGenerator receive(IRI iri) throws IOException {
        if (!iri.equals(resource.id())) {
            receive(resource);
            resource = new DefaultResource(iri);
        }
        return this;
    }

    @Override
    public RdfContentGenerator<XmlContentParams> setParams(XmlContentParams rdfContentParams) {
        this.params = rdfContentParams;
        return this;
    }

    @Override
    public XmlContentGenerator startStream() {
        return this;
    }

    @Override
    public RdfContentGenerator<XmlContentParams> setBaseUri(String baseUri) {
        startPrefixMapping("", baseUri);
        return this;
    }

    @Override
    public XmlContentGenerator receive(Triple triple) {
        resource.add(triple);
        return this;
    }

    @Override
    public XmlContentGenerator endStream() {
        return this;
    }

    @Override
    public XmlContentGenerator startPrefixMapping(String prefix, String uri) {
        if (prefix == null || prefix.isEmpty() || XML_SCHEMA_URI.equals(uri)) {
            return this;
        }
        params.getNamespaceContext().addNamespace(prefix, uri);
        return this;
    }

    @Override
    public XmlContentGenerator endPrefixMapping(String prefix) {
        // we don't remove name spaces. It's troubling RDF serializations.
        return this;
    }

    @Override
    public XmlContentGenerator receive(Resource resource) throws IOException {
        if (resource == null) {
            return this;
        }
        try {
            XMLEventWriter xew = outputFactory.createXMLEventWriter(writer);
            IRI resourceURI = resource.id();
            String nsPrefix = resourceURI.getScheme();
            String name = resourceURI.getSchemeSpecificPart();
            String nsURI = params.getNamespaceContext().getNamespaceURI(nsPrefix);
            writeResource(xew, resource, new QName(nsURI, name, nsPrefix));
            xew.close();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
        return this;
    }


    private void writeResource(XMLEventConsumer consumer, Resource resource, QName parent)
            throws XMLStreamException {
        boolean startElementWritten = false;
        List<Triple> triples = resource.properties();
        for (Triple triple : triples) {
            if (!startElementWritten) {
                if (parent != null) {
                    consumer.add(eventFactory.createStartElement(parent, null, null));
                }
                startElementWritten = true;
            }
            write(consumer, triple);
        }
        if (!startElementWritten && parent != null) {
            consumer.add(eventFactory.createStartElement(parent, null, null));
        }
        if (parent != null) {
            consumer.add(eventFactory.createEndElement(parent, null));
        }
    }

    private void write(XMLEventConsumer consumer, Triple triple)
            throws XMLStreamException {
        IRI predicate = triple.predicate();
        Node object = triple.object();
        String nsPrefix = predicate.getScheme();
        String name = predicate.getSchemeSpecificPart();
        String nsURI = params.getNamespaceContext().getNamespaceURI(nsPrefix);
        if (object instanceof Resource) {
            writeResource(consumer, (Resource) object, new QName(nsURI, name, nsPrefix));
        } else if (object instanceof Literal) {
            String literal = ((Literal) object).object().toString();
            consumer.add(eventFactory.createStartElement(nsPrefix, nsURI, name));
            consumer.add(eventFactory.createCharacters(literal));
            consumer.add(eventFactory.createEndElement(nsPrefix, nsURI, name));
        } else {
            throw new XMLStreamException("can't write object class: " + object.getClass().getName());
        }

    }
}
