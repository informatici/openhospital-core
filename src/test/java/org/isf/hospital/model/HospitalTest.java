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
package org.isf.hospital.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.hospital.TestHospital;
import org.isf.hospital.service.HospitalIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HospitalTest extends OHCoreTestCase {

	private static TestHospital testHospital;

	@Autowired
	HospitalIoOperationRepository hospitalIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testHospital = new TestHospital();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestHospital(false);
		checkHospitalIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestHospital(true);
		checkHospitalIntoDb(code);
	}

	@Test
	void setLock_shouldUpdateLock() throws Exception {
		String code = setupTestHospital(false);
		Hospital hospital = hospitalIoOperationRepository.findById(code).orElse(null);
		assertThat(hospital).isNotNull();

		hospital.getLock();
		hospital.setLock(-1);
		assertThat(hospital.getLock()).isEqualTo(-1);
	}

	@Test
	void hashCodeToString_shouldUseHospitalFields() throws Exception {
		String code = setupTestHospital(false);
		Hospital hospital = hospitalIoOperationRepository.findById(code).orElse(null);
		assertThat(hospital).isNotNull();

		assertThat(hospital.hashCode()).isPositive();

		assertThat(hospital).hasToString(hospital.getDescription());
	}

	private String setupTestHospital(boolean usingSet) throws OHException {
		Hospital hospital = testHospital.setup(usingSet);
		hospitalIoOperationRepository.saveAndFlush(hospital);
		return hospital.getCode();
	}

	private void checkHospitalIntoDb(String code) throws OHException {
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).orElse(null);
		assertThat(foundHospital).isNotNull();
		testHospital.check(foundHospital);
	}

}
