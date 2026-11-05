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
package org.isf.medstockmovtype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperation;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MovementTypeTest extends OHCoreTestCase {

	private static TestMovementType testMovementType;

	@Autowired
	MedicalDsrStockMovementTypeIoOperation medicalDsrStockMovementTypeIoOperation;
	@Autowired
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMovementType = new TestMovementType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestMovementType(false);
		checkMovementTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestMovementType(true);
		checkMovementTypeIntoDb(code);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		MovementType movementType = new MovementType("ZZABCD", "TestDescription", "+", "operational");
		assertThat(movementType).hasToString("TestDescription");
	}

	@Test
	void equals_shouldCompareCodes() throws Exception {
		MovementType movementType1 = new MovementType("ZZABCD", "TestDescription", "+", "operational");
		MovementType movementType2 = new MovementType("ABCDZZ", "TestDescription", "+", "operational");
		MovementType movementType3 = new MovementType("ZZABCD", "AnotherDescription", "+", "operational");
		MovementType movementType4 = new MovementType("ZZABCD", "TestDescription", "++", "operational");

		assertThat(movementType1).isEqualTo(movementType1);
		assertThat(movementType1)
						.isNotEqualTo("someString")
						.isNotEqualTo(movementType2)
						.isEqualTo(movementType3)
						.isEqualTo(movementType4);
	}

	@Test
	void hashCode_shouldReturnSameValue_whenCalledTwice() {
		MovementType movementType = new MovementType("ZZABCD", "TestDescription", "+", "operational");
		// generate hashCode
		int hashCode = movementType.hashCode();
		assertThat(hashCode).isNotZero();
		// used computed value
		assertThat(movementType.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestMovementType(boolean usingSet) throws OHException {
		MovementType movementType = testMovementType.setup(usingSet);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		return movementType.getCode();
	}

	private void checkMovementTypeIntoDb(String code) throws OHException {
		MovementType foundMovementType = medicalDsrStockMovementTypeIoOperation.findOneByCode(code);
		testMovementType.check(foundMovementType);
	}

}
