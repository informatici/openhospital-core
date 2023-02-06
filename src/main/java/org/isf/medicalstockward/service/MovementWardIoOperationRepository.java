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
package org.isf.medicalstockward.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.medicalstockward.model.MovementWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementWardIoOperationRepository extends JpaRepository<MovementWard, Integer> {

	@Query(value = "select movWard from MovementWard movWard where movWard.wardTo.code=:idWardTo and (movWard.date>= :dateFrom and movWard.date < :dateTo)")
	List<MovementWard> findWardMovements(@Param("idWardTo") String idWardTo,
			@Param("dateFrom") LocalDateTime dateFrom,
			@Param("dateTo") LocalDateTime dateTo);

	List<MovementWard> findByPatient_code(int code);

	@Query(value = "SELECT * FROM OH_MEDICALDSRSTOCKMOVWARD WHERE MMVN_PAT_ID = :patId", nativeQuery = true)
	List<MovementWard> findWardMovementPat(@Param("patId") Integer patId);

}
