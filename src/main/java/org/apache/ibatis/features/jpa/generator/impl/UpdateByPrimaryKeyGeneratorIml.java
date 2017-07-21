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

import org.apache.ibatis.features.jpa.generator.MetaDataParser;
import org.apache.ibatis.features.jpa.meta.Column;
import org.apache.ibatis.features.jpa.meta.Table;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class UpdateByPrimaryKeyGeneratorIml extends AbstractSqlGenerator {

    boolean includeNull() {
        return true;
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Column id = table.getSingleIdColumn();
        StringBuilder update = new StringBuilder();
        update.append(" update ").append(table.getName()).append(" ");

        StringBuilder columns = new StringBuilder();
        table.getColumns().forEach(column -> {
            if (includeNull()) {
                columns.append(column.getColumn()).append(" = #{").append(column.getProperty()).append("},");
            } else {
                columns.append(ifNotNull(column.getProperty(),
                        new StringBuilder()
                                .append(column.getColumn()).append(" = #{")
                                .append(column.getProperty()).append("},")
                                .toString()));
            }
        });

        update.append(set(columns.toString()))
                .append(" where ").append(id.getColumn()).append(" = #{").append(id.getProperty()).append("}");
        return update(getMethod(params), dataParser.getEntityClass().getName(), update.toString());
    }
}
