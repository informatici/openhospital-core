package org.isf.priceslist.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PriceIoOperationRepository;
import org.isf.priceslist.service.PriceListIoOperationRepository;
import org.isf.priceslist.service.PricesListIoOperations;
import org.isf.sms.test.TestSms;
import org.isf.sms.test.TestSmsContext;
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

        return;
    }
	
    @Before
    public void setUp() throws OHException
    {
        jpa.open();
        
        _saveContext();
		
		return;
    }
        
    @After
    public void tearDown() throws Exception 
    {
        _restoreContext();   
        
        jpa.flush();
        jpa.close();
                
        return;
    }

    @AfterClass
    public static void tearDownClass() throws OHException 
    {
    	testPriceList = null;
    	testPriceListContext = null;
    	testPrice = null;
    	testPriceContext = null;

    	return;
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
		assertEquals(priceListIoOperationRepository.findOne(id).getName(), priceLists.get(0).getName());
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
		assertEquals(priceIoOperationRepository.findOne(id).getPrice(), prices.get(0).getPrice());
	}
	
	@Test
	public void testIoUpdatePrices() throws OHException, OHServiceException {
		// given:
		ArrayList<Price> prices = new ArrayList<Price>();
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
		assertTrue(result);
		assertEquals(priceList.getId(), foundPrice.getList().getId());
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
		assertEquals("NewListName", priceList.getName());
	}
	
	@Test
	public void testIoDeleteList() throws OHServiceException, OHException {
		// given:
		int id = _setupTestPriceList(true);
		PriceList priceList = priceListIoOperationRepository.findOne(id);

		// when:
		priceListIoOperation.deleteList(priceList);

		// then:
		assertEquals(0, priceListIoOperationRepository.count());
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
		assertEquals(id+1, copyPrice.getId());
		assertEquals(2 * price.getPrice(), copyPrice.getPrice(), 0.10);
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
		assertEquals(id+1, copyPrice.getId());
		assertEquals(Math.round(2 * price.getPrice() / 3) *3, copyPrice.getPrice(), 0.10);
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
		
		return;
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
		
		return;
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
	
	private void _saveContext() throws OHException 
    {	
		testPriceContext.saveAll(jpa);
		testPriceListContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testPriceContext.deleteNews(jpa);
		testPriceListContext.deleteNews(jpa);
        
        return;
    }
}