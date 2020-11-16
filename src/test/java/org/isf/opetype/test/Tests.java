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
package org.isf.opetype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperation;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestOperationType testOperationType;

	@Autowired
	OperationTypeIoOperation operationTypeIoOperation;
	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testOperationType = new TestOperationType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testOperationTypeGets() throws Exception {
		String code = _setupTestOperationType(false);
		_checkOperationTypeIntoDb(code);
	}

	@Test
	public void testOperationTypeSets() throws Exception {
		String code = _setupTestOperationType(true);
		_checkOperationTypeIntoDb(code);
	}

	@Test
	public void testIoGetOperationType() throws Exception {
		String code = _setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findOne(code);
		ArrayList<OperationType> operationTypes = operationTypeIoOperation.getOperationType();
		assertThat(operationTypes.get(operationTypes.size() - 1).getDescription()).isEqualTo(foundOperationType.getDescription());
	}

	@Test
	public void testIoUpdateOperationType() throws Exception {
		String code = _setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findOne(code);
		foundOperationType.setDescription("Update");
		boolean result = operationTypeIoOperation.updateOperationType(foundOperationType);
		assertThat(result).isTrue();
		OperationType updateOperationType = operationTypeIoOperationRepository.findOne(code);
		assertThat(updateOperationType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewOperationType() throws Exception {
		OperationType operationType = testOperationType.setup(true);
		boolean result = operationTypeIoOperation.newOperationType(operationType);
		assertThat(result).isTrue();
		_checkOperationTypeIntoDb(operationType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestOperationType(false);
		boolean result = operationTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteOperationType() throws Exception {
		String code = _setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findOne(code);
		boolean result = operationTypeIoOperation.deleteOperationType(foundOperationType);
		assertThat(result).isTrue();
		result = operationTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private String _setupTestOperationType(boolean usingSet) throws OHException {
		OperationType operationType = testOperationType.setup(usingSet);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		return operationType.getCode();
	}

	private void _checkOperationTypeIntoDb(String code) throws OHException {
		OperationType foundOperationType;
		foundOperationType = operationTypeIoOperationRepository.findOne(code);
		testOperationType.check(foundOperationType);
	}
}