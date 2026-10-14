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
package org.isf.priceslist.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.priceslist.TestPrice;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PricesListIoOperationsTest extends OHCoreTestCase {

	private static TestPriceList testPriceList;
	private static TestPrice testPrice;

	@Autowired
	PricesListIoOperations priceListIoOperation;
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
	void getLists_shouldReturnSavedList() throws Exception {
		// given:
		int id = setupTestPriceList(true);

		// when:
		List<PriceList> priceLists = priceListIoOperation.getLists();

		// then:
		PriceList priceListByID = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(priceListByID).isNotNull();
		assertThat(priceLists.get(0).getName()).isEqualTo(priceListByID.getName());
	}

	@Test
	void getPrices_shouldReturnSavedPrice() throws Exception {
		// given:
		int id = setupTestPrice(false);

		// when:
		List<Price> prices = priceListIoOperation.getPrices();

		// then:
		Price priceByID = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(priceByID).isNotNull();
		assertThat(prices.get(0).getPrice()).isEqualTo(priceByID.getPrice());
	}

	@Test
	void updatePrices_shouldSaveNewPriceInList() throws Exception {
		// given:
		List<Price> prices = new ArrayList<>(1);
		int deleteId = setupTestPrice(false);
		Price deletePrice = priceIoOperationRepository.findById(deleteId).orElse(null);
		assertThat(deletePrice).isNotNull();

		// when:
		PriceList priceList = deletePrice.getList();
		Price insertPrice = testPrice.setup(null, false);
		int insertId = deleteId + 1;
		prices.add(insertPrice);
		priceListIoOperation.updatePrices(priceList, prices);

		// then:
		Price foundPrice = priceIoOperationRepository.findById(insertId).orElse(null);
		assertThat(foundPrice).isNotNull();
		assertThat(foundPrice.getList().getId()).isEqualTo(priceList.getId());
	}

	@Test
	void newList_shouldSaveList() throws Exception {
		// given:
		PriceList pricelist = testPriceList.setup(false);

		// when:
		priceListIoOperation.newList(pricelist);

		// then:
		PriceList foundPriceList = priceListIoOperationRepository.findById(pricelist.getId()).orElse(null);
		assertThat(foundPriceList).isNotNull();
		checkPriceListIntoDb(foundPriceList.getId());
	}

	@Test
	void updateList_shouldKeepNewName() throws Exception {
		// given:
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(priceList).isNotNull();
		priceList.setName("NewListName");

		// when:
		priceListIoOperation.updateList(priceList);

		// then:
		assertThat(priceList.getName()).isEqualTo("NewListName");
	}

	@Test
	void deleteList_shouldRemoveList() throws Exception {
		// given:
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(priceList).isNotNull();

		// when:
		priceListIoOperation.deleteList(priceList);

		// then:
		assertThat(priceListIoOperationRepository.count()).isZero();
	}

	@Test
	void copyList_shouldMultiplyPrices_whenStepIsZero() throws Exception {
		// given:
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(price).isNotNull();
		PriceList priceList = price.getList();

		// when:
		priceListIoOperation.copyList(priceList, 2.0, 0);

		// then:
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isEqualByComparingTo(price.getPrice().multiply(BigDecimal.valueOf(2.0)));
	}

	@Test
	void copyList_shouldRoundPricesToStep_whenStepIsPositive() throws Exception {
		// given:
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(price).isNotNull();
		PriceList priceList = price.getList();

		// when:
		priceListIoOperation.copyList(priceList, 2.0, 3.0);

		// then:
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isEqualByComparingTo(
			price.getPrice().multiply(BigDecimal.valueOf(2.0)).divide(BigDecimal.valueOf(3.0), 0, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(3.0)));
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

	private int setupTestPrice(boolean usingSet) throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = testPrice.setup(priceList, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		priceIoOperationRepository.saveAndFlush(price);
		return price.getId();
	}

}
