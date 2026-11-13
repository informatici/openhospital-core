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
package org.isf.opetype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OperationTypeIoOperationTest extends OHCoreTestCase {

	private static TestOperationType testOperationType;

	@Autowired
	OperationTypeIoOperation operationTypeIoOperation;
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
	void getOperationType_shouldIncludeSavedType() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOperationType).isNotNull();
		List<OperationType> operationTypes = operationTypeIoOperation.getOperationType();
		assertThat(operationTypes.get(operationTypes.size() - 1).getDescription()).isEqualTo(foundOperationType.getDescription());
	}

	@Test
	void updateOperationType_shouldSaveNewDescription() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOperationType).isNotNull();
		foundOperationType.setDescription("Update");
		OperationType updatedOperationType = operationTypeIoOperation.updateOperationType(foundOperationType);
		assertThat(updatedOperationType).isNotNull();
		assertThat(updatedOperationType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newOperationType_shouldSaveOperationType() throws Exception {
		OperationType operationType = testOperationType.setup(true);
		OperationType result = operationTypeIoOperation.newOperationType(operationType);
		assertThat(result).isNotNull();
		checkOperationTypeIntoDb(operationType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestOperationType(false);
		boolean result = operationTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deleteOperationType_shouldRemoveOperationType() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOperationType).isNotNull();
		operationTypeIoOperation.deleteOperationType(foundOperationType);
		assertThat(operationTypeIoOperation.isCodePresent(code)).isFalse();
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
