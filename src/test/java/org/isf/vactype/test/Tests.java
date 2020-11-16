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
package org.isf.vactype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VacTypeIoOperation;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestVaccineType testVaccineType;

	@Autowired
	VacTypeIoOperation vaccineTypeIoOperation;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testVaccineType = new TestVaccineType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testVaccineTypeGets() throws Exception {
		String code = _setupTestVaccineType(false);
		_checkVaccineTypeIntoDb(code);
	}

	@Test
	public void testVaccineTypeSets() throws Exception {
		String code = _setupTestVaccineType(true);
		_checkVaccineTypeIntoDb(code);
	}

	@Test
	public void testIoGetVaccineType() throws Exception {
		String code = _setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		ArrayList<VaccineType> vaccineTypes = vaccineTypeIoOperation.getVaccineType();
		assertThat(vaccineTypes.get(vaccineTypes.size() - 1).getDescription()).isEqualTo(foundVaccineType.getDescription());
	}

	@Test
	public void testIoUpdateVaccineType() throws Exception {
		String code = _setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		foundVaccineType.setDescription("Update");
		boolean result = vaccineTypeIoOperation.updateVaccineType(foundVaccineType);
		assertThat(result).isTrue();
		VaccineType updateVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(updateVaccineType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewVaccineType() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		boolean result = vaccineTypeIoOperation.newVaccineType(vaccineType);
		assertThat(result).isTrue();
		_checkVaccineTypeIntoDb(vaccineType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestVaccineType(false);
		boolean result = vaccineTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteVaccineType() throws Exception {
		String code = _setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		boolean result = vaccineTypeIoOperation.deleteVaccineType(foundVaccineType);
		assertThat(result).isTrue();
		result = vaccineTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testFindVaccineType() throws Exception {
		String code = _setupTestVaccineType(false);
		VaccineType result = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	private String _setupTestVaccineType(boolean usingSet) throws OHException {
		VaccineType vaccineType = testVaccineType.setup(usingSet);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		return vaccineType.getCode();
	}

	private void _checkVaccineTypeIntoDb(String code) throws OHServiceException {
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		testVaccineType.check(foundVaccineType);
	}
}