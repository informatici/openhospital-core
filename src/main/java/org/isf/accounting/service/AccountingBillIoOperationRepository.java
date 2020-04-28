package org.isf.accounting.service;

import org.isf.accounting.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Repository
public interface AccountingBillIoOperationRepository
		extends JpaRepository<Bill, Integer>, AccountingBillIoOperationRepositoryCustom {

	List<Bill> findByStatusOrderByDateDesc(String status);

	List<Bill> findByStatusAndPatient_codeOrderByDateDesc(String status, int patientId);

	List<Bill> findAllByOrderByDateDesc();

	@Modifying
	@Query(value = "update Bill b set b.status='D' where b.id = :billId")
	void updateDeleteWhereId(@Param("billId") Integer billId);

	@Query(value = "select b from Bill b where DATE(b.date) BETWEEN :dateFrom and :dateTo")
	List<Bill> findAllWhereDates(@Param("dateFrom")Timestamp dateFrom, @Param("dateTo")Timestamp dateTo);
	
	@Query(value = "select b from Bill b where b.id = :patientCode and DATE(b.date) BETWEEN :dateFrom and :dateTo")
	ArrayList<Bill> findByDateAndPatient(@Param("dateFrom")GregorianCalendar dateFrom, @Param("dateTo") GregorianCalendar dateTo, @Param("patientCode")Integer patientCode);

	@Query(value = "select b from Bill b where b.status='O' and b.id = :patID")
	ArrayList<Bill> findAllPendindBillsByPatient(@Param("patID")int patID);

	/**
	 * return the bills for date between dateFrom and dateFrom to dateTo and containing items with description desc
	 * @param dateFrom
	 * @param dateTo
	 * @param desc
	 * @return the bill list
	 */
	@Query(value = "select bi.bill from BillItems bi where bi.itemDescription = :desc and DATE(bi.bill.date) BETWEEN :dateFrom and :dateTo")
	List<Bill> findAllWhereDatesAndBillItem(@Param("dateFrom") Timestamp dateFrom, @Param("dateTo") Timestamp dateTo, @Param("desc") String desc);
}