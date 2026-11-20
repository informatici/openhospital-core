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
package org.isf.operation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.operation.TestOperation;
import org.isf.operation.enums.OperationTarget;
import org.isf.operation.model.Operation;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OperationIoOperationsTest extends OHCoreTestCase {

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
	void getOperationByTypeDescription_shouldReturnOperationsOfType_whenTypeDescriptionIsGiven() throws Exception {
		// given:
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);

		// when:
		List<Operation> operations = operationIoOperations.getOperationByTypeDescription(foundOperation.getType().getDescription());

		// then:
		assertThat(operations).isNotEmpty();
		assertThat(operations.get(0).getType().getDescription()).isEqualTo(foundOperation.getType().getDescription());
	}

	@Test
	void getOperationByTypeDescription_shouldReturnAllOperations_whenTypeDescriptionIsNull() throws Exception {
		// given:
		String code = setupTestOperation(true);

		// when:
		List<Operation> operations = operationIoOperations.getOperationByTypeDescription(null);

		// then:
		assertThat(operations).isNotEmpty();
	}

	@Test
	void getOperationOpd_shouldReturnOperation_whenOpeForIsOpdAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.opd_admission);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationOpd()).isNotEmpty();
	}

	@Test
	void getOperationOpd_shouldReturnOperation_whenOpeForIsOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.opd);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationOpd()).isNotEmpty();
	}

	@Test
	void getOperationOpd_shouldReturnEmpty_whenOpeForIsAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.admission);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationOpd()).isEmpty();
	}

	@Test
	void getOperationAdm_shouldReturnOperation_whenOpeForIsOpdAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.opd_admission);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationAdm()).isNotEmpty();
	}

	@Test
	void getOperationAdm_shouldReturnOperation_whenOpeForIsAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.admission);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationAdm()).isNotEmpty();
	}

	@Test
	void getOperationAdm_shouldReturnEmpty_whenOpeForIsOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.opd);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationAdm()).isEmpty();
	}

	@Test
	void newOperation_shouldSaveOperation() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		Operation operation = testOperation.setup(operationType, true);
		assertThat(operationIoOperations.newOperation(operation)).isNotNull();
		checkOperationIntoDb(operation.getCode());
	}

	@Test
	void updateOperation_shouldSaveNewDescriptionAndIncrementLock() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		int lock = foundOperation.getLock();
		foundOperation.setDescription("Update");
		assertThat(operationIoOperations.updateOperation(foundOperation)).isNotNull();
		Operation updateOperation = operationIoOperations.findByCode(code);
		assertThat(updateOperation).isNotNull();
		assertThat(updateOperation.getDescription()).isEqualTo("Update");
		assertThat(updateOperation.getLock().intValue()).isEqualTo(lock + 1);
	}

	@Test
	void deleteOperation_shouldRemoveOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		operationIoOperations.deleteOperation(foundOperation);
		assertThat(operationIoOperations.isCodePresent(code)).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestOperation(false);
		assertThat(operationIoOperations.isCodePresent(code)).isTrue();
	}

	@Test
	void isDescriptionPresent_shouldReturnTrue_whenDescriptionExists() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		assertThat(operationIoOperations.isDescriptionPresent(foundOperation.getDescription(), foundOperation.getType().getCode())).isTrue();
	}

	@Test
	void isDescriptionPresent_shouldReturnFalse_whenDescriptionDoesNotExist() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		assertThat(operationIoOperations.isDescriptionPresent("someOtherDescription", foundOperation.getType().getCode())).isFalse();
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
