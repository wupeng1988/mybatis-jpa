package org.apache.ibatis.singledog.jpa.generator;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2017/7/1.
 */
public class MetaDataParser {
    private static final Logger logger = LoggerFactory.getLogger(MetaDataParser.class);

    private Class entityClass;
    private String baseColumns;
    private ResultMap resultMap;
    private Configuration configuration;

    public MetaDataParser(Class entityClass, Configuration configuration) {
        this.entityClass = entityClass;
        this.configuration = configuration;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public void parse() {
        List<ResultMapping> idMappings = new ArrayList<>();
        List<ResultMapping> columnMappings = new ArrayList<>();
        ReflectionUtils.doWithFields(entityClass, field -> {

        });
    }



}
