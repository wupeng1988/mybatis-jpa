package org.apache.ibatis.singledog.jpa.generator.provider;

import org.apache.ibatis.singledog.jpa.domain.Pageable;
import org.apache.ibatis.singledog.jpa.domain.Sort;
import org.apache.ibatis.singledog.jpa.generator.NamespaceRequiredSqlProvider;

/**
 * Created by Adam on 2017/7/10.
 */
public class PagingAndSortingProvider implements NamespaceRequiredSqlProvider {

    private Class namespace;

    public String findAll(Sort sort) {
        //TODO
        return "";
    }


    public String findAllByPage(Pageable pageable) {
        //TODO
        return "";
    }


    @Override
    public void setNamespace(Class namespace) {

    }
}
