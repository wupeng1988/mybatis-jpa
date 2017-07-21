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
package org.apache.ibatis.features.jpa.generator;

import org.apache.ibatis.session.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Adam on 2017/7/1.
 */
public class EntitySqlDispatcher {

    private static final Map<Class, MetaDataParser> metaDataParserMap = new ConcurrentHashMap<Class, MetaDataParser>();
    private static final Map<String, MetaDataParser> namespaceDataParserMap = new ConcurrentHashMap<String, MetaDataParser>();

    private static final EntitySqlDispatcher instance = new EntitySqlDispatcher();

    private EntitySqlDispatcher() {}

    public static EntitySqlDispatcher getInstance() {
        return instance;
    }

    public MetaDataParser parseEntity(Class clazz, Class idClass, Configuration configuration, String mapper) {
        MetaDataParser metaDataParser = getMetaDataParser(clazz);
        if (metaDataParser == null) {
            metaDataParser = new MetaDataParser(clazz, idClass, configuration, mapper);
            metaDataParser.parse();
            addMetaDataParser(metaDataParser);
        }

        return metaDataParser;
    }

    public MetaDataParser getMetaDataParser(Class clazz) {
        return metaDataParserMap.get(clazz);
    }

    public MetaDataParser getMetaDataParser(String namespace) {
        return namespaceDataParserMap.get(namespace);
    }

    private void addMetaDataParser(MetaDataParser parser) {
        synchronized (metaDataParserMap) {
            metaDataParserMap.put(parser.getEntityClass(), parser);
        }
        synchronized (namespaceDataParserMap) {
            namespaceDataParserMap.put(parser.getMapper(), parser);
        }
    }



}
