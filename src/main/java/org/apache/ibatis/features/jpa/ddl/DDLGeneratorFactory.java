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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2017/7/26.
 */
public class DDLGeneratorFactory {

    private static final DDLGeneratorFactory factory = new DDLGeneratorFactory();

    private static final List<DDLGenerator> GENERATORS = new ArrayList<>();

    static {
        GENERATORS.add(new DefaultMysqlDDLGenerator());
    }

    private DDLGeneratorFactory() {}

    public static final DDLGeneratorFactory getInstance() {
        return factory;
    }

    public synchronized void register(DDLGenerator ddlGenerator) {
        GENERATORS.add(ddlGenerator);
    }

    public DDLGenerator getDDLGenerator(DBType dbType) {
        for (DDLGenerator ddlGenerator : GENERATORS) {
            if (ddlGenerator.support(dbType))
                return ddlGenerator;
        }

        return null;
    }

}
