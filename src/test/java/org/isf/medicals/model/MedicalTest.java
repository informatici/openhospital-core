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
package org.isf.medicals.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalTest extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;

	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		int code = setupTestMedical(false);
		checkMedicalIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int code = setupTestMedical(true);
		checkMedicalIntoDb(code);
	}

	@Test
	void constructor_shouldSetCode_whenOnlyCodeGiven() throws Exception {
		Medical medical = new Medical(-1);
		assertThat(medical).isNotNull();
		assertThat(medical.getCode()).isEqualTo(-1);
	}

	@Test
	void setters_shouldUpdateQuantitiesCodeAndLock() throws Exception {
		int medicalCode = setupTestMedical(false);
		Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
		assertThat(medical).isNotNull();
		medical.setInqty(50);
		medical.setOutqty(25);
		assertThat(medical.getTotalQuantity()).isEqualTo(50 - 25);

		medical.setCode(-1);
		assertThat(medical.getCode()).isEqualTo(-1);

		medical.setLock(-99);
		assertThat(medical.getLock()).isEqualTo(-99);
	}

	@Test
	void equals_shouldUseMedicalFields() throws Exception {
		MedicalType medicalType = new MedicalType("code", "description");
		Medical medical = new Medical(1, medicalType, "TP1", "TestDescription1", 1, 3, 5, 4);

		MedicalType medicalType2 = new MedicalType("code2", "description2");
		Medical medical2 = new Medical(2, medicalType2, "TP2", "TestDescription2", 1, 3, 5, 4);

		MedicalType medicalType3 = new MedicalType("code3", "description3");
		Medical medical3 = new Medical(3, medicalType3, "TP3", "TestDescription2", 1, 3, 5, 4);

		assertThat(medical)
						.isEqualTo(medical)
						.isNotEqualTo("someString")
						.isNotEqualTo(medical2);

		medical2.setProdCode(null);
		medical3.setProdCode(null);
		assertThat(medical2).isNotEqualTo(medical3);
		assertThat(medical3).isNotEqualTo(medical2);
	}

	@Test
	void compareTo_shouldCompareDescriptions() throws Exception {
		MedicalType medicalType = new MedicalType("code", "description");
		Medical medical = new Medical(1, medicalType, "TP1", "TestDescription1", 1, 3, 5, 4);

		MedicalType medicalType2 = new MedicalType("code2", "description2");
		Medical medical2 = new Medical(2, medicalType2, "TP2", "TestDescription2", 1, 3, 5, 4);

		assertThat(medical.compareTo(medical)).isZero();
		assertThat(medical.compareTo(medical2)).isEqualTo(-1);
	}

	@Test
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		MedicalType medicalType = new MedicalType("code", "description");
		Medical medical = new Medical(1, medicalType, "TP1", "TestDescription1", 1, 3, 5, 4);

		// compute hashCode
		int hashCode = medical.hashCode();
		assertThat(hashCode).isPositive();
		// use computed value
		assertThat(medical.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void clone_shouldReturnEqualMedical() throws Exception {
		MedicalType medicalType = new MedicalType("code", "description");
		Medical medical = new Medical(1, medicalType, "TP1", "TestDescription1", 2, 3, 4, 5);

		assertThat(medical.clone()).isEqualTo(medical);
	}

	private int setupTestMedical(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		return medical.getCode();
	}

	private void checkMedicalIntoDb(int code) throws OHException {
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		testMedical.check(foundMedical);
	}

}
