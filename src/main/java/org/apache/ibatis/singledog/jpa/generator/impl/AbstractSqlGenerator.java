package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.generator.SqlGenerator;
import org.apache.ibatis.utils.CollectionUtils;
import org.apache.ibatis.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public abstract class AbstractSqlGenerator implements SqlGenerator {

    public static final String NEW_LINE = "\n";

    public static final String TAG_DELETE = "delete";
    public static final String TAG_INSERT = "insert";
    public static final String TAG_UPDATE = "update";
    public static final String TAG_SELECT = "select";
    public static final String TAG_WHERE = "where";
    public static final String TAG_IF = "if";
    public static final String TAG_FOREACH = "foreach";
    public static final String TAG_SQL = "sql";
    public static final String TAG_TRIM = "trim";
    public static final String TAG_SET = "set";
    public static final String TAG_CHOOSE = "choose";
    public static final String TAG_WHEN = "when";
    public static final String TAG_OTHERWISE = "otherwise";

    String getMethod(Map<String, Object> params) {
        return String.valueOf(params.get(PARAM_KEY_ID));
    }

    void trim(StringBuilder builder) {
        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);
    }

    String wrapProperty(String property) {
        return new StringBuilder("#{").append(property).append("}").toString();
    }

    String createSqlStatement(String id, String originalSql) {
        return new StringBuilder().append("<sql id=\"").append(id).append("\">").append(NEW_LINE)
                .append(originalSql).append(NEW_LINE)
                .append("</sql>").toString();
    }

    static class MapBuilder {
        private Map<String, String> map = new LinkedHashMap<>();

        public MapBuilder put(String k, String v) {
            map.put(k, v);
            return this;
        }

        public Map<String, String> build() {
            return map;
        }

    }

    void appendAttrs(StringBuilder builder, Map<String, String> attrs) {
        if (!CollectionUtils.isEmpty(attrs)) {
            attrs.forEach((k,v) -> {
                builder.append(" ").append(k).append(" = \"").append(v).append("\" ");
            });
        }
    }

    String createNode(String tag, String id, String rawContent, Map<String, String> attrs) {
        return endTag(beginTag(tag, id, attrs).append(rawContent), tag).toString();
    }

    StringBuilder beginTag(String tag, String id, Map<String, String> attrs) {
        return beginTag(new StringBuilder(), tag, id, attrs);
    }

    StringBuilder beginTag(StringBuilder builder, String tag, String id, Map<String, String> attrs) {
        builder.append("<").append(tag);
        if (!StringUtils.isEmpty(id))
            builder.append(" id = \"").append(id).append("\" ");
        appendAttrs(builder, attrs);
        builder.append(">").append(NEW_LINE);
        return builder;
    }

    StringBuilder endTag(StringBuilder builder, String tag) {
        return builder.append(NEW_LINE).append("</").append(tag).append(">");
    }

    String foreach(String item, String index, String collection, String open,
                   String separator, String close, String sql) {
        return createNode("foreach", null, sql, new MapBuilder()
                .put("item", item)
                .put("index", index)
                .put("collection", collection)
                .put("open", open)
                .put("separator", separator)
                .put("close", close)
                .build());
    }

    String select(String id, String parameterType, String resultType, String resultMap, String sql) {
        MapBuilder builder = new MapBuilder();
        builder.put("id", id);
        if (!StringUtils.isEmpty(parameterType))
            builder.put("parameterType", parameterType);
        if (!StringUtils.isEmpty(resultType))
            builder.put("resultType", resultType);
        if (!StringUtils.isEmpty(resultMap))
            builder.put("resultMap", resultMap);
        if (StringUtils.isEmpty(resultType) && StringUtils.isEmpty(resultMap))
            builder.put("resultMap", MetaDataParser.DEFAULT_RESULT_MAP);
        return createNode(TAG_SELECT, id, sql, builder.build());
    }

    String insert(String id, String parameterType, String keyProperty, String keyColumn,
                  String useGeneratedKeys, String sql) {
        MapBuilder builder = new MapBuilder();
        if (!StringUtils.isEmpty(parameterType))
            builder.put("parameterType", parameterType);
        if (!StringUtils.isEmpty(keyProperty))
            builder.put("keyProperty", keyProperty);
        if (!StringUtils.isEmpty(keyColumn))
            builder.put("keyColumn", keyColumn);
        if (!StringUtils.isEmpty(useGeneratedKeys))
            builder.put("useGeneratedKeys", useGeneratedKeys);
        return createNode(TAG_INSERT, id, sql, builder.build());
    }

    String ifNotNull(String column, String sql) {
        return createNode(TAG_IF, null, sql, new MapBuilder().put("test", column + " != null").build());
    }

    String trim(String prefix, String suffix, String suffixOverrides, String sql) {
        return createNode(TAG_TRIM, null, sql, new MapBuilder()
                .put("prefix", prefix)
                .put("suffix", suffix)
                .put("suffixOverrides", suffixOverrides)
                .build());
    }

    String set(String sql) {
        return createNode(TAG_SET, null, sql, null);
    }

    String delete(String id, String parameterType, String sql) {
        MapBuilder builder = new MapBuilder();
        if (!StringUtils.isEmpty(parameterType))
            builder.put("parameterType", parameterType);
        return createNode(TAG_DELETE, id, sql, builder.build());
    }

    String update(String id, String parameterType, String sql) {
        MapBuilder builder = new MapBuilder();
        if (!StringUtils.isEmpty(parameterType))
            builder.put("parameterType", parameterType);
        return createNode(TAG_UPDATE, id, sql, builder.build());
    }
}
