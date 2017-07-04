package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class ClearGeneratorImpl extends AbstractSqlGenerator {

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return delete(getMethod(params), null, "delete from " + dataParser.getTable().getName());
    }
}
