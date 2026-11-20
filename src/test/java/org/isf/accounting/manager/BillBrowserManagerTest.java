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
package org.isf.accounting.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.accounting.service.AccountingBillItemsIoOperationRepository;
import org.isf.accounting.service.AccountingBillPaymentIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
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

class BillBrowserManagerTest extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestBillPayments testBillPayments;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;

	@Autowired
	BillBrowserManager billBrowserManager;
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
	void getItems_shouldReturnItemsOnlyForExistingBillId() throws Exception {
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
	void getPayments_shouldReturnPatientPayment_whenPaymentDateInRange() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = LocalDateTime.of(1, 3, 2, 0, 0, 0, 0);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billItems = billBrowserManager.getPayments(dateFrom, dateTo, foundBillPayment.getBill().getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isEqualByComparingTo(foundBillPayment.getAmount());
	}

	@Test
	void getPayments_shouldReturnAllPayments_whenBillIdIsZero() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = billBrowserManager.getPayments(0); // get all
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isEqualByComparingTo(foundBillPayment.getAmount());
	}

	@Test
	void getPayments_shouldReturnPaymentsOfBill_whenBillIdIsGiven() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		List<BillPayments> billItems = billBrowserManager.getPayments(foundBillPayment.getBill().getId());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isEqualByComparingTo(foundBillPayment.getAmount());
	}

	@Test
	void getDistinctItems_shouldReturnSavedItem() throws Exception {
		int id = setupTestBillItems(false);
		BillItems foundBillItem = accountingBillItemsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillItem).isNotNull();
		List<BillItems> billItems = billBrowserManager.getDistinctItems();
		assertThat(billItems).contains(foundBillItem);
	}

	@Test
	void getBills_shouldReturnPatientBill_whenBillDateInRange() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		LocalDateTime dateFrom = LocalDateTime.of(1, 3, 2, 0, 0, 0, 0);
		LocalDateTime dateTo = TimeTools.getNow();
		List<Bill> billItems = billBrowserManager.getBills(dateFrom, dateTo, foundBill.getBillPatient());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isEqualByComparingTo(foundBill.getAmount());
	}

	@Test
	void getPendingBills_shouldReturnPatientPendingBills_whenPatientIdIsGiven() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> bills = billBrowserManager.getPendingBills(foundBill.getBillPatient().getCode());
		assertThat(bills.get(0).getAmount()).isEqualByComparingTo(foundBill.getAmount());
	}

	@Test
	void newBill_shouldThrow_whenBillHasNoItemsAndNoPayments() throws Exception {
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
	void newBill_shouldSaveBillWithItems_whenBillHasNoPayments() throws Exception {
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
	void newBill_shouldThrow_whenBillHasPaymentsButNoItems() throws Exception {
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
	void newBill_shouldSaveBillWithItemsAndPayments() throws Exception {
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
	void newBill_shouldThrow_whenBillDateIsAfterFirstPayment() throws Exception {
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
	void getBills_shouldReturnBillsOfPayments() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();

		List<BillPayments> payments = new ArrayList<>();
		payments.add(foundBillPayment);
		List<Bill> bills = billBrowserManager.getBills(payments);

		assertThat(bills.get(0).getAmount()).isEqualByComparingTo(foundBillPayment.getBill().getAmount());
	}

	@Test
	void getBills_shouldReturnEmpty_whenPaymentsListIsEmpty() throws Exception {
		List<Bill> bills = billBrowserManager.getBills(new ArrayList<>());
		assertThat(bills).isEmpty();
	}

	@Test
	void getPayments_shouldReturnPayment_whenPaymentDateInRange() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		LocalDateTime dateFrom = foundBillPayment.getDate().minusYears(1);
		LocalDateTime dateTo = TimeTools.getNow();
		List<BillPayments> billPayments = billBrowserManager.getPayments(dateFrom, dateTo);
		assertThat(billPayments).contains(foundBillPayment);
	}

	@Test
	void getPayments_shouldReturnPaymentsOfBills() throws Exception {
		int id = setupTestBillPayments(false);
		BillPayments foundBillPayment = accountingBillPaymentIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBillPayment).isNotNull();
		Bill foundBill = foundBillPayment.getBill();
		List<Bill> bills = new ArrayList<>();
		bills.add(foundBill);
		List<BillPayments> payments = billBrowserManager.getPayments(bills);
		assertThat(payments.get(0).getBill().getAmount()).isEqualByComparingTo(foundBill.getAmount());
	}

	@Test
	void getBills_shouldFilterByDateRangeAndItemDescription() throws Exception {
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
	void getBill_shouldReturnBill_whenIdExists() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		Bill bill = billBrowserManager.getBill(id);
		assertThat(bill.getAmount()).isEqualByComparingTo(foundBill.getAmount());
	}

	@Test
	void getPendingBillsAffiliate_shouldReturnPatientPendingBills() throws Exception {
		int id = setupTestBill(false);
		Bill foundBill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(foundBill).isNotNull();
		List<Bill> billItems = billBrowserManager.getPendingBillsAffiliate(foundBill.getBillPatient().getCode());
		assertThat(billItems).isNotEmpty();
		assertThat(billItems.get(0).getAmount()).isEqualByComparingTo(foundBill.getAmount());
	}

	@Test
	void updateBill_shouldRecomputeAmountAndBalanceFromStoredItems_whenItemsAndPaymentsAreEmpty() throws Exception {
		// Payment-only update (empty items list): amount must be recomputed from the items already stored in the DB
		int id = setupTestBillWithItems(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();
		Bill updatedBill = billBrowserManager.updateBill(bill, new ArrayList<>(), new ArrayList<>());
		assertThat(updatedBill.getAmount()).isEqualByComparingTo(new BigDecimal("202.0"));
		assertThat(updatedBill.getBalance()).isEqualByComparingTo(new BigDecimal("202.0"));
		assertThat(billBrowserManager.getItems(bill.getId())).isNotEmpty();
	}

	@Test
	void deleteBill_shouldRemoveBill() throws Exception {
		int id = setupTestBill(true);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();
		billBrowserManager.deleteBill(bill);
		assertThat(accountingBillIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void getUsers_shouldIncludeUserOfSavedPayment() throws Exception {
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

	@Test
	void newBill_shouldRecomputeAmountAndBalanceFromItems() throws Exception {
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

		assertThat(saved.getAmount()).isEqualByComparingTo(new BigDecimal("202.0"));  // 20 * 10.10
		assertThat(saved.getBalance()).isEqualByComparingTo(new BigDecimal("202.0")); // no payments
	}

	@Test
	void newBill_shouldRecomputeAmountAndSubtractPaymentFromBalance() throws Exception {
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

		assertThat(saved.getAmount()).isEqualByComparingTo(new BigDecimal("202.0"));
		assertThat(saved.getBalance()).isEqualByComparingTo(new BigDecimal("191.9"));
	}

	@Test
	void updateBill_shouldRecomputeAmountAndSubtractPaymentFromBalance_whenOnlyPaymentsAreGiven() throws Exception {
		// Payment-only update: items stay in DB, a payment is added.
		int id = setupTestBillWithItems(false);
		Bill bill = accountingBillIoOperationRepository.findById(id).orElse(null);
		assertThat(bill).isNotNull();

		BillPayments payment = testBillPayments.setup(bill, false);
		payment.setDate(TimeTools.getNow());
		List<BillPayments> billPayments = new ArrayList<>();
		billPayments.add(payment);

		Bill updated = billBrowserManager.updateBill(bill, new ArrayList<>(), billPayments);

		assertThat(updated.getAmount()).isEqualByComparingTo(new BigDecimal("202.0"));
		assertThat(updated.getBalance()).isEqualByComparingTo(new BigDecimal("191.9"));
	}

	@Test
	void newBill_shouldThrow_whenClosedBillHasOutstandingBalance() throws Exception {
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
