package org.xbib.content.rdf.io.rdfxml;

import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class RdfXmlContentParams implements RdfContentParams {

    public static final RdfXmlContentParams RDF_XML_CONTENT_PARAMS = new RdfXmlContentParams();
    private final IRINamespaceContext namespaceContext;

    public RdfXmlContentParams() {
        this.namespaceContext = IRINamespaceContext.newInstance();
    }

    public RdfXmlContentParams(IRINamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    @Override
    public IRINamespaceContext getNamespaceContext() {
        return namespaceContext;
    }
}
