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
	Cannot move this @Query to non-native.
	More appropriate approach is to add new BillItems to Bill instance, then save the
	Bill instance.  FIXME KTM 3/8/2020
	 */
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO BILLITEMS (" +
			"BLI_ID_BILL, BLI_IS_PRICE, BLI_ID_PRICE, BLI_ITEM_DESC, BLI_ITEM_AMOUNT, BLI_QTY) "+
			"VALUES (:id,:isPrice,:price,:description,:amount,:qty)", nativeQuery= true)
	void insertBillItem(
			@Param("id") Integer id, @Param("isPrice") Boolean isPrice, @Param("price") String price,
			@Param("description") String description, @Param("amount") Double amount, @Param("qty") Integer qty);
	
	@Query(value = "SELECT bi from BillItems bi GROUP BY bi.itemDescription")
	ArrayList<BillItems> findAllGroupByDesc();
}