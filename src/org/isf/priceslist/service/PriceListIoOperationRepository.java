package org.isf.priceslist.service;

import org.isf.priceslist.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PriceListIoOperationRepository extends JpaRepository<PriceList, Integer> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM PriceList p WHERE p.id = :id")
    void deleteById(@Param("id") Integer id);
}