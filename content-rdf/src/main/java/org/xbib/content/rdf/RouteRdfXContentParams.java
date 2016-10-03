package org.xbib.content.rdf;

import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class RouteRdfXContentParams extends RdfXContentParams {

    public static final RouteRdfXContentParams ROUTE_RDF_X_CONTENT_PARAMS = new RouteRdfXContentParams();

    private String index;

    private String type;

    private String id;

    private String indexPredicate;

    private String typePredicate;

    private String idPredicate;

    private RouteRdfXContent.RouteHandler handler;

    public RouteRdfXContentParams() {
        super();
    }

    public RouteRdfXContentParams(String index, String type) {
        super();
        this.index = index;
        this.type = type;
    }

    public RouteRdfXContentParams(IRINamespaceContext namespaceContext) {
        super(namespaceContext);
    }

    public RouteRdfXContentParams(IRINamespaceContext namespaceContext, String index, String type) {
        super(namespaceContext);
        this.index = index;
        this.type = type;
    }

    public String getIndex() {
        return index;
    }

    public RouteRdfXContentParams setIndex(String index) {
        this.index = index;
        return this;
    }

    public String getType() {
        return type;
    }

    public RouteRdfXContentParams setType(String type) {
        this.type = type;
        return this;
    }

    public String getId() {
        return id;
    }

    public RouteRdfXContentParams setId(String id) {
        this.id = id;
        return this;
    }

    public String getIndexPredicate() {
        return indexPredicate;
    }

    public RouteRdfXContentParams setIndexPredicate(String indexPredicate) {
        this.indexPredicate = indexPredicate;
        return this;
    }

    public String getTypePredicate() {
        return typePredicate;
    }

    public RouteRdfXContentParams setTypePredicate(String typePredicate) {
        this.typePredicate = typePredicate;
        return this;
    }

    public String getIdPredicate() {
        return idPredicate;
    }

    public RouteRdfXContentParams setIdPredicate(String idPredicate) {
        this.idPredicate = idPredicate;
        return this;
    }

    public RouteRdfXContent.RouteHandler getHandler() {
        return handler;
    }

    public RouteRdfXContentParams setHandler(RouteRdfXContent.RouteHandler handler) {
        this.handler = handler;
        return this;
    }
}
