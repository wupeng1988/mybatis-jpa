package org.apache.ibatis.features.jpa.builder.impl;

import org.apache.ibatis.features.jpa.builder.MethodSqlBuilder;
import org.apache.ibatis.features.jpa.builder.SqlBuilderChain;
import org.apache.ibatis.features.jpa.builder.SqlContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adam on 2017/7/24.
 */
public class SortDirectionSqlBuilder implements MethodSqlBuilder {

    private static final Set<String> directions = new HashSet<>();

    static {
        directions.add("asc");
        directions.add("desc");
    }

    @Override
    public void build(String keyWord, SqlContext sqlContext, SqlBuilderChain builderChain) {
        if (directions.contains(keyWord.toLowerCase())) {
            sqlContext.append(keyWord.toLowerCase());
        } else {
            builderChain.build(keyWord, sqlContext);
        }
    }

}
