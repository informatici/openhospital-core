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
package org.isf.patient.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.accounting.TestBillPayments;
import org.isf.examination.TestPatientExamination;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.TestPatientMergedEventListener;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.visits.TestVisit;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PatientIoOperationsMergeTest extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestPatientExamination testPatientExamination;
	private static TestVisit testVisit;

	@Autowired
	PatientIoOperations patientIoOperation;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
	@Autowired
	VisitsIoOperationRepository visitsIoOperationRepository;
	@Autowired
	TestPatientMergedEventListener testPatientMergedEventListener;

	@BeforeAll
	static void setUpClass() {
		testPatient = new TestPatient();
		testPatientExamination = new TestPatientExamination();
		TestBillPayments testBillPayments = new TestBillPayments();
		testVisit = new TestVisit();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
		testPatientMergedEventListener.setShouldFail(false);
	}

	@Test
	void mergePatientHistory_shouldMarkObsoletePatientDeletedAndMoveVisitAndExamination() throws Exception {
		// given:
		Patient mergedPatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Visit visit = setupVisitAndAssignPatient(obsoletePatient);
		PatientExamination patientExamination = setupPatientExaminationAndAssignPatient(obsoletePatient);

		// when:
		patientIoOperation.mergePatientHistory(mergedPatient, obsoletePatient);

		// then:
		assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(mergedPatient, obsoletePatient);
		assertThatVisitWasMovedFromObsoleteToMergedPatient(visit, mergedPatient);
		assertThatExaminationWasMovedFromObsoleteToMergedPatient(patientExamination, mergedPatient);
		assertThatPatientMergedEventWasSent(mergedPatient, obsoletePatient);
	}

	@Test
	void mergePatientHistory_shouldKeepVisitAndExaminationOnObsoletePatient_whenEventListenerFails() throws OHException {
		// given:
		Patient mergedPatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Visit visit = setupVisitAndAssignPatient(obsoletePatient);
		PatientExamination patientExamination = setupPatientExaminationAndAssignPatient(obsoletePatient);
		testPatientMergedEventListener.setShouldFail(true);

		// when:
		try {
			patientIoOperation.mergePatientHistory(mergedPatient, obsoletePatient);
		} catch (Exception e) {
		}

		// then:
		assertThatObsoletePatientWasNotDeletedAndIsTheActiveOne(mergedPatient);
		assertThatVisitIsStillAssignedToObsoletePatient(visit, obsoletePatient);
		assertThatExaminationIsStillAssignedToObsoletePatient(patientExamination, obsoletePatient);
		assertThatPatientMergedEventWasSent(mergedPatient, obsoletePatient);
	}

	private void assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(Patient mergedPatient, Patient obsoletePatient) {
		Patient mergedPatientResult = patientIoOperationRepository.findById(mergedPatient.getCode()).orElse(null);
		assertThat(mergedPatientResult).isNotNull();
		Patient obsoletePatientResult = patientIoOperationRepository.findById(obsoletePatient.getCode()).orElse(null);
		assertThat(obsoletePatientResult).isNotNull();
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo('Y');
		assertThat(mergedPatientResult.getDeleted()).isEqualTo('N');
	}

	private void assertThatObsoletePatientWasNotDeletedAndIsTheActiveOne(Patient obsoletePatient) throws OHException {
		Patient obsoletePatientResult = patientIoOperationRepository.findById(obsoletePatient.getCode()).orElse(null);
		assertThat(obsoletePatientResult).isNotNull();
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo('N');
	}

	private void assertThatVisitWasMovedFromObsoleteToMergedPatient(Visit visit, Patient mergedPatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.findById(visit.getVisitID()).orElse(null);
		assertThat(visitResult).isNotNull();
		assertThat(visitResult.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private void assertThatVisitIsStillAssignedToObsoletePatient(Visit visit, Patient obsoletePatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.findById(visit.getVisitID()).orElse(null);
		assertThat(visitResult).isNotNull();
		assertThat(visitResult.getPatient().getCode()).isEqualTo(obsoletePatient.getCode());
	}

	private void assertThatExaminationWasMovedFromObsoleteToMergedPatient(PatientExamination examination, Patient mergedPatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.findById(examination.getPex_ID()).orElse(null);
		assertThat(patientResult).isNotNull();
		assertThat(patientResult.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private void assertThatExaminationIsStillAssignedToObsoletePatient(PatientExamination patientExamination, Patient obsoletePatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.findById(patientExamination.getPex_ID()).orElse(null);
		assertThat(patientResult).isNotNull();
		assertThat(patientResult.getPatient().getCode()).isEqualTo(obsoletePatient.getCode());
	}

	private void assertThatPatientMergedEventWasSent(Patient mergedPatient, Patient obsoletePatient) {
		assertThat(testPatientMergedEventListener.getPatientMergedEvent().mergedPatient().getCode()).isEqualTo(mergedPatient.getCode());
		assertThat(testPatientMergedEventListener.getPatientMergedEvent().obsoletePatient().getCode()).isEqualTo(obsoletePatient.getCode());
	}

	private Visit setupVisitAndAssignPatient(Patient patient) throws OHException {
		Visit visit = testVisit.setup(patient, false, null);
		visitsIoOperationRepository.saveAndFlush(visit);
		return visit;
	}

	private PatientExamination setupPatientExaminationAndAssignPatient(Patient patient) throws OHException {
		PatientExamination patientExamination = testPatientExamination.setup(patient, false);
		examinationIoOperationRepository.saveAndFlush(patientExamination);
		return patientExamination;
	}

}
