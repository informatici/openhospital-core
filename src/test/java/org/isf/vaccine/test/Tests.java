/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
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
		String code =_setupTestVaccine(false);
		_checkVaccineIntoDb(code);
	}

	@Test
	public void testVaccineSets() throws Exception {
		String code = _setupTestVaccine(true);
		_checkVaccineIntoDb(code);
	}

	@Test
	public void testIoGetVaccineShouldFindByTypeCode() throws Exception {
		String code = _setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		ArrayList<Vaccine> vaccines = vaccineIoOperation.getVaccine(foundVaccine.getVaccineType().getCode());
		assertThat(vaccines.get(vaccines.size() - 1).getDescription()).isEqualTo(foundVaccine.getDescription());
	}

	@Test
	public void testIoGetVaccineShouldFindAllVaccinesWhenNoCodeProvided() throws Exception {
		// given:
		String code = _setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);

		// when:
		ArrayList<Vaccine> vaccines = vaccineIoOperation.getVaccine(null);

		// then:
		assertThat(vaccines).isNotEmpty();
	}

	@Test
	public void testIoUpdateVaccine() throws Exception {
		String code = _setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		foundVaccine.setDescription("Update");
		boolean result = vaccineIoOperation.updateVaccine(foundVaccine);
		assertThat(result).isTrue();
		Vaccine updateVaccine = vaccineIoOperation.findVaccine(code);
		assertThat(updateVaccine.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewVaccine() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		boolean result = vaccineIoOperation.newVaccine(vaccine);
		assertThat(result).isTrue();
		_checkVaccineIntoDb(vaccine.getCode());
	}

	@Test
	public void testIoDeleteVaccine() throws Exception {
		String code = _setupTestVaccine(false);
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		boolean result = vaccineIoOperation.deleteVaccine(foundVaccine);
		assertThat(result).isTrue();
		result = vaccineIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestVaccine(false);
		boolean result = vaccineIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testFindVaccine() throws Exception {
		String code = _setupTestVaccine(false);
		Vaccine result = vaccineIoOperation.findVaccine(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	private String _setupTestVaccine(boolean usingSet) throws OHException {
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, usingSet);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		return vaccine.getCode();
	}

	private void _checkVaccineIntoDb(String code) throws OHServiceException {
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		testVaccine.check(foundVaccine);
	}
}