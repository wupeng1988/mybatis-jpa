package org.apache.ibatis.features.jpa.builder.impl;

import org.apache.ibatis.features.jpa.builder.MethodSqlBuilder;
import org.apache.ibatis.features.jpa.builder.SqlBuilderChain;
import org.apache.ibatis.features.jpa.builder.SqlContext;
import org.apache.ibatis.features.jpa.meta.Table;
import org.apache.ibatis.utils.StringUtils;

/**
 * Created by Adam on 2017/7/24.
 */
public class PropertySqlBuilder implements MethodSqlBuilder {

    private ComputeSqlBuilder sqlBuilder = new ComputeSqlBuilder();

    @Override
    public void build(String keyWord, SqlContext sqlContext, SqlBuilderChain builderChain) {
        Table table = sqlContext.getTable();
        parseProperty(sqlContext, table, "", keyWord, builderChain);
    }

    private void parseProperty(SqlContext sqlContext, Table table,
                               String parsedProperty, String property,
                               SqlBuilderChain builderChain) {
        property = StringUtils.uncapitalize(property);
        if (table.propertyExists(parsedProperty)) {
            sqlContext.append(table.getColumnByProperty(property));
            if (!StringUtils.isEmpty(property)) {
                sqlBuilder.build(property, sqlContext, builderChain);
            }
        } else {
            parsedProperty += StringUtils.nextHumpWord(property);
            property = property.substring(parsedProperty.length());
            parseProperty(sqlContext, table, parsedProperty, property, builderChain);
        }
    }

}
