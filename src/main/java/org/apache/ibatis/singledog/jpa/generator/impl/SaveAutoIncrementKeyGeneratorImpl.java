package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class SaveAutoIncrementKeyGeneratorImpl extends SaveSqlGeneratorImpl {

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return this.generate(dataParser, false);
    }
}
