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
package org.isf.vaccine.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.TestVaccine;
import org.isf.vaccine.model.Vaccine;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VaccineIoOperationsTest extends OHCoreTestCase {

	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;

	@Autowired
	VaccineIoOperations vaccineIoOperation;
	@Autowired
	VaccineIoOperationRepository vaccineIoOperationRepository;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testVaccine = new TestVaccine();
		testVaccineType = new TestVaccineType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getVaccine_shouldReturnVaccinesOfType_whenTypeCodeIsGiven() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		List<Vaccine> vaccines = vaccineIoOperation.getVaccine(foundVaccine.getVaccineType().getCode());
		assertThat(vaccines.get(vaccines.size() - 1).getDescription()).isEqualTo(foundVaccine.getDescription());
	}

	@Test
	void getVaccine_shouldReturnAllVaccines_whenNoTypeCodeIsGiven() throws Exception {
		// given:
		setupTestVaccine(false);
		
		// when:
		List<Vaccine> vaccines = vaccineIoOperation.getVaccine(null);

		// then:
		assertThat(vaccines).isNotEmpty();
	}

	@Test
	void updateVaccine_shouldSaveNewDescription() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		foundVaccine.setDescription("Update");
		Vaccine updatedVaccine = vaccineIoOperation.updateVaccine(foundVaccine);
		assertThat(updatedVaccine.getDescription()).isEqualTo("Update");
	}

	@Test
	void newVaccine_shouldSaveVaccine() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		Vaccine newVaccine = vaccineIoOperation.newVaccine(vaccine);
		assertThat(newVaccine.getCode()).isEqualTo("Z");
		checkVaccineIntoDb(newVaccine.getCode());
	}

	@Test
	void deleteVaccine_shouldRemoveVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		vaccineIoOperation.deleteVaccine(foundVaccine);
		assertThat(vaccineIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestVaccine(false);
		boolean result = vaccineIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void findVaccine_shouldReturnVaccine_whenCodeExists() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine result = vaccineIoOperation.findVaccine(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	void findVaccine_shouldThrow_whenCodeIsNull() throws Exception {
		assertThatThrownBy(() -> vaccineIoOperation.findVaccine(null))
			.isInstanceOf(OHServiceException.class);
	}

	private String setupTestVaccine(boolean usingSet) throws OHException {
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, usingSet);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		return vaccine.getCode();
	}

	private void checkVaccineIntoDb(String code) throws OHServiceException {
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		testVaccine.check(foundVaccine);
	}

}
