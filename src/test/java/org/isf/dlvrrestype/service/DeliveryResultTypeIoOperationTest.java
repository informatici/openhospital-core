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
package org.isf.dlvrrestype.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.dlvrrestype.TestDeliveryResultType;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeliveryResultTypeIoOperationTest extends OHCoreTestCase {

	private static TestDeliveryResultType testDeliveryResultType;

	@Autowired
	DeliveryResultTypeIoOperation deliveryResultTypeIoOperation;
	@Autowired
	DeliveryResultIoOperationRepository deliveryResultIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testDeliveryResultType = new TestDeliveryResultType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void updateDeliveryResultType_shouldSaveNewDescription() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryResultType).isNotNull();
		foundDeliveryResultType.setDescription("Update");
		DeliveryResultType updatedDeliveryResultType = deliveryResultTypeIoOperation.updateDeliveryResultType(foundDeliveryResultType);
		assertThat(updatedDeliveryResultType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newDeliveryResultType_shouldSaveDeliveryResultType() throws Exception {
		DeliveryResultType deliveryResultType = testDeliveryResultType.setup(true);
		DeliveryResultType newDeliveryResultType = deliveryResultTypeIoOperation.newDeliveryResultType(deliveryResultType);
		checkDeliveryResultTypeIntoDb(newDeliveryResultType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestDeliveryResultType(false);
		boolean result = deliveryResultTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deleteDeliveryResultType_shouldRemoveDeliveryResultType() throws Exception {
		String code = setupTestDeliveryResultType(false);
		DeliveryResultType foundDeliveryResultType = deliveryResultIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDeliveryResultType).isNotNull();
		deliveryResultTypeIoOperation.deleteDeliveryResultType(foundDeliveryResultType);
		boolean result = deliveryResultTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
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
