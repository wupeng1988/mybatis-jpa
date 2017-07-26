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
package org.apache.ibatis.features.jpa.ddl;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.utils.CollectionUtils;
import org.apache.ibatis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Based on entity meta data, check corresponding table meta data
 *
 * Created by Adam on 2017/7/25.
 */
public class SchemaExport {
    private static final Logger logger = LoggerFactory.getLogger(SchemaExport.class);

    /**
     *
     * begin to check db meta data
     *
     * @param showScript print the script
     * @param execute automatically do the ddl operation
     */
    public void export(DataSource dataSource, boolean showScript, boolean execute)  {
        if (!showScript && !execute)
            return;

        logger.info("checking database ....");
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();) {

            DDLGenerator generator = DDLGeneratorFactory.getInstance().getDDLGenerator(checkDBType(connection));
            List<String> sqls = generator.generateDDL(connection);
            if (CollectionUtils.isEmpty(sqls)) {
                logger.info(" checking database complete !");
                return;
            }

            for (String sql : sqls) {
                if (showScript) {
                    logger.warn(sql);
                }

                if (execute) {
                    logger.warn("execute sql : {}", sql);
                    statement.execute(sql);
                }
            }
        } catch (Exception e) {
//            throw new RuntimeException("Update Database schema error !", e);
            logger.error(e.getMessage(), e);
        }
        logger.info(" checking database complete !");
    }

    public DBType checkDBType(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String url = meta.getURL();
        if (!StringUtils.isEmpty(url)) {
            url = url.toLowerCase();
            if (url.contains("mysql"))
                return DBType.MYSQL;
            if (url.contains("db2"))
                return DBType.DB2;
            if (url.contains("oracle"))
                return DBType.ORACLE;
            if (url.contains("sqlserver"))
                return DBType.SQL_SERVER;
            if (url.contains("sybase"))
                return DBType.SYBASE;
            if (url.contains("postgresql"))
                return DBType.PostgreSQL;
        }

        throw new RuntimeException(" Can not detect database type !");
    }

}
