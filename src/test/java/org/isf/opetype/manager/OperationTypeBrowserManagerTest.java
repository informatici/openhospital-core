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
package org.isf.opetype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OperationTypeBrowserManagerTest extends OHCoreTestCase {

	private static TestOperationType testOperationType;

	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;
	@Autowired
	OperationTypeBrowserManager operationTypeBrowserManager;

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
		List<OperationType> operationTypes = operationTypeBrowserManager.getOperationType();
		assertThat(operationTypes.get(operationTypes.size() - 1).getDescription()).isEqualTo(foundOperationType.getDescription());
	}

	@Test
	void updateOperationType_shouldSaveNewDescription() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOperationType).isNotNull();
		foundOperationType.setDescription("Update");
		OperationType updatedOperationType = operationTypeBrowserManager.updateOperationType(foundOperationType);
		assertThat(updatedOperationType).isNotNull();
		assertThat(updatedOperationType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newOperationType_shouldSaveOperationType() throws Exception {
		OperationType operationType = testOperationType.setup(true);
		assertThat(operationTypeBrowserManager.newOperationType(operationType)).isNotNull();
		checkOperationTypeIntoDb(operationType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestOperationType(false);
		assertThat(operationTypeBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void deleteOperationType_shouldRemoveOperationType() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOperationType).isNotNull();
		operationTypeBrowserManager.deleteOperationType(foundOperationType);
		assertThat(operationTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void deleteOperationType_shouldThrow_whenOperationTypeIsNull() throws Exception {
		assertThatThrownBy(() ->
			operationTypeBrowserManager.deleteOperationType(null))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void updateOperationType_shouldThrow_whenCodeIsNull() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setCode(null);
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void updateOperationType_shouldThrow_whenCodeIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setCode("");
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void updateOperationType_shouldThrow_whenCodeIsTooLong() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setCode("keyIsTooLong");
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void updateOperationType_shouldThrow_whenDescriptionIsNull() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setDescription(null);
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void updateOperationType_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setDescription("");
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newOperationType_shouldThrow_whenCodeExists() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationTypeBrowserManager.newOperationType(operationType);
			operationTypeBrowserManager.newOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
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