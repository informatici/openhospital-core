package org.isf.accounting.service;

import org.isf.accounting.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Repository
public interface AccountingBillIoOperationRepository extends JpaRepository<Bill, Integer> {

	List<Bill> findByStatusOrderByDateDesc(String status);

	List<Bill> findByStatusAndPatient_codeOrderByDateDesc(String status, int patientId);

	List<Bill> findAllByOrderByDateDesc();

	List<Bill> findByPatient_code(int patientCode);

	@Modifying
	@Query(value = "update Bill b set b.status='D' where b.id = :billId")
	void updateDeleteWhereId(@Param("billId") Integer billId);

	List<Bill> findByDateBetween(Calendar dateFrom, Calendar dateTo);
	
	@Query(value = "select b from Bill b where b.id = :patientCode and b.date >= :dateFrom and b.date < :dateTo")
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
	@Query(value = "select bi.bill from BillItems bi where bi.itemDescription = :desc and bi.bill.date >= :dateFrom and bi.bill.date < :dateTo")
	List<Bill> findAllWhereDatesAndBillItem(@Param("dateFrom") Calendar dateFrom, @Param("dateTo") Calendar dateTo, @Param("desc") String desc);
}