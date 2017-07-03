package org.apache.ibatis.singledog.jpa.mapper;

import java.util.List;

public interface CrudMapper<T, ID> extends Mapper<T, ID> {

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity will never be {@literal null}.
     */
    <S extends T> int save(S entity);

    <S extends T> int saveAutoIncrementKey(S entity);

    /**
     * Saves all given entities.
     *
     * @param entities must not be {@literal null}.
     * @return the saved entities will never be {@literal null}.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    <S extends T> int saveAll(List<S> entities);

    <S extends T> int saveAllAutoIncrementKey(List<S> entities);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal Optional#empty()} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}.
     */
    T findById(ID id);

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities
     */
    long count();

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    void deleteById(ID id);

    /**
     * Deletes the given entities.
     *
     * @param ids
     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
     */
    void deleteAll(List<? extends ID> ids);

    /**
     * Deletes all entities managed by the repository.
     */
    void clear();

    <S extends T> int updateByPrimaryKeySelective(S entity);

    <S extends T> int updateByPrimaryKey(S entity);

    /**
     * @param entity
     * @param <S>
     * @return
     */
    <S extends T> int saveSelective(S entity);
}