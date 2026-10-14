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
package org.isf.distype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperation;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class DiseaseTypeBrowserManagerTest extends OHCoreTestCase {

	private static TestDiseaseType testDiseaseType;

	@Autowired
	DiseaseTypeIoOperation diseaseTypeIoOperation;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseTypeBrowserManager diseaseTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testDiseaseType = new TestDiseaseType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void deleteDiseaseType_shouldRemoveDiseaseType_whenCheckedViaIoOperation() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getReferenceById(code);
		diseaseTypeBrowserManager.deleteDiseaseType(foundDiseaseType);
		boolean result = diseaseTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	void getDiseaseType_shouldContainSavedDiseaseType() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getReferenceById(code);
		List<DiseaseType> diseaseTypes = diseaseTypeBrowserManager.getDiseaseType();
		assertThat(diseaseTypes).contains(foundDiseaseType);

	}

	@Test
	void updateDiseaseType_shouldSaveNewDescription() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getReferenceById(code);
		foundDiseaseType.setDescription("Update");
		DiseaseType updatedDiseaseType = diseaseTypeBrowserManager.updateDiseaseType(foundDiseaseType);
		assertThat(updatedDiseaseType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newDiseaseType_shouldSaveDiseaseType() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(true);
		DiseaseType newDiseaseType = diseaseTypeBrowserManager.newDiseaseType(diseaseType);
		checkDiseaseTypeIntoDb(newDiseaseType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestDiseaseType(false);
		boolean result = diseaseTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void getDiseaseType_shouldReturnDiseaseType_whenCodeExists() throws Exception {
		String code = setupTestDiseaseType(false);
		assertThat(diseaseTypeBrowserManager.getDiseaseType(code)).isNotNull();
	}

	@Test
	void getDiseaseType_shouldReturnNull_whenCodeDoesNotExist() throws Exception {
		setupTestDiseaseType(false);
		assertThat(diseaseTypeBrowserManager.getDiseaseType("someCodeThatDoesNotExist")).isNull();
	}

	@Test
	void deleteDiseaseType_shouldRemoveDiseaseType() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getReferenceById(code);
		diseaseTypeBrowserManager.deleteDiseaseType(foundDiseaseType);
		boolean result = diseaseTypeBrowserManager.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	void newDiseaseType_shouldThrow_whenCodeIsEmpty() throws Exception {
		DiseaseType diseaseType = new DiseaseType("ZZ", "TestDescription");
		diseaseType.setCode("");
		assertThatThrownBy(() -> diseaseTypeBrowserManager.newDiseaseType(diseaseType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void newDiseaseType_shouldThrow_whenCodeIsTooLong() throws Exception {
		DiseaseType diseaseType = new DiseaseType("ZZ", "TestDescription");
		diseaseType.setCode("12345678901234567");
		assertThatThrownBy(() -> diseaseTypeBrowserManager.newDiseaseType(diseaseType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void newDiseaseType_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		DiseaseType diseaseType = new DiseaseType("ZZ", "TestDescription");
		diseaseType.setDescription("");
		assertThatThrownBy(() -> diseaseTypeBrowserManager.newDiseaseType(diseaseType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void newDiseaseType_shouldThrow_whenCodeExists() throws Exception {
		String code = setupTestDiseaseType(false);
		diseaseTypeIoOperationRepository.getReferenceById(code);
		// code already exists
		DiseaseType diseaseType2 = new DiseaseType("ZZ", "TestDescription");
		diseaseType2.setCode(code);
		assertThatThrownBy(() -> diseaseTypeBrowserManager.newDiseaseType(diseaseType2))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	private String setupTestDiseaseType(boolean usingSet) throws OHException {
		DiseaseType diseaseType = testDiseaseType.setup(usingSet);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		return diseaseType.getCode();
	}

	private void checkDiseaseTypeIntoDb(String code) throws OHException {
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getReferenceById(code);
		testDiseaseType.check(foundDiseaseType);
	}

}