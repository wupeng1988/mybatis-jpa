package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class SaveSqlGeneratorImpl extends AbstractSqlGenerator {

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        Column id = dataParser.getTable().getSingleIdColumn();
        String sql = generate(dataParser, !useGenerateKeys());
        if (useGenerateKeys())
            return insert(getMethod(params), dataParser.getEntityClass().getName(),
                    id.getProperty(), id.getColumn(), "true", sql);
        else
            return insert(getMethod(params), dataParser.getEntityClass().getName(),
                    null, null, null, sql);
    }

    boolean useGenerateKeys() {
        return false;
    }

    boolean includeNull() {
        return true;
    }

    public String generate(MetaDataParser dataParser, boolean includeId) {

        Table table = dataParser.getTable();
        StringBuilder builder = new StringBuilder();
        builder.append(" insert into ").append(table.getName()).append(" ");
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Column> columnList = table.getColumns(includeId);
        if (!includeNull()) {
            table.getColumns(!useGenerateKeys()).forEach(column -> {
                columns.append(ifNotNull(column.getProperty(), column.getColumn() + ","));
                values.append(ifNotNull(column.getProperty(), new StringBuilder("#{")
                        .append(column.getProperty()).append("},").toString()));
            });
        } else {
            for (Column column : columnList) {
                columns.append(column.getColumn()).append(",");
                values.append("#{").append(column.getProperty()).append("},");
            }
        }
        builder.append(trim("(",")",",", columns.toString()))
                .append(" values ")
                .append(trim("(",")",",", values.toString()))
                .toString();
        return builder.toString();
    }
}
