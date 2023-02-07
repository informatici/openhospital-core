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
package org.isf.lab.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.lab.model.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabIoOperationRepository extends JpaRepository<Laboratory, Integer> {
	List<Laboratory> findByCreatedDateBetweenOrderByLabDateDesc(LocalDateTime dateFrom, LocalDateTime dateTo);

	List<Laboratory> findByLabDateBetweenAndExam_DescriptionOrderByLabDateDesc(LocalDateTime dateFrom, LocalDateTime dateTo, String exam);

	List<Laboratory> findByPatient_CodeOrderByLabDate(Integer patient);

	List<Laboratory> findByLabDateBetweenOrderByExam_Examtype_DescriptionDesc(LocalDateTime dateFrom, LocalDateTime dateTo);

	List<Laboratory> findByLabDateBetweenAndExam_DescriptionContainingOrderByExam_Examtype_DescriptionDesc(LocalDateTime dateFrom, LocalDateTime dateTo, String exam);

	List<Laboratory> findByLabDateBetweenAndPatientCode(LocalDateTime dateFrom, LocalDateTime dateTo, Integer patientCode);
	
	List<Laboratory> findByLabDateBetweenAndExamDescriptionAndPatientCode(LocalDateTime dateFrom, LocalDateTime dateTo, String exam, Integer patient);

}
