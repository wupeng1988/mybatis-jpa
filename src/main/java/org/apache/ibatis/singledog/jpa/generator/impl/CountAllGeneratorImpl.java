package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class CountAllGeneratorImpl extends AbstractSqlGenerator {
    @Override
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        return createSelectElement(getMethod(params), null, "java.lang.Long", null,
                () -> generatorSql(dataParser, params));
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return "select * from " + dataParser.getTable().getName() + " ";
    }
}
