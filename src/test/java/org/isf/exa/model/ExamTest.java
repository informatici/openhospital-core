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
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExamTest extends OHCoreTestCase {

	private static TestExam testExam;
	private static TestExamType testExamType;

	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testExam = new TestExam();
		testExamType = new TestExamType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestExam(false);
		checkExamIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestExam(true);
		checkExamIntoDb(code);
	}

	@Test
	void equalsHashCodeToString_shouldUseExamFields() throws Exception {
		String code = setupTestExam(false);
		Exam exam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(exam).isNotNull();
		ExamType examType = testExamType.setup(false);
		Exam exam2 = new Exam("XXX", "TestDescription", examType, 1, "TestDefaultResult");
		assertThat(exam)
				.isEqualTo(exam)
				.isNotEqualTo(exam2)
				.isNotEqualTo("xyzzy");
		exam2.setCode(exam.getCode());
		exam2.setDescription(exam.getDescription());
		exam2.setExamtype(exam.getExamtype());
		assertThat(exam).isEqualTo(exam2);

		assertThat(exam.hashCode()).isPositive();

		assertThat(exam2).hasToString(exam.getDescription());
	}

	@Test
	void setLockAndGetSearchString_shouldReturnLockAndLowerCaseCodeAndDescription() throws Exception {
		String code = setupTestExam(false);
		Exam exam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(exam).isNotNull();
		exam.setLock(-99);
		assertThat(exam.getLock()).isEqualTo(-99);

		assertThat(exam.getSearchString()).isEqualTo("zztestdescription");
	}

	private String setupTestExam(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		return exam.getCode();
	}

	private void checkExamIntoDb(String code) throws OHException {
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		testExam.check(foundExam);
	}

}
