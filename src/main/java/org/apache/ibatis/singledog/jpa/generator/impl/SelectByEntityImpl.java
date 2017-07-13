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
package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;

import java.util.Map;

/**
 * Created by Adam on 2017/7/13.
 */
public class SelectByEntityImpl extends AbstractSqlGenerator {
    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        StringBuilder select = new StringBuilder();
        select.append(" select ").append(include(Table.ALL_COLUMNS))
                .append(" from ").append(dataParser.getTable().getName())
                .append(whereSql(dataParser));
        return select(getMethod(params), dataParser.getEntityClass().getName(), null, null, select.toString());
    }

    String whereSql(MetaDataParser dataParser) {
        StringBuilder where = new StringBuilder(" ");
        Table table = dataParser.getTable();
        table.getColumns(true).forEach(column -> {
           where.append(ifNotNull(column.getProperty(), and(column)));
        });
        return where(where.toString());
    }

    String and(Column column) {
        return " and ".concat(column.getColumn()).concat(" = ")
                .concat(wrapProperty(column.getProperty()));
    }

}
