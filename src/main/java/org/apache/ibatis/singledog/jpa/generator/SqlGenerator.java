package org.apache.ibatis.singledog.jpa.generator;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public interface SqlGenerator {

    String PARAM_KEY_ID = "id";

    /**
     * generate xml select node. select | update | insert | delete sql
     * @param dataParser
     * @param params
     * @return
     */
    String generatorSql(MetaDataParser dataParser, Map<String, Object> params);

}
