package org.apache.ibatis.singledog.jpa.mapper;

import org.apache.ibatis.singledog.jpa.domain.Page;
import org.apache.ibatis.singledog.jpa.domain.Pageable;
import org.apache.ibatis.singledog.jpa.domain.Sort;

import java.util.List;

public interface PagingAndSortingMapper<T, ID> extends CrudMapper<T, ID> {

	/**
	 * Returns all entities sorted by the given options.
	 * 
	 * @param sort
	 * @return all entities sorted by the given options
	 */
	List<T> findAll(Sort sort);

	/**
	 * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
	 * 
	 * @param pageable
	 * @return a page of entities
	 */
	Page<T> findAll(Pageable pageable);
}