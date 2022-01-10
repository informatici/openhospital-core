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
package org.isf.exa.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.exa.model.Exam;
import org.isf.exa.model.ExamRow;
import org.isf.utils.exception.OHException;

public class TestExamRow {

	private String description = "TestDescription";

	public ExamRow setup(Exam exam, boolean usingSet) throws OHException {
		ExamRow examRow;

		if (usingSet) {
			examRow = new ExamRow();
			setParameters(examRow, exam);
		} else {
			// Create ExamRow with all parameters 
			examRow = new ExamRow(exam, description);
		}

		return examRow;
	}

	public void setParameters(ExamRow examRow, Exam exam) {
		examRow.setDescription(description);
		examRow.setExamCode(exam);
	}

	public void check(ExamRow examRow) {
		assertThat(examRow.getDescription()).isEqualTo(description);
	}
}
