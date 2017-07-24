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
public class SelectOrUpdateSqlBuilder implements MethodSqlBuilder {
    private static final Map<String, String> dmlMapping = new HashMap<>();
    private static final String select = "select";
    private static final String delete = "delete";
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
                    sql.append(select).append(" ").append(sqlContext.getTable().getAllColumns())
                            .append(" from ").append(sqlContext.getTable().getName());
                    sqlContext.setFlag(select);
                    break;
            }

            sqlContext.append(sql.toString());
        } else {
            builderChain.build(keyWord, sqlContext);
        }
    }
}
