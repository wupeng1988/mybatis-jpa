package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class DeleteAllGeneratorImpl extends AbstractSqlGenerator {

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Column id = table.getSingleIdColumn();

        StringBuilder delete = beginTag(TAG_DELETE, getMethod(params), null);
        delete.append(" delete from ").append(table.getName())
                .append(" where ").append(id.getColumn()).append(" in ")
                .append(foreach("item", "index", "list", "(",",",")", "#{item}"));
        return endTag(delete, TAG_DELETE).toString();
    }
}
