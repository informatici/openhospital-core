/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.exatype.test;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestExamType testExamType;

	@Autowired
	ExamTypeIoOperation examTypeIoOperation;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	ExamTypeBrowserManager examTypeBrowserManager;

	@BeforeClass
	public static void setUpClass() {
		testExamType = new TestExamType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testExamTypeGets() throws Exception {
		String code = setupTestExamType(false);
		checkExamTypeIntoDb(code);
	}

	@Test
	public void testExamTypeSets() throws Exception {
		String code = setupTestExamType(true);
		checkExamTypeIntoDb(code);
	}

	@Test
	public void testIoGetExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).get();
		List<ExamType> examTypes = examTypeIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	public void testIoUpdateExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).get();
		foundExamType.setDescription("Update");
		ExamType result = examTypeIoOperation.updateExamType(foundExamType);
		assertThat(result);
		ExamType updateExamType = examTypeIoOperationRepository.findById(code).get();
		assertThat(updateExamType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewExamType() throws Exception {
		ExamType examType = testExamType.setup(true);
		ExamType result = examTypeIoOperation.newExamType(examType);
		assertThat(result);
		checkExamTypeIntoDb(examType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestExamType(false);
		boolean result = examTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).get();
		boolean result = examTypeIoOperation.deleteExamType(foundExamType);
		assertThat(result).isTrue();
		result = examTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).get();
		List<ExamType> examTypes = examTypeBrowserManager.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	public void testMgrUpdateExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).get();
		foundExamType.setDescription("Update");
		ExamType result = examTypeBrowserManager.updateExamType(foundExamType);
		assertThat(result);
		ExamType updateExamType = examTypeIoOperationRepository.findById(code).get();
		assertThat(updateExamType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrNewExamType() throws Exception {
		ExamType examType = testExamType.setup(true);
		ExamType result = examTypeBrowserManager.newExamType(examType);
		assertThat(result);
		checkExamTypeIntoDb(examType.getCode());
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestExamType(false);
		boolean result = examTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrDeleteExamType() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).get();
		boolean result = examTypeBrowserManager.deleteExamType(foundExamType);
		assertThat(result).isTrue();
		result = examTypeBrowserManager.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrExamTypeValidation() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).get();

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
	public void testPatientExamTypeHashToString() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).get();

		assertThat(foundExamType.hashCode()).isPositive();

		assertThat(foundExamType).hasToString(foundExamType.getDescription());
	}

	private String setupTestExamType(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		return examType.getCode();
	}

	private void checkExamTypeIntoDb(String code) throws OHException {
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).get();
		testExamType.check(foundExamType);
	}
}