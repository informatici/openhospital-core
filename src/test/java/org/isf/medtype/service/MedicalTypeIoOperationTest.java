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
package org.isf.medtype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalTypeIoOperationTest extends OHCoreTestCase {

	private static TestMedicalType testMedicalType;

	@Autowired
	MedicalTypeIoOperation medicalTypeIoOperation;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMedicalType = new TestMedicalType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getMedicalTypes_shouldIncludeSavedType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		List<MedicalType> medicalTypes = medicalTypeIoOperation.getMedicalTypes();
		assertThat(medicalTypes.get(medicalTypes.size() - 1).getDescription()).isEqualTo(foundMedicalType.getDescription());
	}

	@Test
	void getMedicalTypesNotDeleted_shouldIncludeSavedType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		List<MedicalType> medicalTypes = medicalTypeIoOperation.getMedicalTypesNotDeleted();
		assertThat(medicalTypes.get(medicalTypes.size() - 1).getDescription()).isEqualTo(foundMedicalType.getDescription());
	}

	@Test
	void updateMedicalType_shouldSaveNewDescription() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		foundMedicalType.setDescription("Update");
		MedicalType updatedMedicalType = medicalTypeIoOperation.updateMedicalType(foundMedicalType);
		assertThat(updatedMedicalType).isNotNull();
		assertThat(updatedMedicalType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newMedicalType_shouldSaveMedicalType() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		MedicalType newMedicalType = medicalTypeIoOperation.newMedicalType(medicalType);
		assertThat(newMedicalType).isNotNull();
		checkMedicalTypeIntoDb(newMedicalType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestMedicalType(false);
		boolean result = medicalTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deleteMedicalType_shouldRemoveMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		medicalTypeIoOperation.deleteMedicalType(foundMedicalType);
		assertThat(medicalTypeIoOperation.isCodePresent(code)).isFalse();
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
