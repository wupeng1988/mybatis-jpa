package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class CountAllGeneratorImpl extends AbstractSqlGenerator {
    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return select(getMethod(params), null, "java.lang.Long", null,
                "select count(*) from " + dataParser.getTable().getName() + " ");
    }
}
