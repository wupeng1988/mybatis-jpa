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
package org.apache.ibatis.singledog.jpa.domain;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.singledog.jpa.generator.EntitySqlDispatcher;
import org.apache.ibatis.singledog.jpa.generator.MetaDataParser;
import org.apache.ibatis.singledog.jpa.meta.Table;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.utils.StringUtils;

import java.util.*;

/**
 * 通用的Example查询对象
 *
 */
public final class Example<T> {
    private String orderByClause;

    private boolean distinct;

    private boolean exists;

    private boolean notNull;

    private Set<String> selectColumns;

    private List<Criteria> oredCriteria;

    private Class<T> entityClass;

    private MetaDataParser dataParser;

    //动态表名
    private String tableName;

    private Sort sort;
    /**
     * 默认exists为true
     *
     * @param entityClass
     */
    public Example(Class<T> entityClass) {
        this(entityClass, true);
    }

    /**
     * 带exists参数的构造方法，默认notNull为false，允许为空
     *
     * @param entityClass
     * @param exists      - true时，如果字段不存在就抛出异常，false时，如果不存在就不使用该字段的条件
     */
    public Example(Class<T> entityClass, boolean exists) {
        this(entityClass, exists, false);
    }

    /**
     * 带exists参数的构造方法
     *
     * @param entityClass
     * @param exists      - true时，如果字段不存在就抛出异常，false时，如果不存在就不使用该字段的条件
     * @param notNull     - true时，如果值为空，就会抛出异常，false时，如果为空就不使用该字段的条件
     */
    public Example(Class<T> entityClass, boolean exists, boolean notNull) {
        this.exists = exists;
        this.notNull = notNull;
        oredCriteria = new ArrayList<Criteria>();
        this.entityClass = entityClass;
        this.dataParser = EntitySqlDispatcher.getInstance().getMetaDataParser(entityClass);
        if (dataParser == null) {
            throw new IllegalArgumentException("Unknow entity class ".concat(String.valueOf(entityClass)));
        }
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public void orderBy(Sort.Direction direction, String ... properties) {
        this.sort = Sort.by(direction, properties);
        this.orderByClause = this.sort.toSql(dataParser.getTable());
    }

    public Set<String> getSelectColumns() {
        return selectColumns;
    }

    /**
     * 指定要查询的属性列 - 这里会自动映射到表字段
     *
     * @param properties
     * @return
     */
    public Example selectProperties(String... properties) {
        if (properties != null && properties.length > 0) {
            if (this.selectColumns == null) {
                this.selectColumns = new LinkedHashSet<String>();
            }

            Table table = dataParser.getTable();
            for (String property : properties) {
                String column = table.getColumnByProperty(property);
                if (!StringUtils.isEmpty(column))
                    this.selectColumns.add(property);
            }
        }
        return this;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria(dataParser.getTable(), exists, notNull);
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * 设置表名
     *
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    abstract static class TableColumnMapping {
        Table table;
        TableColumnMapping(Table table) {
            this.table = table;
        }

        String getColumnByProperty(String property) {
            String column = table.getColumnByProperty(property);
            if (StringUtils.isEmpty(column))
                throw new IllegalArgumentException("Unknown property ".concat(property)
                        .concat(" for entity ").concat(String.valueOf(table.getEntity())));
            return column;
        }

        String getExistsProperty(String property) {
            if (table.propertyExists(property))
                return property;
            return null;
        }
    }

    protected abstract static class GeneratedCriteria extends TableColumnMapping {
        protected List<Criterion> criteria;
        //字段是否必须存在
        protected boolean exists;
        //值是否不能为空
        protected boolean notNull;

        protected GeneratedCriteria(Table table, boolean exists, boolean notNull) {
            super(table);
            this.exists = exists;
            this.notNull = notNull;
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            if (condition.startsWith("null")) {
                return;
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                if (notNull) {
                    throw new RuntimeException("Value for " + property + " cannot be null");
                } else {
                    return;
                }
            }
            if (property == null) {
                return;
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                if (notNull) {
                    throw new RuntimeException("Between values for " + property + " cannot be null");
                } else {
                    return;
                }
            }
            if (property == null) {
                return;
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIsNull(String property) {
            addCriterion(getColumnByProperty(property) + " is null");
            return (Criteria) this;
        }

        public Criteria andIsNotNull(String property) {
            addCriterion(getColumnByProperty(property) + " is not null");
            return (Criteria) this;
        }

        public Criteria andEqualTo(String property, Object value) {
            addCriterion(getColumnByProperty(property) + " =", value, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andNotEqualTo(String property, Object value) {
            addCriterion(getColumnByProperty(property) + " <>", value, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andGreaterThan(String property, Object value) {
            addCriterion(getColumnByProperty(property) + " >", value, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andGreaterThanOrEqualTo(String property, Object value) {
            addCriterion(getColumnByProperty(property) + " >=", value, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andLessThan(String property, Object value) {
            addCriterion(getColumnByProperty(property) + " <", value, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andLessThanOrEqualTo(String property, Object value) {
            addCriterion(getColumnByProperty(property) + " <=", value, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andIn(String property, Iterable values) {
            addCriterion(getColumnByProperty(property) + " in", values, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andNotIn(String property, Iterable values) {
            addCriterion(getColumnByProperty(property) + " not in", values, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andBetween(String property, Object value1, Object value2) {
            addCriterion(getColumnByProperty(property) + " between", value1, value2, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andNotBetween(String property, Object value1, Object value2) {
            addCriterion(getColumnByProperty(property) + " not between", value1, value2, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andLike(String property, String value) {
            addCriterion(getColumnByProperty(property) + "  like", value, getExistsProperty(property));
            return (Criteria) this;
        }

        public Criteria andNotLike(String property, String value) {
            addCriterion(getColumnByProperty(property) + "  not like", value, getExistsProperty(property));
            return (Criteria) this;
        }

        /**
         * 手写条件
         *
         * @param condition 例如 "length(countryname)<5"
         * @return
         */
        public Criteria andCondition(String condition) {
            addCriterion(condition);
            return (Criteria) this;
        }

        /**
         * 手写左边条件，右边用value值
         *
         * @param condition 例如 "length(countryname)="
         * @param value     例如 5
         * @return
         */
        public Criteria andCondition(String condition, Object value) {
            criteria.add(new Criterion(condition, value));
            return (Criteria) this;
        }

        /**
         * 手写左边条件，右边用value值
         *
         * @param condition   例如 "length(countryname)="
         * @param value       例如 5
         * @param typeHandler 类型处理
         * @return
         * @deprecated 由于typeHandler起不到作用，该方法会在4.x版本去掉
         */
        @Deprecated
        public Criteria andCondition(String condition, Object value, String typeHandler) {
            criteria.add(new Criterion(condition, value, typeHandler));
            return (Criteria) this;
        }

        /**
         * 手写左边条件，右边用value值
         *
         * @param condition   例如 "length(countryname)="
         * @param value       例如 5
         * @param typeHandler 类型处理
         * @return
         * @deprecated 由于typeHandler起不到作用，该方法会在4.x版本去掉
         */
        @Deprecated
        public Criteria andCondition(String condition, Object value, Class<? extends TypeHandler> typeHandler) {
            criteria.add(new Criterion(condition, value, typeHandler.getCanonicalName()));
            return (Criteria) this;
        }

        /**
         * 将此对象的不为空的字段参数作为相等查询条件
         *
         * @param param 参数对象
         * @author Bob {@link}0haizhu0@gmail.com
         * @Date 2015年7月17日 下午12:48:08
         */
        public Criteria andEqualTo(Object param) {
            MetaObject metaObject = SystemMetaObject.forObject(param);
            String[] properties = metaObject.getGetterNames();
            for (String property : properties) {
                //属性和列对应Map中有此属性
                if (table.propertyExists(property)) {
                    Object value = metaObject.getValue(property);
                    //属性值不为空
                    if (value != null) {
                        andEqualTo(property, value);
                    }
                }
            }
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria(Table table, boolean exists, boolean notNull) {
            super(table, exists, notNull);
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof Collection<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }
    }
}