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

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.accounting.TestBill;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
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

class BillTest extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;

	@Autowired
	AccountingIoOperations accountingIoOperation;
	@Autowired
	AccountingBillIoOperationRepository accountingBillIoOperationRepository;
	@Autowired
	PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testBill = new TestBill();
		testPatient = new TestPatient();
		testPriceList = new TestPriceList();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		int id = setupTestBill(false);
		checkBillIntoDb(id);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int id = setupTestBill(true);
		checkBillIntoDb(id);
	}

	@Test
	void equalsCompareToHashCode_shouldUseBillId() throws Exception {
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

}
