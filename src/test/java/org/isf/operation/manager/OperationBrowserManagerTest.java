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
package org.isf.operation.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.operation.TestOperation;
import org.isf.operation.enums.OperationTarget;
import org.isf.operation.model.Operation;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.service.OperationIoOperations;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OperationBrowserManagerTest extends OHCoreTestCase {

	private static TestOperation testOperation;
	private static TestOperationType testOperationType;

	@Autowired
	OperationIoOperations operationIoOperations;
	@Autowired
	OperationIoOperationRepository operationIoOperationRepository;
	@Autowired
	OperationBrowserManager operationBrowserManager;
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
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		List<Operation> operations = operationBrowserManager.getOperationByTypeDescription(foundOperation.getType().getDescription());
		assertThat(operations).isNotEmpty();
		assertThat(operations.get(0).getType().getDescription()).isEqualTo(foundOperation.getType().getDescription());
	}

	@Test
	void getOperationByTypeDescription_shouldReturnAllOperations_whenTypeDescriptionIsNull() throws Exception {
		setupTestOperation(true);
		List<Operation> operations = operationBrowserManager.getOperationByTypeDescription(null);
		assertThat(operations).isNotEmpty();
	}

	@Test
	void getOperation_shouldReturnAllOperations() throws Exception {
		setupTestOperation(true);
		List<Operation> operations = operationBrowserManager.getOperation();
		assertThat(operations).isNotEmpty();
	}

	@Test
	void getOperationPageable_shouldReturnPageWithSavedOperation() throws Exception {
		// given:
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);

		// when:
		PagedResponse<Operation> operations = operationBrowserManager.getOperationPageable(0, 10);

		// then:
		assertThat(operations.getData().get(0).getType().getDescription()).isEqualTo(foundOperation.getType().getDescription());
	}

	@Test
	void getOperationOpd_shouldReturnOperation_whenOpeForIsOpdAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.opd_admission);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationOpd()).isNotEmpty();
	}

	@Test
	void getOperationOpd_shouldReturnOperation_whenOpeForIsOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.opd);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationOpd()).isNotEmpty();
	}

	@Test
	void getOperationOpd_shouldReturnEmpty_whenOpeForIsAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.admission);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationOpd()).isEmpty();
	}

	@Test
	void getOperationAdm_shouldReturnOperation_whenOpeForIsOpdAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.opd_admission);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationAdm()).isNotEmpty();
	}

	@Test
	void getOperationAdm_shouldReturnOperation_whenOpeForIsAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.admission);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationAdm()).isNotEmpty();
	}

	@Test
	void getOperationAdm_shouldReturnEmpty_whenOpeForIsOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor(OperationTarget.opd);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationAdm()).isEmpty();
	}

	@Test
	void newOperation_shouldSaveOperation() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		Operation operation = testOperation.setup(operationType, true);
		assertThat(operationBrowserManager.newOperation(operation)).isNotNull();
		checkOperationIntoDb(operation.getCode());
	}

	@Test
	void newOperation_shouldThrow_whenCodeAndDescriptionAreEmpty() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		operation.setCode("");
		operation.setDescription("");
		assertThatThrownBy(() -> operationBrowserManager.newOperation(operation))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newOperation_shouldThrow_whenCodeIsTooLong() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		operation.setCode("123456789012"); // max is 10 chars
		assertThatThrownBy(() -> operationBrowserManager.newOperation(operation))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void updateOperation_shouldSaveNewDescriptionAndIncrementLock() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		int lock = foundOperation.getLock();
		foundOperation.setDescription("Update");
		assertThat(operationBrowserManager.updateOperation(foundOperation)).isNotNull();
		Operation updateOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(updateOperation).isNotNull();
		assertThat(updateOperation.getDescription()).isEqualTo("Update");
		assertThat(updateOperation.getLock().intValue()).isEqualTo(lock + 1);
	}

	@Test
	void deleteOperation_shouldRemoveOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		operationBrowserManager.deleteOperation(foundOperation);
		assertThat(operationBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestOperation(false);
		assertThat(operationBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void descriptionControl_shouldReturnTrue_whenDescriptionExists() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(operationBrowserManager.descriptionControl(foundOperation.getDescription(), foundOperation.getType().getCode())).isTrue();
	}

	@Test
	void descriptionControl_shouldReturnFalse_whenDescriptionDoesNotExist() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(operationBrowserManager.descriptionControl("someOtherDescription", foundOperation.getType().getCode())).isFalse();
	}

	@Test
	void getResultsList_shouldReturnThreeResults() throws Exception {
		assertThat(operationBrowserManager.getResultsList()).hasSize(3);
	}

	@Test
	void getResultDescriptionKey_shouldReturnMatchingKeyOrEmptyString() throws Exception {
		assertThat(operationBrowserManager.getResultDescriptionKey("angal.operation.result.failure.txt")).isEqualTo("failure");

		assertThat(operationBrowserManager.getResultDescriptionKey("the_description_is_not_there")).isEqualTo("");
	}

	@Test
	void getResultDescriptionList_shouldReturnNonEmptyList() throws Exception {
		List<String> descriptionList = operationBrowserManager.getResultDescriptionList();
		assertThat(descriptionList).isNotEmpty();
	}

	@Test
	void getResultDescriptionTranslated_shouldTranslateKnownKeyAndReturnNullForUnknownKey() throws Exception {
		assertThat(operationBrowserManager.getResultDescriptionTranslated("failure")).isEqualTo("angal.operation.result.failure.txt");

		assertThat(operationBrowserManager.getResultDescriptionTranslated("the_key_is_not_there")).isNull();
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
