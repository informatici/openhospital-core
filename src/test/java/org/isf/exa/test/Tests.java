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
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.exatype.test.TestExamType;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

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

	@BeforeClass
	public static void setUpClass() {
		testExam = new TestExam();
		testExamType = new TestExamType();
		testExamRow = new TestExamRow();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testExamGets() throws Exception {
		String code = _setupTestExam(false);
		_checkExamIntoDb(code);
	}

	@Test
	public void testExamSets() throws Exception {
		String code = _setupTestExam(true);
		_checkExamIntoDb(code);
	}

	@Test
	public void testExamRowGets() throws Exception {
		int code = _setupTestExamRow(false);
		_checkExamRowIntoDb(code);
	}

	@Test
	public void testExamRowSets() throws Exception {
		int code = _setupTestExamRow(true);
		_checkExamRowIntoDb(code);
	}

	@Test
	public void testIoGetExamRowZero() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		List<ExamRow> examRows = examRowIoOperation.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testIoGetExamRowNoDescription() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		List<ExamRow> examRows = examRowIoOperation.getExamRow(foundExamRow.getCode(), null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testIoGetExamRowWithDescription() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		List<ExamRow> examRows = examRowIoOperation.getExamRow(foundExamRow.getCode(), foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testIoGetExamRows() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		List<ExamRow> examRows = examRowIoOperation.getExamRows();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
		// deprecated method
		examRows = examRowIoOperation.getExamRows();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testIoGetExamsRowByDesc() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		List<ExamRow> examRows = examRowIoOperation.getExamsRowByDesc(foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testIoGetExams() throws Exception {
		String code = _setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findOne(code);
		List<Exam> exams = examIoOperation.getExams();
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());
	}

	@Test
	public void testIoGetExamTypeExam() throws Exception {
		String code = _setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findOne(code);
		List<ExamType> examTypes = examIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	public void testIoGetExamTypeExamRow() throws Exception {
		String code = _setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findOne(code);
		List<ExamType> examTypes = examRowIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	public void testIoNewExamRow() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, true);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examIoOperation.newExamRow(examRow);
		assertThat(result).isTrue();
		_checkExamRowIntoDb(examRow.getCode());
	}

	@Test
	public void testIoNewExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		boolean result = examIoOperation.newExam(exam);
		assertThat(result).isTrue();
		_checkExamIntoDb(exam.getCode());
	}

	@Test
	public void testIoUpdateExam() throws Exception {
		String code = _setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findOne(code);
		foundExam.setDescription("Update");
		boolean result = examIoOperation.updateExam(foundExam);
		assertThat(result).isTrue();
		Exam updateExam = examIoOperationRepository.findOne(code);
		assertThat(updateExam.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoUpdateExamRow() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findOne(code);
		examRow.setDescription("Update");
		boolean result = examRowIoOperation.updateExamRow(examRow);
		assertThat(result).isTrue();
		ExamRow updateExamRow = examRowIoOperationRepository.findOne(code);
		assertThat(updateExamRow.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoDeleteExam() throws Exception {
		String code = _setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findOne(code);
		boolean result = examIoOperation.deleteExam(foundExam);
		assertThat(result).isTrue();
		result = examIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoDeleteExamRow() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		boolean result = examIoOperation.deleteExamRow(foundExamRow);
		assertThat(result).isTrue();
		result = examIoOperation.isRowPresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoIsKeyPresent() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examIoOperation.isKeyPresent(exam);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsKeyPresentExamRow() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findOne(code);
		boolean result = examRowIoOperation.isKeyPresent(examRow);
		assertThat(result).isTrue();
		// fail
		examRow.setCode(-1);
		result = examRowIoOperation.isKeyPresent(examRow);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findOne(code);
		assertThat(examRowIoOperation.isCodePresent(examRow.getCode())).isTrue();
	}

	@Test
	public void testIoIsRowPresent() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findOne(code);
		assertThat(examRowIoOperation.isRowPresent(examRow.getCode())).isTrue();
	}

	@Test
	public void testIoGetExamRowByExamCode() throws Exception {
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
	public void testMgrGetExamRowByExamCode() throws Exception {
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
	public void testMgrGetExamRow() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testMgrGetExams() throws Exception {
		String code = _setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findOne(code);
		List<Exam> exams = examBrowsingManager.getExams();
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());

		exams = examBrowsingManager.getExamsbyDesc();
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
	public void testMgrGetExamType() throws Exception {
		String code = _setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findOne(code);
		List<ExamType> examTypes = examBrowsingManager.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	public void testMgrNewExamRow() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, true);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examRowBrowsingManager.newExamRow(examRow);
		assertThat(result).isTrue();
		_checkExamRowIntoDb(examRow.getCode());
	}

	@Test
	public void testMgrNewExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		boolean result = examBrowsingManager.newExam(exam);
		assertThat(result).isTrue();
		_checkExamIntoDb(exam.getCode());
	}

	@Test
	public void testMgrUpdateExam() throws Exception {
		String code = _setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findOne(code);
		foundExam.setDescription("Update");
		boolean result = examBrowsingManager.updateExam(foundExam);
		assertThat(result).isTrue();
		Exam updateExam = examIoOperationRepository.findOne(code);
		assertThat(updateExam.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrDeleteExam() throws Exception {
		String code = _setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findOne(code);
		boolean result = examBrowsingManager.deleteExam(foundExam);
		assertThat(result).isTrue();
		result = examIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrDeleteExamRow() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		boolean result = examRowBrowsingManager.deleteExamRow(foundExamRow);
		assertThat(result).isTrue();
		result = examIoOperation.isRowPresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrIsKeyPresent() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examBrowsingManager.isKeyPresent(exam);
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrGetExamRowZero() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());

		examRows = examRowBrowsingManager.getExamRow();
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testMgrGetExamRowNoDescription() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode(), null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());

		examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testMgrGetExamRowWithDescription() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		List<ExamRow> examRows = examRowBrowsingManager.getExamRow(foundExamRow.getCode(), foundExamRow.getDescription());
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testMgrExamValidationUpdate() throws Exception {
		ExamType examType = new ExamType("ZZ", "TestDescription");
		Exam exam = testExam.setup(examType, 1, false);
		String code = exam.getCode();
		// code = ""
		exam.setCode("");
		assertThatThrownBy(() -> examBrowsingManager.updateExam(exam))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// description = ""
		exam.setCode(code);
		exam.setDescription("");
		assertThatThrownBy(() -> examBrowsingManager.updateExam(exam))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrExamValidationInsert() throws Exception {
		String code = _setupTestExam(false);
		Exam exam = examIoOperationRepository.findOne(code);
		// code already exists
		ExamType examType = new ExamType("ZZ", "TestDescription");
		Exam exam2 = testExam.setup(examType, 1, false);
		exam2.setCode(code);
		assertThatThrownBy(() -> examBrowsingManager.newExam(exam2))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrExamRowValidationUpdate() throws Exception {
		ExamType examType = new ExamType("ZZ", "TestDescription");
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, false);
		// description = ""
		examRow.setDescription("");
		assertThatThrownBy(() -> examRowBrowsingManager.newExamRow(examRow))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testExamEqualHashToString() throws Exception {
		String code = _setupTestExam(false);
		Exam exam = examIoOperationRepository.findOne(code);
		ExamType examType = testExamType.setup(false);
		Exam exam2 = new Exam("XXX", "TestDescription", examType, 1, "TestDefaultResult");
		assertThat(exam.equals(exam)).isTrue();
		assertThat(exam)
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
	public void testExamRowEqualHashToString() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow examRow = examRowIoOperationRepository.findOne(code);
		ExamType examType = testExamType.setup(false);
		Exam exam2 = new Exam("XXX", "TestDescription", examType, 1, "TestDefaultResult");
		ExamRow examRow2 = new ExamRow(exam2, "NewDescription");
		assertThat(examRow.equals(examRow)).isTrue();
		assertThat(examRow)
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
	public void testExamGetterSetter() throws Exception {
		String code = _setupTestExam(false);
		Exam exam = examIoOperationRepository.findOne(code);
		exam.setLock(-99);
		assertThat(exam.getLock()).isEqualTo(-99);

		assertThat(exam.getSearchString()).isEqualTo("zztestdescription");
	}

	@Test
	public void testIoExamSanitize() throws Exception {
		Method method = examIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(examIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(examIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(examIoOperation, "abcdef")).isEqualTo("abcdef");
	}

	@Test
	public void testIoExamRowSanitize() throws Exception {
		Method method = examRowIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(examRowIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(examRowIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(examRowIoOperation, "abcdef")).isEqualTo("abcdef");
	}

	private String _setupTestExam(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		return exam.getCode();
	}

	private void _checkExamIntoDb(String code) throws OHException {
		Exam foundExam = examIoOperationRepository.findOne(code);
		testExam.check(foundExam);
	}

	private int _setupTestExamRow(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(usingSet);
		Exam exam = testExam.setup(examType, 2, usingSet);
		ExamRow examRow = testExamRow.setup(exam, usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examRowIoOperationRepository.saveAndFlush(examRow);
		return examRow.getCode();
	}

	private void _checkExamRowIntoDb(int code) throws OHException {
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		testExamRow.check(foundExamRow);
	}

	private String _setupTestExamType(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		return examType.getCode();
	}
}