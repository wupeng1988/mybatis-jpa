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
package org.apache.ibatis.singledog.jpa.generator.provider;

import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.singledog.jpa.domain.Pageable;
import org.apache.ibatis.singledog.jpa.domain.Sort;
import org.apache.ibatis.singledog.jpa.generator.EntitySqlDispatcher;
import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.generator.NamespaceRequiredSqlProvider;
import org.apache.ibatis.singledog.jpa.meta.Table;

/**
 * Created by Adam on 2017/7/10.
 */
public class PagingAndSortingProvider implements NamespaceRequiredSqlProvider {

    private Class namespace;

    public String findAll(Sort sort) {
        MetaDataParser dataParser = getMetaDataParser();
        Table table = dataParser.getTable();
        return new SQL() {{
            SELECT(table.getAllColumns());
            FROM(table.getName());
            ORDER_BY(sort.toSql(table));
        }}.toString();
    }

    MetaDataParser getMetaDataParser() {
        return EntitySqlDispatcher.getInstance().getMetaDataParser(namespace.getName());
    }

    @Override
    public void setNamespace(Class namespace) {
        this.namespace = namespace;
    }
}
