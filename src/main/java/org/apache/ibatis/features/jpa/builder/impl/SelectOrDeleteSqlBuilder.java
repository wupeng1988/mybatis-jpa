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
import org.apache.ibatis.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam on 2017/7/24.
 */
public class SelectOrDeleteSqlBuilder implements MethodSqlBuilder {
    private static final Map<String, String> dmlMapping = new HashMap<>();
    private static final String select = "select";
    private static final String delete = "delete";
    private NumberSqlBuilder numberSqlBuilder = new NumberSqlBuilder();

    static {
        // select key words
        dmlMapping.put(select, select);
        dmlMapping.put("find", select);
        dmlMapping.put("get", select);
        dmlMapping.put("read", select);
        dmlMapping.put("query", select);
        dmlMapping.put("load", select);
        dmlMapping.put("count", select);
        //delete key words
        dmlMapping.put(delete, delete);
        dmlMapping.put("del", delete);
        dmlMapping.put("remove", delete);
    }

    @Override
    public void build(String keyWord, SqlContext sqlContext, SqlBuilderChain builderChain) {
        String director = StringUtils.nextHumpWord(keyWord);
        String operator = dmlMapping.get(director);
        if (!StringUtils.isEmpty(operator)) {
            StringBuilder sql = new StringBuilder();
            switch (operator) {
                case delete:
                    sql.append(delete).append(" from ").append(sqlContext.getTable().getName());
                    sqlContext.setFlag(delete);
                    break;
                case select:
                    sql.append(select);
                    sqlContext.setFlag(select);
                    keyWord = keyWord.substring(director.length());
                    if (!StringUtils.isEmpty(keyWord)) {
                        if (StringUtils.startsWithIgnoreCase(keyWord, "distinct")) {
                            sql.append(" distinct ");
                            keyWord = keyWord.substring("distinct".length());
                        }

                        if (!StringUtils.isEmpty(keyWord))
                            numberSqlBuilder.build(keyWord, sqlContext, builderChain);
                    }
                    sql.append(" ").append(sqlContext.getTable().getAllColumns())
                        .append(" from ").append(sqlContext.getTable().getName());
                    break;
            }

            sqlContext.append(sql.toString());
        } else {
            builderChain.build(keyWord, sqlContext);
        }
    }
}
