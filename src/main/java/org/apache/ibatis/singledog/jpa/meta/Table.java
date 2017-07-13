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
package org.apache.ibatis.singledog.jpa.meta;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.utils.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Adam on 2017/7/1.
 */
public final class Table {
    public static final String ALL_COLUMNS = "all_columns";
    public static final String ALL_COLUMNS_UPPERCASE = ALL_COLUMNS.toUpperCase();

    private String name;
    private String catalog;
    private Class entity;
    private String schema;
    private List<Index> indexes;
    private List<Column> columns;
    private List<Column> idColumns;
    private List<SqlSegment> sqlSegments = new ArrayList<>();
    private Map<String, SqlSegment> sqlSegmentsMap;
    private Map<String, String> propertyColumnMap = new HashMap<>();
    private Map<String, String> columnPropertyMap = new HashMap<>();

    public String getColumnByProperty(String property) {
        return propertyColumnMap.get(property);
    }

    public boolean propertyExists(String property) {
        return propertyColumnMap.containsKey(property);
    }

    public String getPropertyByColumn(String column) {
        return columnPropertyMap.get(column);
    }

    public boolean columnExists(String column) {
        return this.columnPropertyMap.containsKey(column);
    }

    public List<Column> getColumns(boolean withId) {
        List<Column> columnList = new ArrayList<>(this.columns);
        if (withId)
            columnList.addAll(0, this.idColumns);
        return Collections.unmodifiableList(columnList);
    }

    public Column getSingleIdColumn() {
        if (this.idColumns.size() > 1)
            throw new UnsupportedOperationException("More than one primary keys found ! "
                    + this.entity.getName());
        return idColumns.get(0);
    }

    public Class getEntity() {
        return entity;
    }

    public void setEntity(Class entity) {
        this.entity = entity;
    }

    public List<Column> getIdColumns() {
        return Collections.unmodifiableList(idColumns);
    }

    public void setIdColumns(List<Column> idColumns) {
        this.idColumns = idColumns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public List<Index> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<SqlSegment> getSqlSegments() {
        return Collections.unmodifiableList(sqlSegments);
    }

    public void setSqlSegments(List<SqlSegment> sqlSegments) {
        this.sqlSegments = sqlSegments;
    }

    public String getAllColumns() {
        return this.sqlSegmentsMap.get(ALL_COLUMNS).getSql();
    }

    public synchronized void afterPropertiesSet() {
        StringBuilder builder = new StringBuilder();

        if (!CollectionUtils.isEmpty(this.idColumns)) {
            this.idColumns.forEach(column -> builder.append(" ").append(column.getColumn()).append(","));
        }

        if (!CollectionUtils.isEmpty(this.columns)) {
            this.columns.forEach(column -> builder.append(" ").append(column.getColumn()).append(","));
        }

        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);

        sqlSegments.add(new SqlSegment(ALL_COLUMNS, builder.toString()));
        sqlSegments.add(new SqlSegment(ALL_COLUMNS_UPPERCASE, builder.toString()));
        sqlSegmentsMap = sqlSegments.stream().collect(Collectors.toMap(SqlSegment::getId, s -> s));
        this.getColumns(true).forEach(column -> {
            propertyColumnMap.put(column.getProperty(), column.getColumn());
            columnPropertyMap.put(column.getColumn(), column.getProperty());
        });
    }

    public ResultMap toResultMap(Configuration configuration, String resultMapId) {
        List<ResultMapping> columnMappings = new ArrayList<>(this.columns.size());

        columns.forEach(column -> columnMappings.add(column.toResultMapping(configuration, resultMapId)));
        idColumns.forEach(column -> columnMappings.add(column.toResultMapping(configuration, resultMapId)));

        return new ResultMap.Builder(configuration, resultMapId, this.entity, columnMappings)
                .build();
    }

    public String toResultMapXml(String resultMapId) {
        StringBuilder builder = new StringBuilder();
        builder.append("<resultMap id=\"").append(resultMapId).append("\" type=\"").append(this.getEntity().getName()).append("\" > \n ");
        this.getColumns(true).forEach(column -> {
            builder.append(column.toResultMappingXml()).append(" \n ");
        });


        return builder.append("</resultMap>").toString();
    }

    public static class SqlSegment {
        private String id;
        private String sql;

        public SqlSegment() {
        }

        public SqlSegment(String id, String sql) {
            this.id = id;
            this.sql = sql;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public String toXml() {
            return new StringBuilder("<sql id=\"")
                    .append(id).append("\" > \n ").append(sql).append(" \n ")
                    .append("</sql>")
                    .toString();
        }
    }

}
