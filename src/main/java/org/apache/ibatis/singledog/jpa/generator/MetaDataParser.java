/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.singledog.jpa.generator;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.singledog.jpa.annotation.Entity;
import org.apache.ibatis.singledog.jpa.annotation.Id;
import org.apache.ibatis.singledog.jpa.annotation.Index;
import org.apache.ibatis.singledog.jpa.meta.Column;
import org.apache.ibatis.singledog.jpa.meta.Table;
import org.apache.ibatis.utils.AnnotationUtils;
import org.apache.ibatis.utils.ReflectionUtils;
import org.apache.ibatis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2017/7/1.
 */
public class MetaDataParser {
    private static final Logger logger = LoggerFactory.getLogger(MetaDataParser.class);

    public static final String DEFAULT_RESULT_MAP = "default";

    private Class entityClass;
    private Class idClass;
    private Table table;
    private String resultMap;
    private Configuration configuration;

    public MetaDataParser(Class entityClass, Class idClass, Configuration configuration) {
        this.entityClass = entityClass;
        this.configuration = configuration;
        this.idClass = idClass;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public synchronized void parse() {
        table = new Table();

        org.apache.ibatis.singledog.jpa.annotation.Table tableAnno = AnnotationUtils.getAnnotation(this.entityClass,
                org.apache.ibatis.singledog.jpa.annotation.Table.class);
        Entity entity = AnnotationUtils.getAnnotation(this.entityClass, Entity.class);
        if (tableAnno == null && entity == null) {
            throw new IllegalArgumentException("Class " + this.entityClass.getName() + " is not a entity class !");
        }

        table.setEntity(this.entityClass);
        if (tableAnno != null) {
            table.setName(tableAnno.name());
            table.setCatalog(tableAnno.catalog());
            table.setSchema(tableAnno.schema());
            Index[] indices = tableAnno.indexes();
            if (indices != null && indices.length > 0) {
                List<org.apache.ibatis.singledog.jpa.meta.Index> indexList = new ArrayList<>(indices.length);
                for (Index index : indices) {
                    indexList.add(new org.apache.ibatis.singledog.jpa.meta.Index(index));
                }
                table.setIndexes(indexList);
            }
        } else {
            table.setName(StringUtils.humpToUnderScore(this.entityClass.getSimpleName()));
        }

        List<Column> idMappings = new ArrayList<>();
        List<Column> columnMappings = new ArrayList<>();
        ReflectionUtils.doWithFields(entityClass, field -> {
            Id id = AnnotationUtils.getAnnotation(field, Id.class);
            org.apache.ibatis.singledog.jpa.annotation.Column columnAnno =
                    AnnotationUtils.getAnnotation(field, org.apache.ibatis.singledog.jpa.annotation.Column.class);
            if (id == null && columnAnno == null) {
                logger.info("field {} on class {} doesn't has @Id or @Column annotation, ignore ", field.getName(), entityClass.getName());
                return;
            }

            Column column = new Column(columnAnno, id, field);
            if (column.isId()) {
                idMappings.add(column);
            } else {
                columnMappings.add(column);
            }
        });

        table.setColumns(columnMappings);
        table.setIdColumns(idMappings);
        table.afterPropertiesSet();
        this.resultMap = table.toResultMapXml(DEFAULT_RESULT_MAP);
    }

    public String getResultMapString() {
        return resultMap;
    }

    public Table getTable() {
        return table;
    }

    public Class getIdClass() {
        return idClass;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
