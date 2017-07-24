package org.apache.ibatis.features.jpa.builder;

/**
 * Created by Adam on 2017/7/24.
 */
public interface MethodSqlBuilder {

    void build(String keyWord, SqlContext sqlContext, SqlBuilderChain builderChain);

}
