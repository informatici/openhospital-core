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
package org.isf.opd.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.opd.model.Opd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpdIoOperationRepository extends JpaRepository<Opd, Integer>, OpdIoOperationRepositoryCustom {

	@Query("select o from Opd o order by o.prog_year")
	List<Opd> findAllOrderByProgYearDesc();

	@Query("select o from Opd o where o.patient.code = :code order by o.prog_year")
	List<Opd> findAllByPatient_CodeOrderByProgYearDesc(@Param("code") Integer code);

	@Query("select max(o.prog_year) from Opd o")
	Integer findMaxProgYear();

	@Query(value = "select max(o.prog_year) from Opd o where o.visitDate >= :dateFrom and o.visitDate < :dateTo")
	Integer findMaxProgYearWhereDateBetween(@Param("dateFrom") GregorianCalendar dateFrom, @Param("dateTo") GregorianCalendar dateTo);

	List<Opd> findTop1ByPatient_CodeOrderByDateDesc(Integer code);

	@Query("select o from Opd o where o.prog_year = :prog_year")
	List<Opd> findByProgYear(@Param("prog_year") Integer prog_year);

	@Query(value = "select op from Opd op where op.prog_year = :prog_year and op.visitDate >= :dateVisitFrom and op.visitDate < :dateVisitTo")
	List<Opd> findByProgYearAndVisitDateBetween(@Param("prog_year") Integer prog_year, @Param("dateVisitFrom") GregorianCalendar dateVisitFrom,
					@Param("dateVisitTo") GregorianCalendar dateVisitTo);

	@Query(value = "select o.createdDate from Opd o where o.active=1 order by o.code desc limit 1", nativeQuery = true)
	Date lastOpdCreationDate();
}
