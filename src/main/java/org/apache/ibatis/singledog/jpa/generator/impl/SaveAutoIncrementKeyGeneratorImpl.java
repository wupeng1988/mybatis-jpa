package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public class SaveAutoIncrementKeyGeneratorImpl extends SaveSqlGeneratorImpl {

    @Override
    public Element generateSqlNode(MetaDataParser dataParser, Map<String, Object> params) {
        StringBuilder keyProps = new StringBuilder();
        StringBuilder keyColumn = new StringBuilder();
        for (Column column : dataParser.getTable().getIdColumns()) {
            keyProps.append(column.getProperty()).append(",");
            keyColumn.append(column.getColumn()).append(",");
        }

        trim(keyProps);
        trim(keyColumn);

        return createSqlNode("insert", this.generatorSql(dataParser, params),
                new MapBuilder<String, String>()
                        .put("id", String.valueOf(params.get(PARAM_KEY_ID)))
                        .put("parameterType", dataParser.getEntityClass().getName())
                        .put("useGeneratedKeys", "true")
                        .put("keyProperty", keyProps.toString())
                        .put("keyColumn", keyColumn.toString())
                        .build());
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return this.generate(dataParser, false);
    }
}
