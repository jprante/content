package org.xbib.content.rdf.io.xml;

import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.resource.NamespaceContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

/**
 * XML handler.
 * @param <P> parameter type
 */
public interface XmlHandler<P extends RdfContentParams>
        extends EntityResolver, DTDHandler, ContentHandler, ErrorHandler {

    NamespaceContext getNamespaceContext();

    XmlHandler<P> setNamespaceContext(NamespaceContext namespaceContext);

    XmlHandler<P> setDefaultNamespace(String prefix, String uri);

    XmlHandler<P> setBuilder(RdfContentBuilder<P> builder);

}
