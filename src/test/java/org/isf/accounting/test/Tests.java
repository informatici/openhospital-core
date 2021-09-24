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
package org.isf.accounting.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.accounting.manager.BillBrowserManager;
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
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PriceListIoOperationRepository;
import org.isf.priceslist.test.TestPriceList;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class Tests extends OHCoreTestCase {

	private static BillBrowserManager billBrowserManager;
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
	public static void setUpClass() {
		testBill = new TestBill();
		testBillItems = new TestBillItems();
		testBillPayments = new TestBillPayments();
		testPatient = new TestPatient();
		testPriceList = new TestPriceList();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
		billBrowserManager = new BillBrowserManager(accountingIoOperation);
	}

	@Test
	public void testBillGets() throws Exception {
		int id = _setupTestBill(false);
		_checkBillIntoDb(id);
	}

	@Test
	public void testBillSets() throws Exception {
		int id = _setupTestBill(true);
		_checkBillIntoDb(id);
	}

	@Test
	public void testBillItemsGets() throws Exception {
		int id = _setupTestBillItems(false);
		_checkBillItemsIntoDb(id);
	}

	@Test
	public void testBillItemsSets() throws Exception {
		int id = _setupTestBillItems(true);
		_checkBillItemsIntoDb(id);
	}

	@Test
	public void testBillPaymentsGets() throws Exception {
		int id = _setupTestBillPayments(false);
		_checkBillPaymentsIntoDb(id);
	}

	@Test
	public void testBillPaymentsSets() throws Exception {
		int id = _setupTestBillPayments(true);
		_checkBillPaymentsIntoDb(id);
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		Patient mergedPatient = _setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(foundBill.getBillPatient(), mergedPatient));

		// then:
		Bill resultBill = accountingBillIoOperationRepository.findOne(id);
		assertThat(resultBill.getBillPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	@Test
	public void testIoGetPendingBills() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		List<Bill> bills = accountingIoOperation.getPendingBills(0);
		assertThat(bills).contains(foundBill);
	}

	@Test
	public void testIoGetPendingBillsPatId() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		List<Bill> bills = accountingIoOperation.getPendingBills(foundBill.getBillPatient().getCode());
		assertThat(foundBill.getAmount()).isCloseTo(bills.get(0).getAmount(), offset(0.1));
	}

	@Test
	public void testIoGetBills() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		List<Bill> bills = accountingIoOperation.getBills();
		assertThat(bills).contains(foundBill);
	}

	@Test
	public void testIoGetBill() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		Bill bill = accountingIoOperation.getBill(id);
		assertThat(bill.getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void ioBillChecks() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		List<Bill> bills = accountingIoOperation.getBills();
		assertThat(bills).hasSize(1);
		Bill bill = bills.get(0);

		int id2 = _setupTestBill(false);
		Bill foundBill2 = accountingBillIoOperationRepository.findOne(id2);

		assertThat(bill.equals(bill)).isTrue();
		assertThat(bill)
				.isNotEqualTo(new GregorianCalendar())
				.isEqualTo(foundBill);
		foundBill2.setId(-1);
		assertThat(bill).isNotEqualTo(foundBill2);
		assertThat(bill.compareTo(foundBill2)).isEqualTo(id + 1);   // id - (-1)
		foundBill.setId(id);

		assertThat(bill.hashCode()).isPositive();
	}

	@Test
	public void testIoGetUsers() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		List<String> userIds = accountingIoOperation.getUsers();
		assertThat(userIds).contains(foundBillPayment.getUser());
	}

	@Test
	public void testIoGetItems() throws Exception {
		int billItemID = _setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(billItemID);
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	public void ioGetAllItems() throws Exception {
		int billItemID = _setupTestBillItems(false);
		List<BillItems> billItems = accountingIoOperation.getItems(0);
		assertThat(billItems).isNotEmpty();
	}

	@Test
	public void testIoGetItemsBillId() throws Exception {
		int id = _setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		assertThat(billItems.get(0).getItemAmount()).isCloseTo(foundBillItem.getItemAmount(), offset(0.1));
	}

	@Test
	public void testIoGetPayments() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		GregorianCalendar dateFrom = new GregorianCalendar(4, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		List<BillPayments> billPayments = accountingIoOperation.getPayments(dateFrom, dateTo);
		assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	public void testIoGetPaymentsBillId() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		List<BillPayments> billItems = accountingIoOperation.getPayments(foundBillPayment.getBill().getId());
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	public void ioGetBillsByDateForPatient() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		GregorianCalendar dateFrom = new GregorianCalendar(1, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		List<Bill> billItems = accountingIoOperation.getBills(dateFrom, dateTo, foundBill.getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void ioGetPendingBills() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		List<Bill> billItems = accountingIoOperation.getPendingBillsAffiliate(foundBill.getBillPatient().getCode());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void testIoNewBill() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		int id = accountingIoOperation.newBill(bill);
		_checkBillIntoDb(id);
	}

	@Test
	public void testIoNewBillItems() throws Exception {
		List<BillItems> billItems = new ArrayList<>();
		int deleteId = _setupTestBillItems(false);
		BillItems deleteBillItem = accountingBillItemsIoOperationRepository.findOne(deleteId);

		Bill bill = deleteBillItem.getBill();
		BillItems insertBillItem = testBillItems.setup(null, false);
		int insertId = deleteId + 1;
		billItems.add(insertBillItem);
		boolean result = accountingIoOperation.newBillItems(bill, billItems);

		BillItems foundBillItems = accountingBillItemsIoOperationRepository.findOne(insertId);
		assertThat(result).isTrue();
		assertThat(foundBillItems.getBill().getId()).isEqualTo(bill.getId());
	}

	@Test
	public void testIoNewBillPayments() throws Exception {
		List<BillPayments> billPayments = new ArrayList<>();
		int deleteId = _setupTestBillPayments(false);
		BillPayments deleteBillPayment = accountingBillPaymentIoOperationRepository.findOne(deleteId);

		Bill bill = deleteBillPayment.getBill();
		BillPayments insertBillPayment = testBillPayments.setup(null, false);
		int insertId = deleteId + 1;
		billPayments.add(insertBillPayment);
		boolean result = accountingIoOperation.newBillPayments(bill, billPayments);

		BillPayments foundBillPayments = accountingBillPaymentIoOperationRepository.findOne(insertId);
		assertThat(result).isTrue();
		assertThat(foundBillPayments.getBill().getId()).isEqualTo(bill.getId());
	}

	@Test
	public void testIoUpdateBill() throws Exception {
		int id = _setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findOne(id);
		bill.setAmount(12.34);

		accountingIoOperation.updateBill(bill);

		assertThat(bill.getAmount()).isCloseTo(12.34, offset(0.1));
	}

	@Test
	public void testIoDeleteBill() throws Exception {
		int id = _setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findOne(id);

		boolean result = accountingIoOperation.deleteBill(bill);

		assertThat(result).isTrue();
	}

	@Test
	public void testIoGetBillsTimeRange() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		GregorianCalendar dateFrom = new GregorianCalendar(4, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		List<Bill> bills = accountingIoOperation.getBills(dateFrom, dateTo);

		assertThat(bills).contains(foundBill);
	}

	@Test
	public void testIoGetBillsTimeRangeAndItem() throws Exception {
		GregorianCalendar dateFrom = new GregorianCalendar(10, 9, 7);
		GregorianCalendar dateTo = new GregorianCalendar(10, 9, 9);

		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);

		List<Bill> bills = accountingIoOperation.getBills(dateFrom, dateTo);
		assertThat(bills).contains(foundBill);

		bills = accountingIoOperation.getBills(new GregorianCalendar(10, 0, 1), dateFrom);
		assertThat(bills).doesNotContain(foundBill);

		bills = accountingIoOperation.getBills(dateTo, new GregorianCalendar(11, 0, 1));
		assertThat(bills).doesNotContain(foundBill);

		id = _setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
		foundBill = accountingBillIoOperationRepository.findOne(foundBillItem.getBill().getId());

		bills = accountingIoOperation.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);

		bills = accountingIoOperation.getBills(dateFrom, dateTo, (BillItems) null);
		assertThat(bills).contains(foundBill);

		id = _setupTestBillItems(true);
		foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);

		bills = accountingIoOperation.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);
	}

	@Test
	public void testIoGetBillsPayment() throws Exception {
		List<BillPayments> payments = new ArrayList<>();

		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);

		payments.add(foundBillPayment);
		List<Bill> bills = accountingIoOperation.getBills(payments);

		assertThat(bills.get(0).getAmount()).isCloseTo(foundBillPayment.getBill().getAmount(), offset(0.1));
	}

	@Test
	public void testIoGetPaymentsBill() throws Exception {
		List<Bill> bills = new ArrayList<>();

		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		Bill foundBill = foundBillPayment.getBill();

		bills.add(foundBill);
		List<BillPayments> payments = accountingIoOperation.getPayments(bills);

		assertThat(payments.get(0).getBill().getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void ioBillPaymentsChecks() throws Exception {
		List<Bill> bills = new ArrayList<>();
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		Bill foundBill = foundBillPayment.getBill();
		bills.add(foundBill);
		List<BillPayments> payments = accountingIoOperation.getPayments(bills);
		assertThat(payments).hasSize(1);

		BillPayments billPayment = payments.get(0);
		assertThat(foundBillPayment.equals(foundBillPayment)).isTrue();
		assertThat(foundBillPayment)
				.isNotEqualTo(new GregorianCalendar())
				.isEqualTo(billPayment);
		int id2 = _setupTestBillPayments(false);
		BillPayments foundBillPayment2 = accountingBillPaymentIoOperationRepository.findOne(id2);
		foundBillPayment2.setId(-1);
		assertThat(foundBillPayment).isNotEqualTo(foundBillPayment2);
		foundBillPayment.setId(id);

		assertThat(billPayment.compareTo(billPayment)).isEqualTo(0);

		assertThat(billPayment.hashCode()).isPositive();
	}

	@Test
	public void ioGetDistictsBillItems() throws Exception {
		int id = _setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	public void ioBillItemChecks() throws Exception {
		int id = _setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertThat(billItems).hasSize(1);

		BillItems billItem = billItems.get(0);
		assertThat(foundBillItem.equals(foundBillItem)).isTrue();
		assertThat(foundBillItem)
				.isNotEqualTo(new GregorianCalendar())
				.isEqualTo(billItem);
		int id2 = _setupTestBillItems(false);
		BillItems foundBillItem2 = accountingBillItemsIoOperationRepository.findOne(id2);
		foundBillItem2.setId(-1);
		assertThat(foundBillItem).isNotEqualTo(foundBillItem2);
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
	public void ioGetPaymentsByDateForPatient() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		GregorianCalendar dateFrom = new GregorianCalendar(1, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		List<BillPayments> billItems = accountingIoOperation.getPayments(dateFrom, dateTo, foundBillPayment.getBill().getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	public void mgrInitManager() {
		assertThat(new BillBrowserManager(accountingIoOperation)).isNotNull();
	}

	@Test
	public void mgrBillItemsChecks() throws Exception {
		int id = _setupTestBillItems(false);
		List<BillItems> billItems = billBrowserManager.getItems(0);
		assertThat(billItems).isEmpty();
		billItems = billBrowserManager.getItems(99999);
		assertThat(billItems).isEmpty();
		billItems = billBrowserManager.getItems();
		assertThat(billItems).hasSize(1);
	}

	@Test
	public void mgrGetPaymentsByDateForPatient() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		GregorianCalendar dateFrom = new GregorianCalendar(1, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		List<BillPayments> billItems = billBrowserManager.getPayments(dateFrom, dateTo, foundBillPayment.getBill().getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetAllPayments() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		List<BillPayments> billItems = billBrowserManager.getPayments();
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetAllPaymentsWithZero() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		List<BillPayments> billItems = billBrowserManager.getPayments(0);
		assertThat(billItems).isEmpty();
	}

	@Test
	public void mgrGetAllPaymentsWithId() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		List<BillPayments> billItems = billBrowserManager.getPayments(foundBillPayment.getBill().getId());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetDistictsBillItems() throws Exception {
		int id = _setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
		List<BillItems> billItems = billBrowserManager.getDistinctItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	public void mgrGetBillsBetweenDatesWherePatient() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		GregorianCalendar dateFrom = new GregorianCalendar(1, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		List<Bill> billItems = billBrowserManager.getBills(dateFrom, dateTo, foundBill.getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetPendingBillsForPatientId() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		List<Bill> bills = billBrowserManager.getPendingBills(foundBill.getBillPatient().getCode());
		assertThat(bills.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrNewBillNoItemsNoPayments() throws Exception {
		int billId = _setupTestBill(false);
		int billItemsId = _setupTestBillItems(false);

		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, false);
		boolean success = billBrowserManager.newBill(
				bill,
				new ArrayList<>(),
				new ArrayList<>());
		assertThat(success).isTrue();
	}

	@Test
	public void mgrNewBillBillItemsNoPayments() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, false);
		BillItems insertBillItem = testBillItems.setup(null, false);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(insertBillItem);
		boolean success = billBrowserManager.newBill(
				bill,
				billItems,
				new ArrayList<>());
		assertThat(success).isTrue();
	}

	@Test
	public void mgrNewBillNoItemsAndPayments() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, false);
		BillPayments insertBillPayment = testBillPayments.setup(bill, false);
		insertBillPayment.setDate(new GregorianCalendar());
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(insertBillPayment);
		boolean success = billBrowserManager.newBill(
				bill,
				new ArrayList<>(),
				billPayments);
		assertThat(success).isTrue();
	}

	@Test
	public void mgrNewBillItemsAndPayments() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, false);
		BillItems insertBillItem = testBillItems.setup(bill, false);
		BillPayments insertBillPayment = testBillPayments.setup(bill, false);
		insertBillPayment.setDate(new GregorianCalendar());
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(insertBillItem);
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(insertBillPayment);
		BillPayments payments = testBillPayments.setup(bill, false);
		boolean success = billBrowserManager.newBill(
				bill,
				billItems,
				billPayments);
		assertThat(success).isTrue();
	}

	@Test
	public void mgrNewBillFailValidation() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, false);
		List<BillItems> billItems = new ArrayList<>();
		BillItems insertBillItem = testBillItems.setup(bill, false);
		billItems.add(insertBillItem);
		List<BillPayments> billPayments = new ArrayList<>();
		BillPayments payments = testBillPayments.setup(bill, false);
		billPayments.add(payments);

		assertThatThrownBy(() -> billBrowserManager.newBill(bill, billItems, billPayments))
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void mgrGetBillsPayment() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);

		List<BillPayments> payments = new ArrayList<>();
		payments.add(foundBillPayment);
		List<Bill> bills = billBrowserManager.getBills(payments);

		assertThat(bills.get(0).getAmount()).isCloseTo(foundBillPayment.getBill().getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetBillsPaymentEmpty() throws Exception {
		List<Bill> bills = billBrowserManager.getBills(new ArrayList<>());
		assertThat(bills).isEmpty();
	}

	@Test
	public void mgrGetPayments() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		GregorianCalendar dateFrom = new GregorianCalendar(4, 3, 2);
		GregorianCalendar dateTo = new GregorianCalendar();
		List<BillPayments> billPayments = billBrowserManager.getPayments(dateFrom, dateTo);
		assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	public void mgrGetPaymentsBill() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		Bill foundBill = foundBillPayment.getBill();
		List<Bill> bills = new ArrayList<>();
		bills.add(foundBill);
		List<BillPayments> payments = billBrowserManager.getPayments(bills);
		assertThat(payments.get(0).getBill().getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetBills() throws Exception {
		GregorianCalendar dateFrom = new GregorianCalendar(10, 9, 7);
		GregorianCalendar dateTo = new GregorianCalendar(10, 9, 9);

		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);

		List<Bill> bills = billBrowserManager.getBills(dateFrom, dateTo);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills(new GregorianCalendar(10, 0, 1), dateFrom);
		assertThat(bills).doesNotContain(foundBill);

		bills = billBrowserManager.getBills(dateTo, new GregorianCalendar(11, 0, 1));
		assertThat(bills).doesNotContain(foundBill);

		id = _setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
		foundBill = accountingBillIoOperationRepository.findOne(foundBillItem.getBill().getId());

		bills = billBrowserManager.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills(dateFrom, dateTo, (BillItems) null);
		assertThat(bills).contains(foundBill);

		id = _setupTestBillItems(true);
		foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);

		bills = billBrowserManager.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills();
		assertThat(bills).contains(foundBill);
	}

	@Test
	public void mgrGetBill() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		Bill bill = billBrowserManager.getBill(id);
		assertThat(bill.getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrGetPendingBillsAffiliate() throws Exception {
		int id = _setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		List<Bill> billItems = billBrowserManager.getPendingBillsAffiliate(foundBill.getBillPatient().getCode());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	public void mgrUpdateBillNoItemsNoPayements() throws Exception {
		int id = _setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findOne(id);
		bill.setAmount(12.34);
		assertThat(billBrowserManager.updateBill(
				bill,
				new ArrayList<>(),
				new ArrayList<>())).isTrue();
		bill = accountingBillIoOperationRepository.findOne(id);
		assertThat(bill.getAmount()).isCloseTo(12.34, offset(0.1));
	}

	@Test
	public void msgDeleteBill() throws Exception {
		int id = _setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findOne(id);
		boolean result = billBrowserManager.deleteBill(bill);
		assertThat(result).isTrue();
	}

	@Test
	public void mgrGetUsers() throws Exception {
		int id = _setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		List<String> userIds = billBrowserManager.getUsers();
		assertThat(userIds).contains(foundBillPayment.getUser());
	}

	private int _setupTestBill(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		return bill.getId();
	}

	private void _checkBillIntoDb(int id) throws OHException {
		Bill foundBill = accountingBillIoOperationRepository.findOne(id);
		testBill.check(foundBill);
		testPriceList.check(foundBill.getPriceList());
		testPatient.check(foundBill.getBillPatient());
	}

	private int _setupTestBillItems(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, false);
		BillItems billItem = testBillItems.setup(bill, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		accountingBillItemsIoOperationRepository.saveAndFlush(billItem);
		return billItem.getId();
	}

	private void _checkBillItemsIntoDb(int id) throws OHException {
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findOne(id);
		testBillItems.check(foundBillItem);
		testBill.check(foundBillItem.getBill());
		testPriceList.check(foundBillItem.getBill().getPriceList());
		testPatient.check(foundBillItem.getBill().getBillPatient());
	}

	private int _setupTestBillPayments(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, false);
		BillPayments billPayment = testBillPayments.setup(bill, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		accountingBillPaymentIoOperationRepository.saveAndFlush(billPayment);
		return billPayment.getId();
	}

	private void _checkBillPaymentsIntoDb(int id) throws OHException {
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findOne(id);
		testBillPayments.check(foundBillPayment);
		testBill.check(foundBillPayment.getBill());
		testPriceList.check(foundBillPayment.getBill().getPriceList());
		testPatient.check(foundBillPayment.getBill().getBillPatient());
	}

	private Patient _setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}
}
