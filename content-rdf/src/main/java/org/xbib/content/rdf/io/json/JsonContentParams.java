package org.xbib.content.rdf.io.json;

import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.rdf.io.xml.XmlContentParams;
import org.xbib.content.resource.IRINamespaceContext;

/**
 *
 */
public class JsonContentParams extends XmlContentParams implements RdfContentParams {

    public static final JsonContentParams JSON_CONTENT_PARAMS = new JsonContentParams(NAMESPACE_CONTEXT);

    public JsonContentParams(IRINamespaceContext namespaceContext) {
        super(namespaceContext);
    }
}
