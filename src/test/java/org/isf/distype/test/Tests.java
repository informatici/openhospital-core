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
package org.isf.distype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperation;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class Tests extends OHCoreTestCase {

	private static TestDiseaseType testDiseaseType;

	@Autowired
	DiseaseTypeIoOperation diseaseTypeIoOperation;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseTypeBrowserManager diseaseTypeBrowserManager;

	@BeforeClass
	public static void setUpClass() {
		testDiseaseType = new TestDiseaseType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testDiseaseTypeGets() throws Exception {
		String code = setupTestDiseaseType(false);
		checkDiseaseTypeIntoDb(code);
	}

	@Test
	public void testDiseaseTypeSets() throws Exception {
		String code = setupTestDiseaseType(true);
		checkDiseaseTypeIntoDb(code);
	}

	@Test
	public void testIoGetDiseaseType() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getById(code);
		List<DiseaseType> diseaseTypes = diseaseTypeIoOperation.getDiseaseTypes();
		assertThat(diseaseTypes).contains(foundDiseaseType);

	}

	@Test
	public void testIoUpdateDiseaseType() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getById(code);
		foundDiseaseType.setDescription("Update");
		boolean result = diseaseTypeIoOperation.updateDiseaseType(foundDiseaseType);
		DiseaseType updateDiseaseType = diseaseTypeIoOperationRepository.getById(code);
		assertThat(result).isTrue();
		assertThat(updateDiseaseType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewDiseaseType() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(true);
		boolean result = diseaseTypeIoOperation.newDiseaseType(diseaseType);
		assertThat(result).isTrue();
		checkDiseaseTypeIntoDb(diseaseType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestDiseaseType(false);
		boolean result = diseaseTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteDiseaseType() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getById(code);
		diseaseTypeBrowserManager.deleteDiseaseType(foundDiseaseType);
		boolean result = diseaseTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetDiseaseType() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getById(code);
		List<DiseaseType> diseaseTypes = diseaseTypeBrowserManager.getDiseaseType();
		assertThat(diseaseTypes).contains(foundDiseaseType);

	}

	@Test
	public void testMgrUpdateDiseaseType() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getById(code);
		foundDiseaseType.setDescription("Update");
		boolean result = diseaseTypeBrowserManager.updateDiseaseType(foundDiseaseType);
		DiseaseType updateDiseaseType = diseaseTypeIoOperationRepository.getById(code);
		assertThat(result).isTrue();
		assertThat(updateDiseaseType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrNewDiseaseType() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(true);
		boolean result = diseaseTypeBrowserManager.newDiseaseType(diseaseType);
		assertThat(result).isTrue();
		checkDiseaseTypeIntoDb(diseaseType.getCode());
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestDiseaseType(false);
		boolean result = diseaseTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrDeleteDiseaseType() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getById(code);
		diseaseTypeBrowserManager.deleteDiseaseType(foundDiseaseType);
		boolean result = diseaseTypeBrowserManager.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrValidateDiseaseTypeCodeEmpty() throws Exception {
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
	public void testMgrValidateDiseaseTypeCodeTooLong() throws Exception {
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
	public void testMgrValidateDiseaseTypeDescriptionEmpty() throws Exception {
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
	public void testMgrValidationInsert() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType diseaseType = diseaseTypeIoOperationRepository.getById(code);
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

	@Test
	public void testDiseaseTypeEqualHashToString() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType diseaseType = diseaseTypeIoOperationRepository.getById(code);
		DiseaseType diseaseType2 = new DiseaseType("code", "description");
		assertThat(diseaseType.equals(diseaseType)).isTrue();
		assertThat(diseaseType)
				.isNotEqualTo(diseaseType2)
				.isNotEqualTo("xyzzy");
		diseaseType2.setCode(diseaseType.getCode());
		diseaseType2.setDescription(diseaseType.getDescription());
		assertThat(diseaseType).isEqualTo(diseaseType2);

		assertThat(diseaseType.hashCode()).isPositive();

		assertThat(diseaseType).hasToString(diseaseType.getDescription());
	}

	private String setupTestDiseaseType(boolean usingSet) throws OHException {
		DiseaseType diseaseType = testDiseaseType.setup(usingSet);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		return diseaseType.getCode();
	}

	private void checkDiseaseTypeIntoDb(String code) throws OHException {
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getById(code);
		testDiseaseType.check(foundDiseaseType);
	}
}