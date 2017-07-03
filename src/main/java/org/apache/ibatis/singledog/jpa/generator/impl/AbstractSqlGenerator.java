package org.apache.ibatis.singledog.jpa.generator.impl;

import org.apache.ibatis.singledog.jpa.generator.SqlGenerator;
import org.apache.ibatis.utils.CollectionUtils;
import org.apache.ibatis.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam on 2017/7/3.
 */
public abstract class AbstractSqlGenerator implements SqlGenerator {

    Document createDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.newDocument();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String getMethod(Map<String, Object> params) {
        return String.valueOf(params.get(PARAM_KEY_ID));
    }

    void trim(StringBuilder builder) {
        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);
    }

    Element createSqlNode(String tagName, String originalSql, Map<String, String> attrs) {
        try {
            Document document = createDocument();
            Element root = document.createElement(tagName);
            Text sqlNode = document.createTextNode(originalSql);
            root.appendChild(sqlNode);
            if (!CollectionUtils.isEmpty(attrs)) {
                attrs.forEach(root::setAttribute);
            }
            return root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class MapBuilder<K, V> {
        private Map<K, V> map = new HashMap<>();

        public MapBuilder put(K k, V v) {
            map.put(k, v);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }

    }

    Element createInsertElement(String id, String parameterType, String keyProperty,
                                       String keyColumn, Boolean useGeneratedKeys, TextProvider provider) {

        Document document = createDocument();
        Element root = document.createElement("insert");
        root.setAttribute("id", id);
        root.setAttribute("parameterType", parameterType);
        if (!StringUtils.isEmpty(keyProperty))
            root.setAttribute("keyProperty", keyProperty);
        if (!StringUtils.isEmpty(keyColumn))
            root.setAttribute("keyColumn", keyColumn);
        if (useGeneratedKeys != null)
            root.setAttribute("useGeneratedKeys", String.valueOf(useGeneratedKeys));
        Text text = document.createTextNode(provider.getText());
        root.appendChild(text);
        return root;
    }

    Element createForEachElement(String item, String index, String collection,
                                        String open, String separate, String close, TextProvider provider) {
        Document document = createDocument();
        Element root = document.createElement("foreach");
        root.setAttribute("item", item);
        root.setAttribute("index", index);
        root.setAttribute("collection", collection);
        root.setAttribute("open", open);
        root.setAttribute("separator", separate);
        root.setAttribute("close", close);
        Text text = document.createTextNode(provider.getText());
        root.appendChild(text);
        return root;
    }

    Element createSelectElement(String id, String parameterType, String resultType,
                                       String resultMap, TextProvider provider) {
        Document document = createDocument();
        Element root = document.createElement("select");
        root.setAttribute("id", id);
        if (!StringUtils.isEmpty(parameterType))
            root.setAttribute("parameterType", parameterType);
        if (!StringUtils.isEmpty(resultType))
            root.setAttribute("resultType", resultType);
        if (!StringUtils.isEmpty(resultMap))
            root.setAttribute("resultMap", resultMap);
        root.appendChild(document.createTextNode(provider.getText()));
        return root;
    }

    Element createDeleteElement(String id, String parameterType, TextProvider provider) {
        Document document = createDocument();
        Element root = document.createElement("delete");
        root.setAttribute("id", id);
        if (!StringUtils.isEmpty(parameterType))
            root.setAttribute("parameterType", parameterType);
        root.appendChild(document.createTextNode(provider.getText()));
        return root;
    }

    Element createUpdateElement(String id, String parameterType, TextProvider provider) {
        Document document = createDocument();
        Element root = document.createElement("update");
        root.setAttribute("id", id);
        if (!StringUtils.isEmpty(parameterType))
            root.setAttribute("parameterType", parameterType);
        root.appendChild(document.createTextNode(provider.getText()));
        return root;
    }

    Element createIfElement(String test, TextProvider provider) {
        Document document = createDocument();
        Element root = document.createElement("if");
        root.setAttribute("test", test);
        root.appendChild(document.createTextNode(provider.getText()));
        return root;
    }

    Element createIfNotNullElement(String property, TextProvider provider) {
        return createIfElement(property + " != null", provider);
    }

    interface TextProvider {
        String getText();
    }

}
