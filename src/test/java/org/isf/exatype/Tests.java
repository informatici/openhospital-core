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
package org.isf.exatype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.exatype.manager.ExamTypeBrowserManager;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperation;
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

	private static TestExamType testExamType;

	@Autowired
	ExamTypeIoOperation examTypeIoOperation;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	ExamTypeBrowserManager examTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testExamType = new TestExamType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testExamTypeGets() throws Exception {
		String code = setupTestExamType(false);
		checkExamTypeIntoDb(code);
	}

	@Test
	void testExamTypeSets() throws Exception {
		String code = setupTestExamType(true);
		checkExamTypeIntoDb(code);
	}

	@Test
	void testIoGetExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examTypeIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void testIoUpdateExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		foundExamType.setDescription("Update");
		ExamType updatedExamType = examTypeIoOperation.updateExamType(foundExamType);
		assertThat(updatedExamType).isNotNull();
		assertThat(updatedExamType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoNewExamType() throws Exception {
		ExamType examType = testExamType.setup(true);
		ExamType newExamType = examTypeIoOperation.newExamType(examType);
		assertThat(newExamType).isNotNull();
		checkExamTypeIntoDb(newExamType.getCode());
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestExamType(false);
		boolean result = examTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testIoDeleteExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		examTypeIoOperation.deleteExamType(foundExamType);
		assertThat(examTypeIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrGetExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examTypeBrowserManager.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void testMgrUpdateExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		foundExamType.setDescription("Update");
		ExamType updatedExamType = examTypeBrowserManager.updateExamType(foundExamType);
		assertThat(updatedExamType).isNotNull();
		assertThat(updatedExamType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewExamType() throws Exception {
		ExamType examType = testExamType.setup(true);
		ExamType newExamType = examTypeBrowserManager.newExamType(examType);
		assertThat(newExamType).isNotNull();
		checkExamTypeIntoDb(newExamType.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestExamType(false);
		boolean result = examTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testMgrDeleteExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		examTypeBrowserManager.deleteExamType(foundExamType);
		assertThat(examTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrExamTypeValidation() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();

		// code is empty
		foundExamType.setCode("");
		assertThatThrownBy(() -> examTypeBrowserManager.updateExamType(foundExamType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);

		// code too long
		foundExamType.setCode("abcdefghijk");
		assertThatThrownBy(() -> examTypeBrowserManager.updateExamType(foundExamType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);

		// description is empty
		foundExamType.setCode(code);
		String description = foundExamType.getDescription();
		foundExamType.setDescription("");
		assertThatThrownBy(() -> examTypeBrowserManager.updateExamType(foundExamType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);

		// duplicate key on insert
		foundExamType.setDescription(description);
		assertThatThrownBy(() -> examTypeBrowserManager.newExamType(foundExamType))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void testPatientExamTypeHashToString() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();

		assertThat(foundExamType.hashCode()).isPositive();

		assertThat(foundExamType).hasToString(foundExamType.getDescription());
	}

	private String setupTestExamType(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		return examType.getCode();
	}

	private void checkExamTypeIntoDb(String code) throws OHException {
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		testExamType.check(foundExamType);
	}
}