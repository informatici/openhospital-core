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

import org.isf.OHCoreTestCase;
import org.isf.accounting.TestBill;
import org.isf.accounting.model.Bill;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class AccountingPatientMergedEventListenerTest extends OHCoreTestCase {

	private static TestBill testBill;
	private static TestPatient testPatient;
	private static TestPriceList testPriceList;

	@Autowired
	ApplicationEventPublisher applicationEventPublisher;
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
	void handle_shouldUpdatePatientToMergedPatient_whenPatientMergedEventArrives() throws Exception {
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

	private int setupTestBill(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PriceList priceList = testPriceList.setup(false);
		Bill bill = testBill.setup(priceList, patient, null, usingSet);
		priceListIoOperationRepository.saveAndFlush(priceList);
		patientIoOperationRepository.saveAndFlush(patient);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		return bill.getId();
	}

	private Patient setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

}
