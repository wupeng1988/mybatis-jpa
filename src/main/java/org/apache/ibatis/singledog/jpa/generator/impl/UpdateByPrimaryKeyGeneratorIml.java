package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class UpdateByPrimaryKeyGeneratorIml extends AbstractSqlGenerator {

    @Override
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        return createUpdateElement(getMethod(params), dataParser.getEntityClass().getName(),
                () -> generatorSql(dataParser, params));
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Column id = table.getSingleIdColumn();
        StringBuilder update = new StringBuilder();
        update.append(" update ").append(table.getName()).append(" set ");
        table.getColumns().forEach(column -> {
            update.append(column.getColumn()).append(" = #{").append(column.getProperty()).append("},");
        });
        trim(update);
        update.append(" where ").append(id.getColumn()).append(" = #{").append(id.getProperty()).append("}");
        return update.toString();
    }
}
