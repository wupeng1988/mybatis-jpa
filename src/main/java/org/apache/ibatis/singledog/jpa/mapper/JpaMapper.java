package org.apache.ibatis.singledog.jpa.mapper;

import java.util.List;

/**
 * Created by Adam on 2017/7/1.
 */
public interface JpaMapper<T, ID> extends PagingAndSortingMapper<T, ID> {

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudMapper#findAll(java.lang.Iterable)
     */
    List<T> findAllById(List<ID> ids);

}
