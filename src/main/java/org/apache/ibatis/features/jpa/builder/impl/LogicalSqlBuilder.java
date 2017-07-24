package org.apache.ibatis.features.jpa.builder.impl;

import org.apache.ibatis.features.jpa.builder.MethodSqlBuilder;
import org.apache.ibatis.features.jpa.builder.SqlBuilderChain;
import org.apache.ibatis.features.jpa.builder.SqlContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Adam on 2017/7/24.
 */
public class LogicalSqlBuilder implements MethodSqlBuilder {
    private static final Set<String> OPERATORS = new HashSet<>();

    static {
        OPERATORS.add("and");
        OPERATORS.add("or");
    }

    @Override
    public void build(String keyWord, SqlContext sqlContext, SqlBuilderChain builderChain) {
        if (OPERATORS.contains(keyWord.toLowerCase()))
            sqlContext.append(keyWord);
        else
            builderChain.build(keyWord, sqlContext);
    }
}
