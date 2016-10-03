package org.xbib.content.xml.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Simple <code>javax.xml.namespace.NamespaceContext</code> implementation. Follows the standard
 * <code>NamespaceContext</code> contract, and is loadable via a <code>java.util.Map</code> or
 * <code>java.util.Properties</code> object
 */
public class SimpleNamespaceContext implements NamespaceContext {

    private Map<String, String> prefixToNamespaceUri = new HashMap<>();

    private Map<String, List<String>> namespaceUriToPrefixes = new HashMap<>();

    private String defaultNamespaceUri = "";

    @Override
    public String getNamespaceURI(String prefix) {
        if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {
            return XMLConstants.XML_NS_URI;
        } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {
            return defaultNamespaceUri;
        } else if (prefixToNamespaceUri.containsKey(prefix)) {
            return prefixToNamespaceUri.get(prefix);
        }
        return "";
    }

    @Override
    public String getPrefix(String namespaceUri) {
        List<String> prefixes = getPrefixesInternal(namespaceUri);
        return prefixes.isEmpty() ? null : prefixes.get(0);
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceUri) {
        return getPrefixesInternal(namespaceUri).iterator();
    }

    /**
     * Sets the bindings for this namespace context. The supplied map must consist of string key value pairs.
     *
     * @param bindings the bindings
     */
    public void setBindings(Map<String, String> bindings) {
        for (Map.Entry<String, String> entry : bindings.entrySet()) {
            bindNamespaceUri(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Binds the given namespace as default namespace.
     *
     * @param namespaceUri the namespace uri
     */
    public void bindDefaultNamespaceUri(String namespaceUri) {
        bindNamespaceUri(XMLConstants.DEFAULT_NS_PREFIX, namespaceUri);
    }

    /**
     * Binds the given prefix to the given namespace.
     *
     * @param prefix       the namespace prefix
     * @param namespaceUri the namespace uri
     */
    public void bindNamespaceUri(String prefix, String namespaceUri) {
        if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {
            defaultNamespaceUri = namespaceUri;
        } else {
            prefixToNamespaceUri.put(prefix, namespaceUri);
            getPrefixesInternal(namespaceUri).add(prefix);
        }
    }

    /**
     * Removes all declared prefixes.
     */
    public void clear() {
        prefixToNamespaceUri.clear();
    }

    /**
     * Returns all declared prefixes.
     *
     * @return the declared prefixes
     */
    public Iterator<String> getBoundPrefixes() {
        return prefixToNamespaceUri.keySet().iterator();
    }

    private List<String> getPrefixesInternal(String namespaceUri) {
        if (defaultNamespaceUri.equals(namespaceUri)) {
            return Collections.singletonList(XMLConstants.DEFAULT_NS_PREFIX);
        } else if (XMLConstants.XML_NS_URI.equals(namespaceUri)) {
            return Collections.singletonList(XMLConstants.XML_NS_PREFIX);
        } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceUri)) {
            return Collections.singletonList(XMLConstants.XMLNS_ATTRIBUTE);
        } else {
            List<String> list = namespaceUriToPrefixes.get(namespaceUri);
            if (list == null) {
                list = new ArrayList<>();
                namespaceUriToPrefixes.put(namespaceUri, list);
            }
            return list;
        }
    }

    /**
     * Removes the given prefix from this context.
     *
     * @param prefix the prefix to be removed
     */
    public void removeBinding(String prefix) {
        if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {
            defaultNamespaceUri = "";
        } else {
            String namespaceUri = prefixToNamespaceUri.remove(prefix);
            List<String> prefixes = getPrefixesInternal(namespaceUri);
            prefixes.remove(prefix);
        }
    }
}
