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
package org.isf.medtype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperation;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestMedicalType testMedicalType;

	@Autowired
	MedicalTypeIoOperation medicalTypeIoOperation;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	MedicalTypeBrowserManager medicalTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testMedicalType = new TestMedicalType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testMedicalTypeGets() throws Exception {
		String code = setupTestMedicalType(false);
		checkMedicalTypeIntoDb(code);
	}

	@Test
	void testMedicalTypeSets() throws Exception {
		String code = setupTestMedicalType(true);
		checkMedicalTypeIntoDb(code);
	}

	@Test
	void testIoGetMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		List<MedicalType> medicalTypes = medicalTypeIoOperation.getMedicalTypes();
		assertThat(medicalTypes.get(medicalTypes.size() - 1).getDescription()).isEqualTo(foundMedicalType.getDescription());
	}

	@Test
	void testIoUpdateMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		foundMedicalType.setDescription("Update");
		MedicalType updatedMedicalType = medicalTypeIoOperation.updateMedicalType(foundMedicalType);
		assertThat(updatedMedicalType).isNotNull();
		assertThat(updatedMedicalType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoNewMedicalType() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		MedicalType newMedicalType = medicalTypeIoOperation.newMedicalType(medicalType);
		assertThat(newMedicalType).isNotNull();
		checkMedicalTypeIntoDb(newMedicalType.getCode());
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestMedicalType(false);
		boolean result = medicalTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testIoDeleteMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		medicalTypeIoOperation.deleteMedicalType(foundMedicalType);
		assertThat(medicalTypeIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrGetMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		List<MedicalType> medicalTypes = medicalTypeBrowserManager.getMedicalType();
		assertThat(medicalTypes.get(medicalTypes.size() - 1).getDescription()).isEqualTo(foundMedicalType.getDescription());
	}

	@Test
	void testMgrUpdateMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		foundMedicalType.setDescription("Update");
		MedicalType updatedMedicalType = medicalTypeBrowserManager.updateMedicalType(foundMedicalType);
		assertThat(updatedMedicalType).isNotNull();
		assertThat(updatedMedicalType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewMedicalType() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		MedicalType newMedicalType = medicalTypeBrowserManager.newMedicalType(medicalType);
		checkMedicalTypeIntoDb(newMedicalType.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestMedicalType(false);
		assertThat(medicalTypeBrowserManager.isCodePresent(code)).isTrue();
		assertThat(medicalTypeBrowserManager.isCodePresent("isNotThere")).isFalse();
	}

	@Test
	void testMgrDeleteMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		medicalTypeBrowserManager.deleteMedicalType(foundMedicalType);
		assertThat(medicalTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrValidateMedicalTypeNoKey() throws Exception {
		assertThatThrownBy(() ->
		{
			MedicalType medicalType = new MedicalType("", "description");
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrValidateMedicalTypeKeyTooLong() throws Exception {
		assertThatThrownBy(() ->
		{
			MedicalType medicalType = new MedicalType("thisIsTooLong", "description");
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrValidateMedicalTypeNoDescription() throws Exception {
		assertThatThrownBy(() ->
		{
			MedicalType medicalType = new MedicalType("Z", "");
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrValidateMedicalTypeCodeAlreadyExists() throws Exception {
		assertThatThrownBy(() ->
		{
			String code = setupTestMedicalType(false);
			MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
			assertThat(foundMedicalType).isNotNull();
			MedicalType medicalType = new MedicalType(foundMedicalType.getCode(), foundMedicalType.getDescription());
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	void testMedicalTypeToString() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType)
				.isNotNull()
				.hasToString(foundMedicalType.getDescription());
	}

	@Test
	void testMedicalTypeEquals() throws Exception {
		MedicalType medicalType1 = new MedicalType("Z", "description");
		MedicalType medicalType2 = new MedicalType("A", "description");
		MedicalType medicalType3 = new MedicalType("Z", "otherDescription");

		assertThat(medicalType1)
				.isEqualTo(medicalType1)
				.isNotEqualTo("someString")
				.isNotEqualTo(medicalType2)
				.isNotEqualTo(medicalType3);
	}

	@Test
	void testMedicalTypeHashCode() throws Exception {
		MedicalType medicalType = new MedicalType("Z", "description");
		// compute value
		int hashCode = medicalType.hashCode();
		assertThat(hashCode).isPositive();
		// used already computed value
		assertThat(medicalType.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestMedicalType(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		return medicalType.getCode();
	}

	private void checkMedicalTypeIntoDb(String code) throws OHException {
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		testMedicalType.check(foundMedicalType);
	}
}