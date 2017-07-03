package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class DeleteAllGeneratorImpl extends AbstractSqlGenerator {
    @Override
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Document document = createDocument();
        Element root = document.createElement("delete");
        root.setAttribute("id", getMethod(params));
        Column id = table.getSingleIdColumn();
        StringBuilder baseInfo = new StringBuilder();
        baseInfo.append("delete from ")
                .append(table.getName())
                .append(" where ")
                .append(id.getColumn()).append(" in ");
        root.appendChild(document.createTextNode(baseInfo.toString()));
        root.appendChild(createForEachElement("item", "index", "list", "(", ",", ")", () -> "#{item}"));
        return root;
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return null;
    }
}
