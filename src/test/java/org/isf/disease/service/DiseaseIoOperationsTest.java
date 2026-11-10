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
package org.isf.disease.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperation;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class DiseaseIoOperationsTest extends OHCoreTestCase {

	private static TestDisease testDisease;
	private static TestDiseaseType testDiseaseType;

	@Autowired
	DiseaseIoOperations diseaseIoOperation;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
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
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		//Disease foundDisease = diseaseIoOperationRepository.findOneByCode(code);
		assertThat(foundDisease).isNotNull();
		testDisease.check(foundDisease);
	}

	@Test
	void getDiseases_shouldFilterByOpdAndIpdFlags() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);

		List<Disease> diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, false, false);
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, false, false);
		assertThat(diseases).doesNotContain(foundDisease);
		foundDisease.setOpdInclude(true);
		diseaseIoOperationRepository.saveAndFlush(foundDisease);
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, false, false);
		assertThat(diseases).contains(foundDisease);

		foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, false);
		assertThat(diseases).doesNotContain(foundDisease);
		foundDisease.setOpdInclude(true);
		foundDisease.setIpdInInclude(true);
		diseaseIoOperationRepository.saveAndFlush(foundDisease);
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, false);
		assertThat(diseases).contains(foundDisease);

		foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, true);
		assertThat(diseases).doesNotContain(foundDisease);
		foundDisease.setOpdInclude(true);
		foundDisease.setIpdInInclude(true);
		foundDisease.setIpdOutInclude(true);
		diseaseIoOperationRepository.saveAndFlush(foundDisease);
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, true);
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, true, true);
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, false, true);
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, true, false);
		assertThat(diseases).contains(foundDisease);
	}

	@Test
	void newDisease_shouldSaveDisease() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		DiseaseType savedDiseaseType = diseaseTypeIoOperation.newDiseaseType(diseaseType);
		Disease disease = testDisease.setup(savedDiseaseType, true);
		Disease result = diseaseIoOperation.newDisease(disease);
		assertThat(result.getCode()).isEqualTo("999");
		checkDiseaseIntoDb(disease.getCode());
	}

	@Test
	void updateDisease_shouldSaveNewDescription() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		foundDisease.setDescription("Update");
		Disease result = diseaseIoOperation.updateDisease(foundDisease);
		Disease updateDisease = diseaseIoOperation.getDiseaseByCode(code);

		assertThat(result.getDescription()).isEqualTo("Update");
		assertThat(updateDisease.getDescription()).isEqualTo("Update");
	}

	@Test
	void deleteDisease_shouldLeaveIncludeFlagsFalse_whenAlreadyFalse() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		diseaseIoOperation.deleteDisease(foundDisease);
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
		assertThat(foundDisease.getOpdInclude()).isFalse();
	}

	@Test
	void deleteDisease_shouldLeaveIncludeFlagsFalse() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		diseaseIoOperation.deleteDisease(foundDisease);
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
		assertThat(foundDisease.getOpdInclude()).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestDisease(false);
		boolean result = diseaseIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void isDescriptionPresent_shouldReturnTrue_whenDescriptionExists() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		boolean result = diseaseIoOperation.isDescriptionPresent(foundDisease.getDescription(), foundDisease.getType().getCode());
		assertThat(result).isTrue();
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
