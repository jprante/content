package org.xbib.content.rdf.internal;

import org.xbib.content.rdf.RdfGraphParams;
import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class DefaultRdfGraphParams implements RdfGraphParams {

    public static final DefaultRdfGraphParams DEFAULT_PARAMS = new DefaultRdfGraphParams();
    private final IRINamespaceContext namespaceContext;
    private final boolean writeNamespaceContext;

    public DefaultRdfGraphParams() {
        this.namespaceContext = IRINamespaceContext.newInstance();
        this.writeNamespaceContext = true;
    }

    public DefaultRdfGraphParams(IRINamespaceContext namespaceContext, boolean writeNamespaceContext) {
        this.namespaceContext = namespaceContext;
        this.writeNamespaceContext = writeNamespaceContext;
    }

    @Override
    public IRINamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    public boolean isWriteNamespaceContext() {
        return writeNamespaceContext;
    }
}
