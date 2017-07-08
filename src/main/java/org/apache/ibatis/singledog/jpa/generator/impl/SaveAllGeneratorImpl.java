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
import org.apache.log4j.pattern.LiteralPatternConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class SaveAllGeneratorImpl extends AbstractSqlGenerator {

    boolean useGeneratedKeys() {
        return false;
    }

    protected String values(MetaDataParser dataParser, boolean includingId) {
        Table table = dataParser.getTable();
        StringBuilder builder = new StringBuilder();
        builder.append(" ( ");
        List<Column> columnList = table.getColumns(includingId);
        columnList.forEach(column -> {
            builder.append("#{item.").append(column.getProperty()).append("},");
        });
        trim(builder);
        builder.append(")");
        return builder.toString();
    }

    protected String baseInfo(MetaDataParser dataParser, boolean includingId) {
        Table table = dataParser.getTable();
        StringBuilder builder = new StringBuilder();
        builder.append(" insert into ").append(table.getName()).append(" (");
        List<Column> columnList = table.getColumns(includingId);
        columnList.forEach(column -> {
            builder.append(column.getColumn()).append(",");
        });
        trim(builder);
        return builder.append(" values ").toString();
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Column id = table.getSingleIdColumn();
        String useGeneratedKeys = null;
        String keyProperty = null;
        String keyColumn = null;
        if (useGeneratedKeys()) {
            useGeneratedKeys = "true";
            keyProperty = id.getProperty();
            keyColumn = id.getColumn();
        }

        StringBuilder insert = new StringBuilder();
        insert.append(baseInfo(dataParser, !useGeneratedKeys()))
                .append(foreach("item,", "index", "list", "",",","",
                        values(dataParser, !useGeneratedKeys())));
        return insert(getMethod(params), dataParser.getEntityClass().getName(),
                keyProperty, keyColumn, useGeneratedKeys, insert.toString());
    }
}
