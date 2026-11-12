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
package org.isf.priceslist.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PriceListTest extends OHCoreTestCase {

	private static TestPriceList testPriceList;

	@Autowired
	PricesListIoOperationRepository priceListIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testPriceList = new TestPriceList();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		// given:
		int id = setupTestPriceList(false);

		// when, then:
		checkPriceListIntoDb(id);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		// given, when:
		int id = setupTestPriceList(true);

		// then:
		checkPriceListIntoDb(id);
	}

	@Test
	void toString_shouldReturnName() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		assertThat(priceList).hasToString("TestName");
	}

	@Test
	void equals_shouldCompareId() throws Exception {
		PriceList priceList = testPriceList.setup(true);

		assertThat(priceList)
				.isEqualTo(priceList)
				.isNotNull()
				.isNotEqualTo("someString");

		PriceList priceList2 = testPriceList.setup(true);
		priceList.setId(-1);
		priceList2.setId(-99);
		assertThat(priceList).isNotEqualTo(priceList2);

		priceList2.setId(-1);
		assertThat(priceList).isEqualTo(priceList2);
	}

	@Test
	void hashCode_shouldBeComputedFromId() throws Exception {
		PriceList priceList = testPriceList.setup(false);
		priceList.setId(1);
		int hashCode = priceList.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1);
		// check computed value
		assertThat(priceList.hashCode()).isEqualTo(hashCode);
	}

	private int setupTestPriceList(boolean usingSet) throws Exception {
		PriceList priceList = testPriceList.setup(usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		return priceList.getId();
	}

	private void checkPriceListIntoDb(int id) throws Exception {
		PriceList foundPriceList;
		foundPriceList = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPriceList).isNotNull();
		testPriceList.check(foundPriceList);
	}

}
