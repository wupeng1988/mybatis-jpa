package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class ClearGeneratorImpl extends AbstractSqlGenerator {
    @Override
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        return createDeleteElement(getMethod(params), null, () -> generatorSql(dataParser, params));
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return "delete from " + dataParser.getTable().getName();
    }
}
