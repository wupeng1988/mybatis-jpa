package org.apache.ibatis.singledog.jpa.meta;

import org.apache.ibatis.singledog.jpa.domain.JdbcTypes;
import org.apache.ibatis.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adam on 7/2/17.
 */
public abstract class JdbcTypeConverter {

    private static final Map<Class, String> typeMapping;

    static {
        Map<Class, String> map = new HashMap<>();
        for (JdbcTypes jdbcTypes : JdbcTypes.values()) {
            map.put(jdbcTypes.getJavaType(), jdbcTypes.getJdbcType());
        }
        typeMapping = Collections.unmodifiableMap(map);
    }

    public static String toJdbcType(Class javaType) {
        return toJdbcType(javaType, 0);
    }

    public static String toJdbcType(Class javaType, int length) {
        if (String.class == javaType) {
            if (length >= 1000) {
                return JdbcTypes.LONGTEXT.getJdbcType();
            }

            return JdbcTypes.STRING.getJdbcType();
        }

        String jdbcType = typeMapping.get(javaType);
        if (StringUtils.isEmpty(jdbcType)) {
            throw new IllegalArgumentException("Unknown jdbc type for java type : " + javaType.getName());
        }

        return jdbcType;
    }

}
