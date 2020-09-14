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

import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PriceIoOperationRepository;
import org.isf.priceslist.service.PriceListIoOperationRepository;
import org.isf.priceslist.service.PricesListIoOperations;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests 
{
	private static TestPriceList testPriceList;
	private static TestPriceListContext testPriceListContext;
	private static TestPrice testPrice;
	private static TestPriceContext testPriceContext;
	private static DbJpaUtil jpa;

    @Autowired
    PricesListIoOperations priceListIoOperation;
    @Autowired
    PriceListIoOperationRepository priceListIoOperationRepository;
    @Autowired
    PriceIoOperationRepository priceIoOperationRepository;
		
	@BeforeClass
    public static void setUpClass()  
    {
		jpa = new DbJpaUtil();
    	testPriceList = new TestPriceList();
    	testPriceListContext = new TestPriceListContext();
    	testPrice = new TestPrice();
    	testPriceContext = new TestPriceContext();
    }

    @Before
    @After
    public void setUp() throws OHException {

		priceIoOperationRepository.deleteAll();
		priceListIoOperationRepository.deleteAll();
    }
    
    @AfterClass
    public static void tearDownClass() throws OHException 
    {
    	testPriceList = null;
    	testPriceListContext = null;
    	testPrice = null;
    	testPriceContext = null;
    }
	
		
	@Test
	public void testPriceListGets() throws OHException {
		// given:
		int id = _setupTestPriceList(false);

		// when, then:
		_checkPriceListIntoDb(id);
	}
	
	@Test
	public void testPriceListSets() throws OHException {
		// given, when:
		int id = _setupTestPriceList(true);

		// then:
		_checkPriceListIntoDb(id);
	}
	
	@Test
	public void testIoGetLists() throws OHException, OHServiceException {
		// given:
		int id = _setupTestPriceList(true);

		// when:
		ArrayList<PriceList> priceLists = priceListIoOperation.getLists();
			
		// then:
		assertThat(priceLists.get(0).getName()).isEqualTo(priceListIoOperationRepository.findOne(id).getName());
	}
	
	@Test
	public void testPriceGets() throws OHException {
		// given:
		int id = _setupTestPrice(false);

		// when, then:
		_checkPriceIntoDb(id);
	}
	
	@Test
	public void testPriceSets() throws OHException {
		// given, when:
		int id = _setupTestPrice(false);

		// then:
		_checkPriceIntoDb(id);
	}
	
	@Test
	public void testIoGetPrices() throws OHException, OHServiceException {
		// given:
		int id = _setupTestPrice(false);

		// when:
		ArrayList<Price> prices = priceListIoOperation.getPrices();
			
		// then:
		assertThat(prices.get(0).getPrice()).isEqualTo(priceIoOperationRepository.findOne(id).getPrice());
	}
	
	@Test
	public void testIoUpdatePrices() throws OHException, OHServiceException {
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
	public void testIoNewList() throws OHServiceException, OHException {
		// given:
		PriceList pricelist = testPriceList.setup(false);

		// when:
		priceListIoOperation.newList(pricelist);

		// then:
		PriceList foundPriceList = priceListIoOperationRepository.findOne(pricelist.getId());
		_checkPriceListIntoDb(foundPriceList.getId());
	}
	
	@Test
	public void testIoUpdateList() throws OHException, OHServiceException {
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
	public void testIoDeleteList() throws OHServiceException, OHException {
		// given:
		int id = _setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findOne(id);

		// when:
		priceListIoOperation.deleteList(priceList);

		// then:
		assertThat(priceListIoOperationRepository.count()).isZero();
	}
	
	@Test
	public void testIoCopyList() throws OHException, OHServiceException {
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
	public void testIoCopyListSteps() throws OHException, OHServiceException {
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


	private int _setupTestPriceList(
			boolean usingSet) throws OHException {
		PriceList priceList = testPriceList.setup(usingSet);
		priceListIoOperationRepository.save(priceList);
		
		return priceList.getId();
	}
	
	private void _checkPriceListIntoDb(
			int id) throws OHException 
	{
		PriceList foundPriceList;
		

		foundPriceList = priceListIoOperationRepository.findOne(id);
		testPriceList.check(foundPriceList);
	}
	
    private int _setupTestPrice(boolean usingSet) throws OHException {
		PriceList priceList = testPriceList.setup(true);
		Price price = testPrice.setup(priceList, usingSet);
		priceListIoOperationRepository.save(priceList);
		priceIoOperationRepository.save(price);

		return price.getId();
	}
	
	private void _checkPriceIntoDb(int id) throws OHException {
		Price foundPrice;
		
		
		foundPrice = priceIoOperationRepository.findOne(id);
		testPrice.check(foundPrice);
		testPriceList.check(foundPrice.getList());
	}
	
	private int _getListMax() throws OHException 
	{
		String query = null;
		Integer id = 0;
				

		jpa.beginTransaction();		
		
		try {
			query = "SELECT MAX(LST_ID) FROM PRICELISTS";
			jpa.createQuery(query, null, false);
			id = (Integer)jpa.getResult();
		}  catch (OHException e) {
			e.printStackTrace();
		} 				
	
		jpa.commitTransaction();

		return id;
	}	

	
	private int _getPriceMax() throws OHException 
	{
		String query = null;
		Integer id = 0;
				

		jpa.beginTransaction();		
		
		try {
			query = "SELECT MAX(PRC_ID) FROM PRICES";
			jpa.createQuery(query, null, false);
			id = (Integer)jpa.getResult();
		}  catch (OHException e) {
			e.printStackTrace();
		} 				
	
		jpa.commitTransaction();

		return id;
	}
}