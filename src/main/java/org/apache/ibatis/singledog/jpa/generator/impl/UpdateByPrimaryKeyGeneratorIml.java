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

    boolean includeNull() {
        return true;
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Column id = table.getSingleIdColumn();
        StringBuilder update = new StringBuilder();
        update.append(" update ").append(table.getName()).append(" ");

        StringBuilder columns = new StringBuilder();
        table.getColumns().forEach(column -> {
            if (includeNull()) {
                columns.append(column.getColumn()).append(" = #{").append(column.getProperty()).append("},");
            } else {
                columns.append(ifNotNull(column.getProperty(),
                        new StringBuilder()
                                .append(column.getColumn()).append(" = #{")
                                .append(column.getProperty()).append("},")
                                .toString()));
            }
        });

        update.append(set(columns.toString()))
                .append(" where ").append(id.getColumn()).append(" = #{").append(id.getProperty()).append("}");
        return update(getMethod(params), dataParser.getEntityClass().getName(), update.toString());
    }
}
