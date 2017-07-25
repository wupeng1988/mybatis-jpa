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
import org.apache.ibatis.features.jpa.generator.MetaDataParser;
import org.apache.ibatis.features.jpa.generator.impl.AbstractSqlGenerator;
import org.apache.ibatis.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam on 2017/7/24.
 */
public class ComputeSqlBuilder extends AbstractSqlGenerator implements MethodSqlBuilder {

    private static final Map<String, String> OPERATORS = new HashMap<>();

    static {
        OPERATORS.put("equals", "=");
        OPERATORS.put("is", "=");
        OPERATORS.put("between", "between");
        OPERATORS.put("lessthan", "<");
        OPERATORS.put("lessthanequal", "<=");
        OPERATORS.put("greaterthan", ">");
        OPERATORS.put("greaterthanequal", ">=");
        OPERATORS.put("after", ">");
        OPERATORS.put("before", "<");
        OPERATORS.put("isnull", "is null");
        OPERATORS.put("isnotnull", "is not null");
        OPERATORS.put("notnull", "is not null");
        OPERATORS.put("like", "like");
        OPERATORS.put("notlike", "not like");
        OPERATORS.put("in", "in");
        OPERATORS.put("notin", "not in");
    }

    @Override
    public void build(String keyWord, SqlContext sqlContext, SqlBuilderChain builderChain) {
        String operator = OPERATORS.get(keyWord.toLowerCase());
        if (!StringUtils.isEmpty(operator)) {
            StringBuilder sql = new StringBuilder();
            switch (operator) {
                case "between":
                   sql.append(operator).append(" ")
                           .append(wrapProperty(sqlContext.getParamName())).append(" and ").append(wrapProperty(sqlContext.getParamName()));
                    break;
                case "is null":
                case "is not null":
                    sql.append(operator);
                    break;
                case "in":
                case "not in":
                    String collection = "list";
                    if (sqlContext.getArgTypes().length > 1)
                        collection = sqlContext.getParamName();
                    sql.append(operator).append(" ").append(foreach("item", "index", collection, "(", ",",")", wrapProperty("item")));
                    break;
                default:
                    sql.append(operator).append(" ").append(wrapProperty(sqlContext.getParamName()));
            }

            sqlContext.append(sql.toString());
        } else {
            builderChain.build(keyWord, sqlContext);
        }
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return null;
    }
}
