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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.OHCoreIntegrationTest;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.accounting.service.AccountingBillItemsIoOperationRepository;
import org.isf.accounting.service.AccountingBillPaymentIoOperationRepository;
import org.isf.accounting.service.AccountingIoOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.patient.test.TestPatientContext;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PriceListIoOperationRepository;
import org.isf.priceslist.test.TestPriceList;
import org.isf.priceslist.test.TestPriceListContext;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


public class Tests extends OHCoreIntegrationTest
{
	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestBillPayments testBillPayments;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;

    @Autowired
    AccountingIoOperations accountingIoOperation;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    
    @Autowired
    AccountingBillIoOperationRepository accountingBillIoOperationRepository;
    @Autowired
    AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository;
    @Autowired
    AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository;
    @Autowired
    PriceListIoOperationRepository priceListIoOperationRepository;
    @Autowired
    PatientIoOperationRepository patientIoOperationRepository;


	@BeforeClass
    public static void setUpClass()  
    {
    	testBill = new TestBill();
    	testBillItems = new TestBillItems();
    	testBillPayments = new TestBillPayments();
    	testPatient = new TestPatient();
    	testPriceList = new TestPriceList();
    }

	@Before
	public void setUp() throws OHException {
		cleanH2InMemoryDb();
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
				
		return;
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
		
		return;
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
				
		return;
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
		
		return;
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
				
		return;
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
		
		return;
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() {
		try {
			// given:
			int id = _setupTestBill(false);
			Bill foundBill = accountingBillIoOperationRepository.findOne(id);
			Patient mergedPatient = _setupTestPatient(false);

			// when:
			applicationEventPublisher.publishEvent(new PatientMergedEvent(foundBill.getPatient(), mergedPatient));

			// then:
			Bill resultBill = accountingBillIoOperationRepository.findOne(id);
			assertEquals(mergedPatient.getCode(), resultBill.getPatient().getCode());
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
			Bill foundBill = accountingBillIoOperationRepository.findOne(id);
			ArrayList<Bill> bills = accountingIoOperation.getPendingBills(0);

			assertTrue(bills.contains(foundBill));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}	
				
		return;
	}

	@Test
	public void testIoGetPendingBillsPatId()
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBill(false);
			Bill foundBill = accountingBillIoOperationRepository.findOne(id);
			ArrayList<Bill> bills = accountingIoOperation.getPendingBills(foundBill.getPatient().getCode());
			
			assertEquals(foundBill.getAmount(), bills.get(0).getAmount(), 0.1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}	
	
	@Test
	public void testIoGetBills() 
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBill(false);
			Bill foundBill = accountingBillIoOperationRepository.findOne(id);
			ArrayList<Bill> bills = accountingIoOperation.getBills();

			assertTrue(bills.contains(foundBill));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}	
	
	@Test
	public void testIoGetBill() 
	{
		int id = 0;
				

		try 
		{
			id = _setupTestBill(false);
			Bill foundBill = accountingBillIoOperationRepository.findOne(id);
			Bill bill = accountingIoOperation.getBill(id);
			
			assertEquals(foundBill.getAmount(), bill.getAmount(), 0.1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}	
	
	@Test
	public void testIoGetUsers()
	{
		int id = 0;
		ArrayList<String> userIds = null;
		
		
		try 
		{
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
			userIds = accountingIoOperation.getUsers();

			assertTrue(userIds.contains(foundBillPayment.getUser()));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}	

	@Test
	public void testIoGetItems() 
	{		
		try 
		{
			int billItemID = _setupTestBillItems(false);
			
			BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(billItemID); 
			ArrayList<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());

			assertTrue(billItems.contains(foundBillItem));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}

	@Test
	public void testIoGetItemsBillId() 
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBillItems(false);
			BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id); 
			ArrayList<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
			
			assertEquals(foundBillItem.getItemAmount(), billItems.get(0).getItemAmount(), 0.1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testIoGetPayments() 
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
			GregorianCalendar dateFrom = new GregorianCalendar(4, 3, 2);
			GregorianCalendar dateTo = new GregorianCalendar();
			ArrayList<BillPayments> billPayments = accountingIoOperation.getPayments(dateFrom, dateTo);

			assertTrue(billPayments.contains(foundBillPayment));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}

	@Test
	public void testIoGetPaymentsBillId()
	{
		int id = 0;
		
		
		try 
		{
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
			ArrayList<BillPayments> billItems = accountingIoOperation.getPayments(foundBillPayment.getBill().getId());
			
			assertEquals(foundBillPayment.getAmount(), billItems.get(0).getAmount(), 0.1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
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
			priceListIoOperationRepository.save(priceList);
			patientIoOperationRepository.save(patient);
			id = accountingIoOperation.newBill(bill);
			_checkBillIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
						
		return;
	}
	
	@Test
	public void testIoNewBillItems()
	{
		ArrayList<BillItems> billItems = new ArrayList<BillItems>(); 
		int deleteId = 0, insertId = 0;
		boolean result = false;
			

		try 
		{
			deleteId = _setupTestBillItems(false);
			BillItems deleteBillItem = accountingBillItemsIoOperationRepository.findOne(deleteId); 
			
			Bill bill = deleteBillItem.getBill();
			BillItems insertBillItem = testBillItems.setup(null, false);
			insertId = deleteId + 1;
			billItems.add(insertBillItem);	
			result = accountingIoOperation.newBillItems(bill, billItems);
			
			BillItems foundBillItems = accountingBillItemsIoOperationRepository.findOne(insertId);
			assertTrue(result);
			assertEquals(bill.getId(), foundBillItems.getBill().getId());		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
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
			BillPayments deleteBillPayment = accountingBillPaymentIoOperationRepository.findOne(deleteId);
			
			Bill bill = deleteBillPayment.getBill();
			BillPayments insertBillPayment = testBillPayments.setup(null, false);
			insertId = deleteId + 1;
			billPayments.add(insertBillPayment);	
			result = accountingIoOperation.newBillPayments(bill, billPayments);
			
			BillPayments foundBillPayments = accountingBillPaymentIoOperationRepository.findOne(insertId);
			assertTrue(result);
			assertEquals(bill.getId(), foundBillPayments.getBill().getId());		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}	
		
		return;
	}	
	
	@Test
	public void testIoUpdateBill() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestBill(true);
			Bill bill = accountingBillIoOperationRepository.findOne(id);
			bill.setAmount(12.34);
			
			accountingIoOperation.updateBill(bill);
			
			assertEquals(12.34, bill.getAmount(), 0.1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
						
		return;
	}

	@Test
	public void testIoDeleteBill()
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestBill(true);
			Bill bill = accountingBillIoOperationRepository.findOne(id);
			
			boolean result = accountingIoOperation.deleteBill(bill);

			assertTrue(result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
						
		return;
	}

	@Test
	public void testIoGetBillsTimeRange() 
	{
		int id = 0;
		
		
		try 
		{		
			id = _setupTestBill(false);
			Bill foundBill = accountingBillIoOperationRepository.findOne(id);
			GregorianCalendar dateFrom = new GregorianCalendar(4, 3, 2);
			GregorianCalendar dateTo = new GregorianCalendar();
			ArrayList<Bill> bills = accountingIoOperation.getBills(dateFrom, dateTo);

			assertTrue(bills.contains(foundBill));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
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
			Bill foundBill = accountingBillIoOperationRepository.findOne(id);
			
			ArrayList<Bill> bills = accountingIoOperation.getBills(dateFrom, dateTo);
			assertTrue(bills.contains(foundBill));
			
			bills = accountingIoOperation.getBills(new GregorianCalendar(10, 0, 1), dateFrom);
			assertFalse(bills.contains(foundBill));
			
			bills = accountingIoOperation.getBills(dateTo, new GregorianCalendar(11, 0, 1));
			assertFalse(bills.contains(foundBill));
			
			id = _setupTestBillItems(false);
			BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
			foundBill = accountingBillIoOperationRepository.findOne(foundBillItem.getBill().getId());
			
			bills = accountingIoOperation.getBills(dateFrom, dateTo, foundBillItem);
			assertTrue(bills.contains(foundBill));

			id = _setupTestBillItems(true);
			foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
			
			bills = accountingIoOperation.getBills(dateFrom, dateTo, foundBillItem);
			assertTrue(bills.contains(foundBill));
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}

	@Test
	public void testIoGetBillsPayment() 
	{
		int id = 0;
		ArrayList<BillPayments> payments = new ArrayList<BillPayments>();
		
		
		try 
		{		
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id); 
			
			payments.add(foundBillPayment);	
			ArrayList<Bill> bills = accountingIoOperation.getBills(payments);
			
			assertEquals(foundBillPayment.getBill().getAmount(), bills.get(0).getAmount(), 0.1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}

	@Test
	public void testIoGetPaymentsBill()
	{
		int id = 0;
		ArrayList<Bill> bills = new ArrayList<Bill>();
		
		
		try 
		{		
			id = _setupTestBillPayments(false);
			BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id); 
			Bill foundBill = foundBillPayment.getBill(); 
			
			bills.add(foundBill);	
			ArrayList<BillPayments> payments = accountingIoOperation.getPayments(bills);
			
			assertEquals(foundBill.getAmount(), payments.get(0).getBill().getAmount(), 0.1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testIoGetDistictsBillItems() throws OHException, OHServiceException {
		int id = _setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertTrue(billItems.contains(foundBillItem));
	}
	
        
	private int _setupTestBill(
			boolean usingSet) throws OHException 
	{
		Bill bill;
		Patient	patient = testPatient.setup(false); 
		PriceList priceList = testPriceList.setup(false);
    	bill = testBill.setup(priceList, patient, usingSet);
		priceListIoOperationRepository.save(priceList);
		patientIoOperationRepository.save(patient);
		accountingBillIoOperationRepository.save(bill);

		return bill.getId();
	}
		
	private void  _checkBillIntoDb(
			int id) throws OHException 
	{
		Bill foundBill;
		

		foundBill = accountingBillIoOperationRepository.findOne(id); 
		testBill.check(foundBill);
		testPriceList.check(foundBill.getList());
		testPatient.check(foundBill.getPatient());
		
		return;
	}
	
	private int _setupTestBillItems(
			boolean usingSet) throws OHException 
	{
		BillItems billItem;
		Patient	patient = testPatient.setup(false); 
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, false);
		

    	billItem = testBillItems.setup(bill, usingSet);
    	priceListIoOperationRepository.save(priceList);
    	patientIoOperationRepository.save(patient);
		accountingBillIoOperationRepository.save(bill);
		accountingBillItemsIoOperationRepository.save(billItem);
    	
		return billItem.getId();		
	}
	
	private void  _checkBillItemsIntoDb(
			int id) throws OHException 
	{
		BillItems foundBillItem;
		

		foundBillItem = accountingBillItemsIoOperationRepository.findOne(id); 
		testBillItems.check(foundBillItem);
		testBill.check(foundBillItem.getBill());
		testPriceList.check(foundBillItem.getBill().getList());
		testPatient.check(foundBillItem.getBill().getPatient());
		
		return;
	}
	
	private int _setupTestBillPayments(
			boolean usingSet) throws OHException 
	{
		BillPayments billPayment;
		Patient	patient = testPatient.setup(false); 
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, false);

    	billPayment = testBillPayments.setup(bill, usingSet);
		priceListIoOperationRepository.save(priceList);
		patientIoOperationRepository.save(patient);
		accountingBillIoOperationRepository.save(bill);
		accountingBillPaymentIoOperationRepository.save(billPayment);
    			
		return billPayment.getId();
	}
		
	private void  _checkBillPaymentsIntoDb(
			int id) throws OHException 
	{
		BillPayments foundBillPayment;
		

		foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id); 
		testBillPayments.check(foundBillPayment);
		testBill.check(foundBillPayment.getBill());
		testPriceList.check(foundBillPayment.getBill().getList());
		testPatient.check(foundBillPayment.getBill().getPatient());
		
		return;
	}

	private Patient _setupTestPatient(
		boolean usingSet) throws OHException
	{
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.save(patient);
		return patient;
	}

}