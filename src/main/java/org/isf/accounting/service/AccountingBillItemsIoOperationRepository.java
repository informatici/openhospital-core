/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.util.List;

import org.isf.accounting.model.BillItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountingBillItemsIoOperationRepository extends JpaRepository<BillItems, Integer> {

	List<BillItems> findByBill_idOrderByIdAsc(int billId);

	List<BillItems> findAllByOrderByIdAsc();

	@Query("select b from BillItems b group by b.itemDescription")
	List<BillItems> findAllGroupByDescription();

	@Modifying
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Query(value = "delete from BillItems b where b.id = :billId")
	void deleteWhereId(@Param("billId") Integer billId);

}