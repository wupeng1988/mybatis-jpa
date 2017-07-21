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
package org.apache.ibatis.features.jpa.generator.impl;

import org.apache.ibatis.features.jpa.meta.Table;
import org.apache.ibatis.features.jpa.generator.MetaDataParser;

import java.util.Map;

/**
 * Created by Adam on 2017/7/18.
 */
public class FindAllGeneratorImpl extends AbstractSqlGenerator {

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        StringBuilder select = new StringBuilder();
        select.append(" select ").append(include(Table.ALL_COLUMNS))
                .append(" from ").append(dataParser.getTable().getName());

        return select(getMethod(params), null, null, null, select.toString());
    }
}
