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
package org.apache.ibatis.features.jpa.generator.impl;

import org.apache.ibatis.features.jpa.domain.Example;
import org.apache.ibatis.features.jpa.meta.Table;
import org.apache.ibatis.features.jpa.generator.MetaDataParser;

import java.util.Map;

/**
 * Created by Adam on 2017/7/13.
 */
public class SelectByExampleImpl extends AbstractSqlGenerator {
    @Override
    public String generatorSql(MetaDataParser dataParser, Map<String, Object> params) {
        StringBuilder select = new StringBuilder();
        select.append("select ")
                .append(include(Table.ALL_COLUMNS))
                .append(" from ").append(dataParser.getTable().getName())
                .append(exampleCondition());

        return select(getMethod(params), Example.class.getName(), null, null, select.toString());
    }

    /**
     *
     *
     <if test="_parameter != null">
         <where>
             <foreach collection="oredCriteria" item="criteria" separator="or">
                 <if test="criteria.valid">
                     <trim prefix="(" prefixOverrides="and" suffix=")">
                         <foreach collection="criteria.criteria" item="criterion">
                             <choose>
                                 <when test="criterion.noValue">and ${criterion.condition}</when>
                                 <when test="criterion.singleValue">and ${criterion.condition} #{criterion.value}</when>
                                 <when test="criterion.betweenValue">and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}</when>
                                 <when test="criterion.listValue">and ${criterion.condition}
                                     <foreach close=")" collection="criterion.value" item="listItem" open="(" separator=",">#{listItem}</foreach>
                                 </when>
                             </choose>
                         </foreach>
                     </trim>
                 </if>
             </foreach>
         </where>
     </if>
     *
     *
     * @return
     */
    String exampleCondition() {
        return " <if test=\"_parameter != null\"> \n" +
                "  <where> \n" +
                "    <foreach collection=\"oredCriteria\" item=\"criteria\" separator=\"or\"> \n" +
                "      <if test=\"criteria.valid\"> \n" +
                "        <trim prefix=\"(\" prefixOverrides=\"and\" suffix=\")\"> \n" +
                "          <foreach collection=\"criteria.criteria\" item=\"criterion\"> \n" +
                "            <choose> \n" +
                "              <when test=\"criterion.noValue\">and ${criterion.condition}</when>  \n" +
                "              <when test=\"criterion.singleValue\">and ${criterion.condition} #{criterion.value}</when>  \n" +
                "              <when test=\"criterion.betweenValue\">and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}</when>  \n" +
                "              <when test=\"criterion.listValue\">and ${criterion.condition} \n" +
                "                <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">#{listItem}</foreach> \n" +
                "              </when> \n" +
                "            </choose> \n" +
                "          </foreach> \n" +
                "        </trim> \n" +
                "      </if> \n" +
                "    </foreach> \n" +
                "  </where> \n" +
                "</if>";
    }

}
