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
package org.isf.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.isf.OHCoreTestCase;
import org.isf.hospital.TestHospital;
import org.isf.hospital.model.Hospital;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HospitalIoOperationsTest extends OHCoreTestCase {

	private static TestHospital testHospital;

	@Autowired
	HospitalIoOperations hospitalIoOperation;
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
	void getHospital_shouldReturnSavedHospital() throws Exception {
		String code = setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).orElse(null);
		assertThat(foundHospital).isNotNull();
		Hospital getHospital = hospitalIoOperation.getHospital();
		assertThat(getHospital).isEqualTo(foundHospital);
	}

	@Test
	void updateHospital_shouldSaveNewDescription() throws Exception {
		String code = setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).orElse(null);
		assertThat(foundHospital).isNotNull();
		foundHospital.setDescription("Update");
		Hospital updatedHospital = hospitalIoOperation.updateHospital(foundHospital);
		assertThat(updatedHospital.getDescription()).isEqualTo("Update");
	}

	@Test
	void getHospitalCurrencyCod_shouldReturnSavedCode() throws Exception {
		String code = setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).orElse(null);
		assertThat(foundHospital).isNotNull();
		String cod = hospitalIoOperation.getHospitalCurrencyCod();
		assertThat(foundHospital.getCurrencyCod()).isEqualTo(cod);

		foundHospital.setCurrencyCod(null);
		Hospital updatedHospital = hospitalIoOperation.updateHospital(foundHospital);
		assertThat(updatedHospital.getCurrencyCod()).isNull();
	}

	@Test
	void sanitize_shouldEscapeSingleQuotes() throws Exception {
		Method method = hospitalIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(hospitalIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(hospitalIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(hospitalIoOperation, "abcdef")).isEqualTo("abcdef");
	}

	private String setupTestHospital(boolean usingSet) throws OHException {
		Hospital hospital = testHospital.setup(usingSet);
		hospitalIoOperationRepository.saveAndFlush(hospital);
		return hospital.getCode();
	}

}