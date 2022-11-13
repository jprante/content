package org.xbib.content.rdf;

import org.xbib.content.resource.IRI;
import org.xbib.content.resource.Node;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @param <R> parameter type
 */
public class RouteRdfXContentGenerator<R extends RouteRdfXContentParams> extends RdfXContentGenerator<R> {

    private boolean flushed;

    RouteRdfXContentGenerator(OutputStream out) {
        super(out);
    }

    @Override
    public RdfXContentGenerator<R> startStream() {
        super.startStream();
        flushed = false;
        return this;
    }

    @Override
    public RdfXContentGenerator<R> receive(IRI identifier) throws IOException {
        super.receive(identifier);
        flushed = false;
        return this;
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        if (flushed) {
            return;
        }
        flushed = true;
        RouteRdfXContent.RouteHandler handler = getParams().getHandler();
        if (handler != null) {
            String s = getParams().getGenerator().get();
            if (s != null && !s.isEmpty()) {
                if (resource.id() != null) {
                    getParams().setId(resource.id().toString());
                }
                handler.complete(s, getParams());
            }
        }
    }

    @Override
    public void filter(IRI predicate, Node object) {
        String indexPredicate = getParams().getIndexPredicate();
        if (indexPredicate != null && indexPredicate.equals(predicate.toString())) {
            getParams().setIndex(object.toString());
        }
        String typePredicate = getParams().getIdPredicate();
        if (typePredicate != null && typePredicate.equals(predicate.toString())) {
            getParams().setType(object.toString());
        }
        String idPredicate = getParams().getIdPredicate();
        if (idPredicate != null && idPredicate.equals(predicate.toString())) {
            getParams().setId(object.toString());
        }
    }
}
