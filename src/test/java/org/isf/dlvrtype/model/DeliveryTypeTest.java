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
package org.isf.dlvrtype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.dlvrtype.TestDeliveryType;
import org.isf.dlvrtype.service.DeliveryTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeliveryTypeTest extends OHCoreTestCase {

	private static TestDeliveryType testDeliveryType;

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
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestDeliveryType(false);
		checkDeliveryTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestDeliveryType(true);
		checkDeliveryTypeIntoDb(code);
	}

	@Test
	void equalsHashCodeToString_shouldUseDeliveryTypeFields() throws Exception {
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
