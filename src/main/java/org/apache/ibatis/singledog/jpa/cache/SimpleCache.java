/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.singledog.jpa.cache;

import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.mapping.CacheBuilder;
import org.apache.ibatis.utils.StringUtils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple MyBatis Cache
 *
 * @author liuzh
 */
public class SimpleCache<K, V> implements Cache<K, V> {

    private final Map<K, V> CACHE = new ConcurrentHashMap<K, V>();

    @Override
    public V get(K key) {
        Object value = CACHE.get(key);
        if (value != null) {
            return (V) value;
        }
        return null;
    }

    @Override
    public synchronized void put(K key, V value) {
        CACHE.put(key, value);
    }
}
