package org.xbib.content.rdf.io.xml;

import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class XmlContentParams implements RdfContentParams {

    protected static final IRINamespaceContext NAMESPACE_CONTEXT = IRINamespaceContext.newInstance();
    public static final XmlContentParams XML_CONTENT_PARAMS = new XmlContentParams(NAMESPACE_CONTEXT);
    private final IRINamespaceContext namespaceContext;

    public XmlContentParams(IRINamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    @Override
    public IRINamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

}
