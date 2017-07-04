package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;

import java.util.Map;

/**
 * Created by Adam on 2017/7/4.
 */
public class FindAllByIdGeneratorImpl extends AbstractSqlGenerator {

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Column id = table.getSingleIdColumn();
        StringBuilder builder = new StringBuilder();
        builder.append("select ").append(table.getAllColumns()).append(" from ")
                .append(table.getName()).append(" where ").append(id.getColumn())
                .append(" in ")
                .append(foreach("item", "index", "list", "("," ,", ")", wrapProperty("item")));
        return select(getMethod(params), null, null, null, builder.toString());
    }
}
