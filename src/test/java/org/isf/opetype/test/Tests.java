/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.opetype.manager.OperationTypeBrowserManager;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperation;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
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
	@Autowired
	OperationTypeBrowserManager operationTypeBrowserManager;

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
		String code = setupTestOperationType(false);
		checkOperationTypeIntoDb(code);
	}

	@Test
	public void testOperationTypeSets() throws Exception {
		String code = setupTestOperationType(true);
		checkOperationTypeIntoDb(code);
	}

	@Test
	public void testIoGetOperationType() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).get();
		List<OperationType> operationTypes = operationTypeIoOperation.getOperationType();
		assertThat(operationTypes.get(operationTypes.size() - 1).getDescription()).isEqualTo(foundOperationType.getDescription());
	}

	@Test
	public void testIoUpdateOperationType() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).get();
		foundOperationType.setDescription("Update");
		OperationType result = operationTypeIoOperation.updateOperationType(foundOperationType);
		assertThat(result);
		OperationType updateOperationType = operationTypeIoOperationRepository.findById(code).get();
		assertThat(updateOperationType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewOperationType() throws Exception {
		OperationType operationType = testOperationType.setup(true);
		OperationType result = operationTypeIoOperation.newOperationType(operationType);
		assertThat(result);
		checkOperationTypeIntoDb(operationType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestOperationType(false);
		boolean result = operationTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteOperationType() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).get();
		boolean result = operationTypeIoOperation.deleteOperationType(foundOperationType);
		assertThat(result).isTrue();
		result = operationTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetOperationType() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).get();
		List<OperationType> operationTypes = operationTypeBrowserManager.getOperationType();
		assertThat(operationTypes.get(operationTypes.size() - 1).getDescription()).isEqualTo(foundOperationType.getDescription());
	}

	@Test
	public void testMgrUpdateOperationType() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).get();
		foundOperationType.setDescription("Update");
		assertThat(operationTypeBrowserManager.updateOperationType(foundOperationType));
		OperationType updateOperationType = operationTypeIoOperationRepository.findById(code).get();
		assertThat(updateOperationType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrNewOperationType() throws Exception {
		OperationType operationType = testOperationType.setup(true);
		assertThat(operationTypeBrowserManager.newOperationType(operationType));
		checkOperationTypeIntoDb(operationType.getCode());
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestOperationType(false);
		assertThat(operationTypeBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	public void testMgrDeleteOperationType() throws Exception {
		String code = setupTestOperationType(false);
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).get();
		assertThat(operationTypeBrowserManager.deleteOperationType(foundOperationType)).isTrue();
		assertThat(operationTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	public void testMgrValidationKeyNull() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setCode(null);
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationKeyEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setCode("");
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationKeyTooLong() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setCode("keyIsTooLong");
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationDescriptionNull() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setDescription(null);
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationDescriptionEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationType.setDescription("");
			operationTypeBrowserManager.updateOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationCodeAlreadyInUse() throws Exception {
		assertThatThrownBy(() ->
		{
			OperationType operationType = new OperationType("Z", "description");
			operationTypeBrowserManager.newOperationType(operationType);
			operationTypeBrowserManager.newOperationType(operationType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testOperationTypeEquals() throws Exception {
		OperationType operationType = new OperationType("Z", "description");

		assertThat(operationType.equals(operationType)).isTrue();
		assertThat(operationType)
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
	public void testOperationTypeHashCode() throws Exception {
		OperationType operationType = new OperationType("Z", "description");
		int hashCode = operationType.hashCode();
		// used computed value
		assertThat(operationType.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testOperationTypeToString() throws Exception {
		OperationType operationType = new OperationType("Z", "description");
		assertThat(operationType).hasToString("description");
	}

	private String setupTestOperationType(boolean usingSet) throws OHException {
		OperationType operationType = testOperationType.setup(usingSet);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		return operationType.getCode();
	}

	private void checkOperationTypeIntoDb(String code) throws OHException {
		OperationType foundOperationType = operationTypeIoOperationRepository.findById(code).get();
		testOperationType.check(foundOperationType);
	}
}