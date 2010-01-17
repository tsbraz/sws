/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2009 SACI Informática Ltda.
 */

package saci.util;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class SimpleCache<K, V> implements Cache<K, V> {

    private class SimpleCacheMap<T> {
        T value;
        long lastAccess;
    }

    private HashMap<K, SimpleCacheMap<V>> map;
    private int length;

    public SimpleCache() {
        this(50);
    }

    public SimpleCache(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }
        setLength(length);
        map = new HashMap<K, SimpleCacheMap<V>>(length);
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public synchronized V get(K key) {
        SimpleCacheMap<V> cache = map.get(key);
        if (cache != null) {
            cache.lastAccess = System.currentTimeMillis();
            return cache.value;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    private synchronized void ensureCapacity() {
        if (map.size() < length) {
            return;
        }
        Set<Entry<K, SimpleCacheMap<V>>> entrySet = map.entrySet();
        long minAccessTime = -1;
        K key = null;
        for (Entry<K, SimpleCacheMap<V>> entry : entrySet) {
            SimpleCacheMap<V> cache = entry.getValue();
            if (minAccessTime == -1) {
                minAccessTime = cache.lastAccess;
            } else {
                if (cache.lastAccess < minAccessTime) {
                    key = entry.getKey();
                }
            }
        }
        if (key != null) {
            map.remove(key);
        }
    }

    @Override
    public synchronized V put(K key, V value) {
        remove(key);
        ensureCapacity();
        SimpleCacheMap<V> cache = new SimpleCacheMap<V>();
        cache.lastAccess = System.currentTimeMillis();
        cache.value = value;
        map.put(key, cache);
        return value;
    }

    public synchronized V remove(K key) {
        SimpleCacheMap<V> cache = map.remove(key);
        if (cache != null) {
            return cache.value;
        }
        return null;
    }

    public int size() {
        return map.size();
    }

}
