package org.xbib.content.rdf;

import org.xbib.content.XContentBuilder;
import org.xbib.content.core.DefaultXContentBuilder;
import org.xbib.content.json.JsonXContent;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.internal.DefaultResource;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @param <P> the parameter type
 */
public class RdfXContentGenerator<P extends RdfXContentParams> implements RdfContentGenerator<P> {

    protected final OutputStream out;

    protected Resource resource;

    protected XContentBuilder builder;

    private P params;

    private boolean flushed;

    RdfXContentGenerator(OutputStream out) {
        this.out = out;
    }

    @Override
    public P getParams() {
        return params;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RdfXContentGenerator<P> setParams(P rdfContentParams) {
        this.params = rdfContentParams;
        params.setGenerator((RdfXContentGenerator<RdfXContentParams>) this);
        return null;
    }

    @Override
    public RdfXContentGenerator<P> startStream() {
        this.resource = new DefaultAnonymousResource();
        flushed = false;
        return this;
    }

    @Override
    public RdfContentGenerator<P> setBaseUri(String baseUri) {
        startPrefixMapping("", baseUri);
        return this;
    }

    @Override
    public RdfXContentGenerator<P> startPrefixMapping(String prefix, String uri) {
        params.getNamespaceContext().addNamespace(prefix, uri);
        return this;
    }

    @Override
    public RdfXContentGenerator<P> endPrefixMapping(String prefix) {
        return this;
    }

    @Override
    public RdfXContentGenerator<P> receive(IRI identifier) throws IOException {
        this.resource = new DefaultResource(identifier);
        flushed = false;
        return this;
    }

    @Override
    public RdfXContentGenerator<P> receive(Triple triple) {
        this.resource.add(triple);
        return this;
    }

    @Override
    public RdfXContentGenerator<P> endStream() throws IOException {
        if (resource != null && !resource.isEmpty()) {
            flush();
        }
        return this;
    }

    @Override
    public void close() throws IOException {
        flush();
    }

    @Override
    public void flush() throws IOException {
        if (this.resource == null || this.resource.isEmpty() || flushed) {
            return;
        }
        flushed = true;
        // JSON output
        builder = DefaultXContentBuilder.builder(JsonXContent.jsonContent(), out);
        builder.startObject();
        build(this.resource);
        builder.endObject();
    }

    @Override
    public RdfXContentGenerator<P> receive(Resource resource) throws IOException {
        if (resource != null) {
            this.resource = resource;
        }
        flush();
        return this;
    }

    public String string() throws IOException {
        if (builder != null) {
            return builder.string();
        }
        return null;
    }

    public String get() throws IOException {
        return string();
    }

    public void filter(IRI predicate, Node object) {
        // empty
    }

    protected void build(Resource resource) throws IOException {
        if (resource == null) {
            return;
        }
        for (IRI predicate : resource.predicates()) {
            // first, the values
            final List<Object> values = new ArrayList<>(32);
            final List<Node> nodes = resource.externalObjects(predicate);
            for (Node node : nodes) {
                if (node instanceof Resource) {
                    values.add(((Resource) node).id().toString()); // URLs
                } else if (node instanceof Literal) {
                    Object o = ((Literal) node).object();
                    if (o != null) {
                        values.add(o);
                    }
                }
                filter(predicate, node);
            }
            if (values.size() == 1) {
                builder.field(params.getNamespaceContext().compact(predicate), values.get(0));
            } else if (values.size() > 1) {
                builder.array(params.getNamespaceContext().compact(predicate), values);
            }
            // second, the embedded resources
            final Collection<Resource> resources = resource.embeddedResources(predicate);
            if (resources.size() == 1) {
                Resource res = resources.iterator().next();
                if (!res.isEmpty()) {
                    builder.field(params.getNamespaceContext().compact(predicate));
                    builder.startObject();
                    build(res);
                    builder.endObject();
                }
            } else if (resources.size() > 1) {
                builder.field(params.getNamespaceContext().compact(predicate));
                builder.startArray();
                for (Resource res : resources) {
                    if (!res.isEmpty()) {
                        builder.startObject();
                        build(res);
                        builder.endObject();
                    }
                }
                builder.endArray();
            }
        }
    }
}
