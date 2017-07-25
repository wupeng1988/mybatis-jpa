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
        builders.add(new SelectOrDeleteSqlBuilder());
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

        MethodSqlBuilder builder = builders.get(chainIndex++);
        builder.build(keyWord, sqlContext, this);
    }

    public void reset() {
        chainIndex = 0;
    }
}
