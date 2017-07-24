package org.apache.ibatis.features.jpa.builder;

import org.apache.ibatis.features.jpa.builder.impl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2017/7/24.
 */
public class SqlBuilderChain  {

    private static final List<MethodSqlBuilder> builders = new ArrayList<>();

    static {
        builders.add(new SelectOrUpdateSqlBuilder());
        builders.add(new WhereSqlBuilder());
        builders.add(new PropertySqlBuilder());
        builders.add(new ComputeSqlBuilder());
        builders.add(new LogicalSqlBuilder());
        builders.add(new OrderBySqlBuilder());
        builders.add(new SortDirectionSqlBuilder());
    }

    private int chainIndex = 0;
    private final int chainLength = builders.size();

    public void build(String keyWord, SqlContext sqlContext) {
        if (chainIndex == chainLength)
            return;

        MethodSqlBuilder builder = builders.get(chainIndex);
        builder.build(keyWord, sqlContext, this);
        chainIndex++;
    }
}
