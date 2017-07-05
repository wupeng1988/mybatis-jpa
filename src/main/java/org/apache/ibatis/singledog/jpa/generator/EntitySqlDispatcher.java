package org.apache.ibatis.singledog.jpa.generator;

import org.apache.ibatis.session.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Adam on 2017/7/1.
 */
public class EntitySqlDispatcher {

    private static final Map<Class, MetaDataParser> metaDataParserMap = new ConcurrentHashMap<>();

    private static final EntitySqlDispatcher instance = new EntitySqlDispatcher();

    private EntitySqlDispatcher() {}

    public static EntitySqlDispatcher getInstance() {
        return instance;
    }

    public MetaDataParser parseEntity(Class clazz, Class idClass, Configuration configuration) {
        MetaDataParser metaDataParser = getMetaDataParser(clazz);
        if (metaDataParser == null) {
            metaDataParser = new MetaDataParser(clazz, idClass, configuration);
            metaDataParser.parse();
            addMetaDataParser(metaDataParser);
        }

        return metaDataParser;
    }

    public MetaDataParser getMetaDataParser(Class clazz) {
        return metaDataParserMap.get(clazz);
    }

    private void addMetaDataParser(MetaDataParser parser) {
        synchronized (metaDataParserMap) {
            metaDataParserMap.put(parser.getEntityClass(), parser);
        }
    }



}
