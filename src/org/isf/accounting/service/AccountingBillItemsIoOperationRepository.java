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
	@Query(value = "delete from BillItems b where b.id = :billId")
	void deleteWhereId(@Param("billId") Integer billId);

	@Query(value = "select b from BillItems b group by b.itemDescription")
	ArrayList<BillItems> findAllGroupByDesc();
}