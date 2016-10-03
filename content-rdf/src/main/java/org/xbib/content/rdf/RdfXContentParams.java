package org.xbib.content.rdf;

import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class RdfXContentParams implements RdfContentParams {

    public static final RdfXContentParams RDF_X_CONTENT_PARAMS = new RdfXContentParams();

    private final IRINamespaceContext namespaceContext;

    private RdfXContentGenerator<RdfXContentParams> generator;

    public RdfXContentParams() {
        this.namespaceContext = IRINamespaceContext.newInstance();
    }

    public RdfXContentParams(IRINamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    public RdfXContentGenerator<RdfXContentParams> getGenerator() {
        return generator;
    }

    public RdfXContentParams setGenerator(RdfXContentGenerator<RdfXContentParams> generator) {
        this.generator = generator;
        return this;
    }

    @Override
    public IRINamespaceContext getNamespaceContext() {
        return namespaceContext;
    }
}
