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
package org.isf.accounting.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.accounting.TestBill;
import org.isf.accounting.TestBillPayments;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.accounting.service.AccountingBillPaymentIoOperationRepository;
import org.isf.accounting.service.AccountingIoOperations;
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

class BillPaymentsTest extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestBillPayments testBillPayments;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;

	@Autowired
	AccountingIoOperations accountingIoOperation;
	@Autowired
	AccountingBillIoOperationRepository accountingBillIoOperationRepository;
	@Autowired
	AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository;
	@Autowired
	PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testBill = new TestBill();
		testBillPayments = new TestBillPayments();
		testPatient = new TestPatient();
		testPriceList = new TestPriceList();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		int id = setupTestBillPayments(false);
		checkBillPaymentsIntoDb(id);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int id = setupTestBillPayments(true);
		checkBillPaymentsIntoDb(id);
	}

	@Test
	void equalsCompareToHashCode_shouldUseBillPaymentsFields() throws Exception {
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

}
