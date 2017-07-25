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
package org.apache.ibatis.features.jpa.builder;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.features.jpa.domain.Pageable;
import org.apache.ibatis.features.jpa.domain.Sort;
import org.apache.ibatis.features.jpa.generator.EntitySqlDispatcher;
import org.apache.ibatis.features.jpa.generator.MetaDataParser;
import org.apache.ibatis.features.jpa.generator.impl.AbstractSqlGenerator;
import org.apache.ibatis.features.jpa.mapper.JpaMapper;
import org.apache.ibatis.features.jpa.meta.Table;
import org.apache.ibatis.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Adam on 2017/7/24.
 */
public class SqlContext extends AbstractSqlGenerator {
    private static final String param = "param";

    private static final String[] SEPARATORS =
            new String[]{"OrderBy","By","And", "Or"};

    private Class clazz;
    private Method method;
    private StringBuilder originalSql = new StringBuilder();
    private String limitSegment = "";
    private MetaDataParser metaDataParser;
    private String flag;
    private Table table;
    private int argIndex;
    private Class[] argTypes;
    private Map<Integer, String> argNames;
    private SqlBuilderChain builderChain;

    private List<String> keyWords = new LinkedList<>();

    public SqlContext(Class clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
        if (!JpaMapper.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Not supported ! ".concat(clazz.getName()));
        }

        this.metaDataParser = EntitySqlDispatcher.getInstance().getMetaDataParserByNamespace(clazz.getName());
        this.table = metaDataParser.getTable();
        argIndex = 0;
        argNames = new HashMap<>();
        argTypes = method.getParameterTypes();
        builderChain = new SqlBuilderChain();
    }

    private void parseMethodName() {
        String name = method.getName();
        for (int i = 0; i < name.length(); i++) {
            for (String s : SEPARATORS) {
                if (i + s.length() < name.length()) {
                    if (StringUtils.equalsIgnoreCase(s, name.substring(i, i + s.length()))) {
                        keyWords.add(name.substring(0, i));
                        keyWords.add(name.substring(i, i + s.length()));
                        name = name.substring(i+s.length());
                        i = 0;
                    }
                }
            }
        }

        keyWords.add(name);
    }

    private void parseParams() {
        Class[] argTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        for (int i = 0; i < argTypes.length; i++) {
            Annotation[] annos = annotations[i];
            Param anno = null;
            for (Annotation a : annos) {
                if (a instanceof Param) {
                    anno = (Param) a;
                    break;
                }
            }

            if (anno != null) {
                argNames.put(i, anno.value());
            } else {
                argNames.put(i, param + (i + 1));
            }
        }
    }

    public void parse() {
        parseMethodName();
        parseParams();
        for (String keyWord : keyWords) {
            builderChain.build(keyWord, this);
            builderChain.reset();
        }
    }

    public String getParamName() {
        return argNames.get(argIndex++);
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public SqlContext append(String sql) {
        originalSql.append(" ").append(sql);
        return this;
    }

    public String getOriginalSql() {
        return originalSql.toString().concat(limitSegment);
    }

    public String getSqlXml() {
        String paramType = null;
        if (argTypes.length > 1)
            paramType = "map";
        else if (argTypes.length == 1)
            paramType = argTypes[0].getName();

        if (StringUtils.equalsIgnoreCase(flag, "select")) {
            return select(method.getName(), paramType, null, null, getOriginalSql());
        } else if (StringUtils.equalsIgnoreCase(flag, "delete")){
            return update(method.getName(), paramType, getOriginalSql());
        }

        throw new BuilderException("Build query from method error ! "
                .concat(String.valueOf(clazz)).concat(".").concat(String.valueOf(method)));
    }

    public Table getTable() {
        return table;
    }

    public void setLimitSegment(String limitSegment) {
        this.limitSegment = limitSegment;
    }

    public Class[] getArgTypes() {
        return argTypes;
    }

    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        return null;
    }
}
