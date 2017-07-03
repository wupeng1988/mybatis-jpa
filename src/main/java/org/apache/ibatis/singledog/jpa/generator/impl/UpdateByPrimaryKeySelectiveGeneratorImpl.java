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
public class UpdateByPrimaryKeySelectiveGeneratorImpl extends AbstractSqlGenerator {

    @Override
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Document document = createDocument();
        Element root = document.createElement("update");
        root.setAttribute("id", getMethod(params));
        root.setAttribute("parameterType", dataParser.getEntityClass().getName());
        StringBuilder baseInfo = new StringBuilder("update ")
                .append(table.getName());
        root.appendChild(document.createTextNode(baseInfo.toString()));

        Element set = document.createElement("set");
        root.appendChild(set);

        Column id = table.getSingleIdColumn();
        table.getColumns().forEach(column -> {
            set.appendChild(createIfNotNullElement(column.getProperty(), () -> {
               return new StringBuilder(" ").append(column.getColumn())
                       .append(" = #{").append(column.getColumn()).append("},").toString();
            }));
        });

        root.appendChild(document.createTextNode(new StringBuilder(" where ")
                .append(id.getColumn()).append("=#{").append(id.getProperty()).append("} ").toString()));
        return root;
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return null;
    }
}
