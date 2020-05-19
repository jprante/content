package org.xbib.content.rdf.io.sink;

import org.xbib.content.rdf.Literal;
import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentBuilderProvider;
import org.xbib.content.rdf.RdfGraph;
import org.xbib.content.rdf.RdfGraphParams;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.rdf.internal.DefaultLiteral;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.rdf.internal.DefaultTriple;
import org.xbib.content.resource.IRI;

import java.io.IOException;
import java.util.Iterator;

/**
 *
 */
public class RdfContentBuilderSink implements QuadSink {

    private final RdfGraph<RdfGraphParams> graph;

    private final RdfContentBuilderProvider<RdfGraphParams> provider;

    public RdfContentBuilderSink(RdfGraph<RdfGraphParams> graph,
                                 RdfContentBuilderProvider<RdfGraphParams> provider) {
        this.graph = graph;
        this.provider = provider;
    }

    @Override
    public void addNonLiteral(String subj, String pred, String obj) throws IOException {
        Resource s = DefaultResource.create(graph.getParams().getNamespaceContext(), subj);
        IRI p = IRI.create(pred);
        Resource o = DefaultResource.create(graph.getParams().getNamespaceContext(), obj);
        Triple t = new DefaultTriple(s, p, o);
        graph.receive(t);
    }

    @Override
    public void addNonLiteral(String subj, String pred, String obj, String graphIRI) throws IOException {
        Resource s = DefaultResource.create(graph.getParams().getNamespaceContext(), subj);
        IRI p = IRI.create(pred);
        Resource o = DefaultResource.create(graph.getParams().getNamespaceContext(), obj);
        Triple t = new DefaultTriple(s, p, o);
        graph.receive(t);
    }

    @Override
    public void addPlainLiteral(String subj, String pred, String content, String lang) throws IOException {
        Resource s = DefaultResource.create(graph.getParams().getNamespaceContext(), subj);
        IRI p = IRI.create(pred);
        Literal o = new DefaultLiteral(content).lang(lang);
        Triple t = new DefaultTriple(s, p, o);
        graph.receive(t);
    }

    @Override
    public void addPlainLiteral(String subj, String pred, String content, String lang, String graphIRI) throws IOException {
        Resource s = DefaultResource.create(graph.getParams().getNamespaceContext(), subj);
        IRI p = IRI.create(pred);
        Literal o = new DefaultLiteral(content).lang(lang);
        Triple t = new DefaultTriple(s, p, o);
        graph.receive(t);
    }

    @Override
    public void addTypedLiteral(String subj, String pred, String content, String type) throws IOException {
        Resource s = DefaultResource.create(graph.getParams().getNamespaceContext(), subj);
        IRI p = IRI.create(pred);
        Literal o = new DefaultLiteral(content).type(IRI.create(type));
        Triple t = new DefaultTriple(s, p, o);
        graph.receive(t);
    }

    @Override
    public void addTypedLiteral(String subj, String pred, String content, String type, String graphIRI) throws IOException {
        Resource s = DefaultResource.create(graph.getParams().getNamespaceContext(), subj);
        IRI p = IRI.create(pred);
        Literal o = new DefaultLiteral(content).type(IRI.create(type));
        Triple t = new DefaultTriple(s, p, o);
        graph.receive(t);
    }

    @Override
    public void setBaseUri(String baseUri) {
        // we don't have a base URI
    }

    @Override
    public void startStream() throws IOException {
        // not used
    }

    @Override
    public void endStream() throws IOException {
        if (graph.getResources() != null) {
            if (provider != null) {
                Iterator<Resource> iterator = graph.getResources();
                while (iterator.hasNext()) {
                    Resource resource = iterator.next();
                    RdfContentBuilder<RdfGraphParams> rdfContentBuilder;
                        rdfContentBuilder = provider.newContentBuilder();
                        rdfContentBuilder.startStream();
                        rdfContentBuilder.receive(resource);
                        rdfContentBuilder.endStream();
                }
            }
        }
    }

    @Override
    public void beginDocument(String id) throws IOException {
        IRI iri = graph.getParams().getNamespaceContext().expandIRI(id);
        graph.receive(iri);
    }

    @Override
    public void endDocument(String id) {
        // not used
    }
}
