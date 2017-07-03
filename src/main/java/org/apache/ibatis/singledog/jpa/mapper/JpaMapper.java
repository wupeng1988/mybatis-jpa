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

    /**
     * Returns a reference to the entity with the given identifier.
     *
     * @param id must not be {@literal null}.
     * @return a reference to the entity with the given identifier.
     * @see EntityManager#getReference(Class, Object)
     */
    T getOne(ID id);
}
