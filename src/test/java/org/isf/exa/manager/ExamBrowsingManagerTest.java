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
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exa.service.ExamIoOperations;
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

class ExamBrowsingManagerTest extends OHCoreTestCase {

	private static TestExam testExam;
	private static TestExamType testExamType;

	@Autowired
	ExamIoOperations examIoOperation;
	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	ExamBrowsingManager examBrowsingManager;

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
	void getExamsVariants_shouldFilterByDescriptionOrTypeDescription() throws Exception {
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
	void getExamType_shouldReturnSavedExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examBrowsingManager.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void newExam_shouldSaveExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		Exam newExam = examBrowsingManager.newExam(exam);
		checkExamIntoDb(newExam.getCode());
	}

	@Test
	void updateExam_shouldSaveNewDescription() throws Exception {
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
	void deleteExam_shouldRemoveExam() throws Exception {
		String code = setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExam).isNotNull();
		examBrowsingManager.deleteExam(foundExam);
		assertThat(examIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void isKeyPresent_shouldReturnTrue_whenExamExists() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examBrowsingManager.isKeyPresent(exam);
		assertThat(result).isTrue();
	}

	@Test
	void updateExam_shouldThrow_whenCodeOrDescriptionIsEmpty() throws Exception {
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
	void newExam_shouldThrow_whenCodeExists() throws Exception {
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

	private String setupTestExamType(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		return examType.getCode();
	}

}
