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

import org.apache.ibatis.features.jpa.generator.EntitySqlDispatcher;
import org.apache.ibatis.features.jpa.meta.Column;
import org.apache.ibatis.features.jpa.meta.Table;
import org.apache.ibatis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * generate ddl for mysql
 *
 * Created by Adam on 2017/7/26.
 */
public class DefaultMysqlDDLGenerator implements DDLGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMysqlDDLGenerator.class);

    @Override
    public boolean support(DBType dbType) {
        return dbType == DBType.MYSQL;
    }

    @Override
    public List<String> generateDDL(Connection connection) {
        try {
            List<String> sql = new ArrayList<>();
            Database database = getDataBase(connection);
            Set<Class> classes = EntitySqlDispatcher.getInstance().getEntities();
            classes.forEach(clazz -> {
                Table table = EntitySqlDispatcher.getInstance().getMetaDataParser(clazz).getTable();
                sql.addAll(compareStruct(table, database.getTable(table.getName())));
            });
            return sql;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public List<String> compareStruct(Table table, DBTable dbtable) {
        logger.info("checking table : {}", table.getName());
        if (dbtable == null) {
            logger.info("table {} doesn't exists !", table.getName());
            return Collections.singletonList(generateDDL4Table(table));
        }

        Set<String> checkedColumns = new HashSet<>();
        List<String> alters = new ArrayList<>();

        table.getColumns(true).forEach(column -> {
            checkedColumns.add(column.getColumn());
            String sql = compareColumn(column, dbtable.getColumn(column.getColumn()));
            if (!StringUtils.isEmpty(sql)) {
                alters.add(sql);
            }
        });

        Map<String, Column> columnMap = table.getColumns(true).stream().collect(Collectors.toMap(Column::getColumn, c -> c));
        dbtable.getTableColumns().forEach((k, v) -> {
            if (checkedColumns.contains(k))
                return;
            String sql = compareColumn(columnMap.get(k), v);
            if (!StringUtils.isEmpty(sql)) {
                alters.add(sql);
            }
        });

        if (alters.size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("alter table ").append(table.getName()).append(" \n ");
            for (int i = 0; i < alters.size(); i++) {
                builder.append(alters.get(i));
                if (i != alters.size() - 1) {
                    builder.append(",");
                }
                builder.append(" \n ");
            }
            return Collections.singletonList(builder.toString());
        } else {
            return Collections.emptyList();
        }
    }

    String compareColumn(Column column, TableColumn tableColumn) {
        StringBuilder builder = new StringBuilder();
        if (tableColumn == null) {
            logger.info("column {} not exists in DB !", column.getColumn());
            return builder.append(" add column ").append(generateDDL4Column(column)).toString();
        }

        if (column == null) {
            logger.info("column {} has been dropped !", tableColumn.getName());
            builder.append(" drop column ").append(tableColumn.getName()).append(" ");
            return builder.toString();
        }

        if (!StringUtils.isEmpty(column.getColumnDefinition())) {
            return null;
        }

        boolean needUpdate = false;
        if (!StringUtils.equalsIgnoreCase(column.getType(), tableColumn.getType())) {// type not match
            logger.info("column type has been changed ! {} -> {}", tableColumn.getType(), column.getType());
            needUpdate = true;
        }

        /*if (!needUpdate && column.getLength() != tableColumn.getLength()) {// length not match
            logger.info("column length has been changed ! {} -> {}", tableColumn.getLength(), column.getLength());
            needUpdate = true;
        }*/

        if (!needUpdate && column.isNullable() != tableColumn.isNullable()) {
            logger.info("column nullable has been changed ! {} -> {}", tableColumn.isNullable(), column.isNullable());
            needUpdate = true;
        }

        if (!needUpdate &&
                !(StringUtils.isEmpty(tableColumn.getDefaultValue())
                        && StringUtils.isEmpty(column.getDefaultValue()))
                && !StringUtils.equalsIgnoreCase(tableColumn.getDefaultValue(), column.getDefaultValue())) {
            logger.info("column default value has been changed ! {} -> {}", tableColumn.getDefaultValue(), column.getDefaultValue());
            needUpdate = true;
        }

        if (needUpdate) {
            return new StringBuilder(" change column ").append(column.getColumn()).append(" ")
                    .append(generateDDL4Column(column)).append(" ").toString();
        }

        return null;
    }

    public Database getDataBase(Connection conn) throws ClassNotFoundException, SQLException {
        Database database = new Database(conn.getCatalog());

        DatabaseMetaData databaseMetaData = conn.getMetaData();
        ResultSet resultSet = databaseMetaData.getTables(conn.getCatalog(), "%", "%", new String[]{"TABLE"});
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            DBTable table = new DBTable(tableName);

            ResultSet primaryKeyResultSet = databaseMetaData.getPrimaryKeys(conn.getCatalog(),null,tableName);
            Set<String> primaryKeys = new HashSet<>();
            while(primaryKeyResultSet.next()){
                String primaryKeyColumnName = primaryKeyResultSet.getString("COLUMN_NAME");
                primaryKeys.add(primaryKeyColumnName);
            }

            ResultSet columnResultSet = databaseMetaData.getColumns(conn.getCatalog(), "%", tableName, "%");

            while (columnResultSet.next()) {
                String columnName = columnResultSet.getString("COLUMN_NAME");
                String columnType = columnResultSet.getString("TYPE_NAME");
                int columnSize = 0;
                try {
                    columnSize = columnResultSet.getInt("COLUMN_SIZE");
                } catch (Exception e) {}

                boolean nullable = columnResultSet.getInt("NULLABLE") == 1;
                boolean primaryKey = primaryKeys.contains(columnName);
                String defaultValue = columnResultSet.getString("COLUMN_DEF");

                table.addColumn(new TableColumn(columnName, columnType, columnSize, nullable, primaryKey, defaultValue));
            }

            database.addTable(table);
        }

        return database;
    }

    public String generateDDL4Table(Table table) {
        StringBuilder builder = new StringBuilder();
        builder.append("create table ").append(table.getName()).append(" ( \n");
        List<Column> columns = table.getColumns(true);
        for (int i = 0; i < columns.size(); i++) {
            builder.append(generateDDL4Column(columns.get(i)));
            if (i != columns.size() - 1) {
                builder.append(",");
            }
            builder.append(" \n");
        }
        builder.append(") engine=InnoDB default charset utf8 ");
        return builder.toString();
    }

    public String generateDDL4Column(Column column) {
        StringBuilder ddl = new StringBuilder();
        ddl.append(column.getColumn()).append(" ");
        if (!StringUtils.isEmpty(column.getColumnDefinition())) {
            ddl.append(column.getColumnDefinition());
            return ddl.toString();
        }

        ddl.append(column.getType()).append("(");
        if (column.getPrecision() > 0 ) {
            ddl.append(String.valueOf(column.getPrecision()));
            if (column.getScale() > 0) {
                ddl.append(",").append(String.valueOf(column.getScale()));
            }
        } else if (column.getLength() > 0) {
            ddl.append(String.valueOf(column.getLength()));
        }

        ddl.append(")");
        if (column.isId()) {
            ddl.append(" primary key ");
        } else
            ddl.append(column.isNullable() ? " null " : " not null ")
                .append(column.isUnique() ? " unique " : "")
                .append(StringUtils.isEmpty(column.getDefaultValue()) ? "" : " default "
                        .concat(column.getDefaultValue()))
                .append(" ");

        return ddl.toString();
    }
}
