/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.dlvrrestype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.service.DeliveryResultIoOperationRepository;
import org.isf.dlvrrestype.service.DeliveryResultTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestDeliveryResultType testDeliveryResultType;

	@Autowired
	DeliveryResultTypeIoOperation deliveryResultTypeIoOperation;
	@Autowired
	DeliveryResultIoOperationRepository deliveryResultIoOperationRepository;
	@Autowired
	DeliveryResultTypeBrowserManager deliveryResultTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testDeliveryResultType = new TestDeliveryResultType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testDeliveryResultTypeGets() throws Exception {
		String code = setupTestDeliveryResultType(false);
		checkDeliveryResultTypeIntoDb(code);
	}

	@Test
	void testDeliveryResultTypeSets() throws Exception {
		String code = setupTestDeliveryResultType(true);
		checkDeliveryResultTypeIntoDb(code);
	}

	@Test
	void testIoGetDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryResultType).isNotNull();
		assertThat(foundDeliveryResultType.getDescription()).isEqualTo("TestDescription");
	}

	@Test
	void testIoUpdateDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryResultType).isNotNull();
		foundDeliveryResultType.setDescription("Update");
		DeliveryResultType updatedDeliveryResultType = deliveryResultTypeIoOperation.updateDeliveryResultType(foundDeliveryResultType);
		assertThat(updatedDeliveryResultType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoNewDeliveryResultType() throws Exception {
		DeliveryResultType deliveryResultType = testDeliveryResultType.setup(true);
		DeliveryResultType newDeliveryResultType = deliveryResultTypeIoOperation.newDeliveryResultType(deliveryResultType);
		checkDeliveryResultTypeIntoDb(newDeliveryResultType.getCode());
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestDeliveryResultType(false);
		boolean result = deliveryResultTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testIoDeleteDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryResultType).isNotNull();
		deliveryResultTypeIoOperation.deleteDeliveryResultType(foundDeliveryResultType);
		boolean result = deliveryResultTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	void testMgrGetDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryResultType).isNotNull();
		List<DeliveryResultType> foundDeliveryResultTypes = deliveryResultTypeBrowserManager.getDeliveryResultType();
		assertThat(foundDeliveryResultTypes).contains(foundDeliveryResultType);
	}

	@Test
	void testMgrUpdateDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryResultType).isNotNull();
		foundDeliveryResultType.setDescription("Update");
		DeliveryResultType updatedDeliveryResultType = deliveryResultTypeBrowserManager.updateDeliveryResultType(foundDeliveryResultType);
		assertThat(updatedDeliveryResultType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewDeliveryResultType() throws Exception {
		DeliveryResultType deliveryResultType = testDeliveryResultType.setup(true);
		DeliveryResultType newDeliveryResultType  = deliveryResultTypeBrowserManager.newDeliveryResultType(deliveryResultType);
		checkDeliveryResultTypeIntoDb(newDeliveryResultType.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestDeliveryResultType(false);
		boolean result = deliveryResultTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testMgrDeleteDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryResultType).isNotNull();
		deliveryResultTypeBrowserManager.deleteDeliveryResultType(foundDeliveryResultType);
		boolean result = deliveryResultTypeBrowserManager.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	void testMgrDeliveryResultTypeValidate() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType deliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(deliveryResultType).isNotNull();
		deliveryResultType.setDescription("Update");
		deliveryResultTypeBrowserManager.updateDeliveryResultType(deliveryResultType);
		// empty string
		deliveryResultType.setCode("");
		assertThatThrownBy(() -> deliveryResultTypeBrowserManager.updateDeliveryResultType(deliveryResultType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// too long
		deliveryResultType.setCode("123456789ABCDEF");
		assertThatThrownBy(() -> deliveryResultTypeBrowserManager.updateDeliveryResultType(deliveryResultType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// key already exists
		deliveryResultType.setCode(code);
		assertThatThrownBy(() -> deliveryResultTypeBrowserManager.newDeliveryResultType(deliveryResultType))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// description empty
		deliveryResultType.setDescription("");
		assertThatThrownBy(() -> deliveryResultTypeBrowserManager.updateDeliveryResultType(deliveryResultType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void testDeliveryResultTypeHashToString() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType deliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(deliveryResultType).isNotNull();
		assertThat(deliveryResultType.hashCode()).isPositive();

		DeliveryResultType deliveryResultType2 = new DeliveryResultType("someCode", "someDescription");
		assertThat(deliveryResultType2).hasToString("someDescription");
	}

	private String setupTestDeliveryResultType(boolean usingSet) throws OHException {
		DeliveryResultType deliveryResultType = testDeliveryResultType.setup(usingSet);
		deliveryResultIoOperationRepository.saveAndFlush(deliveryResultType);
		return deliveryResultType.getCode();
	}

	private void checkDeliveryResultTypeIntoDb(String code) throws OHException {
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryResultType).isNotNull();
		testDeliveryResultType.check(foundDeliveryResultType);
	}
}