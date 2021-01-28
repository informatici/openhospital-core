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
package org.isf.medicalstock.service;

import java.util.List;

import org.isf.medicalstock.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LotIoOperationRepository extends JpaRepository<Lot, String> {

	@Query("select l from Lot l left join l.movements m where m.medical.code = :medical group by l.code order by l.dueDate")
	List<Lot> findByMovements_MedicalOrderByDueDate(@Param("medical") int medicalCode);

	@Query(value = "select LT_ID_A,LT_PREP_DATE,LT_DUE_DATE,LT_COST,"
			+ "SUM(IF(MMVT_TYPE LIKE '%+%',MMV_QTY,-MMV_QTY)) as quantity from "
			+ "((MEDICALDSRLOT join MEDICALDSRSTOCKMOV on MMV_LT_ID_A=LT_ID_A) join MEDICALDSR on MMV_MDSR_ID=MDSR_ID)"
			+ " join MEDICALDSRSTOCKMOVTYPE on MMV_MMVT_ID_A=MMVT_ID_A "
			+ "where LT_ID_A=:code group by LT_ID_A order by LT_DUE_DATE", nativeQuery = true)
	List<Object[]> findAllWhereLot(@Param("code") String code);
}
