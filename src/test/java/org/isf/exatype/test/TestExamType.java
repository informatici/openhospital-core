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
package org.isf.exatype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.exatype.model.ExamType;
import org.isf.utils.exception.OHException;

public class TestExamType {

	private String code = "Z";
	private String description = "TestDescription";

	public ExamType setup(boolean usingSet) throws OHException {
		ExamType examType;

		if (usingSet) {
			examType = new ExamType();
			setParameters(examType);
		} else {
			// Create ExamType with all parameters 
			examType = new ExamType(code, description);
		}

		return examType;
	}

	public void setParameters(ExamType examType) {
		examType.setCode(code);
		examType.setDescription(description);
	}

	public void check(ExamType examType) {
		assertThat(examType.getCode()).isEqualTo(code);
		assertThat(examType.getDescription()).isEqualTo(description);
	}
}
