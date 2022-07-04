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
package org.isf.exa.service;

import java.util.List;

import org.isf.exa.model.ExamRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRowIoOperationRepository extends JpaRepository<ExamRow, Integer> {

	List<ExamRow> findAllByCodeOrderByDescription(int code);

	List<ExamRow> findAllByDescriptionOrderByDescriptionAsc(String description);

	List<ExamRow> findAllByCodeAndDescriptionOrderByCodeAscDescriptionAsc(int code, String description);

	@Modifying
	void deleteByExam_Code(String code);

	List<ExamRow> findAllByExam_CodeOrderByDescription(String examCode);
}