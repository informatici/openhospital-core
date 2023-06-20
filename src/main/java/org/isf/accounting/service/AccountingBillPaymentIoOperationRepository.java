/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.accounting.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingBillPaymentIoOperationRepository extends JpaRepository<BillPayments, Integer> {

	@Query(value = "select distinct bp.user FROM BillPayments bp ORDER BY bp.user asc")
	List<String> findUserDistinctByOrderByUserAsc();

	@Query(value = "SELECT BP FROM BillPayments BP where BP.date >= :start and BP.date < :end ORDER BY BP.id")
	List<BillPayments> findByDateBetweenOrderByIdAscDateAsc(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	List<BillPayments> findAllByBillIn(Collection<Bill> bills);

	@Query(value = "SELECT BP FROM BillPayments BP ORDER BY BP.bill, BP.date ASC")
	List<BillPayments> findAllByOrderByBillAndDate();

	@Query(value = "SELECT BP FROM BillPayments BP WHERE BP.bill.id = :billId ORDER BY BP.bill, BP.date ASC")
	List<BillPayments> findAllWherBillIdByOrderByBillAndDate(@Param("billId") Integer billId);

	@Modifying
	@Query(value = "DELETE FROM BillPayments BP where BP.bill.id = :billId")
	void deleteWhereId(@Param("billId") Integer billId);

	@Query(value = "SELECT BP FROM BillPayments BP WHERE " +
			"BP.bill.billPatient.code = :patientCode and " +
			"DATE(BP.date) between DATE(:dateFrom) and DATE(:dateTo) " +
			"ORDER BY BP.bill, BP.date ASC")
	List<BillPayments> findByDateAndPatient(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
			@Param("patientCode") Integer patientCode);
}