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
package org.isf.exatype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
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

class ExamTypeBrowserManagerTest extends OHCoreTestCase {

	private static TestExamType testExamType;

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
	void getExamType_shouldReturnSavedExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		List<ExamType> examTypes = examTypeBrowserManager.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	void updateExamType_shouldSaveNewDescription() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		foundExamType.setDescription("Update");
		ExamType updatedExamType = examTypeBrowserManager.updateExamType(foundExamType);
		assertThat(updatedExamType).isNotNull();
		assertThat(updatedExamType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newExamType_shouldSaveExamType() throws Exception {
		ExamType examType = testExamType.setup(true);
		ExamType newExamType = examTypeBrowserManager.newExamType(examType);
		assertThat(newExamType).isNotNull();
		checkExamTypeIntoDb(newExamType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestExamType(false);
		boolean result = examTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deleteExamType_shouldRemoveExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		examTypeBrowserManager.deleteExamType(foundExamType);
		assertThat(examTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void newAndUpdateExamType_shouldThrow_whenCodeOrDescriptionIsInvalid() throws Exception {
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