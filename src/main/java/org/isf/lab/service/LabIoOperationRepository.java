/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.lab.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.lab.model.Laboratory;
import org.isf.patient.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabIoOperationRepository extends JpaRepository<Laboratory, Integer> {

	List<Laboratory> findByLabDateBetweenOrderByLabDateDesc(LocalDateTime dateFrom, LocalDateTime dateTo);

	List<Laboratory> findByLabDateBetweenAndExamDescriptionOrderByLabDateDesc(LocalDateTime dateFrom, LocalDateTime dateTo, String exam);

	List<Laboratory> findByPatient_CodeOrderByLabDate(Integer patient);

	List<Laboratory> findByLabDateBetweenOrderByExam_Examtype_DescriptionDesc(LocalDateTime dateFrom, LocalDateTime dateTo);

	List<Laboratory> findByLabDateBetweenAndExam_DescriptionContainingOrderByExam_Examtype_DescriptionDesc(LocalDateTime dateFrom, LocalDateTime dateTo,
					String exam);

	List<Laboratory> findByLabDateBetweenAndPatientCode(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientCode);

	List<Laboratory> findByLabDateBetweenAndExamDescriptionAndPatientCode(LocalDateTime dateFrom, LocalDateTime dateTo, String exam, Integer patient);

	@Query(value = "select lab from Laboratory lab where lab.labDate >= :dateFrom and lab.labDate < :dateTo order by lab.labDate desc")
	Page<Laboratory> findByLabDateBetweenOrderByLabDateDescPage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
					Pageable pageable);

	@Query(value = "select lab from Laboratory lab where (lab.labDate >= :dateFrom and lab.labDate < :dateTo) and lab.exam.description = :exam order by lab.labDate desc")
	Page<Laboratory> findByLabDateBetweenAndExam_DescriptionOrderByLabDateDescPage(@Param("dateFrom") LocalDateTime dateFrom,
					@Param("dateTo") LocalDateTime dateTo, @Param("exam") String exam, Pageable pageable);

	@Query(value = "select lab from Laboratory lab where (lab.labDate >= :dateFrom and lab.labDate < :dateTo) and lab.patient = :patient order by lab.labDate desc")
	Page<Laboratory> findByLabDateBetweenAndPatientCodePage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
					@Param("patient") Patient patient, Pageable pageable);

	@Query(value = "select lab from Laboratory lab where (lab.labDate >= :dateFrom and lab.labDate < :dateTo) and lab.exam.description = :exam and lab.patient = :patient order by lab.labDate desc")
	Page<Laboratory> findByLabDateBetweenAndExamDescriptionAndPatientCodePage(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo,
					@Param("exam") String exam, @Param("patient") Patient patient, Pageable pageable);

	@Query("select count(l) from Laboratory l where active=1")
	long countAllActiveLabs();

}
