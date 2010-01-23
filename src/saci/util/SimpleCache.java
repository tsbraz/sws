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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

public class SimpleCache<K, V> implements Cache<K, V> {

    public class SimpleCacheMap<T> {
    	public K key;
    	public T value;
    	public long lastAccess;
    }

    private HashMap<K, SimpleCacheMap<V>> map;
    private int length, cacheCapacity;
    private SimpleCacheMap<V>[] cleanPolicyKeys;

    public SimpleCache() {
        this(50, 10);
    }

    public SimpleCache(int cacheCapacity, int cleanPolicy) {
        if (cacheCapacity <= 0) {
            throw new IllegalArgumentException("Cache capacity must be greater than zero");
        }
        if (cleanPolicy <= 0) {
            throw new IllegalArgumentException("Clean policy must be greater than zero");
        }
        setCacheCapacity(cacheCapacity);
        setCleanPolicy(cleanPolicy);
        map = new HashMap<K, SimpleCacheMap<V>>(cacheCapacity);
    }

    public void setCacheCapacity(int cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
    }

    @SuppressWarnings("unchecked")
	public void setCleanPolicy(int cleanPolicy) {
        cleanPolicyKeys = new SimpleCacheMap[cleanPolicy];
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

    public boolean isEmpty() {
        return map.isEmpty();
    }

    private synchronized void ensureCapacity() {
        if (length < cacheCapacity) {
            return;
        }
        Set<Entry<K, SimpleCacheMap<V>>> entrySet = map.entrySet();
        for (Entry<K, SimpleCacheMap<V>> entry : entrySet) {
            SimpleCacheMap<V> cache = entry.getValue();
        	checkCleanPolicy(cache);
        }
        clean();
    }
    
    private void checkCleanPolicy(SimpleCacheMap<V> cache) {
    	int minPolicyKeyPosition = -1;
    	long minAccessTime = cache.lastAccess;
    	for (int i = 0; i < cleanPolicyKeys.length; i++) {
    		if (cleanPolicyKeys[i] == cache) {
    			return;
    		} else if (cleanPolicyKeys[i] == null) {
    			cleanPolicyKeys[i] = cache;
    			return;
    		} else {
        		if (minAccessTime < cleanPolicyKeys[i].lastAccess) {
        			minAccessTime = cleanPolicyKeys[i].lastAccess;
        			minPolicyKeyPosition = i;
        		}
    		}
    	}
    	if (minPolicyKeyPosition > -1) {
    		cleanPolicyKeys[minPolicyKeyPosition] = cache;
    	}
    }
    
	private void clean() {
    	for (int i = 0; i < cleanPolicyKeys.length; i++) {
    		if (cleanPolicyKeys[i] != null) {
    			remove(cleanPolicyKeys[i].key);
    		} else {
    			break;
    		}
    	}
    	Arrays.fill(cleanPolicyKeys, null);
    }

    public synchronized V put(K key, V value) {
        remove(key);
        ensureCapacity();
        SimpleCacheMap<V> cache = new SimpleCacheMap<V>();
        cache.lastAccess = System.currentTimeMillis();
        cache.value = value;
        cache.key = key;
        map.put(key, cache);
        length++;
        return value;
    }

    public synchronized V remove(K key) {
        SimpleCacheMap<V> cache = map.remove(key);
        if (cache != null) {
        	length--;
            return cache.value;
        }
        return null;
    }

    public int size() {
        return length;
    }

}