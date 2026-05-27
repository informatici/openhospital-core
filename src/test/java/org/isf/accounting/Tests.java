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
package org.isf.accounting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class Tests extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestBillPayments testBillPayments;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;

	@Autowired
	BillBrowserManager billBrowserManager;
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
	PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testBill = new TestBill();
		testBillItems = new TestBillItems();
		testBillPayments = new TestBillPayments();
		testPatient = new TestPatient();
		testPriceList = new TestPriceList();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testBillGets() throws Exception {
		int id = setupTestBill(false);
		checkBillIntoDb(id);
	}

	@Test
	void testBillSets() throws Exception {
		int id = setupTestBill(true);
		checkBillIntoDb(id);
	}

	@Test
	void testBillItemsGets() throws Exception {
		int id = setupTestBillItems(false);
		checkBillItemsIntoDb(id);
	}

	@Test
	void testBillItemsSets() throws Exception {
		int id = setupTestBillItems(true);
		checkBillItemsIntoDb(id);
	}

	@Test
	void testBillPaymentsGets() throws Exception {
		int id = setupTestBillPayments(false);
		checkBillPaymentsIntoDb(id);
	}

	@Test
	void testBillPaymentsSets() throws Exception {
		int id = setupTestBillPayments(true);
		checkBillPaymentsIntoDb(id);
	}

	@Test
	void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		Patient mergedPatient = setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(foundBill.getBillPatient(), mergedPatient));

		// then:
		Bill resultBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(resultBill).isNotNull();
		assertThat(resultBill.getBillPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	@Test
	void testIoGetPendingBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getPendingBills(0);
		assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetPendingBillsPatId() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getPendingBills(foundBill.getBillPatient().getCode());
		assertThat(foundBill.getAmount()).isCloseTo(bills.get(0).getAmount(), offset(0.1));
	}

	@Test
	void testIoGetBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getBills();
		assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetBill() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		Bill bill = accountingIoOperation.getBill(id);
		assertThat(bill.getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void ioBillChecks() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getBills();
		assertThat(bills).hasSize(1);
		Bill bill = bills.get(0);

		int id2 = setupTestBill(false);
		Bill foundBill2 = accountingBillIoOperationRepository.findById(id2).orElse(null);
		assertThat(foundBill2).isNotNull();

		assertThat(bill)
			.isNotEqualTo(TimeTools.getNow())
			.isEqualTo(foundBill);
		foundBill2.setId(-1);
		assertThat(bill).isNotEqualTo(foundBill2);
		assertThat(bill.compareTo(foundBill2)).isEqualTo(id + 1); // id - (-1)
		foundBill.setId(id);

		assertThat(bill.hashCode()).isPositive();
	}

	@Test
	void testIoGetUsers() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<String> userIds = accountingIoOperation.getUsers();
		assertThat(userIds).contains(foundBillPayment.getUser());
	}

	@Test
	void testIoGetItems() throws Exception {
		int billItemID = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(billItemID).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void ioGetAllItems() throws Exception {
		setupTestBillItems(false);
		List<BillItems> billItems = accountingIoOperation.getItems(0);
		assertThat(billItems).isNotEmpty();
	}

	@Test
	void testIoGetItemsBillId() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		assertThat(billItems.get(0).getItemAmount()).isCloseTo(foundBillItem.getItemAmount(), offset(0.1));
	}

	@Test
	void testIoGetPayments() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = foundBillPayment.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billPayments = accountingIoOperation.getPayments(dateFrom, dateTo);
		assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	void testIoGetPaymentsBillId() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = accountingIoOperation.getPayments(foundBillPayment.getBill().getId());
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void ioGetBillsByDateForPatient() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = foundBill.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> billItems = accountingIoOperation.getBillsBetweenDatesWherePatient(dateFrom, dateTo, foundBill.getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void ioGetPendingBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> billItems = accountingIoOperation.getPendingBillsAffiliate(foundBill.getBillPatient().getCode());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void testIoNewBill() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill newBill = accountingIoOperation.newBill(bill);
		checkBillIntoDb(newBill.getId());
	}

	@Test
	void testIoNewBillItems() throws Exception {
		// given: an existing bill with one item already stored
		int existingId = setupTestBillItems(false);
		BillItems existingManaged = accountingBillItemsIoOperationRepository.findById(existingId).orElse(null);
		assertThat(existingManaged).isNotNull();

		Bill bill = existingManaged.getBill();

		// Simulate same item as new object from GUI
		BillItems existingFromGui = testBillItems.setup(null, false);
		existingFromGui.setId(existingId);

		// and: a second (new) item created by the GUI (id = null / 0)
		BillItems newItemFromGui = testBillItems.setup(null, false);

		// GUI behaviour: resend the whole list: existing + new
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(existingFromGui);
		billItems.add(newItemFromGui);

		// when: we call the service that internally does delete + re-insert
		accountingIoOperation.newBillItems(bill, billItems);

		// then: for that bill we now have exactly two items
		List<BillItems> persisted = accountingIoOperation.getItems(bill.getId());
		assertThat(persisted).hasSize(2);

		// all items belong to the correct bill
		assertThat(persisted)
			.extracting(i -> i.getBill().getId())
			.containsOnly(bill.getId());

		// and none of them keeps the old id (they've been re-inserted)
		assertThat(persisted)
			.extracting(BillItems::getId)
			.doesNotContain(existingId);
	}

	@Test
	void testIoNewBillPaymentsResendExistingAndNew() throws Exception {
		// given: an existing bill with one payment already stored
		int existingId = setupTestBillPayments(false);
		BillPayments existingPayment = accountingBillPaymentIoOperationRepository.findById(existingId).orElse(null);
		assertThat(existingPayment).isNotNull();

		// Simulate same payment as new object from GUI
		Bill bill = existingPayment.getBill();
		BillPayments existingFromGui = new BillPayments();
		existingFromGui.setId(existingPayment.getId());
		existingFromGui.setAmount(existingPayment.getAmount());
		existingFromGui.setDate(existingPayment.getDate());
		existingFromGui.setUser(existingPayment.getUser());
		existingFromGui.setBill(bill); // oppure null, tanto lo setti in newBillPayments

		// and: a second (new) payment created by the GUI (id = null / 0)
		BillPayments newPayment = testBillPayments.setup(null, false);

		// GUI behaviour: resend the whole list: existing + new
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(existingFromGui); // existing, with original id
		billPayments.add(newPayment); // new, with no id

		// when: we call the service that internally does delete + re-insert
		accountingIoOperation.newBillPayments(bill, billPayments);

		// then: for that bill we now have exactly two payments
		List<BillPayments> persisted = accountingIoOperation.getPayments(bill.getId());
		assertThat(persisted).hasSize(2);

		// all payments belong to the correct bill
		assertThat(persisted)
			.extracting(p -> p.getBill().getId())
			.containsOnly(bill.getId());

		// and none of them keeps the old id (they've been re-inserted)
		assertThat(persisted)
			.extracting(BillPayments::getId)
			.doesNotContain(existingId);
	}

	@Test
	void testIoUpdateBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();
		bill.setAmount(12.34);

		accountingIoOperation.updateBill(bill);

		assertThat(bill.getAmount()).isCloseTo(12.34, offset(0.1));
	}

	@Test
	void testIoDeleteBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();

		accountingIoOperation.deleteBill(bill);
		assertThat(accountingBillIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void testIoGetBillsTimeRange() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = foundBill.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> bills = accountingIoOperation.getBillsBetweenDates(dateFrom, dateTo);

		assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetBillsTimeRangeAndItem() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(10, 9, 7, 0, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(10, 9, 9, 0, 0, 0);

		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();

		List<Bill> bills = accountingIoOperation.getBillsBetweenDates(dateFrom, dateTo);
		assertThat(bills).contains(foundBill);

		bills = accountingIoOperation.getBillsBetweenDates(LocalDateTime.of(10, 1, 1, 0, 0, 0), dateFrom);
		assertThat(bills).doesNotContain(foundBill);

		bills = accountingIoOperation.getBillsBetweenDates(dateTo, LocalDateTime.of(11, 1, 1, 0, 0, 0));
		assertThat(bills).doesNotContain(foundBill);

		id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		foundBill = accountingBillIoOperationRepository.findById(foundBillItem.getBill().getId()).orElse(null);
		assertThat(foundBill).isNotNull();

		bills = accountingIoOperation.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);

		bills = accountingIoOperation.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, (BillItems) null);
		assertThat(bills).contains(foundBill);

		id = setupTestBillItems(true);
		foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();

		bills = accountingIoOperation.getBillsBetweenDatesWhereBillItem(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);
	}

	@Test
	void testIoGetBillsPayment() throws Exception {
		List<BillPayments> payments = new ArrayList<>();

		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();

		payments.add(foundBillPayment);
		List<Bill> bills = accountingIoOperation.getBills(payments);

		assertThat(bills.get(0).getAmount()).isCloseTo(foundBillPayment.getBill().getAmount(), offset(0.1));
	}

	@Test
	void testIoGetPaymentsBill() throws Exception {
		List<Bill> bills = new ArrayList<>();

		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		Bill foundBill = foundBillPayment.getBill();

		bills.add(foundBill);
		List<BillPayments> payments = accountingIoOperation.getPayments(bills);

		assertThat(payments.get(0).getBill().getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void ioBillPaymentsChecks() throws Exception {
		List<Bill> bills = new ArrayList<>();
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		Bill foundBill = foundBillPayment.getBill();
		bills.add(foundBill);
		List<BillPayments> payments = accountingIoOperation.getPayments(bills);
		assertThat(payments).hasSize(1);

		BillPayments billPayment = payments.get(0);
		assertThat(foundBillPayment).isEqualTo(foundBillPayment);
		assertThat(foundBillPayment)
			.isNotEqualTo(TimeTools.getNow())
			.isEqualTo(billPayment);
		int id2 = setupTestBillPayments(false);
		BillPayments foundBillPayment2 = accountingBillPaymentIoOperationRepository.findById(id2).orElse(null);
		assertThat(foundBillPayment2).isNotNull();
		foundBillPayment2.setId(-1);
		assertThat(foundBillPayment).isNotEqualTo(foundBillPayment2);
		foundBillPayment.setId(id);

		assertThat(billPayment.compareTo(billPayment)).isZero();

		assertThat(billPayment.hashCode()).isPositive();
	}

	@Test
	void ioGetDistictsBillItems() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void ioBillItemChecks() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertThat(billItems).hasSize(1);
		BillItems billItem = billItems.get(0);

		assertThat(foundBillItem)
			.isNotEqualTo(TimeTools.getNow())
			.isEqualTo(billItem);
		int id2 = setupTestBillItems(false);
		BillItems foundBillItem2 = accountingBillItemsIoOperationRepository.findById(id2).orElse(null);
		assertThat(foundBillItem2).isNotNull();
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
	void ioGetPaymentsByDateForPatient() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = LocalDateTime.of(1, 3, 2, 0, 0, 0, 0);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billItems = accountingIoOperation.getPaymentsBetweenDatesWherePatient(dateFrom, dateTo, foundBillPayment.getBill().getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void mgrBillItemsChecks() throws Exception {
		int id = setupTestBillItems(false);
		BillItems billitem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(billitem).isNotNull();
		List<BillItems> billItems = billBrowserManager.getItems(0);
		assertThat(billItems).isEmpty();
		billItems = billBrowserManager.getItems(99999);
		assertThat(billItems).isEmpty();
		billItems = billBrowserManager.getItems(billitem.getBill().getId());
		assertThat(billItems).hasSize(1);
	}

	@Test
	void mgrGetPaymentsByDateForPatient() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = LocalDateTime.of(1, 3, 2, 0, 0, 0, 0);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billItems = billBrowserManager.getPayments(dateFrom, dateTo, foundBillPayment.getBill().getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetAllPayments() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = billBrowserManager.getPayments(0); // get all
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetAllPaymentsWithId() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = billBrowserManager.getPayments(foundBillPayment.getBill().getId());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBillPayment.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetDistictsBillItems() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = billBrowserManager.getDistinctItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void mgrGetBillsBetweenDatesWherePatient() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = LocalDateTime.of(1, 3, 2, 0, 0, 0, 0);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> billItems = billBrowserManager.getBills(dateFrom, dateTo, foundBill.getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetPendingBillsForPatientId() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = billBrowserManager.getPendingBills(foundBill.getBillPatient().getCode());
		assertThat(bills.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrNewBillNoItemsNoPayments() throws Exception {
		// A bill with no items must be rejected
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		assertThatThrownBy(() -> billBrowserManager.newBill(bill, new ArrayList<>(), new ArrayList<>()))
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void mgrNewBillBillItemsNoPayments() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		BillItems insertBillItem = testBillItems.setup(null, false);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(insertBillItem);
		billBrowserManager.newBill(
			bill,
			billItems,
			new ArrayList<>());
		assertThat(billBrowserManager.getBill(bill.getId()).getId()).isEqualTo(bill.getId());
		assertThat(billBrowserManager.getItems(bill.getId())).isNotEmpty();
		assertThat(billBrowserManager.getPayments(bill.getId())).isEmpty();
	}

	@Test
	void mgrNewBillNoItemsAndPayments() throws Exception {
		// A bill with no items must be rejected even when payments are supplied
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		BillPayments insertBillPayment = testBillPayments.setup(bill, false);
		insertBillPayment.setDate(TimeTools.getNow());
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(insertBillPayment);
		assertThatThrownBy(() -> billBrowserManager.newBill(bill, new ArrayList<>(), billPayments))
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void mgrNewBillItemsAndPayments() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		BillItems insertBillItem = testBillItems.setup(bill, false);
		BillPayments insertBillPayment = testBillPayments.setup(bill, false);
		insertBillPayment.setDate(TimeTools.getNow());
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(insertBillItem);
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(insertBillPayment);
		billBrowserManager.newBill(
			bill,
			billItems,
			billPayments);
		assertThat(billBrowserManager.getBill(bill.getId()).getId()).isEqualTo(bill.getId());
		assertThat(billBrowserManager.getItems(bill.getId())).isNotEmpty();
		assertThat(billBrowserManager.getPayments(bill.getId())).isNotEmpty();
	}

	@Test
	void mgrNewBillFailValidation() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, false);
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
	void mgrGetBillsPayment() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();

		List<BillPayments> payments = new ArrayList<>();
		payments.add(foundBillPayment);
		List<Bill> bills = billBrowserManager.getBills(payments);

		assertThat(bills.get(0).getAmount()).isCloseTo(foundBillPayment.getBill().getAmount(), offset(0.1));
	}

	@Test
	void mgrGetBillsPaymentEmpty() throws Exception {
		List<Bill> bills = billBrowserManager.getBills(new ArrayList<>());
		assertThat(bills).isEmpty();
	}

	@Test
	void mgrGetPayments() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = foundBillPayment.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billPayments = billBrowserManager.getPayments(dateFrom, dateTo);
		assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	void mgrGetPaymentsBill() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		Bill foundBill = foundBillPayment.getBill();
		List<Bill> bills = new ArrayList<>();
		bills.add(foundBill);
		List<BillPayments> payments = billBrowserManager.getPayments(bills);
		assertThat(payments.get(0).getBill().getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetBills() throws Exception {
		LocalDateTime dateFrom = LocalDateTime.of(10, 9, 7, 0, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(10, 9, 9, 0, 0, 0);

		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();

		List<Bill> bills = billBrowserManager.getBills(dateFrom, dateTo);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills(LocalDateTime.of(10, 1, 1, 0, 0, 0), dateFrom);
		assertThat(bills).doesNotContain(foundBill);

		bills = billBrowserManager.getBills(dateTo, LocalDateTime.of(11, 1, 1, 0, 0, 0));
		assertThat(bills).doesNotContain(foundBill);

		id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		foundBill = accountingBillIoOperationRepository.findById(foundBillItem.getBill().getId()).orElse(null);
		assertThat(foundBill).isNotNull();

		bills = billBrowserManager.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);

		bills = billBrowserManager.getBills(dateFrom, dateTo, (BillItems) null);
		assertThat(bills).contains(foundBill);

		id = setupTestBillItems(true);
		foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();

		bills = billBrowserManager.getBills(dateFrom, dateTo, foundBillItem);
		assertThat(bills).contains(foundBill);
	}

	@Test
	void mgrGetBill() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		Bill bill = billBrowserManager.getBill(id);
		assertThat(bill.getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrGetPendingBillsAffiliate() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> billItems = billBrowserManager.getPendingBillsAffiliate(foundBill.getBillPatient().getCode());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isCloseTo(foundBill.getAmount(), offset(0.1));
	}

	@Test
	void mgrUpdateBillNoItemsNoPayements() throws Exception {
		// Payment-only update (empty items list): amount must be recomputed from the items already stored in the DB
		int id = setupTestBillWithItems(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();
		Bill updatedBill = billBrowserManager.updateBill(bill, new ArrayList<>(), new ArrayList<>());
		assertThat(updatedBill.getAmount()).isCloseTo(202.0, offset(0.1));
		assertThat(updatedBill.getBalance()).isCloseTo(202.0, offset(0.1));
		assertThat(billBrowserManager.getItems(bill.getId())).isNotEmpty();
	}

	@Test
	void mgrDeleteBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();
		billBrowserManager.deleteBill(bill);
		assertThat(accountingBillIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void mgrGetUsers() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<String> userIds = billBrowserManager.getUsers();
		assertThat(userIds).contains(foundBillPayment.getUser());
	}

	private int setupTestBill(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		return bill.getId();
	}

	private void checkBillIntoDb(int id) throws OHException {
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		testBill.check(foundBill);
		testPriceList.check(foundBill.getPriceList());
		testPatient.check(foundBill.getBillPatient());
	}

	private int setupTestBillItems(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, usingSet);
		BillItems billItem = testBillItems.setup(bill, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		accountingBillItemsIoOperationRepository.saveAndFlush(billItem);
		return billItem.getId();
	}

	private void checkBillItemsIntoDb(int id) throws OHException {
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		testBillItems.check(foundBillItem);
		testBill.check(foundBillItem.getBill());
		testPriceList.check(foundBillItem.getBill().getPriceList());
		testPatient.check(foundBillItem.getBill().getBillPatient());
	}

	private int setupTestBillPayments(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, usingSet);
		BillPayments billPayment = testBillPayments.setup(bill, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		accountingBillPaymentIoOperationRepository.saveAndFlush(billPayment);
		return billPayment.getId();
	}

	private void checkBillPaymentsIntoDb(int id) throws OHException {
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		testBillPayments.check(foundBillPayment);
		testBill.check(foundBillPayment.getBill());
		testPriceList.check(foundBillPayment.getBill().getPriceList());
		testPatient.check(foundBillPayment.getBill().getBillPatient());
	}

	private int setupTestBillWithItems(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		BillItems billItem = testBillItems.setup(bill, usingSet);
		accountingBillItemsIoOperationRepository.saveAndFlush(billItem);
		return bill.getId();
	}

	private Patient setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	@Test
	void mgrNewBillRecomputesAmountAndBalance() throws Exception {
		// Verify that amount and balance are authoritative from items, ignoring whatever the ones passed as bill.amount / bill.balance.
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		BillItems item = testBillItems.setup(null, false);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(item);

		Bill saved = billBrowserManager.newBill(bill, billItems, new ArrayList<>());

		assertThat(saved.getAmount()).isCloseTo(202.0, offset(0.01));  // 20 * 10.10
		assertThat(saved.getBalance()).isCloseTo(202.0, offset(0.01)); // no payments
	}

	@Test
	void mgrNewBillWithPaymentRecomputesBalance() throws Exception {
		// Balance = gross total - payments.
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		BillItems item = testBillItems.setup(null, false);
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(item);
		BillPayments payment = testBillPayments.setup(bill, false);
		payment.setDate(TimeTools.getNow());
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(payment);

		Bill saved = billBrowserManager.newBill(bill, billItems, billPayments);

		assertThat(saved.getAmount()).isCloseTo(202.0, offset(0.01));
		assertThat(saved.getBalance()).isCloseTo(191.9, offset(0.01));
	}

	@Test
	void mgrUpdateBillPaymentOnlyRecomputesBalance() throws Exception {
		// Payment-only update: items stay in DB, a payment is added.
		int id = setupTestBillWithItems(false);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();

		BillPayments payment = testBillPayments.setup(bill, false);
		payment.setDate(TimeTools.getNow());
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(payment);

		Bill updated = billBrowserManager.updateBill(bill, new ArrayList<>(), billPayments);

		assertThat(updated.getAmount()).isCloseTo(202.0, offset(0.01));
		assertThat(updated.getBalance()).isCloseTo(191.9, offset(0.01));
	}

	@Test
	void mgrNewBillClosedWithOutstandingBalanceThrows() throws Exception {
		// A closed bill whose balance is non-zero after recalculation must be rejected.
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill bill = testBill.setup(priceList, patient, null, false);
		bill.setStatus("C"); // mark closed
		BillItems item = testBillItems.setup(null, false);
		item.setItemQuantity(1); // 1 * 10.10 = 10.10 → balance ≠ 0
		List<BillItems> billItems = new ArrayList<>();
		billItems.add(item);

		assertThatThrownBy(() -> billBrowserManager.newBill(bill, billItems, new ArrayList<>()))
				.isInstanceOf(OHDataValidationException.class);
	}
}
