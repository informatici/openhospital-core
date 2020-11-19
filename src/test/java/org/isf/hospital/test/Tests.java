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
package org.isf.hospital.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.hospital.model.Hospital;
import org.isf.hospital.service.HospitalIoOperationRepository;
import org.isf.hospital.service.HospitalIoOperations;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestHospital testHospital;

	@Autowired
	HospitalIoOperations hospitalIoOperation;
	@Autowired
	HospitalIoOperationRepository hospitalIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testHospital = new TestHospital();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testHospitalGets() throws Exception {
		String code = _setupTestHospital(false);
		_checkHospitalIntoDb(code);
	}

	@Test
	public void testHospitalSets() throws Exception {
		String code = _setupTestHospital(true);
		_checkHospitalIntoDb(code);
	}

	@Test
	public void testIoUpdateHospital() throws Exception {
		String code = _setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findOne(code);
		foundHospital.setDescription("Update");
		boolean result = hospitalIoOperation.updateHospital(foundHospital);
		assertThat(result).isTrue();
		Hospital updateHospital = hospitalIoOperationRepository.findOne(code);
		assertThat(updateHospital.getDescription()).isEqualTo("Update");
	}

	private String _setupTestHospital(boolean usingSet) throws OHException {
		Hospital hospital = testHospital.setup(usingSet);
		hospitalIoOperationRepository.saveAndFlush(hospital);
		return hospital.getCode();
	}

	private void _checkHospitalIntoDb(String code) throws OHException {
		Hospital foundHospital = hospitalIoOperationRepository.findOne(code);
		testHospital.check(foundHospital);
	}
}