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
public class SaveSelectiveGeneratorImpl extends AbstractSqlGenerator {

    @Override
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Column id = table.getSingleIdColumn();
        Document document = createDocument();
        Element root = document.createElement("insert");
        root.setAttribute("id", getMethod(params));
        root.setAttribute("parameterType", dataParser.getEntityClass().getName());
        if (useGenerateKeys()) {
            root.setAttribute("keyProperty", id.getProperty());
            root.setAttribute("keyColumn", id.getColumn());
            root.setAttribute("useGeneratedKeys", String.valueOf(useGenerateKeys()));
        }

        root.appendChild(document.createTextNode(new StringBuilder()
                .append("insert into ")
                .append(table.getName()).append(" (").toString()));//insert into test (

        table.getColumns(!useGenerateKeys()).forEach(column -> {
            root.appendChild(createIfNotNullElement(column.getProperty(), () -> {
                return new StringBuilder().append(column.getColumn()).append(",").toString();
            }));
        });

        return null;
    }

    boolean useGenerateKeys() {
        return false;
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return null;
    }
}
