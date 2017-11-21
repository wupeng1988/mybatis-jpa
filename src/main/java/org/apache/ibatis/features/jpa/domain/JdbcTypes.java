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
package org.apache.ibatis.features.jpa.domain;

import org.apache.ibatis.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by adam on 7/2/17.
 */
public enum  JdbcTypes {
    STRING("varchar", String.class),
    LONGTEXT("longtext", String.class),
    CHAR("char", char.class),
    CHAR_OBj("char", Character.class),
    INT("int", int.class),
    INTEGER("int", Integer.class),
    SHORT("tinyint", short.class),
    SHORT_OBJ("tinyint", Short.class),
    BYTE("tinyint", byte.class),
    BYTE_Obj("tinyint", Byte.class),
    BIGDECIMAL("decimal", BigDecimal.class),
    DATETIME("datetime", Date.class),
    FLOAT("float", float.class),
    FLOAT_OBJ("float", Float.class),
    BIGINT_OBJ("bigint", Long.class),
    BIGINT("bigint", long.class),
    DOUBLE("double", double.class),
    DOUBLE_OBJ("double", Double.class);

    private String jdbcType;
    private Class javaType;

    JdbcTypes(String jdbcType, Class javaType) {
        this.jdbcType = jdbcType;
        this.javaType = javaType;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public Class getJavaType() {
        return javaType;
    }

    public static JdbcTypes jdbcTypeOf(String jdbcType) {
        for (JdbcTypes jdbcTypes : values()) {
            if (StringUtils.equals(jdbcType, jdbcTypes.getJdbcType())) {
                return jdbcTypes;
            }
        }

        throw new IllegalArgumentException("No constant match for jdbcType : " + jdbcType);
    }

    public static JdbcTypes javaTypeOf(Class javaType) {
        for (JdbcTypes jdbcTypes : values()) {
            if (javaType == jdbcTypes.getJavaType()) {
                return jdbcTypes;
            }
        }

        throw new IllegalArgumentException("No constant match for javaType : " + javaType);
    }
}
