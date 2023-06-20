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

import java.time.LocalDateTime;
import java.util.List;

import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementIoOperationRepository extends JpaRepository<Movement, Integer>, MovementIoOperationRepositoryCustom {

	@Query(value = "select m from Movement m join m.medical med where med.code = :code")
	Movement findAllByMedicalCode(@Param("code") Integer code);

	@Query(value = "select m from Movement m join m.medical med where med.code = :code")
	Movement findAllByMedicalCodeOrderByLot_(@Param("code") Integer code);

	@Query(value = "select distinct med.code from Movement mov " +
			"join mov.medical med " +
			"join mov.type movtype " +
			"join mov.lot lot " +
			"where lot.code=:lot")
	List<Integer> findAllByLot(@Param("lot") String lot);

	@Query(value = "select mov from Movement mov " +
			"join mov.type movtype " +
			"left join mov.lot lot " +
			"left join mov.ward ward " +
			"where mov.refNo = :refNo order by mov.date, mov.refNo")
	List<Movement> findAllByRefNo(@Param("refNo") String refNo);

	List<Movement> findByLot(Lot lot);

	@Query(value = "select max(mov.date) from Movement mov")
	LocalDateTime findMaxDate();

	@Query(value = "select mov.refNo from Movement mov where mov.refNo like :refNo")
	List<String> findAllWhereRefNo(@Param("refNo") String refNo);
}
