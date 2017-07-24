package org.apache.ibatis.features.jpa.builder.impl;

import org.apache.ibatis.features.jpa.builder.MethodSqlBuilder;
import org.apache.ibatis.features.jpa.builder.SqlBuilderChain;
import org.apache.ibatis.features.jpa.builder.SqlContext;
import org.apache.ibatis.utils.StringUtils;

/**
 * Created by Adam on 2017/7/24.
 */
public class OrderBySqlBuilder implements MethodSqlBuilder {

    @Override
    public void build(String keyWord, SqlContext sqlContext, SqlBuilderChain builderChain) {
        if (StringUtils.equalsIgnoreCase("orderby", keyWord))
            sqlContext.append("order by");
        else
            builderChain.build(keyWord, sqlContext);
    }
}
