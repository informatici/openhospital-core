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
package org.isf.exa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.manager.ExamRowBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exa.model.ExamRow;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exa.service.ExamIoOperations;
import org.isf.exa.service.ExamRowIoOperationRepository;
import org.isf.exa.service.ExamRowIoOperations;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestExam testExam;
	private static TestExamRow testExamRow;
	private static TestExamType testExamType;

	@Autowired
	ExamIoOperations examIoOperation;
	@Autowired
	ExamRowIoOperations examRowIoOperation;
	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	ExamRowIoOperationRepository examRowIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	ExamBrowsingManager examBrowsingManager;
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
	void testExamGets() throws Exception {
		String code = setupTestExam(false);
		checkExamIntoDb(code);
	}

	@Test
	void testExamSets() throws Exception {
		String code = setupTestExam(true);
		checkExamIntoDb(code);
	}

	@Test
	void testExamRowGets() throws Exception {
		int code = setupTestExamRow(false);
		checkExamRowIntoDb(code);
	}

	@Test
	void testExamRowSets() throws Exception {
		int code = setupTestExamRow(true);
		checkExamRowIntoDb(code);
	}

	@Test
	void testIoGetExamRowZero() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testIoGetExamRowNoDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRow(foundExamRow.getCode(), null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testIoGetExamRowWithDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamRow(foundExamRow.getCode(), foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testIoGetExamRows() throws Exception {
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
	void testIoGetExamsRowByDesc() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowIoOperation.getExamsRowByDesc(foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testIoGetExams() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		List<Exam> exams = examIoOperation.getExams();
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());
	}

	@Test
	void testIoGetExamTypeExam() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void testIoGetExamTypeExamRow() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examRowIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void testIoNewExamRow() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, true);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		ExamRow newExamRow = examIoOperation.newExamRow(examRow);
		checkExamRowIntoDb(newExamRow.getCode());
	}

	@Test
	void testIoNewExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		Exam newExam = examIoOperation.newExam(exam);
		checkExamIntoDb(newExam.getCode());
	}

	@Test
	void testIoUpdateExam() throws Exception {
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
	void testIoUpdateExamRow() throws Exception {
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
	void testIoDeleteExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		examIoOperation.deleteExam(foundExam);
		assertThat(examIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testIoDeleteExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		examIoOperation.deleteExamRow(foundExamRow);
		assertThat(examIoOperation.isRowPresent(code)).isFalse();
	}

	@Test
	void testIoIsKeyPresent() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examIoOperation.isKeyPresent(exam);
		assertThat(result).isTrue();
	}

	@Test
	void testIoIsKeyPresentExamRow() throws Exception {
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
	void testIoIsCodePresent() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		assertThat(examRowIoOperation.isCodePresent(examRow.getCode())).isTrue();
	}

	@Test
	void testIoIsRowPresent() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(examRow).isNotNull();
		assertThat(examRowIoOperation.isRowPresent(examRow.getCode())).isTrue();
	}

	@Test
	void testIoGetExamRowByExamCode() throws Exception {
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
	void testMgrGetExamRowByExamCode() throws Exception {
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
	void testMgrGetExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testMgrGetExams() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		List<Exam> exams = examBrowsingManager.getExams();
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());

		exams = examBrowsingManager.getExamsByTypeDescription("xxxx");
		assertThat(exams).isEmpty();

		exams = examBrowsingManager.getExamsByTypeDescription("TestDescription");
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());

		exams = examBrowsingManager.getExamsByTypeDescription(null);
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());

		exams = examBrowsingManager.getExams("TestDescription");
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());
	}

	@Test
	void testMgrGetExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examBrowsingManager.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void testMgrNewExamRow() throws Exception {
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
	void testMgrNewExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		Exam newExam = examBrowsingManager.newExam(exam);
		checkExamIntoDb(newExam.getCode());
	}

	@Test
	void testMgrUpdateExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		foundExam.setDescription("Update");
		Exam result = examBrowsingManager.updateExam(foundExam);
		assertThat(result).isNotNull();
		Exam updatedExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedExam).isNotNull();
		assertThat(updatedExam.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrDeleteExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		examBrowsingManager.deleteExam(foundExam);
		assertThat(examIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrDeleteExamRow() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		examRowBrowsingManager.deleteExamRow(foundExamRow);
		assertThat(examIoOperation.isRowPresent(code)).isFalse();
	}

	@Test
	void testMgrIsKeyPresent() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examBrowsingManager.isKeyPresent(exam);
		assertThat(result).isTrue();
	}

	@Test
	void testMgrGetExamRowZero() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());

		examRows = examRowBrowsingManager.getExamRow();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testMgrGetExamRowNoDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode(), null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());

		examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testMgrGetExamRowWithDescription() throws Exception {
		int code = setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamRow).isNotNull();
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode(), foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	void testMgrExamValidationUpdate() throws Exception {
		ExamType examType = new ExamType("ZZ", "TestDescription");
		Exam exam = testExam.setup(examType, 1, false);
		String code = exam.getCode();
		// code = ""
		exam.setCode("");
		assertThatThrownBy(() -> examBrowsingManager.updateExam(exam))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
                                e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
				);
		// description = ""
		exam.setCode(code);
		exam.setDescription("");
		assertThatThrownBy(() -> examBrowsingManager.updateExam(exam))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
                                e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
				);
	}

	@Test
	void testMgrExamValidationInsert() throws Exception {
		String code = setupTestExam(false);
		// code already exists
		ExamType examType = new ExamType("ZZ", "TestDescription");
		Exam exam2 = testExam.setup(examType, 1, false);
		exam2.setCode(code);
		assertThatThrownBy(() -> examBrowsingManager.newExam(exam2))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
                                e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
				);
	}

	@Test
	void testMgrExamRowValidationUpdate() throws Exception {
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

	@Test
	void testExamEqualHashToString() throws Exception {
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
	void testExamRowEqualHashToString() throws Exception {
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

	@Test
	void testExamGetterSetter() throws Exception {
		String code = setupTestExam(false);
		Exam exam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(exam).isNotNull();
		exam.setLock(-99);
		assertThat(exam.getLock()).isEqualTo(-99);

		assertThat(exam.getSearchString()).isEqualTo("zztestdescription");
	}

	@Test
	void testIoExamSanitize() throws Exception {
		Method method = examIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(examIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(examIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(examIoOperation, "abcdef")).isEqualTo("abcdef");
	}

	@Test
	void testIoExamRowSanitize() throws Exception {
		Method method = examRowIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(examRowIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(examRowIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(examRowIoOperation, "abcdef")).isEqualTo("abcdef");
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