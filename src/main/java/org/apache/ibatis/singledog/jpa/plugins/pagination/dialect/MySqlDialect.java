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
import org.apache.ibatis.singledog.jpa.domain.Pageable;

/**
 * @author liuzh
 */
public class MySqlDialect extends AbstractDialect {

    @Override
    public String getPageSql(String sql, Pageable page, CacheKey pageKey) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        if (page.getOffset() == 0) {
            sqlBuilder.append(" LIMIT ");
            sqlBuilder.append(page.getPageSize());
        } else {
            sqlBuilder.append(" LIMIT ");
            sqlBuilder.append(page.getOffset());
            sqlBuilder.append(",");
            sqlBuilder.append(page.getPageSize());
            pageKey.update(page.getOffset());
        }
        pageKey.update(page.getPageSize());
        return sqlBuilder.toString();
    }

}
