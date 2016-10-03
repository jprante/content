package org.xbib.content.rdf.internal;

import org.xbib.content.rdf.RdfContentGenerator;
import org.xbib.content.rdf.RdfGraph;
import org.xbib.content.rdf.RdfGraphParams;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.Triple;
import org.xbib.content.resource.IRI;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class DefaultRdfGraph implements RdfGraph<RdfGraphParams> {

    private static final Logger logger = Logger.getLogger(DefaultRdfGraph.class.getName());

    private RdfGraphParams params = DefaultRdfGraphParams.DEFAULT_PARAMS;

    private Map<IRI, Resource> resources = new LinkedHashMap<>();

    @Override
    public Iterator<Resource> getResources() {
        return resources.values().stream().iterator();
    }

    @Override
    public RdfGraph<RdfGraphParams> putResource(IRI id, Resource resource) {
        resources.put(id, resource);
        return this;
    }

    @Override
    public Resource getResource(IRI predicate) {
        return resources.get(predicate);
    }

    @Override
    public Resource removeResource(IRI predicate) {
        return resources.remove(predicate);
    }

    @Override
    public boolean hasResource(IRI predicate) {
        return resources.containsKey(predicate);
    }

    @Override
    public RdfGraphParams getParams() {
        return params;
    }

    @Override
    public DefaultRdfGraph setParams(RdfGraphParams params) {
        this.params = params;
        return this;
    }

    @Override
    public DefaultRdfGraph startStream() {
        return this;
    }

    @Override
    public RdfContentGenerator<RdfGraphParams> setBaseUri(String baseUri) {
        startPrefixMapping("", baseUri);
        return this;
    }

    @Override
    public DefaultRdfGraph startPrefixMapping(String prefix, String uri) {
        params.getNamespaceContext().addNamespace(prefix, uri);
        return this;
    }

    @Override
    public DefaultRdfGraph endPrefixMapping(String prefix) {
        // ignore
        return this;
    }

    @Override
    public DefaultRdfGraph receive(IRI identifier) {
        // ignore
        return this;
    }

    @Override
    public DefaultRdfGraph receive(Triple triple) {
        IRI subject = triple.subject().id();
        if (!resources.containsKey(subject)) {
            resources.put(subject, new DefaultResource(subject));
        }
        resources.get(subject).add(triple);
        return this;
    }

    @Override
    public DefaultRdfGraph endStream() {
        return this;
    }

    @Override
    public DefaultRdfGraph receive(Resource resource) throws IOException {
        resources.put(resource.id(), resource);
        return this;
    }

    @Override
    public void close() throws IOException {
        // nothing to do here
    }

    @Override
    public void flush() throws IOException {
        // nothing to do here
    }

    private Resource expand(Resource resource) {
        Resource expanded = new DefaultResource(resource.id());
        new GraphTriples(resource).triples.forEach(expanded::add);
        return expanded;
    }

    private class GraphTriples {

        private final List<Triple> triples;

        GraphTriples(Resource resource) {
            this.triples = unfold(resource);
        }

        private List<Triple> unfold(final Resource resource) {
            List<Triple> list = new LinkedList<>();
            if (resource == null) {
                return list;
            }
            resource.predicates().forEach(pred -> resource.objects(pred).forEach(node -> {
                        if (node instanceof Resource) {
                            Resource resource1 = (Resource) node;
                            if (resource1.isEmbedded()) {
                                Resource r = getResource(resource1.id());
                                if (r != null) {
                                    list.add(new DefaultTriple(resource1, pred, r.id()));
                                    list.addAll(unfold(r));
                                } else {
                                    logger.log(Level.SEVERE, "huh? {}", resource1.id());
                                }
                            } else {
                                list.addAll(unfold(resource1));
                            }
                        } else {
                            list.add(new DefaultTriple(resource, pred, node));
                        }
                    }
            ));
            return list;
        }
    }


}
