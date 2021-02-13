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
package org.isf.patient;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.examination.test.TestPatientExamination;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.service.PatientIoOperations;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.test.TestVisit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestMergePatient extends OHCoreTestCase {

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

	@BeforeClass
	public static void setUpClass() {
		testPatient = new TestPatient();
		testPatientExamination = new TestPatientExamination();
		testVisit = new TestVisit();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
		testPatientMergedEventListener.setShouldFail(false);
	}

	@Test
	public void testMergePatientHistory() throws Exception {
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
	public void testWholeMergeOperationShouldBeRolledBackWhenOneOfUpdateOperationsFails() throws OHException {
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
		Patient mergedPatientResult = patientIoOperationRepository.findOne(mergedPatient.getCode());
		Patient obsoletePatientResult = patientIoOperationRepository.findOne(obsoletePatient.getCode());
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo("Y");
		assertThat(mergedPatientResult.getDeleted()).isEqualTo("N");
	}

	private void assertThatObsoletePatientWasNotDeletedAndIsTheActiveOne(Patient obsoletePatient) throws OHException {
		Patient obsoletePatientResult = patientIoOperationRepository.findOne(obsoletePatient.getCode());
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo("N");
	}

	private void assertThatVisitWasMovedFromObsoleteToMergedPatient(Visit visit, Patient mergedPatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.findOne(visit.getVisitID());
		assertThat(visitResult.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private void assertThatVisitIsStillAssignedToObsoletePatient(Visit visit, Patient obsoletePatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.findOne(visit.getVisitID());
		assertThat(visitResult.getPatient().getCode()).isEqualTo(obsoletePatient.getCode());
	}

	private void assertThatExaminationWasMovedFromObsoleteToMergedPatient(PatientExamination examination, Patient mergedPatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.findOne(examination.getPex_ID());
		assertThat(patientResult.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private void assertThatExaminationIsStillAssignedToObsoletePatient(PatientExamination patientExamination, Patient obsoletePatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.findOne(patientExamination.getPex_ID());
		assertThat(patientResult.getPatient().getCode()).isEqualTo(obsoletePatient.getCode());
	}

	private void assertThatPatientMergedEventWasSent(Patient mergedPatient, Patient obsoletePatient) {
		assertThat(testPatientMergedEventListener.getPatientMergedEvent().getMergedPatient().getCode()).isEqualTo(mergedPatient.getCode());
		assertThat(testPatientMergedEventListener.getPatientMergedEvent().getObsoletePatient().getCode()).isEqualTo(obsoletePatient.getCode());
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
