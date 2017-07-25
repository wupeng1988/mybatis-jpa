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
 * Created by Adam on 2017/7/25.
 */
public class NumberSqlBuilder implements MethodSqlBuilder {

    private static final Map<String, Integer> LIMIT_MAP = new HashMap<>();

    static {
        LIMIT_MAP.put("first", 1);
        LIMIT_MAP.put("one", 1);
        LIMIT_MAP.put("top", 1);
    }

    @Override
    public void build(String keyWord, SqlContext sqlContext, SqlBuilderChain builderChain) {
        for (Map.Entry<String, Integer> entry : LIMIT_MAP.entrySet()) {
            if (StringUtils.startsWithIgnoreCase(keyWord, entry.getKey())) {
                if (StringUtils.equalsIgnoreCase(keyWord, entry.getKey())) {
                    sqlContext.setLimitSegment(" limit ".concat(String.valueOf(entry.getValue())));
                } else {
                    keyWord = keyWord.substring(entry.getKey().length());
                    if (StringUtils.isNumberic(keyWord)) {
                        sqlContext.setLimitSegment(" limit ".concat(keyWord));
                    }
                }
            }
        }
    }
}
