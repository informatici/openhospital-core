package org.isf.accounting.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountingBillPaymentIoOperationRepository extends JpaRepository<BillPayments, Integer> {

	@Query(value = "select distinct bp.user FROM BillPayments bp ORDER BY bp.user asc")
	List<String> findUserDistinctByOrderByUserAsc();

	@Query(value = "SELECT BP FROM BillPayments BP where BP.date >= :start and BP.date < :end ORDER BY BP.id")
	List<BillPayments> findByDateBetweenOrderByIdAscDateAsc(@Param("start") GregorianCalendar start, @Param("end") GregorianCalendar end);

	List<BillPayments> findAllByBillIn(Collection<Bill> bills);

	@Query(value = "SELECT BP FROM BillPayments BP ORDER BY BP.bill, BP.date ASC")
	List<BillPayments> findAllByOrderByBillAndDate();

	@Query(value = "SELECT BP FROM BillPayments BP WHERE BP.bill.id = :billId ORDER BY BP.bill, BP.date ASC")
	List<BillPayments> findAllWherBillIdByOrderByBillAndDate(@Param("billId") Integer billId);
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM BillPayments BP where BP.bill.id = :billId")
	void deleteWhereId(@Param("billId") Integer billId);

	
	@Query(value = "SELECT BP FROM BillPayments BP WHERE " +
			"BP.bill.patient.code = :patientCode and " +
			"DATE(BP.bill.date) between DATE(:dateFrom) and DATE(:dateTo) " +
			"ORDER BY BP.bill, BP.date ASC")
	ArrayList<BillPayments> findByDateAndPatient(@Param("dateFrom") GregorianCalendar dateFrom , @Param("dateTo") GregorianCalendar dateTo, @Param("patientCode") Integer patientCode);
}