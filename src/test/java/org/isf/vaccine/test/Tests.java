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
package org.isf.vaccine.test;

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
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.isf.vactype.test.TestVaccineType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

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

	@BeforeClass
	public static void setUpClass() {
		testVaccine = new TestVaccine();
		testVaccineType = new TestVaccineType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testVaccineGets() throws Exception {
		String code = setupTestVaccine(false);
		checkVaccineIntoDb(code);
	}

	@Test
	public void testVaccineSets() throws Exception {
		String code = setupTestVaccine(true);
		checkVaccineIntoDb(code);
	}

	@Test
	public void testIoGetVaccineShouldFindByTypeCode() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		List<Vaccine> vaccines = vaccineIoOperation.getVaccine(foundVaccine.getVaccineType().getCode());
		assertThat(vaccines.get(vaccines.size() - 1).getDescription()).isEqualTo(foundVaccine.getDescription());
	}

	@Test
	public void testIoGetVaccineShouldFindAllVaccinesWhenNoCodeProvided() throws Exception {
		// given:
		setupTestVaccine(false);
		
		// when:
		List<Vaccine> vaccines = vaccineIoOperation.getVaccine(null);

		// then:
		assertThat(vaccines).isNotEmpty();
	}

	@Test
	public void testIoUpdateVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		foundVaccine.setDescription("Update");
		Vaccine result = vaccineIoOperation.updateVaccine(foundVaccine);
		assertThat(result.getDescription()).isEqualTo("Update");
		Vaccine updateVaccine = vaccineIoOperation.findVaccine(code);
		assertThat(updateVaccine.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewVaccine() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		Vaccine result = vaccineIoOperation.newVaccine(vaccine);
		assertThat(result.getCode()).isEqualTo("Z");
		checkVaccineIntoDb(vaccine.getCode());
	}

	@Test
	public void testIoDeleteVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		boolean result = vaccineIoOperation.deleteVaccine(foundVaccine);
		assertThat(result).isTrue();
		result = vaccineIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestVaccine(false);
		boolean result = vaccineIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoFindVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine result = vaccineIoOperation.findVaccine(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	public void testIoFindVaccineNull() throws Exception {
		assertThatThrownBy(() -> vaccineIoOperation.findVaccine(null))
				.isInstanceOf(RuntimeException.class);
	}

	@Test
	public void testMgrGetVaccineShouldFindByTypeCode() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineBrowserManager.findVaccine(code);
		List<Vaccine> vaccines = vaccineBrowserManager.getVaccine(foundVaccine.getVaccineType().getCode());
		assertThat(vaccines.get(vaccines.size() - 1).getDescription()).isEqualTo(foundVaccine.getDescription());
	}

	@Test
	public void testMgrGetVaccineShouldFindAllVaccinesWhenNoCodeProvided() throws Exception {
		setupTestVaccine(false);
		List<Vaccine> vaccines = vaccineBrowserManager.getVaccine();
		assertThat(vaccines).isNotEmpty();
	}

	@Test
	public void testMgrUpdateVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineBrowserManager.findVaccine(code);
		foundVaccine.setDescription("Update");
		assertThat(vaccineBrowserManager.updateVaccine(foundVaccine)).isNotNull();
		Vaccine updateVaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(updateVaccine.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrNewVaccine() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		assertThat(vaccineBrowserManager.newVaccine(vaccine)).isNotNull();
		checkVaccineIntoDb(vaccine.getCode());
	}

	@Test
	public void testMgrDeleteVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine foundVaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(vaccineBrowserManager.deleteVaccine(foundVaccine)).isTrue();
		assertThat(vaccineBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestVaccine(false);
		assertThat(vaccineBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	public void testMgrFindVaccine() throws Exception {
		String code = setupTestVaccine(false);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(vaccine).isNotNull();
		assertThat(vaccine.getCode()).isEqualTo(code);
	}

	@Test
	public void testMgrFindVaccineNull() throws Exception {
		assertThatThrownBy(() -> vaccineBrowserManager.findVaccine(null))
				.isInstanceOf(RuntimeException.class);
	}

	@Test
	public void testMgrValidationCodeEmpty() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		vaccine.setCode("");
		assertThatThrownBy(() -> vaccineBrowserManager.newVaccine(vaccine))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationCodeTooLong() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		vaccine.setCode("thisIsACodeThatIsTooLong");
		assertThatThrownBy(() -> vaccineBrowserManager.newVaccine(vaccine))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDescriptionEmpty() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		vaccine.setDescription("");
		assertThatThrownBy(() -> vaccineBrowserManager.newVaccine(vaccine))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationCodeExists() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThatThrownBy(() -> vaccineBrowserManager.newVaccine(vaccine))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testVaccineToString() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(vaccine).hasToString(vaccine.getDescription());
	}

	@Test
	public void testVaccinePrint() throws Exception {
		String code = setupTestVaccine(true);
		Vaccine vaccine = vaccineBrowserManager.findVaccine(code);
		assertThat(vaccine.print()).isEqualTo("Vaccine code =." + vaccine.getCode() + ". description =." + vaccine.getDescription() + '.');
	}

	@Test
	public void testVaccineEquals() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		Vaccine vaccine = new Vaccine("aCode", "aDescription", vaccineType);

		assertThat(vaccine.equals(vaccine)).isTrue();
		assertThat(vaccine)
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
	public void testVaccineMiscGetSet() throws Exception {
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