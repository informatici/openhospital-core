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
package org.isf.medtype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalTypeBrowserManagerTest extends OHCoreTestCase {

	private static TestMedicalType testMedicalType;

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
	void getMedicalType_shouldIncludeSavedType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		List<MedicalType> medicalTypes = medicalTypeBrowserManager.getMedicalType();
		assertThat(medicalTypes.get(medicalTypes.size() - 1).getDescription()).isEqualTo(foundMedicalType.getDescription());
	}

	@Test
	void getAllActiveMedicalType_shouldIncludeSavedType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		List<MedicalType> medicalTypes = medicalTypeBrowserManager.getAllActiveMedicalType();
		assertThat(medicalTypes.get(medicalTypes.size() - 1).getDescription()).isEqualTo(foundMedicalType.getDescription());
	}

	@Test
	void updateMedicalType_shouldSaveNewDescription() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		foundMedicalType.setDescription("Update");
		MedicalType updatedMedicalType = medicalTypeBrowserManager.updateMedicalType(foundMedicalType);
		assertThat(updatedMedicalType).isNotNull();
		assertThat(updatedMedicalType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newMedicalType_shouldSaveMedicalType() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		MedicalType newMedicalType = medicalTypeBrowserManager.newMedicalType(medicalType);
		checkMedicalTypeIntoDb(newMedicalType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnWhetherCodeExists() throws Exception {
		String code = setupTestMedicalType(false);
		assertThat(medicalTypeBrowserManager.isCodePresent(code)).isTrue();
		assertThat(medicalTypeBrowserManager.isCodePresent("isNotThere")).isFalse();
	}

	@Test
	void deleteMedicalType_shouldRemoveMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		medicalTypeBrowserManager.deleteMedicalType(foundMedicalType);
		assertThat(medicalTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void newMedicalType_shouldThrow_whenCodeIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			MedicalType medicalType = new MedicalType("", "description");
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMedicalType_shouldThrow_whenCodeIsTooLong() throws Exception {
		assertThatThrownBy(() ->
		{
			MedicalType medicalType = new MedicalType("thisIsTooLong", "description");
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMedicalType_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			MedicalType medicalType = new MedicalType("Z", "");
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMedicalType_shouldThrowDataIntegrityViolation_whenCodeExists() throws Exception {
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