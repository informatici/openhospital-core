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
package org.isf.priceslist.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.priceslist.TestPrice;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PriceIoOperationRepository;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.serviceprinting.print.PriceForPrint;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PriceListManagerTest extends OHCoreTestCase {

	private static TestPriceList testPriceList;
	private static TestPrice testPrice;

	@Autowired
	PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	PriceIoOperationRepository priceIoOperationRepository;
	@Autowired
	PriceListManager priceListManager;

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
		int id = setupTestPriceList(true);
		List<PriceList> priceLists = priceListManager.getLists();
		PriceList priceListByID = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(priceListByID).isNotNull();
		assertThat(priceLists.get(0).getName()).isEqualTo(priceListByID.getName());
	}

	@Test
	void getPrices_shouldReturnSavedPrice() throws Exception {
		int id = setupTestPrice(false);
		List<Price> prices = priceListManager.getPrices();
		Price priceByID = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(priceByID).isNotNull();
		assertThat(prices.get(0).getPrice()).isEqualTo(priceByID.getPrice());
	}

	@Test
	void updatePrices_shouldSaveNewPriceInList() throws Exception {
		List<Price> prices = new ArrayList<>(1);
		int deleteId = setupTestPrice(false);
		Price deletePrice = priceIoOperationRepository.findById(deleteId).orElse(null);
		assertThat(deletePrice).isNotNull();
		PriceList priceList = deletePrice.getList();
		Price insertPrice = testPrice.setup(null, false);
		int insertId = deleteId + 1;
		prices.add(insertPrice);
		priceListManager.updatePrices(priceList, prices);
		Price foundPrice = priceIoOperationRepository.findById(insertId).orElse(null);
		assertThat(foundPrice).isNotNull();
		assertThat(foundPrice.getList().getId()).isEqualTo(priceList.getId());
	}

	@Test
	void newList_shouldSaveList() throws Exception {
		PriceList pricelist = testPriceList.setup(false);
		priceListManager.newList(pricelist);
		PriceList foundPriceList = priceListIoOperationRepository.findById(pricelist.getId()).orElse(null);
		assertThat(foundPriceList).isNotNull();
		checkPriceListIntoDb(foundPriceList.getId());
	}

	@Test
	void updateList_shouldKeepNewName() throws Exception {
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(priceList).isNotNull();
		priceList.setName("NewListName");
		priceListManager.updateList(priceList);
		assertThat(priceList.getName()).isEqualTo("NewListName");
	}

	@Test
	void deleteList_shouldRemoveList() throws Exception {
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(priceList).isNotNull();
		priceListManager.deleteList(priceList);
		assertThat(priceListIoOperationRepository.count()).isZero();
	}

	@Test
	void copyList_shouldMultiplyPrices_whenStepIsZero() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(price).isNotNull();
		PriceList priceList = price.getList();
		priceListManager.copyList(priceList, 2.0, 0);
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isEqualByComparingTo(price.getPrice().multiply(BigDecimal.valueOf(2.0)));
	}

	@Test
	void copyList_shouldRoundPricesToStep_whenStepIsPositive() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(price).isNotNull();
		PriceList priceList = price.getList();
		priceListManager.copyList(priceList, 2.0, 3.0);
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isEqualByComparingTo(
			price.getPrice().multiply(BigDecimal.valueOf(2.0)).divide(BigDecimal.valueOf(3.0), 0, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(3.0)));
	}

	@Test
	void copyList_shouldCopySamePrices_whenFactorAndStepOmitted() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(price).isNotNull();
		PriceList priceList = price.getList();
		priceListManager.copyList(priceList);
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isEqualByComparingTo(price.getPrice());
	}

	@Test
	void convertPrice_shouldReturnPricesWithListCurrency() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(price).isNotNull();
		PriceList priceList = price.getList();
		List<PriceForPrint> priceForPrints = priceListManager.convertPrice(priceList, priceListManager.getPrices());
		assertThat(priceForPrints).isNotNull();
		assertThat(priceForPrints.get(0))
				.extracting(PriceForPrint::getPrice, PriceForPrint::getCurrency)
				.containsExactly(price.getPrice(), priceList.getCurrency());
	}

	@Test
	void newList_shouldThrow_whenCodeIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PriceList priceList = testPriceList.setup(false);
			priceList.setCode("");
			priceListManager.newList(priceList);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newList_shouldThrow_whenNameIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PriceList priceList = testPriceList.setup(false);
			priceList.setName("");
			priceListManager.newList(priceList);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newList_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PriceList priceList = testPriceList.setup(false);
			priceList.setDescription("");
			priceListManager.newList(priceList);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newList_shouldThrow_whenCurrencyIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PriceList priceList = testPriceList.setup(false);
			priceList.setCurrency("");
			priceListManager.newList(priceList);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
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