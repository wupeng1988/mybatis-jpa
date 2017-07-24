package org.apache.ibatis.features.jpa.builder;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.features.jpa.domain.Pageable;
import org.apache.ibatis.features.jpa.domain.Sort;
import org.apache.ibatis.features.jpa.generator.EntitySqlDispatcher;
import org.apache.ibatis.features.jpa.generator.MetaDataParser;
import org.apache.ibatis.features.jpa.mapper.JpaMapper;
import org.apache.ibatis.features.jpa.meta.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Adam on 2017/7/24.
 */
public class SqlContext {
    private static final String param = "param";

    private static final String[] SEPARATORS =
            new String[]{"By","Equals","Is","And", "Or"};

    private Class clazz;
    private Method method;
    private StringBuilder originalSql = new StringBuilder();
    private MetaDataParser metaDataParser;
    private String flag;
    private Table table;
    private int argIndex;
    private Map<Integer, String> argNames;

    private List<String> keyWords = new LinkedList<>();

    public SqlContext(Class clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
        if (!JpaMapper.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Not supported ! ".concat(clazz.getName()));
        }

        this.metaDataParser = EntitySqlDispatcher.getInstance().getMetaDataParser(clazz);
        this.table = metaDataParser.getTable();
        argIndex = 0;
        argNames = new HashMap<>();
    }

    private void parseMethodName() {
        String name = method.getName();
        for (String separator : SEPARATORS) {
            int index = name.indexOf(separator);
            if (index == -1)
                continue;

            keyWords.add(name.substring(0, index));
            keyWords.add(separator);

            name = name.substring(index + separator.length());
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
                argNames.put(i, param + i);
            }
        }
    }

    public void parse() {
        parseMethodName();
        parseParams();
        for (String keyWord : keyWords) {
            new SqlBuilderChain().build(keyWord, this);
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
        return originalSql.toString();
    }

    public String getSqlXml() {
        return originalSql.toString();//TODO
    }

    public boolean isSelect() {
        return false;//TODO
    }

    public Table getTable() {
        return table;
    }

}
