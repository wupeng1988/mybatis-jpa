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
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        Map<String, String> attrs = new MapBuilder<String, String>()
                .put("id", String.valueOf(params.get(PARAM_KEY_ID)))
                .put("parameterType", dataParser.getEntityClass().getName())
                .build();
        return createSqlNode("insert", this.generatorSql(dataParser, params), attrs);
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return generate(dataParser, true);
    }

    public String generate(MetaDataParser dataParser, boolean includeId) {
        Table table = dataParser.getTable();
        StringBuilder builder = new StringBuilder();
        builder.append(" insert into ").append(table.getName()).append(" ( ");
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Column> columnList = new ArrayList<>(table.getColumns());
        if (includeId) {
            columnList.addAll(0, table.getIdColumns());
        }
        for (Column column : table.getColumns()) {
            columns.append(column.getColumn()).append(",");
            //TODO add jdbcType
            values.append("#{").append(column.getProperty()).append("},");
        }
        columns.deleteCharAt(columns.length() - 1);
        values.deleteCharAt(values.length() - 1);
        builder.append(columns.toString()).append(") values (").append(values).append(")");
        return builder.toString();
    }
}
