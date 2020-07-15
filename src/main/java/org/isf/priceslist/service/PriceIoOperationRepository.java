package org.isf.priceslist.service;

import java.util.List;

import org.isf.priceslist.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PriceIoOperationRepository extends JpaRepository<Price, Integer> {
    List<Price> findAllByOrderByDescriptionAsc();
	
    List<Price> findByList_id(Integer id);

    @Modifying
    @Transactional
    @Query("delete from Price p where p.list.id = :listId")
	void deleteByListId(@Param("listId") Integer listId);
}