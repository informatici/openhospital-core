/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.priceslist.test;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

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

	@BeforeClass
	public static void setUpClass() {
		testPriceList = new TestPriceList();
		testPrice = new TestPrice();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testPriceListGets() throws Exception {
		// given:
		int id = setupTestPriceList(false);

		// when, then:
		checkPriceListIntoDb(id);
	}

	@Test
	public void testPriceListSets() throws Exception {
		// given, when:
		int id = setupTestPriceList(true);

		// then:
		checkPriceListIntoDb(id);
	}

	@Test
	public void testIoGetLists() throws Exception {
		// given:
		int id = setupTestPriceList(true);

		// when:
		List<PriceList> priceLists = priceListIoOperation.getLists();

		// then:
		assertThat(priceLists.get(0).getName()).isEqualTo(priceListIoOperationRepository.findById(id).get().getName());
	}

	@Test
	public void testPriceGets() throws Exception {
		// given:
		int id = setupTestPrice(false);

		// when, then:
		checkPriceIntoDb(id);
	}

	@Test
	public void testPriceSets() throws Exception {
		// given, when:
		int id = setupTestPrice(false);

		// then:
		checkPriceIntoDb(id);
	}

	@Test
	public void testIoGetPrices() throws Exception {
		// given:
		int id = setupTestPrice(false);

		// when:
		List<Price> prices = priceListIoOperation.getPrices();

		// then:
		assertThat(prices.get(0).getPrice()).isEqualTo(priceIoOperationRepository.findById(id).get().getPrice());
	}

	@Test
	public void testIoUpdatePrices() throws Exception {
		// given:
		ArrayList<Price> prices = new ArrayList<>();
		int deleteId = setupTestPrice(false);
		Price deletePrice = priceIoOperationRepository.findById(deleteId).get();

		// when:
		PriceList priceList = deletePrice.getList();
		Price insertPrice = testPrice.setup(null, false);
		int insertId = deleteId + 1;
		prices.add(insertPrice);
		boolean result = priceListIoOperation.updatePrices(priceList, prices);

		// then:
		Price foundPrice = priceIoOperationRepository.findById(insertId).get();
		assertThat(result).isTrue();
		assertThat(foundPrice.getList().getId()).isEqualTo(priceList.getId());
	}

	@Test
	public void testIoNewList() throws Exception {
		// given:
		PriceList pricelist = testPriceList.setup(false);

		// when:
		priceListIoOperation.newList(pricelist);

		// then:
		PriceList foundPriceList = priceListIoOperationRepository.findById(pricelist.getId()).get();
		checkPriceListIntoDb(foundPriceList.getId());
	}

	@Test
	public void testIoUpdateList() throws Exception {
		// given:
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).get();
		priceList.setName("NewListName");

		// when:
		priceListIoOperation.updateList(priceList);

		// then:
		assertThat(priceList.getName()).isEqualTo("NewListName");
	}

	@Test
	public void testIoDeleteList() throws Exception {
		// given:
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).get();

		// when:
		priceListIoOperation.deleteList(priceList);

		// then:
		assertThat(priceListIoOperationRepository.count()).isZero();
	}

	@Test
	public void testIoCopyList() throws Exception {
		// given:
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).get();
		PriceList priceList = price.getList();

		// when:
		priceListIoOperation.copyList(priceList, 2, 0);

		// then:
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isCloseTo(2 * price.getPrice(), within(0.10D));
	}

	@Test
	public void testIoCopyListSteps() throws Exception {
		// given:
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).get();
		PriceList priceList = price.getList();

		// when:
		priceListIoOperation.copyList(priceList, 2, 3);

		// then:
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isCloseTo(Math.round(2 * price.getPrice() / 3) * 3, within(0.10D));
	}

	@Test
	public void testMgrGetLists() throws Exception {
		int id = setupTestPriceList(true);
		List<PriceList> priceLists = priceListManager.getLists();
		assertThat(priceLists.get(0).getName()).isEqualTo(priceListIoOperationRepository.findById(id).get().getName());
	}

	@Test
	public void testMgrGetPrices() throws Exception {
		int id = setupTestPrice(false);
		List<Price> prices = priceListManager.getPrices();
		assertThat(prices.get(0).getPrice()).isEqualTo(priceIoOperationRepository.findById(id).get().getPrice());
	}

	@Test
	public void testMgrUpdatePrices() throws Exception {
		ArrayList<Price> prices = new ArrayList<>();
		int deleteId = setupTestPrice(false);
		Price deletePrice = priceIoOperationRepository.findById(deleteId).get();
		PriceList priceList = deletePrice.getList();
		Price insertPrice = testPrice.setup(null, false);
		int insertId = deleteId + 1;
		prices.add(insertPrice);
		assertThat(priceListManager.updatePrices(priceList, prices)).isTrue();
		Price foundPrice = priceIoOperationRepository.findById(insertId).get();
		assertThat(foundPrice.getList().getId()).isEqualTo(priceList.getId());
	}

	@Test
	public void testMgrNewList() throws Exception {
		PriceList pricelist = testPriceList.setup(false);
		priceListManager.newList(pricelist);
		PriceList foundPriceList = priceListIoOperationRepository.findById(pricelist.getId()).get();
		checkPriceListIntoDb(foundPriceList.getId());
	}

	@Test
	public void testMgrUpdateList() throws Exception {
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).get();
		priceList.setName("NewListName");
		priceListManager.updateList(priceList);
		assertThat(priceList.getName()).isEqualTo("NewListName");
	}

	@Test
	public void testMgrDeleteList() throws Exception {
		int id = setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findById(id).get();
		priceListManager.deleteList(priceList);
		assertThat(priceListIoOperationRepository.count()).isZero();
	}

	@Test
	public void testMgrCopyListStep0() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).get();
		PriceList priceList = price.getList();
		priceListManager.copyList(priceList, 2, 0);
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isCloseTo(2 * price.getPrice(), within(0.10D));
	}

	@Test
	public void testMgrCopyListSteps3() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).get();
		PriceList priceList = price.getList();
		priceListManager.copyList(priceList, 2, 3);
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isCloseTo(Math.round(2 * price.getPrice() / 3) * 3, within(0.10D));
	}

	@Test
	public void testMgrCopyList() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).get();
		PriceList priceList = price.getList();
		priceListManager.copyList(priceList);
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isEqualTo(price.getPrice());
	}

	@Test
	public void testConvertPrice() throws Exception {
		int id = setupTestPrice(true);
		Price price = priceIoOperationRepository.findById(id).get();
		PriceList priceList = price.getList();
		List<PriceForPrint> priceForPrints = priceListManager.convertPrice(priceList, priceListManager.getPrices());
		assertThat(priceForPrints).isNotNull();
		assertThat(priceForPrints.get(0))
				.extracting(PriceForPrint::getPrice, PriceForPrint::getCurrency)
				.containsExactly(price.getPrice(), priceList.getCurrency());
	}

	@Test
	public void testPriceListValidationCodeIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PriceList priceList = testPriceList.setup(false);
			priceList.setCode("");
			priceListManager.newList(priceList);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testPriceListValidationNameIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PriceList priceList = testPriceList.setup(false);
			priceList.setName("");
			priceListManager.newList(priceList);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testPriceListValidationDescriptionIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PriceList priceList = testPriceList.setup(false);
			priceList.setDescription("");
			priceListManager.newList(priceList);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testPriceListValidationCurrenyIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PriceList priceList = testPriceList.setup(false);
			priceList.setCurrency("");
			priceListManager.newList(priceList);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testPriceToString() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = new Price(priceList, "TG", "TestItem", "TestDescription", 10.10, true);
		assertThat(price).hasToString("TestDescription");
	}

	@Test
	public void testPriceEquals() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = new Price(priceList, "TG", "TestItem", "TestDescription", 10.10);

		assertThat(price.equals(price)).isTrue();
		assertThat(price)
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
	public void testPriceHashCode() throws Exception {
		PriceList priceList = testPriceList.setup(false);
		Price price = testPrice.setup(priceList, false);
		price.setId(1);
		int hashCode = price.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1);
		// check computed value
		assertThat(price.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testPriceIs() throws Exception {
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
	public void testPriceListToString() throws Exception {
		PriceList priceList = testPriceList.setup(true);
		assertThat(priceList).hasToString("TestName");
	}

	@Test
	public void testPriceListEquals() throws Exception {
		PriceList priceList = testPriceList.setup(true);

		assertThat(priceList.equals(priceList)).isTrue();
		assertThat(priceList)
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
	public void testPriceListHashCode() throws Exception {
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
		foundPriceList = priceListIoOperationRepository.findById(id).get();
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
		Price foundPrice = priceIoOperationRepository.findById(id).get();
		testPrice.check(foundPrice);
		testPriceList.check(foundPrice.getList());
	}
}