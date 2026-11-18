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
package org.isf.vactype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.service.VacTypeIoOperation;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VaccineTypeTest extends OHCoreTestCase {

	private static TestVaccineType testVaccineType;

	@Autowired
	VacTypeIoOperation vaccineTypeIoOperation;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;
	@Autowired
	VaccineTypeBrowserManager vaccineTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testVaccineType = new TestVaccineType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestVaccineType(false);
		checkVaccineTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestVaccineType(true);
		checkVaccineTypeIntoDb(code);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(vaccineType).hasToString(vaccineType.getDescription());
	}

	@Test
	void print_shouldUseCodeAndDescription() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(vaccineType).isNotNull();
		assertThat(vaccineType.print()).isEqualTo("vaccineType code=." + vaccineType.getCode() + ". description=." + vaccineType.getDescription() + '.');
	}

	@Test
	void equals_shouldUseCodeAndDescription() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);

		assertThat(vaccineType)
				.isEqualTo(vaccineType)
				.isNotNull()
				.isNotEqualTo("someStringValue");

		VaccineType vaccineType2 = new VaccineType("A", "adescription");
		assertThat(vaccineType).isNotEqualTo(vaccineType2);

		vaccineType2.setCode(vaccineType.getCode());
		assertThat(vaccineType).isNotEqualTo(vaccineType2);

		vaccineType2.setDescription(vaccineType.getDescription());
		assertThat(vaccineType).isEqualTo(vaccineType2);
	}

	@Test
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		// compute value
		int hashCode = vaccineType.hashCode();
		// compare with stored value
		assertThat(vaccineType.hashCode()).isEqualTo(hashCode);
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
