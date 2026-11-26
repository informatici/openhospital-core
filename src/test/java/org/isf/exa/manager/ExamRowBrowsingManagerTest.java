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
package org.isf.exa.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.exa.TestExam;
import org.isf.exa.TestExamRow;
import org.isf.exa.model.Exam;
import org.isf.exa.model.ExamRow;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exa.service.ExamIoOperations;
import org.isf.exa.service.ExamRowIoOperationRepository;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExamRowBrowsingManagerTest extends OHCoreTestCase {

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
	@Autowired
	ExamRowBrowsingManager examRowBrowsingManager;

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
	void getExamRowByExamCode_shouldReturnRowsOfExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examRowIoOperationRepository.saveAndFlush(examRow);
		List<ExamRow> examRows = examRowBrowsingManager.getExamRowByExamCode(String.valueOf(exam.getCode()));
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(examRow.getDescription());
	}

	@Test
	void getExamRow_shouldReturnSavedRow_whenExamCodeIsZero() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void newExamRow_shouldSaveExamRow() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, true);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		ExamRow result = examRowBrowsingManager.newExamRow(examRow);
		assertThat(result).isNotNull();
		checkExamRowIntoDb(examRow.getCode());
	}

	@Test
	void deleteExamRow_shouldRemoveExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		examRowBrowsingManager.deleteExamRow(foundExamRow);
		assertThat(examIoOperation.isRowPresent(code)).isFalse();
	}

	@Test
	void getExamRow_shouldReturnSavedRow_whenExamCodeIsZeroOrOmitted() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());

		examRows = examRowBrowsingManager.getExamRow();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void getExamRow_shouldReturnSavedRow_whenCodeIsGivenAndDescriptionIsNullOrOmitted() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode(), null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());

		examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void getExamRow_shouldReturnSavedRow_whenCodeAndDescriptionAreGiven() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode(), foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void newExamRow_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		ExamType examType = new ExamType("ZZ", "TestDescription");
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, false);
		// description = ""
		examRow.setDescription("");
		assertThatThrownBy(() -> examRowBrowsingManager.newExamRow(examRow))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
                                e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
				);
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
