package org.xbib.content.resource;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
public final class IRINamespaceContext implements NamespaceContext {

    private static final String DEFAULT_RESOURCE =
            IRINamespaceContext.class.getPackage().getName().replace('.', '/') + '/' + "namespace";

    private static IRINamespaceContext instance;

    private static final IRINamespaceContext DEFAULT_CONTEXT = newInstance(DEFAULT_RESOURCE);

    // sort namespace by length in descending order, useful for compacting prefix
    private final SortedMap<String, String> namespaces;

    private final SortedMap<String, Set<String>> prefixes;

    protected final Object lock;

    private List<String> sortedNamespacesByPrefixLength;


    public static IRINamespaceContext getInstance() {
        return DEFAULT_CONTEXT;
    }

    public static IRINamespaceContext newInstance(String bundleName) {
        return newInstance(bundleName, Locale.getDefault(), Thread.currentThread().getContextClassLoader());
    }

    public static IRINamespaceContext newInstance(String bundleName, Locale locale, ClassLoader classLoader) {
        if (instance == null) {
            try {
                instance = new IRINamespaceContext(ResourceBundle.getBundle(bundleName, locale, classLoader));
            } catch (MissingResourceException e) {
                instance = new IRINamespaceContext();
            }
        }
        return instance;
    }

    public static IRINamespaceContext newInstance() {
        return new IRINamespaceContext();
    }

    protected IRINamespaceContext() {
        this.namespaces = new TreeMap<>();
        this.prefixes = new TreeMap<>();
        this.lock = new Object();
    }

    private IRINamespaceContext(ResourceBundle bundle) {
        this();
        Enumeration<String> en = bundle.getKeys();
        while (en.hasMoreElements()) {
            String prefix = en.nextElement();
            String namespace = bundle.getString(prefix);
            addNamespace(prefix, namespace);
        }
    }

    @Override
    public void addNamespace(String prefix, String namespace) {
        if (prefix != null && namespace != null) {
            synchronized (lock) {
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
        synchronized (lock) {
            sortedNamespacesByPrefixLength = new ArrayList<>(getNamespaces().values());
            // sort from longest to shortest prefix for successful matching
            sortedNamespacesByPrefixLength.sort((s1, s2) -> {
                Integer l1 = s1.length();
                Integer l2 = s2.length();
                return l2.compareTo(l1);
            });
        }
    }

    public IRINamespaceContext add(Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            addNamespace(e.getKey(), e.getValue());
        }
        synchronized (lock) {
            sortedNamespacesByPrefixLength = new ArrayList<>(getNamespaces().values());
            // sort from longest to shortest prefix for successful matching
            sortedNamespacesByPrefixLength.sort((s1, s2) -> {
                Integer l1 = s1.length();
                Integer l2 = s2.length();
                return l2.compareTo(l1);
            });
        }
        return this;
    }

    /**
     * Abbreviate an URI with a full namespace URI to a short form URI with help of
     * the prefix in this namespace context.
     *
     * @param uri the long URI
     * @return a compact short URI or the original URI if there is no prefix in
     * this context
     */
    public String compact(IRI uri) {
        return compact(uri, false);
    }

    public String compact(IRI uri, boolean dropfragment) {
        if (uri == null) {
            return null;
        }
        // drop fragment (useful for resource counters in fragments)
        String s = dropfragment ? new IRI(uri.getScheme(), uri.getSchemeSpecificPart(), null).toString() : uri.toString();
        synchronized (lock) {
            // search from longest to shortest namespace prefix
            if (sortedNamespacesByPrefixLength != null) {
                for (String ns : sortedNamespacesByPrefixLength) {
                    if (s.startsWith(ns)) {
                        s = getPrefix(ns) + ':' + s.substring(ns.length());
                        break;
                    }
                }
            }
        }
        return s;
    }

    public IRI expandIRI(IRI curie) {
        String ns = getNamespaceURI(curie.getScheme());
        return ns != null ? IRI.builder().curie(ns + curie.getSchemeSpecificPart()).build() : curie;
    }

    public IRI expandIRI(String iri) {
        IRI curie = IRI.builder().curie(iri).build();
        String ns = getNamespaceURI(curie.getScheme());
        return ns != null ? IRI.builder().curie(ns + curie.getSchemeSpecificPart()).build() : curie;
    }

    public String getPrefix(IRI uri) {
        return getNamespaceURI(uri.getScheme()) != null ? uri.getScheme() : getPrefix(uri.toString());
    }

    @Override
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
