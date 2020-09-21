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
package org.isf.accounting.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.test.TestPatient;
import org.isf.patient.test.TestPatientContext;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.test.TestPriceList;
import org.isf.priceslist.test.TestPriceListContext;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests  
{
	private static DbJpaUtil jpa;
	private static BillBrowserManager billBrowserManager;
	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestBillPayments testBillPayments;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;
	private static TestBillContext testBillContext;
	private static TestBillItemsContext testBillItemsContext;
	private static TestBillPaymentsContext testBillPaymentsContext;
	private static TestPatientContext testPatientContext;
	private static TestPriceListContext testPriceListContext;

    @Autowired
    AccountingIoOperations accountingIoOperation;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

	@BeforeClass
    public static void setUpClass()  
    {
		jpa = new DbJpaUtil();
    	testBill = new TestBill();
    	testBillItems = new TestBillItems();
    	testBillPayments = new TestBillPayments();
    	testPatient = new TestPatient();
    	testPriceList = new TestPriceList();
    	testBillContext = new TestBillContext();
    	testBillItemsContext = new TestBillItemsContext();
    	testBillPaymentsContext = new TestBillPaymentsContext();
    	testPatientContext = new TestPatientContext();
    	testPriceListContext = new TestPriceListContext();
    }

    @Before
    public void setUp() throws OHException
    {
	    billBrowserManager = new BillBrowserManager(accountingIoOperation);

        jpa.open();
        
        _saveContext();
    }
        
    @After
    public void tearDown() throws Exception 
    {
        _restoreContext();   
        
        jpa.flush();
        jpa.close();
    }
    
    @AfterClass
    public static void tearDownClass() throws OHException 
    {
    	testBill = null;
    	testBillItems = null;
    	testBillPayments = null;
    	testPatient = null;
    	testPriceList = null;
    	testBillContext = null;
    	testBillItemsContext = null;
    	testBillPaymentsContext = null;
    	testPatientContext = null;
    	testPriceListContext = null;
	}
	
		
	@Test
	public void testBillGets()
	{
		int id = 0;
			

		try 
		{
			id = _setupTestBill(false);
			_checkBillIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();	
			fail();
		}
	}

	@Test
	public void testBillSets() 
	{
		int id = 0;
			

		try 
		{
			id = _setupTestBill(true);
			_checkBillIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testBillItemsGets()
	{
		int id = 0;
			

		try 
		{
			id = _setupTestBillItems(false);
			_checkBillItemsIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testBillItemsSets()
	{
		int id = 0;
			

		try 
		{
			id = _setupTestBillItems(true);
			_checkBillItemsIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testBillPaymentsGets()
	{
		int id = 0;
			

		try 
		{
			id = _setupTestBillPayments(false);
			_checkBillPaymentsIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testBillPaymentsSets() 
	{
		int id = 0;
			

		try 
		{
			id = _setupTestBillPayments(true);
			_checkBillPaymentsIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() {
		try {
			// given:
			int id = _setupTestBill(false);
			Bill foundBill = (Bill)jpa.find(Bill.class, id);
			Patient mergedPatient = _setupTestPatient(false);

			// when:
			applicationEventPublisher.publishEvent(new PatientMergedEvent(foundBill.getPatient(), mergedPatient));

			// then:
			Bill resultBill = (Bill)jpa.find(Bill.class, id);
			assertThat(resultBill.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoGetPendingBills()
	{
		int id = 0;
	
		
		try 
		{
			id = _setupTestBill(false);
			Bill foundBill = (Bill)jpa.find(Bill.class, id); 
			ArrayList<Bill> bills = accountingIoOperation.getPendingBills(0);

			assertThat(bills).contains(foundBill);
		}
		catch (Exception e)
		{
			e.printStackTrace();		
			fail();
		}	
	}

	@Test
	public void testIoGetPendingBillsPatId()
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBill(false);
			Bill foundBill = (Bill)jpa.find(Bill.class, id); 
			ArrayList<Bill> bills = accountingIoOperation.getPendingBills(foundBill.getPatient().getCode());
			
			assertThat(foundBill.getAmount()).isCloseTo(bills.get(0).getAmount(), offset(0.1));
		}
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetBills() 
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBill(false);
			Bill foundBill = (Bill)jpa.find(Bill.class, id); 
			ArrayList<Bill> bills = accountingIoOperation.getBills();

			assertThat(bills).contains(foundBill);
		}
		catch (Exception e)
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetBill() 
	{
		int id = 0;
				

		try 
		{
			id = _setupTestBill(false);
			Bill foundBill = (Bill)jpa.find(Bill.class, id); 
			Bill bill = accountingIoOperation.getBill(id);

			assertThat(bill.getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
		}
		catch (Exception e)
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void ioBillChecks() throws OHException, OHServiceException {
		int id = _setupTestBill(false);
		Bill foundBill = (Bill)jpa.find(Bill.class, id);
		ArrayList<Bill> bills = accountingIoOperation.getBills();
		assertThat(bills).hasSize(1);
		Bill bill = bills.get(0);

		assertThat(bill.equals(bill)).isTrue();
		assertThat(bill.equals(new GregorianCalendar())).isFalse();
		assertThat(bill.equals(foundBill)).isTrue();
		foundBill.setId(-1);
		assertThat(bill.equals(foundBill)).isFalse();
		assertThat(bill.compareTo(foundBill)).isEqualTo(id + 1);   // id - (-1)
		foundBill.setId(id);

		assertThat(bill.hashCode()).isPositive();
	}

	@Test
	public void testIoGetUsers()
	{
		int id = 0;
		ArrayList<String> userIds = null;
		
		
		try 
		{
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = (BillPayments)jpa.find(BillPayments.class, id); 
			userIds = accountingIoOperation.getUsers();

			assertThat(userIds).contains(foundBillPayment.getUser());
		}
		catch (Exception e)
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetItems() 
	{		
		try 
		{
			int billItemID = _setupTestBillItems(false);
			
			BillItems foundBillItem = (BillItems)jpa.find(BillItems.class, billItemID); 
			ArrayList<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());

			assertThat(billItems).contains(foundBillItem);
		}
		catch (Exception e)
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void ioGetAllItems()
	{
		try
		{
			int billItemID = _setupTestBillItems(false);

			ArrayList<BillItems> billItems = accountingIoOperation.getItems(0);

			assertThat(billItems).isNotEmpty();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoGetItemsBillId() 
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBillItems(false);
			BillItems foundBillItem = (BillItems)jpa.find(BillItems.class, id); 
			ArrayList<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());

			assertThat(billItems.get(0).getItemAmount()).isCloseTo(foundBillItem.getItemAmount(), offset(0.1));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetPayments() 
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = (BillPayments)jpa.find(BillPayments.class, id); 
			GregorianCalendar dateFrom = new GregorianCalendar(4, 3, 2);
			GregorianCalendar dateTo = new GregorianCalendar();
			ArrayList<BillPayments> billPayments = accountingIoOperation.getPayments(dateFrom, dateTo);

			assertThat(billPayments).contains(foundBillPayment);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetPaymentsBillId()
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = (BillPayments)jpa.find(BillPayments.class, id); 
			ArrayList<BillPayments> billItems = accountingIoOperation.getPayments(foundBillPayment.getBill().getId());

			assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void ioGetBillsByDateForPatient()
	{
		int id = 0;

		try
		{
			id = _setupTestBill(false);
			Bill foundBill = (Bill)jpa.find(Bill.class, id);
			GregorianCalendar dateFrom = new GregorianCalendar(1, 3, 2);
			GregorianCalendar dateTo = new GregorianCalendar();
			ArrayList<Bill> billItems = accountingIoOperation.getBills(dateFrom, dateTo, foundBill.getPatient());
			assertThat(billItems).isNotEmpty();
			assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void ioGetPendingBills()
	{
		int id = 0;

		try
		{
			id = _setupTestBill(false);
			Bill foundBill = (Bill)jpa.find(Bill.class, id);
			ArrayList<Bill> billItems = accountingIoOperation.getPendingBillsAffiliate(foundBill.getPatient().getCode());
			assertThat(billItems).isNotEmpty();
			assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoNewBill() 
	{
		int id = 0;
		Bill bill = null;
		Patient	patient = null; 
		PriceList priceList = null;
		

		try 
		{
			patient = testPatient.setup(false); 
			priceList = testPriceList.setup(false);
			bill = testBill.setup(priceList, patient, false);
			jpa.beginTransaction();	
			jpa.persist(priceList);
			jpa.persist(patient);
			jpa.commitTransaction();
			id = accountingIoOperation.newBill(bill);
			_checkBillIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewBillItems()
	{
		ArrayList<BillItems> billItems = new ArrayList<>();
		int deleteId = 0, insertId = 0;
		boolean result = false;
			

		try 
		{
			deleteId = _setupTestBillItems(false);
			BillItems deleteBillItem = (BillItems)jpa.find(BillItems.class, deleteId); 
			
			Bill bill = deleteBillItem.getBill();
			BillItems insertBillItem = testBillItems.setup(null, false);
			insertId = deleteId + 1;
			billItems.add(insertBillItem);	
			result = accountingIoOperation.newBillItems(bill, billItems);
			
			BillItems foundBillItems = (BillItems)jpa.find(BillItems.class, insertId);
			assertThat(result).isTrue();
			assertThat(foundBillItems.getBill().getId()).isEqualTo(bill.getId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoNewBillPayments() 
	{
		ArrayList<BillPayments> billPayments = new ArrayList<BillPayments>(); 
		int deleteId = 0, insertId = 0;
		boolean result = false;
			

		try 
		{		
			deleteId = _setupTestBillPayments(false);
			BillPayments deleteBillPayment = (BillPayments)jpa.find(BillPayments.class, deleteId); 
			
			Bill bill = deleteBillPayment.getBill();
			BillPayments insertBillPayment = testBillPayments.setup(null, false);
			insertId = deleteId + 1;
			billPayments.add(insertBillPayment);	
			result = accountingIoOperation.newBillPayments(bill, billPayments);
			
			BillPayments foundBillPayments = (BillPayments)jpa.find(BillPayments.class, insertId);
			assertThat(result).isTrue();
			assertThat(foundBillPayments.getBill().getId()).isEqualTo(bill.getId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}	
	}
	
	@Test
	public void testIoUpdateBill() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestBill(true);
			Bill bill = (Bill)jpa.find(Bill.class, id); 
			bill.setAmount(12.34);
			
			accountingIoOperation.updateBill(bill);

			assertThat(bill.getAmount()).isCloseTo(12.34, offset(0.1));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeleteBill()
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestBill(true);
			Bill bill = (Bill)jpa.find(Bill.class, id); 
			
			boolean result = accountingIoOperation.deleteBill(bill);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetBillsTimeRange() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestBill(false);
			Bill foundBill = (Bill)jpa.find(Bill.class, id); 
			GregorianCalendar dateFrom = new GregorianCalendar(4, 3, 2);
			GregorianCalendar dateTo = new GregorianCalendar();
			ArrayList<Bill> bills = accountingIoOperation.getBills(dateFrom, dateTo);

			assertThat(bills).contains(foundBill);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetBillsTimeRangeAndItem() 
	{
		int id = 0;
		//billDate = new GregorianCalendar(10, 9, 8); //from src/org/isf/accounting/test/TestBill.java
		GregorianCalendar dateFrom = new GregorianCalendar(10, 9, 7);
		GregorianCalendar dateTo = new GregorianCalendar(10, 9, 9);
		
		try 
		{
			id = _setupTestBill(false);
			Bill foundBill = (Bill)jpa.find(Bill.class, id); 
			
			ArrayList<Bill> bills = accountingIoOperation.getBills(dateFrom, dateTo);
			assertThat(bills).contains(foundBill);
			
			bills = accountingIoOperation.getBills(new GregorianCalendar(10, 0, 1), dateFrom);
			assertThat(bills).doesNotContain(foundBill);
			
			bills = accountingIoOperation.getBills(dateTo, new GregorianCalendar(11, 0, 1));
			assertThat(bills).doesNotContain(foundBill);;
			
			id = _setupTestBillItems(false);
			BillItems foundBillItem = (BillItems)jpa.find(BillItems.class, id);
			foundBill = (Bill)jpa.find(Bill.class, foundBillItem.getBill().getId());
			
			bills = accountingIoOperation.getBills(dateFrom, dateTo, foundBillItem);
			assertThat(bills).contains(foundBill);

			bills = accountingIoOperation.getBills(dateFrom, dateTo, (BillItems)null);
			assertThat(bills).contains(foundBill);

			id = _setupTestBillItems(true);
			foundBillItem = (BillItems)jpa.find(BillItems.class, id);
			
			bills = accountingIoOperation.getBills(dateFrom, dateTo, foundBillItem);
			assertThat(bills).contains(foundBill);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetBillsPayment() 
	{
		int id = 0;
		ArrayList<BillPayments> payments = new ArrayList<BillPayments>();
		
		
		try 
		{		
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = (BillPayments)jpa.find(BillPayments.class, id); 
			
			payments.add(foundBillPayment);	
			ArrayList<Bill> bills = accountingIoOperation.getBills(payments);

			assertThat(bills.get(0).getAmount()).isCloseTo(foundBillPayment.getBill().getAmount(), offset(0.1));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetPaymentsBill()
	{
		int id = 0;
		ArrayList<Bill> bills = new ArrayList<>();
		
		
		try 
		{		
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = (BillPayments)jpa.find(BillPayments.class, id); 
			Bill foundBill = foundBillPayment.getBill(); 
			
			bills.add(foundBill);	
			ArrayList<BillPayments> payments = accountingIoOperation.getPayments(bills);

			assertThat(payments.get(0).getBill().getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void ioBillPaymentsChecks() throws OHException, OHServiceException {
		ArrayList<Bill> bills = new ArrayList<>();
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments) jpa.find(BillPayments.class, id);
		Bill foundBill = foundBillPayment.getBill();
		bills.add(foundBill);
		ArrayList<BillPayments> payments = accountingIoOperation.getPayments(bills);
		assertThat(payments).hasSize(1);

		BillPayments billPayment = payments.get(0);
		assertThat(foundBillPayment.equals(foundBillPayment)).isTrue();
		assertThat(foundBillPayment.equals(new GregorianCalendar())).isFalse();
		assertThat(foundBillPayment.equals(billPayment)).isTrue();
		foundBillPayment.setId(-1);
		assertThat(foundBillPayment.equals(billPayment)).isFalse();
		foundBillPayment.setId(id);

		assertThat(billPayment.compareTo(new GregorianCalendar())).isEqualTo(0);
		assertThat(billPayment.compareTo(billPayment)).isEqualTo(0);
		billPayment.setDate(new GregorianCalendar());
		assertThat(billPayment.compareTo(foundBillPayment)).isEqualTo(1);

		assertThat(billPayment.hashCode()).isPositive();
	}

	@Test
	public void ioGetDistictsBillItems() throws OHException, OHServiceException {
		int id = _setupTestBillItems(false);
		BillItems foundBillItem = (BillItems)jpa.find(BillItems.class, id);
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	public void ioBillItemChecks() throws OHException, OHServiceException {
		int id = _setupTestBillItems(false);
		BillItems foundBillItem = (BillItems)jpa.find(BillItems.class, id);
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertThat(billItems).hasSize(1);

		BillItems billItem = billItems.get(0);
		assertThat(foundBillItem.equals(foundBillItem)).isTrue();
		assertThat(foundBillItem.equals(new GregorianCalendar())).isFalse();
		assertThat(foundBillItem.equals(billItem)).isTrue();
		foundBillItem.setId(-1);
		assertThat(foundBillItem.equals(billItem)).isFalse();
		foundBillItem.setId(id);

		String itemId = billItem.getItemId();
		String displayCode = billItem.getItemDisplayCode();
		billItem.setItemDisplayCode(null);
		assertThat(billItem.getItemDisplayCode()).isNull();
		billItem.setItemDisplayCode("");
		assertThat(billItem.getItemDisplayCode()).isNull();
		billItem.setItemId("displayCode");
		assertThat(billItem.getItemDisplayCode()).isEqualTo("displayCode");
		billItem.setItemDisplayCode(displayCode);

		billItem.setItemId(itemId);
		billItem.setItemDisplayCode(displayCode);

		assertThat(billItem.hashCode()).isPositive();
	}

	@Test
	public void ioGetPaymentsByDateForPatient() throws OHException, OHServiceException {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments) jpa.find(BillPayments.class, id);
		GregorianCalendar dateFrom = new GregorianCalendar(1, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		ArrayList<BillPayments> billItems = accountingIoOperation.getPayments(dateFrom, dateTo, foundBillPayment.getBill().getPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	public void mgrInitManager() {
		assertThat(new BillBrowserManager(accountingIoOperation)).isNotNull();
	}

	@Test
	public void mgrBillItemsChecks() throws OHServiceException, OHException {
		int id = _setupTestBillItems(false);
		ArrayList<BillItems> billItems = billBrowserManager.getItems(0);
		assertThat(billItems).isEmpty();
		billItems = billBrowserManager.getItems(99999);
		assertThat(billItems).isEmpty();
		billItems = billBrowserManager.getItems();
		assertThat(billItems).isNotEmpty().hasSize(1);
	}

	@Test
	public void mgrGetPaymentsByDateForPatient() throws OHException, OHServiceException {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments) jpa.find(BillPayments.class, id);
		GregorianCalendar dateFrom = new GregorianCalendar(1, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		ArrayList<BillPayments> billItems = billBrowserManager.getPayments(dateFrom, dateTo, foundBillPayment.getBill().getPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetAllPayments() throws OHException, OHServiceException {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments)jpa.find(BillPayments.class, id);
		ArrayList<BillPayments> billItems = billBrowserManager.getPayments();
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetAllPaymentsWithZero() throws OHException, OHServiceException {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments)jpa.find(BillPayments.class, id);
		ArrayList<BillPayments> billItems = billBrowserManager.getPayments(0);
		assertThat(billItems).isEmpty();
	}

	@Test
	public void mgrGetAllPaymentsWithId() throws OHException, OHServiceException {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments)jpa.find(BillPayments.class, id);
		ArrayList<BillPayments> billItems = billBrowserManager.getPayments(foundBillPayment.getBill().getId());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetDistictsBillItems() throws OHException, OHServiceException {
		int id = _setupTestBillItems(false);
		BillItems foundBillItem = (BillItems)jpa.find(BillItems.class, id);
		List<BillItems> billItems = billBrowserManager.getDistinctItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	public void mgrGetBillsBetweenDatesWherePatient() throws OHException, OHServiceException {
		int id = _setupTestBill(false);
		Bill foundBill = (Bill)jpa.find(Bill.class, id);
		GregorianCalendar dateFrom = new GregorianCalendar(1, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		ArrayList<Bill> billItems = billBrowserManager.getBills(dateFrom, dateTo, foundBill.getPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetPendingBillsForPatientId() throws OHException, OHServiceException {
		int id = _setupTestBill(false);
		Bill foundBill = (Bill)jpa.find(Bill.class, id);
		ArrayList<Bill> bills = billBrowserManager.getPendingBills(foundBill.getPatient().getCode());
		assertThat(bills.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrNewBillNoItemsNoPayments() throws OHException, OHServiceException {
		int billId = _setupTestBill(false);
		int billItemsId = _setupTestBillItems(false);

		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		jpa.beginTransaction();
		jpa.persist(priceList);
		jpa.persist(patient);
		jpa.commitTransaction();
		Bill bill = testBill.setup(priceList, patient, false);
		boolean success = billBrowserManager.newBill(
				bill,
				new ArrayList<BillItems>(),
				new ArrayList<BillPayments>());
		assertThat(success).isTrue();
	}

	@Test
	public void mgrNewBillBillItemsNoPayments() throws OHException, OHServiceException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		jpa.beginTransaction();
		jpa.persist(priceList);
		jpa.persist(patient);
		jpa.commitTransaction();
		Bill bill = testBill.setup(priceList, patient, false);
		BillItems insertBillItem = testBillItems.setup(null, false);
		ArrayList<BillItems> billItems = new ArrayList<>();
		billItems.add(insertBillItem);
		boolean success = billBrowserManager.newBill(
				bill,
				billItems,
				new ArrayList<BillPayments>());
		assertThat(success).isTrue();
	}

	@Test
	public void mgrNewBillNoItemsAndPayments() throws OHException, OHServiceException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		jpa.beginTransaction();
		jpa.persist(priceList);
		jpa.persist(patient);
		jpa.commitTransaction();
		Bill bill = testBill.setup(priceList, patient, false);
		BillPayments insertBillPayment = testBillPayments.setup(bill, false);
		insertBillPayment.setDate(new GregorianCalendar());
		ArrayList<BillPayments> billPayments = new ArrayList<BillPayments>();
		billPayments.add(insertBillPayment);
		boolean success = billBrowserManager.newBill(
				bill,
				new ArrayList<>(),
				billPayments);
		assertThat(success).isTrue();
	}

	@Test
	public void mgrNewBillItemsAndPayments() throws OHException, OHServiceException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		jpa.beginTransaction();
		jpa.persist(priceList);
		jpa.persist(patient);
		jpa.commitTransaction();
		Bill bill = testBill.setup(priceList, patient, false);
		BillItems insertBillItem = testBillItems.setup(bill, false);
		BillPayments insertBillPayment = testBillPayments.setup(bill, false);
		insertBillPayment.setDate(new GregorianCalendar());
		ArrayList<BillItems> billItems = new ArrayList<>();
		billItems.add(insertBillItem);
		ArrayList<BillPayments> billPayments = new ArrayList<BillPayments>();
		billPayments.add(insertBillPayment);
		BillPayments payments = testBillPayments.setup(bill, false);
		boolean success = billBrowserManager.newBill(
				bill,
				billItems,
				billPayments);
		assertThat(success).isTrue();
	}

	@Test
	public void mgrNewBillFailValidation() throws OHException, OHServiceException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, false);
		ArrayList<BillItems> billItems = new ArrayList<>();
		BillItems insertBillItem = testBillItems.setup(bill, false);
		billItems.add(insertBillItem);
		ArrayList<BillPayments> billPayments = new ArrayList<BillPayments>();
		BillPayments payments = testBillPayments.setup(bill, false);
		billPayments.add(payments);

		assertThatThrownBy(() -> billBrowserManager.newBill(bill, billItems, billPayments))
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void mgrGetBillsPayment() throws OHException, OHServiceException {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments) jpa.find(BillPayments.class, id);

		ArrayList<BillPayments> payments = new ArrayList<>();
		payments.add(foundBillPayment);
		ArrayList<Bill> bills = billBrowserManager.getBills(payments);

		assertThat(bills.get(0).getAmount()).isCloseTo(foundBillPayment.getBill().getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetBillsPaymentEmpty() throws OHException, OHServiceException {
		ArrayList<Bill> bills = billBrowserManager.getBills(new ArrayList<>());
		assertThat(bills).isEmpty();
	}

	@Test
	public void mgrGetPayments() throws OHException, OHServiceException {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments) jpa.find(BillPayments.class, id);
		GregorianCalendar dateFrom = new GregorianCalendar(4, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		ArrayList<BillPayments> billPayments = billBrowserManager.getPayments(dateFrom, dateTo);
		assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	public void mgrGetPaymentsBill() throws OHException, OHServiceException {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments) jpa.find(BillPayments.class, id);
		Bill foundBill = foundBillPayment.getBill();
		ArrayList<Bill> bills = new ArrayList<>();
		bills.add(foundBill);
		ArrayList<BillPayments> payments = billBrowserManager.getPayments(bills);
		assertThat(payments.get(0).getBill().getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetBills() throws OHException, OHServiceException {
		GregorianCalendar dateFrom = new GregorianCalendar(10, 9, 7);
		GregorianCalendar dateTo = new GregorianCalendar(10, 9, 9);

		int	id = _setupTestBill(false);
		Bill foundBill = (Bill)jpa.find(Bill.class, id);

		ArrayList<Bill> bills = billBrowserManager.getBills(dateFrom, dateTo);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills(new GregorianCalendar(10, 0, 1), dateFrom);
		assertThat(bills).doesNotContain(foundBill);

		bills = billBrowserManager.getBills(dateTo, new GregorianCalendar(11, 0, 1));
		assertThat(bills).doesNotContain(foundBill);

		id = _setupTestBillItems(false);
		BillItems foundBillItem = (BillItems)jpa.find(BillItems.class, id);
		foundBill = (Bill)jpa.find(Bill.class, foundBillItem.getBill().getId());

		bills = billBrowserManager.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills(dateFrom, dateTo, (BillItems)null);
		assertThat(bills).contains(foundBill);

		id = _setupTestBillItems(true);
		foundBillItem = (BillItems)jpa.find(BillItems.class, id);

		bills = billBrowserManager.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills();
		assertThat(bills).contains(foundBill);
	}

	@Test
	public void mgrGetBill() throws OHException, OHServiceException {
		int id = _setupTestBill(false);
		Bill foundBill = (Bill) jpa.find(Bill.class, id);
		Bill bill = billBrowserManager.getBill(id);
		assertThat(bill.getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetPendingBillsAffiliate() throws OHException, OHServiceException {
		int id = _setupTestBill(false);
		Bill foundBill = (Bill) jpa.find(Bill.class, id);
		ArrayList<Bill> billItems = billBrowserManager.getPendingBillsAffiliate(foundBill.getPatient().getCode());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrUpdateBillNoItemsNoPayements() throws OHException, OHServiceException {
		int id = 0;
		id = _setupTestBill(true);
		Bill bill = (Bill) jpa.find(Bill.class, id);
		bill.setAmount(12.34);
		assertThat(billBrowserManager.updateBill(
				bill,
				new ArrayList<BillItems>(),
				new ArrayList<BillPayments>())).isTrue();
		bill = (Bill) jpa.find(Bill.class, id);
		assertThat(bill.getAmount()).isCloseTo(12.34, offset(0.1));
	}

	@Test
	public void msgDeleteBill() throws OHException, OHServiceException {
		int id = _setupTestBill(true);
		Bill bill = (Bill) jpa.find(Bill.class, id);
		boolean result = billBrowserManager.deleteBill(bill);
		assertThat(result).isTrue();
	}

	@Test
	public void mgrGetUsers() throws OHException, OHServiceException {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = (BillPayments) jpa.find(BillPayments.class, id);
		ArrayList<String> userIds = billBrowserManager.getUsers();
		assertThat(userIds).isNotEmpty();
		assertThat(userIds).contains(foundBillPayment.getUser());
	}

	private void _saveContext() throws OHException
    {	
		testBillContext.saveAll(jpa);
		testBillItemsContext.saveAll(jpa);
		testBillPaymentsContext.saveAll(jpa);
		testPatientContext.saveAll(jpa);
		testPriceListContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testBillPaymentsContext.deleteNews(jpa);
		testBillItemsContext.deleteNews(jpa);
		testBillContext.deleteNews(jpa);
		testPatientContext.deleteNews(jpa);
		testPriceListContext.deleteNews(jpa);
    }
        
	private int _setupTestBill(
			boolean usingSet) throws OHException 
	{
		Bill bill;
		Patient	patient = testPatient.setup(false); 
		PriceList priceList = testPriceList.setup(false);
		

    	jpa.beginTransaction();	
    	bill = testBill.setup(priceList, patient, usingSet);
    	jpa.persist(priceList);
    	jpa.persist(patient);
		jpa.persist(bill);
    	jpa.commitTransaction();
    	
		return bill.getId();
	}
		
	private void  _checkBillIntoDb(
			int id) throws OHException 
	{
		Bill foundBill;
		

		foundBill = (Bill)jpa.find(Bill.class, id); 
		testBill.check(foundBill);
		testPriceList.check(foundBill.getList());
		testPatient.check(foundBill.getPatient());
	}
	
	private int _setupTestBillItems(
			boolean usingSet) throws OHException 
	{
		BillItems billItem;
		Patient	patient = testPatient.setup(false); 
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, false);
		

    	jpa.beginTransaction();	
    	billItem = testBillItems.setup(bill, usingSet);
    	jpa.persist(priceList);
    	jpa.persist(patient);
		jpa.persist(bill);
		jpa.persist(billItem);
    	jpa.commitTransaction();
    	
		return billItem.getId();		
	}
	
	private void  _checkBillItemsIntoDb(
			int id) throws OHException 
	{
		BillItems foundBillItem;
		

		foundBillItem = (BillItems)jpa.find(BillItems.class, id); 
		testBillItems.check(foundBillItem);
		testBill.check(foundBillItem.getBill());
		testPriceList.check(foundBillItem.getBill().getList());
		testPatient.check(foundBillItem.getBill().getPatient());
	}
	
	private int _setupTestBillPayments(
			boolean usingSet) throws OHException 
	{
		BillPayments billPayment;
		Patient	patient = testPatient.setup(false); 
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, false);
		

    	jpa.beginTransaction();	
    	billPayment = testBillPayments.setup(bill, usingSet);
    	jpa.persist(priceList);
    	jpa.persist(patient);
		jpa.persist(bill);
		jpa.persist(billPayment);
    	jpa.commitTransaction();
    			
		return billPayment.getId();
	}
		
	private void  _checkBillPaymentsIntoDb(
			int id) throws OHException 
	{
		BillPayments foundBillPayment;
		

		foundBillPayment = (BillPayments)jpa.find(BillPayments.class, id); 
		testBillPayments.check(foundBillPayment);
		testBill.check(foundBillPayment.getBill());
		testPriceList.check(foundBillPayment.getBill().getList());
		testPatient.check(foundBillPayment.getBill().getPatient());
	}

	private Patient _setupTestPatient(
		boolean usingSet) throws OHException
	{
		Patient patient;


		jpa.beginTransaction();
		patient = testPatient.setup(usingSet);
		jpa.persist(patient);
		jpa.commitTransaction();

		return patient;
	}
}
