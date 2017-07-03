package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class DeleteByIdGeneratorImpl extends AbstractSqlGenerator {
    @Override
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        return createDeleteElement(getMethod(params), dataParser.getIdClass().getName(),
                () -> generatorSql(dataParser, params));
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Column id = table.getSingleIdColumn();
        return new StringBuilder()
                .append(" delete from ").append(table.getName())
                .append(" where ").append(id.getColumn()).append(" = #{")
                .append(id.getProperty()).append("} ")
                .toString();
    }
}
