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
package org.apache.ibatis.singledog.jpa.plugins.pagination.dialect;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.singledog.jpa.domain.Pageable;

import java.util.List;
import java.util.Properties;

/**
 * 数据库方言，针对不同数据库进行实现
 *
 * @author liuzh
 */
public interface Dialect {
    /**
     * 跳过 count 和 分页查询
     *
     * @param ms              MappedStatement
     * @param parameterObject 方法参数
     * @param rowBounds       分页参数
     * @return true 跳过，返回默认查询结果，false 执行分页查询
     */
    boolean isPageQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds);

    Pageable processPageParam(MappedStatement ms, Object paramObject);

    boolean isSortQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds);

    /**
     * 生成 count 查询 sql
     *
     * @param ms              MappedStatement
     * @param boundSql        绑定 SQL 对象
     * @param parameterObject 方法参数
     * @param rowBounds       分页参数
     * @param countKey        count 缓存 key
     * @return
     */
    String getCountSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey countKey);

    /**
     * 生成分页查询 sql
     *
     * @param ms              MappedStatement
     * @param boundSql        绑定 SQL 对象
     * @param parameterObject 方法参数
     * @param rowBounds       分页参数
     * @param pageKey         分页缓存 key
     * @return
     */
    String getPageSql(MappedStatement ms, BoundSql boundSql, Pageable parameterObject, RowBounds rowBounds, CacheKey pageKey);

    /**
     * 排序SQL
     * @param ms
     * @param boundSql
     * @param parameterObject
     * @param rowBounds
     * @param pageKey
     * @return
     */
    String getSortSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey);
}
