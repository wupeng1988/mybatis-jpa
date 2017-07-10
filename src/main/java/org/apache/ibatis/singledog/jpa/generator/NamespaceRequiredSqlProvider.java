package org.apache.ibatis.singledog.jpa.generator;

/**
 * Created by Adam on 2017/7/10.
 */
public interface NamespaceRequiredSqlProvider<T> {

    void setNamespace(Class namespace);

}
