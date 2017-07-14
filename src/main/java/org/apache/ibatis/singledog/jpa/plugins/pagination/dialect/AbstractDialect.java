package org.apache.ibatis.singledog.jpa.plugins.pagination.dialect;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.singledog.jpa.cache.Cache;
import org.apache.ibatis.singledog.jpa.cache.SimpleCache;
import org.apache.ibatis.singledog.jpa.domain.Pageable;
import org.apache.ibatis.singledog.jpa.domain.Sort;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Adam on 2017/7/14.
 */
public abstract class AbstractDialect implements Dialect {

    private static final String DEFAULT_INDEX = "";

    /**
     * Cache,
     */
    private static final Cache<String, String> MS_CACHE = new SimpleCache<>();

    @Override
    public boolean isPageQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        String index = MS_CACHE.get(ms.getId());
        if (index == null) {//null means no statement hasn't been parsed
            return extractPageParam(ms, parameterObject) != null;
        } else {//already parsed
            return !DEFAULT_INDEX.equals(index);
        }
    }

    @Override
    public boolean isSortQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        String index = MS_CACHE.get(ms.getId());
        if (index == null) {
            return extractSortParam(ms, parameterObject) != null;
        } else {
            return !DEFAULT_INDEX.equals(index);
        }
    }

    @Override
    public String getCountSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey countKey) {
        return null;
    }

    @Override
    public String getPageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
        return null;
    }

    @Override
    public String getSortSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
        return null;
    }

    Sort extractSortParam(MappedStatement ms, Object parameterObject) {
        String index = MS_CACHE.get(ms.getId());
        if (index == null) {
            if (parameterObject instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) parameterObject).entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Sort) {
                        MS_CACHE.put(ms.getId(), entry.getKey());
                        return (Sort) value;
                    }
                    if (value instanceof Pageable) {
                        MS_CACHE.put(ms.getId(), entry.getKey());
                        return ((Pageable) value).getSort();
                    }
                }
            }
        } else if (!DEFAULT_INDEX.equals(index)) {
            return (Sort) ((Map) parameterObject).get(index);
        }

        MS_CACHE.put(ms.getId(), DEFAULT_INDEX);
        return null;
    }

    Pageable extractPageParam(MappedStatement ms, Object parameterObject) {
        String index = MS_CACHE.get(ms.getId());
        if (index == null) {
            if (parameterObject instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) parameterObject).entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Pageable) {
                        MS_CACHE.put(ms.getId(), entry.getKey());
                        return (Pageable) value;
                    }
                }
            }
        } else if (!DEFAULT_INDEX.equals(index)) {
            return (Pageable) ((Map) parameterObject).get(index);
        }

        MS_CACHE.put(ms.getId(), DEFAULT_INDEX);
        return null;
    }

}
