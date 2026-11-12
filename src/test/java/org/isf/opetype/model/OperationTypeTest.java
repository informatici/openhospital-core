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
package org.isf.opetype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OperationTypeTest extends OHCoreTestCase {

	private static TestOperationType testOperationType;

	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testOperationType = new TestOperationType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestOperationType(false);
		checkOperationTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestOperationType(true);
		checkOperationTypeIntoDb(code);
	}

	@Test
	void equals_shouldCompareCodeAndDescription() throws Exception {
		OperationType operationType = new OperationType("Z", "description");

		assertThat(operationType)
				.isEqualTo(operationType)
				.isNotNull()
				.isNotEqualTo("someString");

		OperationType operationType1 = new OperationType("Z", "description");
		assertThat(operationType).isEqualTo(operationType1);

		operationType1.setCode("A");
		assertThat(operationType).isNotEqualTo(operationType1);

		operationType1.setCode(operationType.getCode());
		operationType1.setDescription("some other description");
		assertThat(operationType).isNotEqualTo(operationType1);
	}

	@Test
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		OperationType operationType = new OperationType("Z", "description");
		int hashCode = operationType.hashCode();
		// used computed value
		assertThat(operationType.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		OperationType operationType = new OperationType("Z", "description");
		assertThat(operationType).hasToString("description");
	}

	private String setupTestOperationType(boolean usingSet) throws OHException {
		OperationType operationType = testOperationType.setup(usingSet);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		return operationType.getCode();
	}

	private void checkOperationTypeIntoDb(String code) throws OHException {
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOperationType).isNotNull();
		testOperationType.check(foundOperationType);
	}

}
