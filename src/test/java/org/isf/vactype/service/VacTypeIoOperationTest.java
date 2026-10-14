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
package org.isf.vactype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.model.VaccineType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VacTypeIoOperationTest extends OHCoreTestCase {

	private static TestVaccineType testVaccineType;

	@Autowired
	VacTypeIoOperation vaccineTypeIoOperation;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testVaccineType = new TestVaccineType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getVaccineType_shouldIncludeSavedVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		List<VaccineType> vaccineTypes = vaccineTypeIoOperation.getVaccineType();
		assertThat(vaccineTypes.get(vaccineTypes.size() - 1).getDescription()).isEqualTo(foundVaccineType.getDescription());
	}

	@Test
	void updateVaccineType_shouldSaveNewDescription() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		foundVaccineType.setDescription("Update");
		VaccineType updatedVaccineType = vaccineTypeIoOperation.updateVaccineType(foundVaccineType);
		assertThat(updatedVaccineType).isNotNull();
		assertThat(updatedVaccineType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newVaccineType_shouldSaveVaccineType() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		VaccineType newVaccineType = vaccineTypeIoOperation.newVaccineType(vaccineType);
		checkVaccineTypeIntoDb(newVaccineType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestVaccineType(false);
		assertThat(vaccineTypeIoOperation.isCodePresent(code)).isTrue();
	}

	@Test
	void deleteVaccineType_shouldRemoveVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		vaccineTypeIoOperation.deleteVaccineType(foundVaccineType);
		assertThat(vaccineTypeIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void findVaccineType_shouldReturnVaccineType_whenCodeExists() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		assertThat(foundVaccineType.getCode()).isEqualTo(code);
	}

	private String setupTestVaccineType(boolean usingSet) throws OHException {
		VaccineType vaccineType = testVaccineType.setup(usingSet);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		return vaccineType.getCode();
	}

	private void checkVaccineTypeIntoDb(String code) throws OHServiceException {
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		testVaccineType.check(foundVaccineType);
	}

}
