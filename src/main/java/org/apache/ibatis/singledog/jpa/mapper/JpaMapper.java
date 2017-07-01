package org.apache.ibatis.singledog.jpa.mapper;

import org.apache.ibatis.singledog.jpa.domain.Sort;

import java.util.List;

/**
 * Created by Adam on 2017/7/1.
 */
public interface JpaMapper<T, ID> extends PagingAndSortingMapper<T, ID> {

    /*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudMapper#findAll()
	 */
    List<T> findAll();

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingMapper#findAll(org.springframework.data.domain.Sort)
     */
    List<T> findAll(Sort sort);

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudMapper#findAll(java.lang.Iterable)
     */
    List<T> findAllById(Iterable<ID> ids);

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudMapper#save(java.lang.Iterable)
     */
    <S extends T> List<S> saveAll(Iterable<S> entities);

    /**
     * Flushes all pending changes to the database.
     */
    void flush();

    /**
     * Saves an entity and flushes changes instantly.
     *
     * @param entity
     * @return the saved entity
     */
    <S extends T> S saveAndFlush(S entity);

    /**
     * Deletes the given entities in a batch which means it will create a single {@link Query}. Assume that we will clear
     * the {@link javax.persistence.EntityManager} after the call.
     *
     * @param entities
     */
    void deleteInBatch(Iterable<T> entities);

    /**
     * Deletes all entities in a batch call.
     */
    void deleteAllInBatch();

    /**
     * Returns a reference to the entity with the given identifier.
     *
     * @param id must not be {@literal null}.
     * @return a reference to the entity with the given identifier.
     * @see EntityManager#getReference(Class, Object)
     */
    T getOne(ID id);
}
