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
package org.isf.priceslist.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PriceIoOperationRepository;
import org.isf.priceslist.service.PriceListIoOperationRepository;
import org.isf.priceslist.service.PricesListIoOperations;
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
	PriceListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	PriceIoOperationRepository priceIoOperationRepository;

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
		int id = _setupTestPriceList(false);

		// when, then:
		_checkPriceListIntoDb(id);
	}

	@Test
	public void testPriceListSets() throws Exception {
		// given, when:
		int id = _setupTestPriceList(true);

		// then:
		_checkPriceListIntoDb(id);
	}

	@Test
	public void testIoGetLists() throws Exception {
		// given:
		int id = _setupTestPriceList(true);

		// when:
		ArrayList<PriceList> priceLists = priceListIoOperation.getLists();

		// then:
		assertThat(priceLists.get(0).getName()).isEqualTo(priceListIoOperationRepository.findOne(id).getName());
	}

	@Test
	public void testPriceGets() throws Exception {
		// given:
		int id = _setupTestPrice(false);

		// when, then:
		_checkPriceIntoDb(id);
	}

	@Test
	public void testPriceSets() throws Exception {
		// given, when:
		int id = _setupTestPrice(false);

		// then:
		_checkPriceIntoDb(id);
	}

	@Test
	public void testIoGetPrices() throws Exception {
		// given:
		int id = _setupTestPrice(false);

		// when:
		ArrayList<Price> prices = priceListIoOperation.getPrices();

		// then:
		assertThat(prices.get(0).getPrice()).isEqualTo(priceIoOperationRepository.findOne(id).getPrice());
	}

	@Test
	public void testIoUpdatePrices() throws Exception {
		// given:
		ArrayList<Price> prices = new ArrayList<>();
		int deleteId = _setupTestPrice(false);
		Price deletePrice = priceIoOperationRepository.findOne(deleteId);

		// when:
		PriceList priceList = deletePrice.getList();
		Price insertPrice = testPrice.setup(null, false);
		int insertId = deleteId + 1;
		prices.add(insertPrice);
		boolean result = priceListIoOperation.updatePrices(priceList, prices);

		// then:
		Price foundPrice = priceIoOperationRepository.findOne(insertId);
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
		PriceList foundPriceList = priceListIoOperationRepository.findOne(pricelist.getId());
		_checkPriceListIntoDb(foundPriceList.getId());
	}

	@Test
	public void testIoUpdateList() throws Exception {
		// given:
		int id = _setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findOne(id);
		priceList.setName("NewListName");

		// when:
		priceListIoOperation.updateList(priceList);

		// then:
		assertThat(priceList.getName()).isEqualTo("NewListName");
	}

	@Test
	public void testIoDeleteList() throws Exception {
		// given:
		int id = _setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findOne(id);

		// when:
		priceListIoOperation.deleteList(priceList);

		// then:
		assertThat(priceListIoOperationRepository.count()).isZero();
	}

	@Test
	public void testIoCopyList() throws Exception {
		// given:
		int id = _setupTestPrice(true);
		Price price = priceIoOperationRepository.findOne(id);
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
		int id = _setupTestPrice(true);
		Price price = priceIoOperationRepository.findOne(id);
		PriceList priceList = price.getList();

		// when:
		priceListIoOperation.copyList(priceList, 2, 3);

		// then:
		Price copyPrice = priceIoOperationRepository.findAll().get(1);
		assertThat(copyPrice.getId()).isEqualTo(id + 1);
		assertThat(copyPrice.getPrice()).isCloseTo(Math.round(2 * price.getPrice() / 3) * 3, within(0.10D));
	}

	private int _setupTestPriceList(boolean usingSet) throws Exception {
		PriceList priceList = testPriceList.setup(usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		return priceList.getId();
	}

	private void _checkPriceListIntoDb(int id) throws Exception {
		PriceList foundPriceList;
		foundPriceList = priceListIoOperationRepository.findOne(id);
		testPriceList.check(foundPriceList);
	}

	private int _setupTestPrice(boolean usingSet) throws Exception {
		PriceList priceList = testPriceList.setup(true);
		Price price = testPrice.setup(priceList, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		priceIoOperationRepository.saveAndFlush(price);
		return price.getId();
	}

	private void _checkPriceIntoDb(int id) throws Exception {
		Price foundPrice = priceIoOperationRepository.findOne(id);
		testPrice.check(foundPrice);
		testPriceList.check(foundPrice.getList());
	}
}