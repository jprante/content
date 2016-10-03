package org.xbib.content.rdf.io.ntriple;

import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class NTripleContentParams implements RdfContentParams {

    public static final NTripleContentParams N_TRIPLE_CONTENT_PARAMS = new NTripleContentParams();
    private final IRINamespaceContext namespaceContext;

    public NTripleContentParams() {
        this.namespaceContext = IRINamespaceContext.newInstance();
    }

    public NTripleContentParams(IRINamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    @Override
    public IRINamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

}
