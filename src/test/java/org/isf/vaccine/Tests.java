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
package org.isf.vaccine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vaccine.service.VaccineIoOperations;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

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
	void testVaccineGets() throws Exception {
		String code = setupTestVaccine(false);
		checkVaccineIntoDb(code);
	}

	@Test
	void testVaccineSets() throws Exception {
		String code = setupTestVaccine(true);
		checkVaccineIntoDb(code);
	}

	@Test
	void testIoGetVaccineShouldFindByTypeCode() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		List<Vaccine> vaccines = vaccineIoOperation.getVaccine(foundVaccine.getVaccineType().getCode());
		assertThat(vaccines.get(vaccines.size() - 1).getDescription()).isEqualTo(foundVaccine.getDescription());
	}

	@Test
	void testIoGetVaccineShouldFindAllVaccinesWhenNoCodeProvided() throws Exception {
		// given:
		setupTestVaccine(false);
		
		// when:
		List<Vaccine> vaccines = vaccineIoOperation.getVaccine(null);

		// then:
		assertThat(vaccines).isNotEmpty();
	}

	@Test
	void testIoUpdateVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		foundVaccine.setDescription("Update");
		Vaccine updatedVaccine = vaccineIoOperation.updateVaccine(foundVaccine);
		assertThat(updatedVaccine.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoNewVaccine() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		Vaccine newVaccine = vaccineIoOperation.newVaccine(vaccine);
		assertThat(newVaccine.getCode()).isEqualTo("Z");
		checkVaccineIntoDb(newVaccine.getCode());
	}

	@Test
	void testIoDeleteVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		vaccineIoOperation.deleteVaccine(foundVaccine);
		assertThat(vaccineIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestVaccine(false);
		boolean result = vaccineIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testIoFindVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine result = vaccineIoOperation.findVaccine(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	void testIoFindVaccineNull() throws Exception {
		assertThatThrownBy(() -> vaccineIoOperation.findVaccine(null))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void testMgrGetVaccineShouldFindByTypeCode() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineBrowserManager.findVaccine(code);
		List<Vaccine> vaccines = vaccineBrowserManager.getVaccine(foundVaccine.getVaccineType().getCode());
		assertThat(vaccines.get(vaccines.size() - 1).getDescription()).isEqualTo(foundVaccine.getDescription());
	}

	@Test
	void testMgrGetVaccineShouldFindAllVaccinesWhenNoCodeProvided() throws Exception {
		setupTestVaccine(false);
		List<Vaccine> vaccines = vaccineBrowserManager.getVaccine();
		assertThat(vaccines).isNotEmpty();
	}

	@Test
	void testMgrUpdateVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineBrowserManager.findVaccine(code);
		foundVaccine.setDescription("Update");
		Vaccine updatedVaccine = vaccineBrowserManager.updateVaccine(foundVaccine);
		assertThat(updatedVaccine.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewVaccine() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		Vaccine newVaccine = vaccineBrowserManager.newVaccine(vaccine);
		checkVaccineIntoDb(newVaccine.getCode());
	}

	@Test
	void testMgrDeleteVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineBrowserManager.findVaccine(code);
		vaccineBrowserManager.deleteVaccine(foundVaccine);
		assertThat(vaccineBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestVaccine(false);
		assertThat(vaccineBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void testMgrFindVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(vaccine).isNotNull();
		assertThat(vaccine.getCode()).isEqualTo(code);
	}

	@Test
	void testMgrFindVaccineNull() throws Exception {
		assertThatThrownBy(() -> vaccineBrowserManager.findVaccine(null))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void testMgrValidationCodeEmpty() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		vaccine.setCode("");
		assertThatThrownBy(() -> vaccineBrowserManager.newVaccine(vaccine))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationCodeTooLong() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		vaccine.setCode("thisIsACodeThatIsTooLong");
		assertThatThrownBy(() -> vaccineBrowserManager.newVaccine(vaccine))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationDescriptionEmpty() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		vaccine.setDescription("");
		assertThatThrownBy(() -> vaccineBrowserManager.newVaccine(vaccine))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationCodeExists() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThatThrownBy(() -> vaccineBrowserManager.newVaccine(vaccine))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testVaccineToString() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(vaccine).hasToString(vaccine.getDescription());
	}

	@Test
	void testVaccinePrint() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(vaccine.print()).isEqualTo("Vaccine code =." + vaccine.getCode() + ". description =." + vaccine.getDescription() + '.');
	}

	@Test
	void testVaccineEquals() throws Exception {
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
	void testVaccineMiscGetSet() throws Exception {
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