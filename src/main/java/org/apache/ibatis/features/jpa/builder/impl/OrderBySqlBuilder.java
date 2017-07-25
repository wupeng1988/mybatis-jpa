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
