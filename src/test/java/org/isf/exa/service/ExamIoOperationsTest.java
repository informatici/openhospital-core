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
package org.isf.exa.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.exa.TestExam;
import org.isf.exa.TestExamRow;
import org.isf.exa.model.Exam;
import org.isf.exa.model.ExamRow;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExamIoOperationsTest extends OHCoreTestCase {

	private static TestExam testExam;
	private static TestExamRow testExamRow;
	private static TestExamType testExamType;

	@Autowired
	ExamIoOperations examIoOperation;
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
	void getExams_shouldReturnSavedExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		List<Exam> exams = examIoOperation.getExams();
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());
	}

	@Test
	void getExamType_shouldReturnSavedExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void newExamRow_shouldSaveExamRow() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, true);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		ExamRow newExamRow = examIoOperation.newExamRow(examRow);
		checkExamRowIntoDb(newExamRow.getCode());
	}

	@Test
	void newExam_shouldSaveExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		Exam newExam = examIoOperation.newExam(exam);
		checkExamIntoDb(newExam.getCode());
	}

	@Test
	void updateExam_shouldSaveNewDescription() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		foundExam.setDescription("Update");
		Exam result = examIoOperation.updateExam(foundExam);
		assertThat(result).isNotNull();
		Exam updateExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(updateExam).isNotNull();
		assertThat(updateExam.getDescription()).isEqualTo("Update");
	}

	@Test
	void deleteExam_shouldRemoveExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		examIoOperation.deleteExam(foundExam);
		assertThat(examIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void deleteExamRow_shouldRemoveExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		examIoOperation.deleteExamRow(foundExamRow);
		assertThat(examIoOperation.isRowPresent(code)).isFalse();
	}

	@Test
	void isKeyPresent_shouldReturnTrue_whenExamExists() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examIoOperation.isKeyPresent(exam);
		assertThat(result).isTrue();
	}

	@Test
	void sanitize_shouldDoubleSingleQuotes() throws Exception {
		Method method = examIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(examIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(examIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(examIoOperation, "abcdef")).isEqualTo("abcdef");
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

	private String setupTestExamType(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		return examType.getCode();
	}

}
