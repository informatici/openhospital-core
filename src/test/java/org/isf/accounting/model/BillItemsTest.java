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
import org.isf.accounting.TestBillItems;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.accounting.service.AccountingBillItemsIoOperationRepository;
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

class BillItemsTest extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;

	@Autowired
	AccountingIoOperations accountingIoOperation;
	@Autowired
	AccountingBillIoOperationRepository accountingBillIoOperationRepository;
	@Autowired
	AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository;
	@Autowired
	PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testBill = new TestBill();
		testBillItems = new TestBillItems();
		testPatient = new TestPatient();
		testPriceList = new TestPriceList();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		int id = setupTestBillItems(false);
		checkBillItemsIntoDb(id);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int id = setupTestBillItems(true);
		checkBillItemsIntoDb(id);
	}

	@Test
	void equalsHashCodeGetItemDisplayCode_shouldUseBillItemsFields() throws Exception {
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

}
