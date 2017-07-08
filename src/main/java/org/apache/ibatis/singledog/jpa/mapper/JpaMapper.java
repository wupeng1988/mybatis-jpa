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
package org.apache.ibatis.singledog.jpa.mapper;

import org.apache.ibatis.singledog.jpa.annotation.CustomProvider;
import org.apache.ibatis.singledog.jpa.generator.impl.FindAllByIdGeneratorImpl;

import java.util.List;

/**
 * Created by Adam on 2017/7/1.
 */
public interface JpaMapper<T, ID> extends PagingAndSortingMapper<T, ID> {

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudMapper#findAll(java.lang.Iterable)
     */
    @CustomProvider(FindAllByIdGeneratorImpl.class)
    List<T> findAllById(List<ID> ids);

}
