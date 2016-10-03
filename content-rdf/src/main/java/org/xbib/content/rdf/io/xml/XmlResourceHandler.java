package org.xbib.content.rdf.io.xml;

import org.xbib.content.rdf.RdfContentParams;

import java.io.IOException;

import javax.xml.namespace.QName;

/**
 * XML resource handler.
 * @param <P> parameter type
 */
public interface XmlResourceHandler<P extends RdfContentParams> extends XmlHandler<P> {

    void openResource() throws IOException;

    void closeResource() throws IOException;

    void openPredicate(QName parent, QName name, int level);

    void addToPredicate(QName parent, String content);

    void closePredicate(QName parent, QName name, int level);

    Object toObject(QName name, String content);

}
