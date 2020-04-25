package org.isf.accounting.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.accounting.model.BillPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountingBillPaymentIoOperationRepository extends JpaRepository<BillPayments, Integer>, AccountingBillPaymentIoOperationRepositoryCustom {

//	@Query(value = "SELECT DISTINCT BLP_USR_ID_A FROM BILLPAYMENTS ORDER BY BLP_USR_ID_A ASC", nativeQuery= true)
	@Query(value = "SELECT distinct BP.user from BillPayments bp ORDER BY bp.user ASC")
	List<String> findUserDistinctByOrderByUserAsc();

	@Query("SELECT BP FROM BillPayments BP WHERE DATE(BLP_DATE) BETWEEN ?1 AND ?2"
				+ " ORDER BY BLP_ID_BILL, BLP_DATE ASC")
	List<BillPayments> findByDateBetweenOrderByIdAscDateAsc(Date start, Date end);

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
			"DATE(BP.bill.date) between :dateFrom and :dateTo " +
			"ORDER BY BP.bill, BP.date ASC")
	ArrayList<BillPayments> findByDateAndPatient(@Param("dateFrom") GregorianCalendar dateFrom , @Param("dateTo") GregorianCalendar dateTo, @Param("patientCode") Integer patientCode);
}