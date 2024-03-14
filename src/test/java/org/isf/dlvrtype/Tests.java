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
package org.isf.dlvrtype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.service.DeliveryTypeIoOperation;
import org.isf.dlvrtype.service.DeliveryTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestDeliveryType testDeliveryType;

	@Autowired
	DeliveryTypeIoOperation deliveryTypeIoOperation;
	@Autowired
	DeliveryTypeIoOperationRepository deliveryTypeIoOperationRepository;
	@Autowired
	DeliveryTypeBrowserManager deliveryTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testDeliveryType = new TestDeliveryType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testDeliveryTypeGets() throws Exception {
		String code = setupTestDeliveryType(false);
		checkDeliveryTypeIntoDb(code);
	}

	@Test
	void testDeliveryTypeSets() throws Exception {
		String code = setupTestDeliveryType(true);
		checkDeliveryTypeIntoDb(code);
	}

	@Test
	void testIoGetDeliveryType() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		List<DeliveryType> deliveryTypes = deliveryTypeIoOperation.getDeliveryType();
		assertThat(deliveryTypes.get(deliveryTypes.size() - 1).getDescription()).isEqualTo(foundDeliveryType.getDescription());
	}

	@Test
	void testIoUpdateDeliveryType() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		foundDeliveryType.setDescription("Update");
		DeliveryType updatedDeliveryType = deliveryTypeIoOperation.updateDeliveryType(foundDeliveryType);
		assertThat(updatedDeliveryType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoNewDeliveryType() throws Exception {
		DeliveryType deliveryType = testDeliveryType.setup(true);
		DeliveryType newDeliveryType = deliveryTypeIoOperation.newDeliveryType(deliveryType);
		checkDeliveryTypeIntoDb(newDeliveryType.getCode());
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestDeliveryType(false);
		boolean result = deliveryTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testIoDeleteDeliveryType() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		deliveryTypeIoOperation.deleteDeliveryType(foundDeliveryType);
		assertThat(deliveryTypeIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrGetDeliveryType() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		List<DeliveryType> deliveryTypes = deliveryTypeBrowserManager.getDeliveryType();
		assertThat(deliveryTypes.get(deliveryTypes.size() - 1).getDescription()).isEqualTo(foundDeliveryType.getDescription());
	}

	@Test
	void testMgrUpdateDeliveryType() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		foundDeliveryType.setDescription("Update");
		DeliveryType updatedDeliveryType = deliveryTypeBrowserManager.updateDeliveryType(foundDeliveryType);
		assertThat(updatedDeliveryType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewDeliveryType() throws Exception {
		DeliveryType deliveryType = testDeliveryType.setup(true);
		DeliveryType newDeliveryType = deliveryTypeBrowserManager.newDeliveryType(deliveryType);
		checkDeliveryTypeIntoDb(newDeliveryType.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestDeliveryType(false);
		boolean result = deliveryTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testMgrDeleteDeliveryType() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		deliveryTypeBrowserManager.deleteDeliveryType(foundDeliveryType);
		assertThat(deliveryTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrDeliveryTypeValidate() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType deliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(deliveryType).isNotNull();
		deliveryType.setDescription("Update");
		deliveryTypeBrowserManager.updateDeliveryType(deliveryType);
		// empty string
		deliveryType.setCode("");
		assertThatThrownBy(() -> deliveryTypeBrowserManager.updateDeliveryType(deliveryType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// too long
		deliveryType.setCode("123456789ABCDEF");
		assertThatThrownBy(() -> deliveryTypeBrowserManager.updateDeliveryType(deliveryType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// key already exists
		deliveryType.setCode(code);
		assertThatThrownBy(() -> deliveryTypeBrowserManager.newDeliveryType(deliveryType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// description empty
		deliveryType.setDescription("");
		assertThatThrownBy(() -> deliveryTypeBrowserManager.updateDeliveryType(deliveryType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void testDeliveryTypeEqualHashToString() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType deliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(deliveryType).isNotNull();
		DeliveryType deliveryType2 = new DeliveryType("someCode", "someDescription");
		assertThat(deliveryType)
				.isEqualTo(deliveryType)
				.isNotEqualTo(deliveryType2)
				.isNotEqualTo("xyzzy");
		deliveryType2.setCode(code);
		deliveryType2.setDescription(deliveryType.getDescription());
		assertThat(deliveryType).isEqualTo(deliveryType2);

		assertThat(deliveryType.hashCode()).isPositive();

		assertThat(deliveryType2).hasToString(deliveryType.getDescription());
	}

	private String setupTestDeliveryType(boolean usingSet) throws OHException {
		DeliveryType deliveryType = testDeliveryType.setup(usingSet);
		deliveryTypeIoOperationRepository.saveAndFlush(deliveryType);
		return deliveryType.getCode();
	}

	private void checkDeliveryTypeIntoDb(String code) throws OHException {
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		testDeliveryType.check(foundDeliveryType);
	}
}