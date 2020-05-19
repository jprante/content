package org.xbib.content.resource;

import java.util.Iterator;
import java.util.SortedMap;

public interface NamespaceContext {

    String getNamespaceURI(String prefix);

    String getPrefix(String namespaceURI);

    Iterator<String> getPrefixes(String namespaceURI);

    SortedMap<String, String> getNamespaces();

    void addNamespace(String prefix, String namespace);
}
