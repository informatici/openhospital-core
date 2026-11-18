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
package org.isf.dlvrtype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.dlvrtype.TestDeliveryType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeliveryTypeIoOperationTest extends OHCoreTestCase {

	private static TestDeliveryType testDeliveryType;

	@Autowired
	DeliveryTypeIoOperation deliveryTypeIoOperation;
	@Autowired
	DeliveryTypeIoOperationRepository deliveryTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testDeliveryType = new TestDeliveryType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getDeliveryType_shouldReturnSavedDeliveryType() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		List<DeliveryType> deliveryTypes = deliveryTypeIoOperation.getDeliveryType();
		assertThat(deliveryTypes.get(deliveryTypes.size() - 1).getDescription()).isEqualTo(foundDeliveryType.getDescription());
	}

	@Test
	void updateDeliveryType_shouldSaveNewDescription() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		foundDeliveryType.setDescription("Update");
		DeliveryType updatedDeliveryType = deliveryTypeIoOperation.updateDeliveryType(foundDeliveryType);
		assertThat(updatedDeliveryType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newDeliveryType_shouldSaveDeliveryType() throws Exception {
		DeliveryType deliveryType = testDeliveryType.setup(true);
		DeliveryType newDeliveryType = deliveryTypeIoOperation.newDeliveryType(deliveryType);
		checkDeliveryTypeIntoDb(newDeliveryType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestDeliveryType(false);
		boolean result = deliveryTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deleteDeliveryType_shouldRemoveDeliveryType() throws Exception {
		String code = setupTestDeliveryType(false);
		DeliveryType foundDeliveryType = deliveryTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryType).isNotNull();
		deliveryTypeIoOperation.deleteDeliveryType(foundDeliveryType);
		assertThat(deliveryTypeIoOperation.isCodePresent(code)).isFalse();
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
