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
package org.isf.vaccine.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.TestVaccine;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vaccine.service.VaccineIoOperations;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VaccineTest extends OHCoreTestCase {

	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;

	@Autowired
	VaccineIoOperations vaccineIoOperation;
	@Autowired
	VaccineIoOperationRepository vaccineIoOperationRepository;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;
	@Autowired
	VaccineBrowserManager vaccineBrowserManager;

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
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestVaccine(false);
		checkVaccineIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestVaccine(true);
		checkVaccineIntoDb(code);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(vaccine).hasToString(vaccine.getDescription());
	}

	@Test
	void print_shouldUseCodeAndDescription() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(vaccine.print()).isEqualTo("Vaccine code =." + vaccine.getCode() + ". description =." + vaccine.getDescription() + '.');
	}

	@Test
	void equals_shouldUseCodeDescriptionAndVaccineType() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		Vaccine vaccine = new Vaccine("aCode", "aDescription", vaccineType);

		assertThat(vaccine)
				.isEqualTo(vaccine)
				.isNotNull()
				.isNotEqualTo("someStringValue");

		VaccineType vaccineType2 = new VaccineType("A", "adescription");
		Vaccine vaccine2 = new Vaccine("bCode", "bDescription", vaccineType2);
		assertThat(vaccine).isNotEqualTo(vaccine2);

		vaccine2.setCode(vaccine.getCode());
		assertThat(vaccine).isNotEqualTo(vaccine2);

		vaccine2.setDescription(vaccine.getDescription());
		assertThat(vaccine).isNotEqualTo(vaccine2);

		vaccine2.setVaccineType(vaccineType);
		assertThat(vaccine).isEqualTo(vaccine2);
	}

	@Test
	void setLock_shouldUpdateLock() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		Vaccine vaccine = new Vaccine("aCode", "aDescription", vaccineType);

		assertThat(vaccine.getLock()).isNull();
		vaccine.setLock(-1);
		assertThat(vaccine.getLock()).isEqualTo(-1);
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
