/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.dlvrrestype.test;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestDeliveryResultType testDeliveryResultType;

	@Autowired
	DeliveryResultTypeIoOperation deliveryResultTypeIoOperation;
	@Autowired
	DeliveryResultIoOperationRepository deliveryResultIoOperationRepository;
	@Autowired
	DeliveryResultTypeBrowserManager deliveryResultTypeBrowserManager;

	@BeforeClass
	public static void setUpClass() {
		testDeliveryResultType = new TestDeliveryResultType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testDeliveryResultTypeGets() throws Exception {
		String code = setupTestDeliveryResultType(false);
		checkDeliveryResultTypeIntoDb(code);
	}

	@Test
	public void testDeliveryResultTypeSets() throws Exception {
		String code = setupTestDeliveryResultType(true);
		checkDeliveryResultTypeIntoDb(code);
	}

	@Test
	public void testIoGetDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		assertThat(foundDeliveryResultType.getDescription()).isEqualTo("TestDescription");
	}

	@Test
	public void testIoUpdateDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		foundDeliveryResultType.setDescription("Update");
		boolean result = deliveryResultTypeIoOperation.updateDeliveryResultType(foundDeliveryResultType);
		assertThat(result).isTrue();
		DeliveryResultType updateDeliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		assertThat(updateDeliveryResultType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewDeliveryResultType() throws Exception {
		DeliveryResultType deliveryResultType = testDeliveryResultType.setup(true);
		boolean result = deliveryResultTypeIoOperation.newDeliveryResultType(deliveryResultType);
		assertThat(result).isTrue();
		checkDeliveryResultTypeIntoDb(deliveryResultType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestDeliveryResultType(false);
		boolean result = deliveryResultTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		deliveryResultTypeIoOperation.deleteDeliveryResultType(foundDeliveryResultType);
		boolean result = deliveryResultTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		List<DeliveryResultType> foundDeliveryResultTypes = deliveryResultTypeBrowserManager.getDeliveryResultType();
		assertThat(foundDeliveryResultTypes).contains(foundDeliveryResultType);
	}

	@Test
	public void testMgrUpdateDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		foundDeliveryResultType.setDescription("Update");
		boolean result = deliveryResultTypeBrowserManager.updateDeliveryResultType(foundDeliveryResultType);
		assertThat(result).isTrue();
		DeliveryResultType updateDeliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		assertThat(updateDeliveryResultType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrNewDeliveryResultType() throws Exception {
		DeliveryResultType deliveryResultType = testDeliveryResultType.setup(true);
		boolean result = deliveryResultTypeBrowserManager.newDeliveryResultType(deliveryResultType);
		assertThat(result).isTrue();
		checkDeliveryResultTypeIntoDb(deliveryResultType.getCode());
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestDeliveryResultType(false);
		boolean result = deliveryResultTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrDeleteDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		deliveryResultTypeBrowserManager.deleteDeliveryResultType(foundDeliveryResultType);
		boolean result = deliveryResultTypeBrowserManager.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrDeliveryResultTypeValidate() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType deliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		deliveryResultType.setDescription("Update");
		boolean result = deliveryResultTypeBrowserManager.updateDeliveryResultType(deliveryResultType);
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
	public void testDeliveryResultTypeHashToString() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType deliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
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
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).get();
		testDeliveryResultType.check(foundDeliveryResultType);
	}
}