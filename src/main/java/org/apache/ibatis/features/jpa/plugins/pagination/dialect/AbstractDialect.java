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
package org.apache.ibatis.features.jpa.plugins.pagination.dialect;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.features.jpa.generator.EntitySqlDispatcher;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.features.jpa.cache.Cache;
import org.apache.ibatis.features.jpa.cache.SimpleCache;
import org.apache.ibatis.features.jpa.domain.Pageable;
import org.apache.ibatis.features.jpa.domain.Sort;
import org.apache.ibatis.features.jpa.generator.MetaDataParser;

import java.util.Map;

/**
 * Created by Adam on 2017/7/14.
 */
public abstract class AbstractDialect implements Dialect {

    private static final String DEFAULT_INDEX = "";

    /**
     * Cache,
     */
    private static final Cache<String, String> MS_PAGE_CACHE = new SimpleCache<>();
    private static final Cache<String, String> MS_SORT_CACHE = new SimpleCache<>();

    //处理SQL
    protected CountSqlParser countSqlParser = new CountSqlParser();

    @Override
    public boolean isPageQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        return extractPageParam(ms, parameterObject) != null;//TODO profiling
    }

    @Override
    public boolean isSortQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        return extractSortParam(ms, parameterObject) != null;//TODO profiling
    }

    @Override
    public String getCountSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey countKey) {
        return countSqlParser.getSmartCountSql(boundSql.getSql());
    }

    @Override
    public String getPageSql(MappedStatement ms, BoundSql boundSql, Pageable pageable, RowBounds rowBounds, CacheKey pageKey) {
        String sql = boundSql.getSql();
//        Pageable pageable = extractPageParam(ms, parameterObject);
        if (pageable == null) {
            return sql;
        }

        return getPageSql(getSortSql(ms, sql, pageable.getSort()), pageable, pageKey);
    }

    protected abstract String getPageSql(String sql, Pageable pageable, CacheKey cacheKey) ;

    @Override
    public String getSortSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
        String sql = boundSql.getSql();
        Sort sort = extractSortParam(ms, parameterObject);

        return getSortSql(ms, sql, sort);
    }

    protected String getSortSql(MappedStatement ms, String sql, Sort sort) {
        if (sql.toLowerCase().contains("order by")) {
            return sql;
        }
        MetaDataParser dataParser = EntitySqlDispatcher.getInstance()
                .getMetaDataParserByNamespace(ms.getNamespace());

        if (sort != null) {
            String sortSql = sort.toSql(dataParser.getTable());
            return sql.concat(" order by ").concat(sortSql);
        }
        return sql;
    }

    @Override
    public Pageable processPageParam(MappedStatement ms, Object paramObject) {
        return extractPageParam(ms, paramObject);
    }

    Sort extractSortParam(MappedStatement ms, Object parameterObject) {
        String index = MS_SORT_CACHE.get(ms.getId());
        if (index == null) {
            if (parameterObject instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) parameterObject).entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Sort) {
                        MS_SORT_CACHE.put(ms.getId(), entry.getKey());
                        return (Sort) value;
                    }
                    if (value instanceof Pageable) {
                        MS_SORT_CACHE.put(ms.getId(), entry.getKey());
                        return ((Pageable) value).getSort();
                    }
                }
            }

            MS_SORT_CACHE.put(ms.getId(), DEFAULT_INDEX);
        } else if (!DEFAULT_INDEX.equals(index)) {
            return (Sort) ((Map) parameterObject).get(index);
        }

        if (parameterObject instanceof Sort) {
            return (Sort) parameterObject;
        } else if (parameterObject instanceof Pageable) {
            return ((Pageable) parameterObject).getSort();
        }

        return null;
    }

    Pageable extractPageParam(MappedStatement ms, Object parameterObject) {
        String index = MS_PAGE_CACHE.get(ms.getId());
        if (index == null) {
            if (parameterObject instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) parameterObject).entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Pageable) {
                        MS_PAGE_CACHE.put(ms.getId(), entry.getKey());
                        return (Pageable) value;
                    }
                }
            }

            MS_PAGE_CACHE.put(ms.getId(), DEFAULT_INDEX);
        } else if (!DEFAULT_INDEX.equals(index)) {
            return (Pageable) ((Map) parameterObject).get(index);
        }

        if (parameterObject instanceof Pageable) {
            return (Pageable) parameterObject;
        }

        return null;
    }

}
