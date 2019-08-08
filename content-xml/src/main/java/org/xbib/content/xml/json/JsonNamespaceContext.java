package org.xbib.content.xml.json;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

/**
 *
 */
public class JsonNamespaceContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

}
