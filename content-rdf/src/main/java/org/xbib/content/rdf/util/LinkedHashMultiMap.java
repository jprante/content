package org.xbib.content.rdf.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A linked hash map multi map.
 * @param <K> te key type
 * @param <V> the value type
 */
public class LinkedHashMultiMap<K, V> implements MultiMap<K, V> {

    private final Map<K, Set<V>> map;

    public LinkedHashMultiMap() {
        this.map = new LinkedHashMap<>();
    }

    public LinkedHashMultiMap(MultiMap<K, V> multiMap) {
        Objects.requireNonNull(multiMap);
        this.map = new LinkedHashMap<>();
        for (K k : multiMap.keySet()) {
            putAll(k, multiMap.get(k));
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public boolean put(K key, V value) {
        Set<V> set = map.get(key);
        if (set == null) {
            set =  new LinkedHashSet<>();
            set.add(value);
            map.put(key, set);
            return true;
        } else {
            set.add(value);
            return false;
        }
    }

    @Override
    public void putAll(K key, Collection<V> values) {
        if (values == null) {
            return;
        }
        Set<V> set = map.get(key);
        if (set == null) {
            set = new LinkedHashSet<>();
            map.put(key, set);
        }
        set.addAll(values);
    }

    @Override
    public Collection<V> get(K key) {
        return map.get(key);
    }

    @Override
    public Set<V> remove(K key) {
        return map.remove(key);
    }

    @Override
    public boolean remove(K key, V value) {
        Set<V> set = map.get(key);
        return set != null && set.remove(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof LinkedHashMultiMap && map.equals(((LinkedHashMultiMap) obj).map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
