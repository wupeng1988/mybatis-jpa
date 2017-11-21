/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
    private SortDirectionSqlBuilder sortDirectionSqlBuilder = new SortDirectionSqlBuilder();

    @Override
    public void build(String keyWord, SqlContext sqlContext, SqlBuilderChain builderChain) {
        Table table = sqlContext.getTable();
        parseProperty(keyWord, sqlContext, table, "", keyWord, builderChain);
    }

    private void parseProperty(String originalKeyWord, SqlContext sqlContext, Table table,
                               String parsedProperty, String property,
                               SqlBuilderChain builderChain) {
        if (StringUtils.isEmpty(parsedProperty))
            property = StringUtils.uncapitalize(property);

        if (table.propertyExists(parsedProperty)) {
            sqlContext.append(table.getColumnByProperty(parsedProperty));
            if (!StringUtils.isEmpty(property)) {
                sqlBuilder.build(property, sqlContext, builderChain);
            } else if (!StringUtils.contains(sqlContext.getOriginalSql().toLowerCase(), "order by")) {
                sqlBuilder.build("is", sqlContext, builderChain);
            } else {
                sortDirectionSqlBuilder.build("asc", sqlContext, builderChain);
            }
        } else {
            if (StringUtils.isEmpty(property)) {
                builderChain.build(originalKeyWord, sqlContext);
                return;
            }
            String nextWord = StringUtils.nextHumpWord(property);
            parsedProperty += nextWord;
            property = property.substring(nextWord.length());
            parseProperty(originalKeyWord, sqlContext, table, parsedProperty, property, builderChain);
        }
    }

}
