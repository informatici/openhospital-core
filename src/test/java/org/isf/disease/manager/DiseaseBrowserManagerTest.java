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
package org.isf.disease.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperation;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class DiseaseBrowserManagerTest extends OHCoreTestCase {

	private static TestDisease testDisease;
	private static TestDiseaseType testDiseaseType;

	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	DiseaseBrowserManager diseaseBrowserManager;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseTypeIoOperation diseaseTypeIoOperation;

	@BeforeAll
	static void setUpClass() {
		testDisease = new TestDisease();
		testDiseaseType = new TestDiseaseType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getDiseaseByCode_shouldReturnDisease_whenCodeExists() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		testDisease.check(foundDisease);
	}

	@Test
	void getDiseaseVariants_shouldFilterByOpdAndIpdFlags() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);

		List<Disease> diseases = diseaseBrowserManager.getDisease(foundDisease.getType().getCode());
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseBrowserManager.getDiseaseOpd(foundDisease.getType().getCode());
		assertThat(diseases).doesNotContain(foundDisease);
		foundDisease.setOpdInclude(true);
		diseaseIoOperationRepository.saveAndFlush(foundDisease);
		diseases = diseaseBrowserManager.getDiseaseOpd(foundDisease.getType().getCode());
		assertThat(diseases).contains(foundDisease);

		foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		diseases = diseaseBrowserManager.getDiseaseIpdIn();
		assertThat(diseases).doesNotContain(foundDisease);
		foundDisease.setOpdInclude(true);
		foundDisease.setIpdInInclude(true);
		diseaseIoOperationRepository.saveAndFlush(foundDisease);
		diseases = diseaseBrowserManager.getDiseaseOpd();
		assertThat(diseases).contains(foundDisease);

		foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		diseases = diseaseBrowserManager.getDiseaseIpdOut(foundDisease.getType().getCode());
		assertThat(diseases).doesNotContain(foundDisease);
		foundDisease.setOpdInclude(true);
		foundDisease.setIpdInInclude(true);
		foundDisease.setIpdOutInclude(true);
		diseaseIoOperationRepository.saveAndFlush(foundDisease);
		diseases = diseaseBrowserManager.getDiseaseIpdOut();
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseBrowserManager.getDiseaseIpdIn();
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseBrowserManager.getDiseaseIpdOut(foundDisease.getType().getCode());
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseBrowserManager.getDiseaseIpdIn(foundDisease.getType().getCode());
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseBrowserManager.getDiseaseAll();
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseBrowserManager.getDisease();
		assertThat(diseases).contains(foundDisease);
	}

	@Test
	void newDisease_shouldSaveDisease() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		diseaseTypeIoOperation.newDiseaseType(diseaseType);
		Disease disease = testDisease.setup(diseaseType, true);
		Disease result = diseaseBrowserManager.newDisease(disease);
		assertThat(result.getCode()).isEqualTo("999");
		checkDiseaseIntoDb(disease.getCode());
	}

	@Test
	void updateDisease_shouldSaveNewDescription() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		foundDisease.setDescription("Update");
		Disease result = diseaseBrowserManager.updateDisease(foundDisease);
		assertThat(result.getDescription()).isEqualTo("Update");
		Disease updateDisease = diseaseBrowserManager.getDiseaseByCode(code);
		assertThat(updateDisease.getDescription()).isEqualTo("Update");
	}

	@Test
	void deleteDisease_shouldLeaveIncludeFlagsFalse_whenAlreadyFalse() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		diseaseBrowserManager.deleteDisease(foundDisease);
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
		assertThat(foundDisease.getOpdInclude()).isFalse();
	}

	@Test
	void deleteDisease_shouldLeaveIncludeFlagsFalse() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		diseaseBrowserManager.deleteDisease(foundDisease);
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
		assertThat(foundDisease.getOpdInclude()).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestDisease(false);
		boolean result = diseaseBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void descriptionControl_shouldReturnTrue_whenDescriptionExists() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		boolean result = diseaseBrowserManager.descriptionControl(foundDisease.getDescription(), foundDisease.getType().getCode());
		assertThat(result).isTrue();
	}

	@Test
	void newDisease_shouldThrow_whenCodeIsEmpty() throws Exception {
		DiseaseType diseaseType = new DiseaseType("ZZ", "TestDescription");
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("");
		assertThatThrownBy(() -> diseaseBrowserManager.newDisease(disease))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void newDisease_shouldThrow_whenCodeIsTooLong() throws Exception {
		DiseaseType diseaseType = new DiseaseType("ZZ", "TestDescription");
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("12345678901234567");
		assertThatThrownBy(() -> diseaseBrowserManager.newDisease(disease))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void updateDisease_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		DiseaseType diseaseType = new DiseaseType("ZZ", "TestDescription");
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setDescription("");
		assertThatThrownBy(() -> diseaseBrowserManager.updateDisease(disease))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void newDisease_shouldThrowTwoErrors_whenCodeAndDescriptionExist() throws Exception {
		String code = setupTestDisease(false);
		diseaseBrowserManager.getDiseaseByCode(code);
		// code already exists and same description used
		DiseaseType diseaseType = new DiseaseType("ZZ", "TestDescription");
		Disease disease2 = testDisease.setup(diseaseType, false);
		disease2.setCode(code);
		assertThatThrownBy(() -> diseaseBrowserManager.newDisease(disease2))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
				);
	}

	private String setupTestDisease(boolean usingSet) throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, usingSet);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		return disease.getCode();
	}

	private void checkDiseaseIntoDb(String code) throws Exception {
		Disease foundDisease = diseaseIoOperationRepository.getReferenceById(code);
		testDisease.check(foundDisease);
	}

}
