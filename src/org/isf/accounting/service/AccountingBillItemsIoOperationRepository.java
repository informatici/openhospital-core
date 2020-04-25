package org.isf.accounting.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.accounting.model.BillItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountingBillItemsIoOperationRepository extends JpaRepository<BillItems, Integer> {
	
	List<BillItems> findByBill_idOrderByIdAsc(int billId);
		
	List<BillItems> findAllByOrderByIdAsc();
	
	@Modifying
	@Transactional
	@Query(value = "delete BillItems where id = :billId")
	void deleteWhereId(@Param("billId") Integer billId);

	/*
	This 'GROUP BY' does not work in more recent versions of mySQL.  FIXME KTM 4/25/2020
	 */
	@Query(value = "SELECT * FROM BILLITEMS GROUP BY BLI_ITEM_DESC", nativeQuery = true)
	ArrayList<BillItems> findAllGroupByDesc();
}