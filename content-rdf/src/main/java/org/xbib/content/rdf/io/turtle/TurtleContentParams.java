package org.xbib.content.rdf.io.turtle;

import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class TurtleContentParams implements RdfContentParams {

    public static final TurtleContentParams TURTLE_CONTENT_PARAMS = new TurtleContentParams();
    private final IRINamespaceContext namespaceContext;
    private final boolean writeNamespaceContext;

    public TurtleContentParams() {
        this.namespaceContext = IRINamespaceContext.newInstance();
        this.writeNamespaceContext = true;
    }

    public TurtleContentParams(IRINamespaceContext namespaceContext, boolean writeNamespaceContext) {
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
