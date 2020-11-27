/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.operation.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.operation.model.Operation;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.service.OperationIoOperations;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.opetype.test.TestOperationType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestOperation testOperation;
	private static TestOperationType testOperationType;

	@Autowired
	OperationIoOperations operationIoOperations;
	@Autowired
	OperationIoOperationRepository operationIoOperationRepository;
	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testOperation = new TestOperation();
		testOperationType = new TestOperationType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testOperationGets() throws Exception {
		String code = _setupTestOperation(false);
		_checkOperationIntoDb(code);
	}

	@Test
	public void testOperationSets() throws Exception {
		String code = _setupTestOperation(true);
		_checkOperationIntoDb(code);
	}

	@Test
	public void testIoGetOperations() throws Exception {
		String code = _setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		ArrayList<Operation> operations = operationIoOperations.getOperationByTypeDescription(foundOperation.getDescription());
		assertThat(operations.get(0).getDescription()).isEqualTo(foundOperation.getDescription());
	}

	@Test
	public void testIoGetOperationByTypeDescription() throws Exception {
		// given:
		String code = _setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);

		// when:
		ArrayList<Operation> operations = operationIoOperations.getOperationByTypeDescription(foundOperation.getType().getDescription());

		// then:
		assertThat(operations).isNotEmpty();
	}

	@Test
	public void testIoNewOperation() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		Operation operation = testOperation.setup(operationType, true);
		boolean result = operationIoOperations.newOperation(operation);
		assertThat(result).isTrue();
		_checkOperationIntoDb(operation.getCode());
	}

	public void testIoUpdateOperation() throws Exception {
		String code = _setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		int lock = foundOperation.getLock();
		foundOperation.setDescription("Update");
		boolean result = operationIoOperations.updateOperation(foundOperation);
		assertThat(result).isTrue();
		Operation updateOperation = operationIoOperations.findByCode(code);
		assertThat(updateOperation.getDescription()).isEqualTo("Update");
		assertThat(updateOperation.getLock().intValue()).isEqualTo(lock + 1);
	}

	@Test
	public void testIoDeleteOperation() throws Exception {
		String code = _setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		boolean result = operationIoOperations.deleteOperation(foundOperation);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestOperation(false);
		boolean result = operationIoOperations.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsDescriptionPresent() throws Exception {
		String code = _setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		boolean result = operationIoOperations.isDescriptionPresent(foundOperation.getDescription(), foundOperation.getType().getCode());
		assertThat(result).isTrue();
	}

	private String _setupTestOperation(boolean usingSet) throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, usingSet);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		return operation.getCode();
	}

	private void _checkOperationIntoDb(String code) throws Exception {
		Operation foundOperation = operationIoOperations.findByCode(code);
		testOperation.check(foundOperation);
	}
}