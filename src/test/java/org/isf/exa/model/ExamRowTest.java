/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.exa.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.exa.TestExam;
import org.isf.exa.TestExamRow;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exa.service.ExamRowIoOperationRepository;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExamRowTest extends OHCoreTestCase {

	private static TestExam testExam;
	private static TestExamRow testExamRow;
	private static TestExamType testExamType;

	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	ExamRowIoOperationRepository examRowIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testExam = new TestExam();
		testExamType = new TestExamType();
		testExamRow = new TestExamRow();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		int code = setupTestExamRow(false);
		checkExamRowIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int code = setupTestExamRow(true);
		checkExamRowIntoDb(code);
	}

	@Test
	void equalsHashCodeToString_shouldUseExamRowFields() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		ExamType examType = testExamType.setup(false);
		Exam exam2 = new Exam("XXX", "TestDescription", examType, 1, "TestDefaultResult");
		ExamRow examRow2 = new ExamRow(exam2, "NewDescription");
		assertThat(examRow)
				.isEqualTo(examRow)
				.isNotEqualTo(examRow2)
				.isNotEqualTo("xyzzy");
		examRow2.setCode(examRow.getCode());
		examRow2.setExamCode(examRow.getExamCode());
		examRow2.setDescription(examRow.getDescription());
		assertThat(examRow).isEqualTo(examRow2);

		assertThat(examRow.hashCode()).isPositive();

		assertThat(examRow).hasToString(examRow.getDescription());
	}

	private int setupTestExamRow(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(usingSet);
		Exam exam = testExam.setup(examType, 2, usingSet);
		ExamRow examRow = testExamRow.setup(exam, usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examRowIoOperationRepository.saveAndFlush(examRow);
		return examRow.getCode();
	}

	private void checkExamRowIntoDb(int code) throws OHException {
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		testExamRow.check(foundExamRow);
	}

}
