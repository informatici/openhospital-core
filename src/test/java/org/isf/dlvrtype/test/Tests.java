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
package org.isf.dlvrtype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.service.DeliveryTypeIoOperation;
import org.isf.dlvrtype.service.DeliveryTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestDeliveryType testDeliveryType;

	@Autowired
	DeliveryTypeIoOperation deliveryTypeIoOperation;
	@Autowired
	DeliveryTypeIoOperationRepository deliveryTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testDeliveryType = new TestDeliveryType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testDeliveryTypeGets() throws Exception {
		String code = _setupTestDeliveryType(false);
		_checkDeliveryTypeIntoDb(code);
	}

	@Test
	public void testDeliveryTypeSets() throws Exception {
		String code = _setupTestDeliveryType(true);
		_checkDeliveryTypeIntoDb(code);
	}

	@Test
	public void testIoGetDeliveryType() throws Exception {
		String code = _setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findOne(code);
		ArrayList<DeliveryType> deliveryTypes = deliveryTypeIoOperation.getDeliveryType();
		assertThat(deliveryTypes.get(deliveryTypes.size() - 1).getDescription()).isEqualTo(foundDeliveryType.getDescription());
	}

	@Test
	public void testIoUpdateDeliveryType() throws Exception {
		String code = _setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findOne(code);
		foundDeliveryType.setDescription("Update");
		boolean result = deliveryTypeIoOperation.updateDeliveryType(foundDeliveryType);
		DeliveryType updateDeliveryType = deliveryTypeIoOperationRepository.findOne(code);
		assertThat(result).isTrue();
		assertThat(updateDeliveryType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewDeliveryType() throws Exception {
		DeliveryType deliveryType = testDeliveryType.setup(true);
		boolean result = deliveryTypeIoOperation.newDeliveryType(deliveryType);
		assertThat(result).isTrue();
		_checkDeliveryTypeIntoDb(deliveryType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestDeliveryType(false);
		boolean result = deliveryTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteDeliveryType() throws Exception {
		String code = _setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findOne(code);
		boolean result = deliveryTypeIoOperation.deleteDeliveryType(foundDeliveryType);
		assertThat(result).isTrue();
		result = deliveryTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private String _setupTestDeliveryType(boolean usingSet) throws OHException {
		DeliveryType deliveryType = testDeliveryType.setup(usingSet);
		deliveryTypeIoOperationRepository.saveAndFlush(deliveryType);
		return deliveryType.getCode();
	}

	private void _checkDeliveryTypeIntoDb(String code) throws OHException {
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findOne(code);
		testDeliveryType.check(foundDeliveryType);
	}
}