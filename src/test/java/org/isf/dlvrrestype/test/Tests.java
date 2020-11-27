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
package org.isf.dlvrrestype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.service.DeliveryResultIoOperationRepository;
import org.isf.dlvrrestype.service.DeliveryResultTypeIoOperation;
import org.isf.utils.exception.OHException;
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
		String code = _setupTestDeliveryResultType(false);
		_checkDeliveryResultTypeIntoDb(code);
	}

	@Test
	public void testDeliveryResultTypeSets() throws Exception {
		String code = _setupTestDeliveryResultType(true);
		_checkDeliveryResultTypeIntoDb(code);
	}

	@Test
	public void testIoGetDeliveryResultType() throws Exception {
		String code = _setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findOne(code);
		assertThat(foundDeliveryResultType.getDescription()).isEqualTo("TestDescription");
	}

	@Test
	public void testIoUpdateDeliveryResultType() throws Exception {
		String code = _setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findOne(code);
		foundDeliveryResultType.setDescription("Update");
		boolean result = deliveryResultTypeIoOperation.updateDeliveryResultType(foundDeliveryResultType);
		assertThat(result).isTrue();
		DeliveryResultType updateDeliveryResultType = deliveryResultIoOperationRepository.findOne(code);
		assertThat(updateDeliveryResultType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewDeliveryResultType() throws Exception {
		DeliveryResultType deliveryResultType = testDeliveryResultType.setup(true);
		boolean result = deliveryResultTypeIoOperation.newDeliveryResultType(deliveryResultType);
		assertThat(result).isTrue();
		_checkDeliveryResultTypeIntoDb(deliveryResultType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestDeliveryResultType(false);
		boolean result = deliveryResultTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteDeliveryResultType() throws Exception {
		String code = _setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findOne(code);
		boolean result = deliveryResultTypeIoOperation.deleteDeliveryResultType(foundDeliveryResultType);
		result = deliveryResultTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private String _setupTestDeliveryResultType(boolean usingSet) throws OHException {
		DeliveryResultType deliveryResultType = testDeliveryResultType.setup(usingSet);
		deliveryResultIoOperationRepository.saveAndFlush(deliveryResultType);
		return deliveryResultType.getCode();
	}

	private void _checkDeliveryResultTypeIntoDb(String code) throws OHException {
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findOne(code);
		testDeliveryResultType.check(foundDeliveryResultType);
	}
}