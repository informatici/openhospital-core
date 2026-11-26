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

class ExamRowIoOperationsTest extends OHCoreTestCase {

	private static TestExam testExam;
	private static TestExamRow testExamRow;
	private static TestExamType testExamType;

	@Autowired
	ExamRowIoOperations examRowIoOperation;
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
	void getExamRow_shouldReturnSavedRow_whenExamCodeIsZero() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void getExamRow_shouldReturnSavedRow_whenCodeIsGivenAndDescriptionIsNull() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRow(foundExamRow.getCode(), null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void getExamRow_shouldReturnSavedRow_whenCodeAndDescriptionAreGiven() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRow(foundExamRow.getCode(), foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void getExamRows_shouldReturnSavedRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRows();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
		// deprecated method
		examRows = examRowIoOperation.getExamRows();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void getExamsRowByDesc_shouldReturnRowsWithDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamsRowByDesc(foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void getExamType_shouldReturnSavedExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examRowIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void updateExamRow_shouldSaveNewDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		examRow.setDescription("Update");
		ExamRow result = examRowIoOperation.updateExamRow(examRow);
		assertThat(result).isNotNull();
		ExamRow updateExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(updateExamRow).isNotNull();
		assertThat(updateExamRow.getDescription()).isEqualTo("Update");
	}

	@Test
	void isKeyPresent_shouldReturnWhetherRowCodeExists() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		boolean result = examRowIoOperation.isKeyPresent(examRow);
		assertThat(result).isTrue();
		// fail
		examRow.setCode(-1);
		result = examRowIoOperation.isKeyPresent(examRow);
		assertThat(result).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		assertThat(examRowIoOperation.isCodePresent(examRow.getCode())).isTrue();
	}

	@Test
	void isRowPresent_shouldReturnTrue_whenRowExists() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		assertThat(examRowIoOperation.isRowPresent(examRow.getCode())).isTrue();
	}

	@Test
	void getExamRowByExamCode_shouldReturnRowsOfExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examRowIoOperationRepository.saveAndFlush(examRow);
		List<ExamRow> examRows = examRowIoOperation.getExamRowByExamCode(String.valueOf(exam.getCode()));
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(examRow.getDescription());
	}

	@Test
	void sanitize_shouldDoubleSingleQuotes() throws Exception {
		Method method = examRowIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(examRowIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(examRowIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(examRowIoOperation, "abcdef")).isEqualTo("abcdef");
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

	private String setupTestExamType(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		return examType.getCode();
	}

}
