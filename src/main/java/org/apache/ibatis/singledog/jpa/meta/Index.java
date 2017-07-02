package org.apache.ibatis.singledog.jpa.meta;

/**
 * Created by adam on 7/2/17.
 */
public final class Index {

    private String name;
    private String columns;
    private boolean unique;

    public Index() {}

    public Index(org.apache.ibatis.singledog.jpa.annotation.Index index) {
        this(index.name(), index.columnList(), index.unique());
    }

    public Index(String name, String columns, boolean unique) {
        this.name = name;
        this.columns = columns;
        this.unique = unique;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
}
