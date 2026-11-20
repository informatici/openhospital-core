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
package org.isf.accounting.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.accounting.TestBill;
import org.isf.accounting.TestBillItems;
import org.isf.accounting.TestBillPayments;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AccountingIoOperationsTest extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestBillPayments testBillPayments;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;

	@Autowired
	AccountingIoOperations accountingIoOperation;
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
	void getPendingBills_shouldReturnAllPendingBills_whenPatientIdIsZero() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getPendingBills(0);
		assertThat(bills).contains(foundBill);
	}

	@Test
	void getPendingBills_shouldReturnPatientPendingBills_whenPatientIdIsGiven() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getPendingBills(foundBill.getBillPatient().getCode());
		assertThat(foundBill.getAmount()).isEqualByComparingTo(bills.get(0).getAmount());
	}

	@Test
	void getBills_shouldReturnSavedBill() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = accountingIoOperation.getBills();
		assertThat(bills).contains(foundBill);
	}

	@Test
	void getBill_shouldReturnBill_whenIdExists() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		Bill bill = accountingIoOperation.getBill(id);
		assertThat(bill.getAmount()).isEqualByComparingTo(foundBill.getAmount());
	}

	@Test
	void getUsers_shouldIncludeUserOfSavedPayment() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<String> userIds = accountingIoOperation.getUsers();
		assertThat(userIds).contains(foundBillPayment.getUser());
	}

	@Test
	void getItems_shouldReturnItemsOfBill_whenBillIdIsGiven() throws Exception {
		int billItemID = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(billItemID).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void getItems_shouldReturnAllItems_whenBillIdIsZero() throws Exception {
		setupTestBillItems(false);
		List<BillItems> billItems = accountingIoOperation.getItems(0);
		assertThat(billItems).isNotEmpty();
	}

	@Test
	void getItems_shouldReturnItemWithSavedAmount_whenBillIdIsGiven() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getItems(foundBillItem.getBill().getId());
		assertThat(billItems.get(0).getItemAmount()).isEqualByComparingTo(foundBillItem.getItemAmount());
	}

	@Test
	void getPayments_shouldReturnPayment_whenPaymentDateInRange() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = foundBillPayment.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billPayments = accountingIoOperation.getPayments(dateFrom, dateTo);
		assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	void getPayments_shouldReturnPaymentsOfBill_whenBillIdIsGiven() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = accountingIoOperation.getPayments(foundBillPayment.getBill().getId());
		assertThat(billItems.get(0).getAmount()).isEqualByComparingTo(foundBillPayment.getAmount());
	}

	@Test
	void getBillsBetweenDatesWherePatient_shouldReturnPatientBill_whenBillDateInRange() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = foundBill.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> billItems = accountingIoOperation.getBillsBetweenDatesWherePatient(dateFrom, dateTo, foundBill.getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isEqualByComparingTo(foundBill.getAmount());
	}

	@Test
	void getPendingBillsAffiliate_shouldReturnPatientPendingBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> billItems = accountingIoOperation.getPendingBillsAffiliate(foundBill.getBillPatient().getCode());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isEqualByComparingTo(foundBill.getAmount());
	}

	@Test
	void newBill_shouldSaveBill() throws Exception {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		Bill newBill = accountingIoOperation.newBill(bill);
		checkBillIntoDb(newBill.getId());
	}

	@Test
	void newBillItems_shouldReplaceItemsOfBill_whenExistingAndNewItemsAreResent() throws Exception {
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
	void newBillPayments_shouldReplacePaymentsOfBill_whenExistingAndNewPaymentsAreResent() throws Exception {
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
	void updateBill_shouldKeepNewAmount() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();
		bill.setAmount(new BigDecimal("12.34"));

		accountingIoOperation.updateBill(bill);

		assertThat(bill.getAmount()).isEqualByComparingTo(new BigDecimal("12.34"));
	}

	@Test
	void deleteBill_shouldRemoveBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();

		accountingIoOperation.deleteBill(bill);
		assertThat(accountingBillIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void getBillsBetweenDates_shouldReturnBill_whenBillDateInRange() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = foundBill.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> bills = accountingIoOperation.getBillsBetweenDates(dateFrom, dateTo);

		assertThat(bills).contains(foundBill);
	}

	@Test
	void getBillsBetweenDatesVariants_shouldFilterByDateRangeAndItemDescription() throws Exception {
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
	void getBills_shouldReturnBillsOfPayments() throws Exception {
		List<BillPayments> payments = new ArrayList<>();

		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();

		payments.add(foundBillPayment);
		List<Bill> bills = accountingIoOperation.getBills(payments);

		assertThat(bills.get(0).getAmount()).isEqualByComparingTo(foundBillPayment.getBill().getAmount());
	}

	@Test
	void getPayments_shouldReturnPaymentsOfBills() throws Exception {
		List<Bill> bills = new ArrayList<>();

		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		Bill foundBill = foundBillPayment.getBill();

		bills.add(foundBill);
		List<BillPayments> payments = accountingIoOperation.getPayments(bills);

		assertThat(payments.get(0).getBill().getAmount()).isEqualByComparingTo(foundBill.getAmount());
	}

	@Test
	void getDistictsBillItems_shouldReturnSavedItem() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = accountingIoOperation.getDistictsBillItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void getPaymentsBetweenDatesWherePatient_shouldReturnPatientPayment_whenPaymentDateInRange() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = LocalDateTime.of(1, 3, 2, 0, 0, 0, 0);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billItems = accountingIoOperation.getPaymentsBetweenDatesWherePatient(dateFrom, dateTo, foundBillPayment.getBill().getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isEqualByComparingTo(foundBillPayment.getAmount());
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

}
