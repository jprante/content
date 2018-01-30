package org.xbib.content.resource;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;

/**
 * Contains a simple context for XML namespaces.
 */
public class XmlNamespaceContext implements NamespaceContext {

    private static final Logger logger = Logger.getLogger(XmlNamespaceContext.class.getName());

    private static final String DEFAULT_RESOURCE =
            XmlNamespaceContext.class.getPackage().getName().replace('.', '/') + '/' + "namespace";

    private static final XmlNamespaceContext DEFAULT_CONTEXT = newDefaultInstance();

    // sort namespace by length in descending order, useful for compacting prefix
    private final SortedMap<String, String> namespaces;

    private final SortedMap<String, Set<String>> prefixes;

    protected XmlNamespaceContext() {
        this.namespaces = new TreeMap<>();
        this.prefixes = new TreeMap<>();
    }

    protected XmlNamespaceContext(ResourceBundle bundle) {
        this();
        Enumeration<String> en = bundle.getKeys();
        while (en.hasMoreElements()) {
            String prefix = en.nextElement();
            String namespace = bundle.getString(prefix);
            addNamespace(prefix, namespace);
        }
    }

    public static XmlNamespaceContext getInstance() {
        return DEFAULT_CONTEXT;
    }

    /**
     * Empty namespace context.
     *
     * @return an XML namespace context
     */
    public static XmlNamespaceContext newInstance() {
        return new XmlNamespaceContext();
    }

    public static XmlNamespaceContext newDefaultInstance() {
        return newInstance(DEFAULT_RESOURCE);
    }

    /**
     * Use thread context class laoder to instantiate a namespace context.
     * @param bundleName the resource bundle name
     * @return XML namespace context
     */
    public static XmlNamespaceContext newInstance(String bundleName) {
        return newInstance(bundleName, Locale.getDefault(), Thread.currentThread().getContextClassLoader());
    }

    public static XmlNamespaceContext newInstance(String bundleName, Locale locale, ClassLoader classLoader) {
        try {
            return new XmlNamespaceContext(ResourceBundle.getBundle(bundleName, locale, classLoader));
        } catch (MissingResourceException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return new XmlNamespaceContext();
        }
    }

    public void addNamespace(String prefix, String namespace) {
        if (prefix != null && namespace != null) {
            synchronized (namespaces) {
                namespaces.put(prefix, namespace);
                if (prefixes.containsKey(namespace)) {
                    prefixes.get(namespace).add(prefix);
                } else {
                    Set<String> set = new HashSet<>();
                    set.add(prefix);
                    prefixes.put(namespace, set);
                }
            }
        }
    }

    public SortedMap<String, String> getNamespaces() {
        return namespaces;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            return null;
        }
        return namespaces.getOrDefault(prefix, null);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        Iterator<String> it = getPrefixes(namespaceURI);
        return it != null && it.hasNext() ? it.next() : null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException("namespace URI cannot be null");
        }
        return prefixes.containsKey(namespace) ?
                prefixes.get(namespace).iterator() : null;
    }

    @Override
    public String toString() {
        return namespaces.toString();
    }

}
