package org.apache.ibatis.singledog.jpa.meta;

import org.apache.ibatis.singledog.jpa.annotation.Id;

import java.lang.reflect.Field;

/**
 * Created by Adam on 2017/7/1.
 */
public class Column {

    private String table;
    private boolean isId;
    private String property;
    private String column;
    private String javaType;
    private String jdbcType;
    private boolean nullable = true;
    private boolean unique = false;
    private String columnDefinition;
    private int length;

    public Column() {}

    public Column(org.apache.ibatis.singledog.jpa.annotation.Column column, Field field) {
        this.column = column.name();
        this.columnDefinition = column.columnDefinition();
        this.unique = column.unique();
        this.nullable = column.nullable();
        this.table = column.table();
        this.length = column.length();

        if (field != null) {
            this.property = field.getName();
            this.javaType = field.getType().getName();
//            Id id = Anno
        }
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public boolean isId() {
        return isId;
    }

    public void setId(boolean id) {
        isId = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
