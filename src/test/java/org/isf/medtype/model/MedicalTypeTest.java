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
package org.isf.medtype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalTypeTest extends OHCoreTestCase {

	private static TestMedicalType testMedicalType;

	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMedicalType = new TestMedicalType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestMedicalType(false);
		checkMedicalTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestMedicalType(true);
		checkMedicalTypeIntoDb(code);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType)
				.isNotNull()
				.hasToString(foundMedicalType.getDescription());
	}

	@Test
	void equals_shouldCompareCodeAndDescription() throws Exception {
		MedicalType medicalType1 = new MedicalType("Z", "description");
		MedicalType medicalType2 = new MedicalType("A", "description");
		MedicalType medicalType3 = new MedicalType("Z", "otherDescription");

		assertThat(medicalType1)
				.isEqualTo(medicalType1)
				.isNotEqualTo("someString")
				.isNotEqualTo(medicalType2)
				.isNotEqualTo(medicalType3);
	}

	@Test
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		MedicalType medicalType = new MedicalType("Z", "description");
		// compute value
		int hashCode = medicalType.hashCode();
		assertThat(hashCode).isPositive();
		// used already computed value
		assertThat(medicalType.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestMedicalType(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		return medicalType.getCode();
	}

	private void checkMedicalTypeIntoDb(String code) throws OHException {
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalType).isNotNull();
		testMedicalType.check(foundMedicalType);
	}

}
