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
package org.apache.ibatis.features.jpa.plugins.pagination;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.features.jpa.domain.Page;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.features.jpa.cache.Cache;
import org.apache.ibatis.features.jpa.cache.SimpleCache;
import org.apache.ibatis.features.jpa.domain.PageImpl;
import org.apache.ibatis.features.jpa.domain.Pageable;
import org.apache.ibatis.features.jpa.plugins.pagination.dialect.Dialect;
import org.apache.ibatis.features.jpa.plugins.pagination.dialect.MySqlDialect;
import org.apache.ibatis.utils.ReflectionUtils;
import org.apache.ibatis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Mybatis - 通用分页拦截器<br/>
 * 项目地址 : http://git.oschina.net/free/Mybatis_PageHelper
 *
 * @author liuzh/abel533/isea533
 * @version 5.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts(
    {
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    }
)
public class PageInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(PageInterceptor.class);

    //缓存count查询的ms
    private Cache<String, MappedStatement> msCountMap = new SimpleCache<>();
    private Cache<String, Class> returnTypeCache = new SimpleCache<>();

    private Dialect dialect = new MySqlDialect();
    private Field additionalParametersField;
    private String countSuffix = "_COUNT";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];//org.apache.ibatis.binding.MapperMethod$ParamMap
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if(args.length == 4){
            //4 个参数时
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
        List resultList;

        Pageable pageable = dialect.processPageParam(ms, parameter);
        if (pageable != null) {
            logger.info("statement {} is a page query !", ms.getId());
            String msId = ms.getId();
            Configuration configuration = ms.getConfiguration();
            Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);
            //query list for page
            resultList = getPaginationList(ms, parameter, rowBounds, resultHandler, executor, cacheKey, boundSql,
                    configuration, additionalParameters, pageable);
            if (isReturnPage(ms)) {
                Long count = getCountNum(ms, parameter, rowBounds, resultHandler, executor, boundSql, msId, configuration);
                return Collections.singletonList(new PageImpl(resultList, pageable, count));
            }
            return resultList;
        } else if (dialect.isSortQuery(ms, parameter, rowBounds)) {
            logger.info("statement {} is sort query !", ms.getId());
            Configuration configuration = ms.getConfiguration();
            Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);
            resultList = getSortedList(ms, parameter, rowBounds, resultHandler, executor, cacheKey, boundSql, configuration, additionalParameters);
            return resultList;
        } else {
            return invocation.proceed();
        }
    }

    private boolean isReturnPage(MappedStatement ms) {
        String key = ms.getId();
        Class type = returnTypeCache.get(key);
        if (type == null) {
            try {
                Class mapperClass = Class.forName(ms.getNamespace());
                String methodName = ms.getId().substring(ms.getId().lastIndexOf(".") + 1);
                Method method = ReflectionUtils.findMethodByName(mapperClass, methodName);
                if (method != null)
                    type = method.getReturnType();
            } catch (Exception e) {
                logger.error("can not parse return type for method: {}", ms.getId());
                logger.error(e.getMessage(), e);
            }

            if (type == null)
                type = List.class;
            returnTypeCache.put(key, type);
        }

        return Page.class.isAssignableFrom(type);
    }

    private List getSortedList(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, Executor executor, CacheKey cacheKey, BoundSql boundSql, Configuration configuration, Map<String, Object> additionalParameters) throws SQLException {
        List resultList;//生成分页的缓存 key
        //调用方言获取分页 sql
        String sortedSql = dialect.getSortSql(ms, boundSql, parameter, rowBounds, cacheKey);
        logger.info("sorted sql is : {}, for statement: {}", sortedSql, ms.getId());
        BoundSql pageBoundSql = new BoundSql(configuration, sortedSql, boundSql.getParameterMappings(), parameter);
        //设置动态参数
        for (String key : additionalParameters.keySet()) {
            pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
        //执行分页查询
        resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);
        return resultList;
    }

    private List getPaginationList(MappedStatement ms, Object parameter, RowBounds rowBounds,
                                   ResultHandler resultHandler, Executor executor, CacheKey cacheKey,
                                   BoundSql boundSql, Configuration configuration, Map<String, Object> additionalParameters,
                                   Pageable pageable) throws SQLException {
        List resultList;//生成分页的缓存 key
        CacheKey pageKey = cacheKey;
        //调用方言获取分页 sql
        String pageSql = dialect.getPageSql(ms, boundSql, pageable, rowBounds, pageKey);
        logger.info("page sql for ms {} is : {}", ms.getId(), pageSql);
        BoundSql pageBoundSql = new BoundSql(configuration, pageSql, boundSql.getParameterMappings(), parameter);
        //设置动态参数
        for (String key : additionalParameters.keySet()) {
            pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
        //执行分页查询
        resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, pageKey, pageBoundSql);
        return resultList;
    }

    private Long getCountNum(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, Executor executor, BoundSql boundSql, String msId, Configuration configuration) throws IllegalAccessException, SQLException {
        String countMsId = msId + countSuffix;
        Long count;
        //先判断是否存在手写的 count 查询
        MappedStatement countMs = getExistedMappedStatement(configuration, countMsId);
        if(countMs != null){
            count = executeManualCount(executor, countMs, parameter, boundSql, resultHandler);
        } else {
            countMs = msCountMap.get(countMsId);
            //自动创建
            if (countMs == null) {
                //根据当前的 ms 创建一个返回值为 Long 类型的 ms
                countMs = MSUtils.newCountMappedStatement(ms, countMsId);
                msCountMap.put(countMsId, countMs);
            }
            count = executeAutoCount(executor, countMs, parameter, boundSql, rowBounds, resultHandler);
        }
        return count;
    }

    /**
     * 执行手动设置的 count 查询，该查询支持的参数必须和被分页的方法相同
     *
     * @param executor
     * @param countMs
     * @param parameter
     * @param boundSql
     * @param resultHandler
     * @return
     * @throws IllegalAccessException
     * @throws SQLException
     */
    private Long executeManualCount(Executor executor, MappedStatement countMs,
                                    Object parameter, BoundSql boundSql,
                                    ResultHandler resultHandler) throws IllegalAccessException, SQLException {
        CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);
        BoundSql countBoundSql = countMs.getBoundSql(parameter);
        Object countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
        Long count = ((Number) ((List) countResultList).get(0)).longValue();
        return count;
    }

    /**
     * 执行自动生成的 count 查询
     *
     * @param executor
     * @param countMs
     * @param parameter
     * @param boundSql
     * @param rowBounds
     * @param resultHandler
     * @return
     * @throws IllegalAccessException
     * @throws SQLException
     */
    private Long executeAutoCount(Executor executor, MappedStatement countMs,
                                  Object parameter, BoundSql boundSql,
                                  RowBounds rowBounds, ResultHandler resultHandler) throws IllegalAccessException, SQLException {
        Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);
        //创建 count 查询的缓存 key
        CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);
        //调用方言获取 count sql
        String countSql = dialect.getCountSql(countMs, boundSql, parameter, rowBounds, countKey);
        //countKey.update(countSql);
        BoundSql countBoundSql = new BoundSql(countMs.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);
        //当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
        for (String key : additionalParameters.keySet()) {
            countBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
        //执行 count 查询
        Object countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
        Long count = (Long) ((List) countResultList).get(0);
        return count;
    }

    /**
     * 尝试获取已经存在的在 MS，提供对手写count和page的支持
     *
     * @param configuration
     * @param msId
     * @return
     */
    private MappedStatement getExistedMappedStatement(Configuration configuration, String msId){
        MappedStatement mappedStatement = null;
        try {
            mappedStatement = configuration.getMappedStatement(msId, false);
        } catch (Throwable t){
            //ignore
        }
        return mappedStatement;
    }

    @Override
    public Object plugin(Object target) {
        //TODO Spring bean 方式配置时，如果没有配置属性就不会执行下面的 setProperties 方法，就不会初始化，因此考虑在这个方法中做一次判断和初始化
        //TODO https://github.com/pagehelper/Mybatis-PageHelper/issues/26
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String dialectClass = properties.getProperty("dialect");
        if (StringUtils.isEmpty(dialectClass)) {
            this.dialect = new MySqlDialect();
        } else {
            try {
                this.dialect = (Dialect) Class.forName(dialectClass).newInstance();
            } catch (Exception e) {
                logger.error("can not create instance of {}", dialectClass);
                throw new RuntimeException(e);
            }
        }
        //反射获取 BoundSql 中的 additionalParameters 属性
        try {
            additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
            additionalParametersField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Error found field additionalParameters of BoundSql");
        }
    }

}
