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
package org.isf.disease.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.disease.service.DiseaseIoOperations;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.distype.test.TestDiseaseType;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class Tests extends OHCoreTestCase {

	private static TestDisease testDisease;
	private static TestDiseaseType testDiseaseType;

	@Autowired
	DiseaseIoOperations diseaseIoOperation;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	DiseaseBrowserManager diseaseBrowserManager;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testDisease = new TestDisease();
		testDiseaseType = new TestDiseaseType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testDiseaseGets() throws Exception {
		String code = setupTestDisease(false);
		checkDiseaseIntoDb(code);
	}

	@Test
	public void testDiseaseSets() throws Exception {
		String code = setupTestDisease(true);
		checkDiseaseIntoDb(code);
	}

	@Test
	public void testIoGetDiseaseByCode() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		//Disease foundDisease = diseaseIoOperationRepository.findOneByCode(code);
		testDisease.check(foundDisease);
	}

	@Test
	public void testIoGetDiseases() throws Exception {
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
	public void testIoNewDisease() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, true);
		Disease result = diseaseIoOperation.newDisease(disease);
		assertThat(result.getCode()).isEqualTo("999");
		checkDiseaseIntoDb(disease.getCode());
	}

	@Test
	public void testIoUpdateDisease() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		foundDisease.setDescription("Update");
		Disease result = diseaseIoOperation.updateDisease(foundDisease);
		Disease updateDisease = diseaseIoOperation.getDiseaseByCode(code);

		assertThat(result.getDescription()).isEqualTo("Update");
		assertThat(updateDisease.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoHasDiseaseModified() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		boolean result = diseaseIoOperation.deleteDisease(foundDisease);
		assertThat(result).isTrue();
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
	}

	@Test
	public void testIoDeleteDisease() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		boolean result = diseaseIoOperation.deleteDisease(foundDisease);
		assertThat(result).isTrue();
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
		assertThat(foundDisease.getOpdInclude()).isFalse();
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestDisease(false);
		boolean result = diseaseIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsDescriptionPresent() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(code);
		boolean result = diseaseIoOperation.isDescriptionPresent(foundDisease.getDescription(), foundDisease.getType().getCode());
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrGetDiseaseByCode() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		testDisease.check(foundDisease);
	}

	@Test
	public void testMgrGetDiseases() throws Exception {
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
	public void testMgrNewDisease() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, true);
		Disease result = diseaseBrowserManager.newDisease(disease);
		assertThat(result.getCode()).isEqualTo("999");
		checkDiseaseIntoDb(disease.getCode());
	}

	@Test
	public void testMgrUpdateDisease() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		foundDisease.setDescription("Update");
		Disease result = diseaseBrowserManager.updateDisease(foundDisease);
		assertThat(result.getDescription()).isEqualTo("Update");
		Disease updateDisease = diseaseBrowserManager.getDiseaseByCode(code);
		assertThat(updateDisease.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrHasDiseaseModified() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		boolean result = diseaseBrowserManager.deleteDisease(foundDisease);
		assertThat(result).isTrue();
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
	}

	@Test
	public void testMgrDeleteDisease() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		boolean result = diseaseBrowserManager.deleteDisease(foundDisease);
		assertThat(result).isTrue();
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
		assertThat(foundDisease.getOpdInclude()).isFalse();
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestDisease(false);
		boolean result = diseaseBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrIsDescriptionPresent() throws Exception {
		String code = setupTestDisease(false);
		Disease foundDisease = diseaseBrowserManager.getDiseaseByCode(code);
		boolean result = diseaseBrowserManager.descriptionControl(foundDisease.getDescription(), foundDisease.getType().getCode());
		assertThat(result).isTrue();
	}

	@Test
	public void testDiseaseEqualHashToString() throws Exception {
		String code = setupTestDisease(false);
		Disease disease = diseaseBrowserManager.getDiseaseByCode(code);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);
		Disease disease2 = new Disease("998", "someDescription", diseaseType2);
		assertThat(disease.equals(disease)).isTrue();
		assertThat(disease)
				.isNotEqualTo(disease2)
				.isNotEqualTo("xyzzy");
		disease2.setCode(disease.getCode());
		disease2.setType(disease.getType());
		disease2.setDescription(disease.getDescription());
		assertThat(disease).isEqualTo(disease2);

		assertThat(disease.hashCode()).isPositive();

		assertThat(disease2).hasToString(disease.getDescription());
	}

	@Test
	public void testDiseaseGetterSetter() throws Exception {
		String code = setupTestDisease(false);
		Disease disease = diseaseBrowserManager.getDiseaseByCode(code);
		disease.setLock(-99);
		assertThat(disease.getLock()).isEqualTo(-99);
	}

	@Test
	public void testMgrValidationUpdateCodeEmpty() throws Exception {
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
	public void testMgrValidationUpdateCodeTooLong() throws Exception {
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
	public void testMgrValidationUpdateDescriptionEmpty() throws Exception {
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
	public void testMgrValidationInsert() throws Exception {
		String code = setupTestDisease(false);
		Disease disease = diseaseBrowserManager.getDiseaseByCode(code);
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
		Disease foundDisease = diseaseIoOperationRepository.getById(code);
		testDisease.check(foundDisease);
	}

}
