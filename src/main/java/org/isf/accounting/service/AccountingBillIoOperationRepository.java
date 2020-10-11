/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.accounting.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository
public interface AccountingBillIoOperationRepository extends JpaRepository<Bill, Integer> {

	List<Bill> findByStatusOrderByDateDesc(String status);

	List<Bill> findByStatusAndBillPatient_codeOrderByDateDesc(String status, int patientId);

	List<Bill> findAllByOrderByDateDesc();

	List<Bill> findByBillPatient_code(int patientCode);

	@Modifying
	@Query(value = "update Bill b set b.status='D' where b.id = :billId")
	void updateDeleteWhereId(@Param("billId") Integer billId);

	@Query(value = "select b from Bill b where b.date >= :dateFrom and b.date < :dateTo")
	List<Bill> findByDateBetween(@Param("dateFrom") Calendar dateFrom, @Param("dateTo") Calendar dateTo);
	
	@Query(value = "select b from Bill b where b.billPatient.id = :patientCode and b.date >= :dateFrom and b.date < :dateTo")
	ArrayList<Bill> findByDateAndPatient(@Param("dateFrom") Calendar dateFrom, @Param("dateTo") Calendar dateTo, @Param("patientCode")Integer patientCode);

	@Query(value = "select b from Bill b where b.status='O' and b.billPatient.id = :patID")
	ArrayList<Bill> findAllPendindBillsByBillPatient(@Param("patID")int patID);

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