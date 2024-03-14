/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.priceslist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PriceIoOperationRepository;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.priceslist.service.PricesListIoOperations;
import org.isf.serviceprinting.print.PriceForPrint;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestPriceList testPriceList;
	private static TestPrice testPrice;

	@Autowired
	PricesListIoOperations priceListIoOperation;
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
	void testPriceListGets() throws Exception {
		// given:
		int id = setupTestPriceList(false);

		// when, then:
		checkPriceListIntoDb(id);
	}

	@Test
	void testPriceListSets() throws Exception {
		// given, when:
		int id = setupTestPriceList(true);

		// then:
		checkPriceListIntoDb(id);
	}

	@Test
	void testIoGetLists() throws Exception {
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
	void testPriceGets() throws Exception {
		// given:
		int id = setupTestPrice(false);

		// when, then:
		checkPriceIntoDb(id);
	}

	@Test
	void testPriceSets() throws Exception {
		// given, when:
		int id = setupTestPrice(false);

		// then:
		checkPriceIntoDb(id);
	}

	@Test
	void testIoGetPrices() throws Exception {
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
	void testIoUpdatePrices() throws Exception {
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
	void testIoNewList() throws Exception {
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
	void testIoUpdateList() throws Exception {
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
	void testIoDeleteList() throws Exception {
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
	void testIoCopyList() throws Exception {
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
		assertThat(copyPrice.getPrice()).isCloseTo(2.0 * price.getPrice(), within(0.10d));
	}

	@Test
	void testIoCopyListSteps() throws Exception {
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
		assertThat(copyPrice.getPrice()).isCloseTo(Math.round(2.0 * price.getPrice() / 3.0) * 3L, within(0.10d));
	}

	@Test
	void testMgrGetLists() throws Exception {
		int id = setupTestPriceList(true);
		List<PriceList> priceLists = priceListManager.getLists();
		PriceList priceListByID = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(priceListByID).isNotNull();
		assertThat(priceLists.get(0).getName()).isEqualTo(priceListByID.getName());
	}

	@Test
	void testMgrGetPrices() throws Exception {
		int id = setupTestPrice(false);
		List<Price> prices = priceListManager.getPrices();
		Price priceByID = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(priceByID).isNotNull();
		assertThat(prices.get(0).getPrice()).isEqualTo(priceByID.getPrice());
	}

	@Test
	void testMgrUpdatePrices() throws Exception {
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
	void testMgrNewList() throws Exception {
		PriceList pricelist = testPriceList.setup(false);
		priceListManager.newList(pricelist);
		PriceList foundPriceList = priceListIoOperationRepository.findById(pricelist.getId()).orElse(null);
		assertThat(foundPriceList).isNotNull();
		checkPriceListIntoDb(foundPriceList.getId());
	}

	@Test
	void testMgrUpdateList() throws Exception {
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(priceList).isNotNull();
		priceList.setName("NewListName");
		priceListManager.updateList(priceList);
		assertThat(priceList.getName()).isEqualTo("NewListName");
	}

	@Test
	void testMgrDeleteList() throws Exception {
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).orElse(null);
		assertThat(priceList).isNotNull();
		priceListManager.deleteList(priceList);
		assertThat(priceListIoOperationRepository.count()).isZero();
	}

	@Test
	void testMgrCopyListStep0() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(price).isNotNull();
		PriceList priceList = price.getList();
		priceListManager.copyList(priceList, 2.0, 0);
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isCloseTo(2.0 * price.getPrice(), within(0.10d));
	}

	@Test
	void testMgrCopyListSteps3() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(price).isNotNull();
		PriceList priceList = price.getList();
		priceListManager.copyList(priceList, 2.0, 3.0);
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isCloseTo(Math.round(2.0 * price.getPrice() / 3.0) * 3, within(0.10d));
	}

	@Test
	void testMgrCopyList() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).orElse(null);
		assertThat(price).isNotNull();
		PriceList priceList = price.getList();
		priceListManager.copyList(priceList);
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isEqualTo(price.getPrice());
	}

	@Test
	void testConvertPrice() throws Exception {
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
	void testPriceListValidationCodeIsEmpty() throws Exception {
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
	void testPriceListValidationNameIsEmpty() throws Exception {
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
	void testPriceListValidationDescriptionIsEmpty() throws Exception {
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
	void testPriceListValidationCurrenyIsEmpty() throws Exception {
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

	@Test
	void testPriceToString() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = new Price(priceList, "TG", "TestItem", "TestDescription", 10.10, true);
		assertThat(price).hasToString("TestDescription");
	}

	@Test
	void testPriceEquals() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = new Price(priceList, "TG", "TestItem", "TestDescription", 10.10);

		assertThat(price)
				.isEqualTo(price)
				.isNotNull()
				.isNotEqualTo("someString");

		PriceList priceList2 = testPriceList.setup(true);
		Price price2 = new Price(priceList2, "TG", "TestItem", "TestDescriptionOther", 10.10);
		assertThat(price).isEqualTo(price2);

		price2.setDesc("TestDescription");
		price.setId(-1);
		price2.setId(-99);
		assertThat(price).isNotEqualTo(price2);

		price2.setId(-1);
		assertThat(price).isEqualTo(price2);
	}

	@Test
	void testPriceHashCode() throws Exception {
		PriceList priceList = testPriceList.setup(false);
		Price price = testPrice.setup(priceList, false);
		price.setId(1);
		int hashCode = price.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1);
		// check computed value
		assertThat(price.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testPriceIs() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = new Price(priceList, "TG", "TestItem", "TestDescription", 10.10);

		assertThat(price.isEditable()).isTrue();
		price.setEditable(false);
		assertThat(price.isEditable()).isFalse();

		assertThat(price.isPrice()).isTrue();
		price.setItem("");
		assertThat(price.isPrice()).isFalse();
	}

	@Test
	void testPriceListToString() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		assertThat(priceList).hasToString("TestName");
	}

	@Test
	void testPriceListEquals() throws Exception {
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
	void testPriceListHashCode() throws Exception {
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