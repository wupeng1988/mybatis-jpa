package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;
import org.apache.log4j.pattern.LiteralPatternConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class SaveAllGeneratorImpl extends AbstractSqlGenerator {

    @Override
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        Table table = dataParser.getTable();
        Document document = createDocument();
        Element root = document.createElement("insert");
        root.setAttribute("id", getMethod(params));

        StringBuilder keyProps = new StringBuilder();
        StringBuilder keyColumns = new StringBuilder();
        table.getIdColumns().forEach(column -> {
            keyProps.append(column.getProperty()).append(",");
            keyColumns.append(column.getColumn()).append(",");
        });

        trim(keyColumns);
        trim(keyProps);

        if (useGeneratedKeys()) {
            root.setAttribute("useGeneratedKeys", String.valueOf(useGeneratedKeys()));
            root.setAttribute("keyProperty", keyProps.toString());
            root.setAttribute("keyColumn", keyColumns.toString());
        }
        Text insert = document.createTextNode(baseInfo(dataParser, !useGeneratedKeys()));
        root.appendChild(insert);
        root.appendChild(createForEachElement("item", "index", "list", "", ",", "",
                () -> values(dataParser, !useGeneratedKeys())));
        return root;
    }

    boolean useGeneratedKeys() {
        return false;
    }

    protected String values(MetaDataParser dataParser, boolean includingId) {
        Table table = dataParser.getTable();
        StringBuilder builder = new StringBuilder();
        builder.append(" ( ");
        List<Column> columnList = table.getColumns(includingId);
        columnList.forEach(column -> {
            builder.append("#{item.").append(column.getProperty()).append("},");
        });
        trim(builder);
        builder.append(")");
        return builder.toString();
    }

    protected String baseInfo(MetaDataParser dataParser, boolean includingId) {
        Table table = dataParser.getTable();
        StringBuilder builder = new StringBuilder();
        builder.append(" insert into ").append(table.getName()).append(" (");
        List<Column> columnList = table.getColumns(includingId);
        columnList.forEach(column -> {
            builder.append(column.getColumn()).append(",");
        });
        trim(builder);
        return builder.append(" values ").toString();
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return null;
    }
}
