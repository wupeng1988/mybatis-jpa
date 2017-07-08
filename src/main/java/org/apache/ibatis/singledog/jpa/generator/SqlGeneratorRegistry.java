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
package org.apache.ibatis.singledog.jpa.generator;

import org.apache.ibatis.singledog.jpa.annotation.CustomProvider;
import org.apache.ibatis.utils.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Registry center of SqlGenerator implements
 *
 * Created by Adam on 2017/7/4.
 */
public class SqlGeneratorRegistry {
    private static final Logger logger = LoggerFactory.getLogger(SqlGeneratorRegistry.class);

    private static final Map<String, SqlGenerator> GENERATOR_CACHE = new ConcurrentHashMap<>();

    private static final SqlGeneratorRegistry instance = new SqlGeneratorRegistry();

    private SqlGeneratorRegistry() {}

    public static SqlGeneratorRegistry getInstance() {
        return instance;
    }

    public SqlGenerator getGenerator(Method method) throws InstantiationException, IllegalAccessException {
        String key = generatorCacheKey(method);
        SqlGenerator generator = GENERATOR_CACHE.get(key);
        if (generator == null) {
            return parseGenerator(method);
        }

        return generator;
    }

    public SqlGenerator parseGenerator(Method method) throws IllegalAccessException, InstantiationException {
        CustomProvider customProvider = AnnotationUtils.getAnnotation(method, CustomProvider.class);
        String key = generatorCacheKey(method);
        if (customProvider == null) {
            throw new IllegalArgumentException(key.concat(" doesn't has a @CustomProvider annotation !"));
        }

        SqlGenerator generator = customProvider.value().newInstance();
        synchronized (GENERATOR_CACHE) {
            GENERATOR_CACHE.put(key, generator);
        }

        return generator;
    }

    String generatorCacheKey(Method method) {
        return method.getDeclaringClass().getName().concat("#").concat(method.getName());
    }

}
