package org.xbib.content.rdf.util;

import java.util.Collection;
import java.util.Set;

/**
 * A multi map.
 * @param <K> the key type
 * @param <V> the value type
 */
public interface MultiMap<K, V> {

    void clear();

    int size();

    boolean isEmpty();

    boolean containsKey(K key);

    Collection<V> get(K key);

    Set<K> keySet();

    boolean put(K key, V value);

    void putAll(K key, Collection<V> values);

    Collection<V> remove(K key);

    boolean remove(K key, V value);

}
