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
package org.isf.medicalstock.service;

import java.util.List;

import org.isf.medicalstock.model.Lot;
import org.isf.ward.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LotIoOperationRepository extends JpaRepository<Lot, String> {

	@Query("select l from Lot l where l.medical.code = :medical order by l.dueDate")
	List<Lot> findByMedicalOrderByDueDate(@Param("medical") int medicalCode);

	@Query("select coalesce(sum(case when m.type.type like '+%' then m.quantity else -m.quantity end), 0) from Movement m where m.lot = :lot")
	Integer getMainStoreQuantity(@Param("lot") Lot lot);

	@Query("select coalesce(sum(w.in_quantity - w.out_quantity),0) FROM MedicalWard w WHERE w.id.lot = :lot")
	Double getWardsTotalQuantity(@Param("lot") Lot lot);

	@Query("select sum(w.in_quantity - w.out_quantity) FROM MedicalWard w WHERE w.id.lot = :lot and w.id.ward = :ward")
	Double getQuantityByWard(@Param("lot") Lot lot, @Param("ward") Ward ward);

	@Query(value = "select LT_ID_A,LT_PREP_DATE,LT_DUE_DATE,LT_COST,"
			+ "SUM(IF(MMVT_TYPE LIKE '%+%',MMV_QTY,-MMV_QTY)) as quantity from "
			+ "((OH_MEDICALDSRLOT join OH_MEDICALDSRSTOCKMOV on MMV_LT_ID_A=LT_ID_A) join OH_MEDICALDSR on MMV_MDSR_ID=MDSR_ID)"
			+ " join OH_MEDICALDSRSTOCKMOVTYPE on MMV_MMVT_ID_A=MMVT_ID_A "
			+ "where LT_ID_A=:code group by LT_ID_A order by LT_DUE_DATE", nativeQuery = true)
	List<Object[]> findAllWhereLot(@Param("code") String code);
}
