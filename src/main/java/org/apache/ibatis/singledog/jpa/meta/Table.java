package org.apache.ibatis.singledog.jpa.meta;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2017/7/1.
 */
public final class Table {

    private String name;
    private String catalog;
    private Class entity;
    private String schema;
    private List<Index> indexes;
    private List<Column> columns;
    private List<Column> idColumns;
    private String allColumns;

    public Class getEntity() {
        return entity;
    }

    public void setEntity(Class entity) {
        this.entity = entity;
    }

    public List<Column> getIdColumns() {
        return idColumns;
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
        return indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String getAllColumns() {
        return allColumns;
    }

    public void setAllColumns(String allColumns) {
        this.allColumns = allColumns;
    }

    public void afterPropertiesSet() {
        StringBuilder builder = new StringBuilder();

        if (!CollectionUtils.isEmpty(this.idColumns)) {
            this.idColumns.forEach(column -> builder.append(column.getColumn()).append(","));
        }

        if (!CollectionUtils.isEmpty(this.columns)) {
            this.columns.forEach(column -> builder.append(column.getColumn()).append(","));
        }

        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);

        setAllColumns(builder.toString());
    }

    public ResultMap toResultMap(Configuration configuration, String resultMapId) {
        List<ResultMapping> columnMappings = new ArrayList<>(this.columns.size());

        columns.forEach(column -> columnMappings.add(column.toResultMapping(configuration, resultMapId)));
        idColumns.forEach(column -> columnMappings.add(column.toResultMapping(configuration, resultMapId)));

        return new ResultMap.Builder(configuration, resultMapId, this.entity, columnMappings)
                .build();
    }
}
