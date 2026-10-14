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

import java.math.BigDecimal;

import org.isf.OHCoreTestCase;
import org.isf.priceslist.TestPrice;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.service.PriceIoOperationRepository;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PriceTest extends OHCoreTestCase {

	private static TestPriceList testPriceList;
	private static TestPrice testPrice;

	@Autowired
	PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	PriceIoOperationRepository priceIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testPriceList = new TestPriceList();
		testPrice = new TestPrice();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		// given:
		int id = setupTestPrice(false);

		// when, then:
		checkPriceIntoDb(id);
	}

	@Test
	void constructor_shouldKeepPriceList() throws Exception {
		// given, when:
		int id = setupTestPrice(false);

		// then:
		checkPriceIntoDb(id);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = new Price(priceList, "TG", "TestItem", "TestDescription", new BigDecimal("10.10"), true);
		assertThat(price).hasToString("TestDescription");
	}

	@Test
	void equals_shouldCompareIdOnly() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = new Price(priceList, "TG", "TestItem", "TestDescription", new BigDecimal("10.10"));

		assertThat(price)
				.isEqualTo(price)
				.isNotNull()
				.isNotEqualTo("someString");

		PriceList priceList2 = testPriceList.setup(true);
		Price price2 = new Price(priceList2, "TG", "TestItem", "TestDescriptionOther", new BigDecimal("10.10"));
		assertThat(price).isEqualTo(price2);

		price2.setDesc("TestDescription");
		price.setId(-1);
		price2.setId(-99);
		assertThat(price).isNotEqualTo(price2);

		price2.setId(-1);
		assertThat(price).isEqualTo(price2);
	}

	@Test
	void hashCode_shouldBeComputedFromId() throws Exception {
		PriceList priceList = testPriceList.setup(false);
		Price price = testPrice.setup(priceList, false);
		price.setId(1);
		int hashCode = price.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1);
		// check computed value
		assertThat(price.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void isEditableAndIsPrice_shouldReflectEditableFlagAndItem() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = new Price(priceList, "TG", "TestItem", "TestDescription", new BigDecimal("10.10"));

		assertThat(price.isEditable()).isTrue();
		price.setEditable(false);
		assertThat(price.isEditable()).isFalse();

		assertThat(price.isPrice()).isTrue();
		price.setItem("");
		assertThat(price.isPrice()).isFalse();
	}

	private int setupTestPrice(boolean usingSet) throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = testPrice.setup(priceList, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		priceIoOperationRepository.saveAndFlush(price);
		return price.getId();
	}

	private void checkPriceIntoDb(int id) throws Exception {
		Price foundPrice = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPrice).isNotNull();
		testPrice.check(foundPrice);
		testPriceList.check(foundPrice.getList());
	}

}
