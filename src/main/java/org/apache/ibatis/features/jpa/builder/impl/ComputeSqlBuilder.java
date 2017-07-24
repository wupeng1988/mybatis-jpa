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
                   sql.append(operator)
                           .append(wrapProperty(sqlContext.getParamName())).append(" and ").append(wrapProperty(sqlContext.getParamName()));
                    break;
                case "is null":
                case "is not null":
                    sql.append(operator);
                    break;
                case "in":
                case "not in":
                    sql.append(operator).append(" ").append(foreach("item", "index", "list", "(", ",",")", wrapProperty("item")));
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
