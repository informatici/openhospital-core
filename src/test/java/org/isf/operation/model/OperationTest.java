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
package org.isf.operation.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.operation.TestOperation;
import org.isf.operation.enums.OperationTarget;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.service.OperationIoOperations;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OperationTest extends OHCoreTestCase {

	private static TestOperation testOperation;
	private static TestOperationType testOperationType;

	@Autowired
	OperationIoOperations operationIoOperations;
	@Autowired
	OperationIoOperationRepository operationIoOperationRepository;
	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testOperation = new TestOperation();
		testOperationType = new TestOperationType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestOperation(false);
		checkOperationIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestOperation(true);
		checkOperationIntoDb(code);
	}

	@Test
	void setters_shouldUpdateMajorLockAndOpeFor() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		operation.setMajor(-9);
		assertThat(operation.getMajor()).isEqualTo(-9);

		operation.setLock(-1);
		assertThat(operation.getLock()).isEqualTo(-1);

		operation.setOpeFor(OperationTarget.admission);
		assertThat(operation.getOpeFor()).isEqualTo(OperationTarget.admission);
	}

	@Test
	void equals_shouldCompareCodeDescriptionTypeAndMajor() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		assertThat(operation)
			.isEqualTo(operation)
			.isNotNull()
			.isNotEqualTo("a string");

		OperationType operationType1 = testOperationType.setup(false);
		Operation operation1 = testOperation.setup(operationType, true);

		assertThat(operation).isEqualTo(operation1);

		operation1.setCode("some new code");
		assertThat(operation).isNotEqualTo(operation1);

		operation1.setCode(operation.getCode());
		operation.setDescription("some new description");
		assertThat(operation).isNotEqualTo(operation1);

		operation1.setDescription(operation.getDescription());
		operationType1.setCode("new code");
		operation1.setType(operationType1);
		assertThat(operation).isNotEqualTo(operation1);

		operation1.setType(operation.getType());
		operation1.setMajor(-1);
		assertThat(operation).isNotEqualTo(operation1);

		operation1.setMajor(operation.getMajor());
		assertThat(operation).isEqualTo(operation1);
	}

	@Test
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		int hashCode = operation.hashCode();
		// get stored value
		assertThat(operation.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		assertThat(operation).hasToString(operation.getDescription());
	}

	private String setupTestOperation(boolean usingSet) throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, usingSet);
		operation.setOpeFor(OperationTarget.opd_admission);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		return operation.getCode();
	}

	private void checkOperationIntoDb(String code) throws Exception {
		Operation foundOperation = operationIoOperations.findByCode(code);
		testOperation.check(foundOperation);
	}

}
