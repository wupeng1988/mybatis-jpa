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
package org.apache.ibatis.builder.xml;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.exceptions.EntityNotFoundException;
import org.apache.ibatis.features.jpa.generator.EntitySqlDispatcher;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;

import java.io.ByteArrayInputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Adam on 2017/9/22.
 */
public class SingletonJpaMapperBuilder extends XMLMapperBuilder {

    private Configuration configuration;
    private Class clazz;
    private Class entityClass;
    private Class idClass;
    private String resource;
    private String namespace;

    public SingletonJpaMapperBuilder(Configuration configuration, Class<? extends Mapper> clazz) {
        super(new ByteArrayInputStream(getXmlContent(clazz).getBytes()), configuration, getResource(clazz), configuration.getSqlFragments());
        this.resource = getResource(clazz);
        this.clazz = clazz;
        this.namespace = clazz.getName();
        this.configuration = configuration;
        this.findMetaClass(clazz);
    }

    private static String getXmlContent(Class clazz) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper\n" +
                "        PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
                "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"" + clazz.getName() + "\"></mapper>";
    }

    private static String getResource(Class clazz) {
        return "JpaMapper:".concat(clazz.getCanonicalName());
    }

    private void findMetaClass(Class mapperClass) {
        Type[] types = mapperClass.getGenericInterfaces();
        if (types != null && types.length > 0) {
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    Class rawClass = (Class) ((ParameterizedType) type).getRawType();
                    if (org.apache.ibatis.features.jpa.mapper.Mapper.class.isAssignableFrom(rawClass)) {
                        this.entityClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                        this.idClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
                        EntitySqlDispatcher.getInstance().parseEntity(this.entityClass, this.idClass, configuration, namespace);
                        return;
                    }
                }
            }
        }

        throw new EntityNotFoundException("No entity found for mapper : " + mapperClass.getName());
    }

    public void parse() {
        try {
            if (!configuration.isResourceLoaded(resource) && !configuration.isJpaMapperLoaded(clazz)) {
                super.parse();
            }
        } catch (Exception e) {
            throw new BuilderException("Error parsing JpaMapper. Cause: " + e, e);
        }
    }

}
