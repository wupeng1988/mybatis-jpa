package org.apache.ibatis.singledog.jpa.meta;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.singledog.jpa.annotation.Id;
import org.apache.ibatis.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2017/7/1.
 */
public final class Column {

    private String table;
    private boolean isId;
    private String property;
    private String column;
    private String javaType;
    private Class javaTypeClass;
    private String type;
    private boolean nullable = true;
    private boolean unique = false;
    private String columnDefinition;
    private int scale = 2;
    private int length = 255;

    public Column() {}

    public Column(Id id, Field field) {
        this(null, id, field);
    }

    public Column(org.apache.ibatis.singledog.jpa.annotation.Column column, Id id, Field field) {
        this.property = field.getName();
        this.javaType = field.getType().getName();
        this.javaTypeClass = field.getType();
        this.isId = id != null;

        if (column != null) {
            this.column = column.name();
            this.columnDefinition = column.columnDefinition();
            this.unique = column.unique();
            this.nullable = column.nullable();
            this.table = column.table();
            this.length = column.length();
            this.type = column.type();
            this.scale = column.scale();
        } else if (isId) {
            this.column = StringUtils.humpToUnderScore(this.property);
        }

        if (StringUtils.isEmpty(type)) {
            this.type = JdbcTypeConverter.toJdbcType(field.getType(), this.length);
        }
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public Class getJavaTypeClass() {
        return javaTypeClass;
    }

    public void setJavaTypeClass(Class javaTypeClass) {
        this.javaTypeClass = javaTypeClass;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public ResultMapping toResultMapping(Configuration configuration, String resultMapId) {
        List<ResultFlag> flags = new ArrayList<>();
        if (isId()) {
            flags.add(ResultFlag.ID);
        }
        return new ResultMapping.Builder(configuration, this.getProperty(),
                this.getColumn(), this.getJavaTypeClass())
                .nestedResultMapId(resultMapId)
                .flags(flags)
                .build();
    }
}
