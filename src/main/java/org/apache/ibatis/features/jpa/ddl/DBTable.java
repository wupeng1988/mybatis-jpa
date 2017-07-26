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
package org.apache.ibatis.features.jpa.ddl;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Adam.Wu on 2016/3/28.
 */
public class DBTable {
    private String tableName;
    private Map<String, TableColumn> tableColumns = new TreeMap<>();

    public DBTable() {
    }

    public DBTable(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void addColumn(TableColumn column) {
        tableColumns.put(column.getName(), column);
    }

    public Map<String, TableColumn> getTableColumns() {
        return tableColumns;
    }

    public void setTableColumns(Map<String, TableColumn> tableColumns) {
        this.tableColumns = tableColumns;
    }

    public TableColumn getColumn(String columnName) {
        return this.tableColumns.get(columnName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(tableName).append(" {\n");
        for (TableColumn column : this.tableColumns.values()) {
            sb.append("\t ").append(column.toString()).append(" ,\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}