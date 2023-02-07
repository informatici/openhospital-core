/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.accounting.model.Bill;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.accounting.test.TestBill;
import org.isf.accounting.test.TestBillPayments;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admission.test.TestAdmission;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.admtype.test.TestAdmissionType;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.disease.test.TestDisease;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.distype.test.TestDiseaseType;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.examination.test.TestPatientExamination;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.service.PatientIoOperations;
import org.isf.patient.test.TestPatient;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.priceslist.test.TestPriceList;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.test.TestVisit;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestMergePatient extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestPatientExamination testPatientExamination;
	private static TestAdmission testAdmission;
	private static TestAdmissionType testAdmissionType;
	private static TestBill testBill;
	private static TestBillPayments testBillPayments;
	private static TestDisease testDisease;
	private static TestDiseaseType testDiseaseType;
	private static TestPriceList testPriceList;
	private static TestVisit testVisit;
	private static TestWard testWard;

	@Autowired
	PatientIoOperations patientIoOperation;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	PatientBrowserManager patientBrowserManager;
	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
	@Autowired
	VisitsIoOperationRepository visitsIoOperationRepository;
	@Autowired
	TestPatientMergedEventListener testPatientMergedEventListener;
	@Autowired
	PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	AccountingBillIoOperationRepository accountingBillIoOperationRepository;
	@Autowired
	AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	AdmissionIoOperationRepository admissionIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testPatient = new TestPatient();
		testPatientExamination = new TestPatientExamination();
		testAdmission = new TestAdmission();
		testAdmissionType = new TestAdmissionType();
		testBill = new TestBill();
		testBillPayments = new TestBillPayments();
		testDisease = new TestDisease();
		testDiseaseType = new TestDiseaseType();
		testPriceList = new TestPriceList();
		testVisit = new TestVisit();
		testWard = new TestWard();
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

	@Test
	public void testMgrMergePatient() throws Exception {
		// given:
		Patient mergedPatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Visit visit = setupVisitAndAssignPatient(obsoletePatient);
		PatientExamination patientExamination = setupPatientExaminationAndAssignPatient(obsoletePatient);

		// when:
		patientBrowserManager.mergePatient(mergedPatient, obsoletePatient);

		// then:
		assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(mergedPatient, obsoletePatient);
		assertThatVisitWasMovedFromObsoleteToMergedPatient(visit, mergedPatient);
		assertThatExaminationWasMovedFromObsoleteToMergedPatient(patientExamination, mergedPatient);
		assertThatPatientMergedEventWasSent(mergedPatient, obsoletePatient);
	}

	@Test
	public void testMgrMergePatientPatient1MissingInformation() throws Exception {
		// given:
		Patient patient1 = testPatient.setup(false);
		patient1.setAddress(null);
		patient1.setCity(null);
		patient1.setNextKin(null);
		patient1.setTelephone(null);
		patient1.setMotherName(null);
		patient1.setMother('U');
		patient1.setFatherName(null);
		patient1.setFather('U');
		patient1.setBloodType(null);
		patient1.setHasInsurance('U');
		patient1.setParentTogether('U');
		patient1.setNote(null);
		Patient mergedPatient = patientIoOperationRepository.saveAndFlush(patient1);
		Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Visit visit = setupVisitAndAssignPatient(obsoletePatient);
		PatientExamination patientExamination = setupPatientExaminationAndAssignPatient(obsoletePatient);

		// when:
		patientBrowserManager.mergePatient(mergedPatient, obsoletePatient);

		// then:
		assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(mergedPatient, obsoletePatient);
		assertThatVisitWasMovedFromObsoleteToMergedPatient(visit, mergedPatient);
		assertThatExaminationWasMovedFromObsoleteToMergedPatient(patientExamination, mergedPatient);
		assertThatPatientMergedEventWasSent(mergedPatient, obsoletePatient);
	}

	@Test
	public void testMgrMergePatientMergeNotes() throws Exception {
		// given:
		Patient patient1 = testPatient.setup(false);
		patient1.setNote("Note 1");
		Patient mergedPatient = patientIoOperationRepository.saveAndFlush(patient1);
		Patient patient2 = testPatient.setup(false);
		patient2.setNote("Note 2");
		Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(patient2);

		// when:
		assertThat(patientBrowserManager.mergePatient(mergedPatient, obsoletePatient)).isTrue();

		// then:
		assertThat(mergedPatient.getNote()).isEqualTo("Note 2\n\nNote 1");
	}

	@Test
	public void testMgrMergePatientMergeBirthDateMissingAgeTypePatient2HasBirthDate() throws Exception {
		// given:
		Patient patient1 = testPatient.setup(false);
		patient1.setBirthDate(LocalDate.of(50, 1, 1));
		patient1.setAgetype(null);
		Patient mergedPatient = patientIoOperationRepository.saveAndFlush(patient1);

		Patient patient2 = testPatient.setup(false);
		patient2.setBirthDate(LocalDate.of(100, 1, 1));
		patient2.setAge(199);
		Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(patient2);

		// when:
		assertThat(patientBrowserManager.mergePatient(mergedPatient, obsoletePatient)).isTrue();

		// then:
		assertThat(mergedPatient.getAge()).isGreaterThanOrEqualTo(21);   // in 2021 the value of age is 21
		assertThat(mergedPatient.getBirthDate()).isEqualTo(LocalDate.of(100, 1, 1));
	}

	@Test
	public void testMgrMergePatientMergeBirthDatePatient2HasAgeType() throws Exception {
		// given:
		Patient patient1 = testPatient.setup(false);
		patient1.setBirthDate(LocalDate.of(50, 1, 1));
		patient1.setAgetype("d5");
		Patient mergedPatient = patientIoOperationRepository.saveAndFlush(patient1);

		Patient patient2 = testPatient.setup(false);
		patient2.setBirthDate(LocalDate.of(100, 1, 1));
		patient2.setAge(199);
		Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(patient2);

		// when:
		assertThat(patientBrowserManager.mergePatient(mergedPatient, obsoletePatient)).isTrue();

		// then:
		assertThat(mergedPatient.getAge()).isGreaterThanOrEqualTo(71);  // in 2021 the value of age is 71
		assertThat(mergedPatient.getAgetype()).isEqualTo("d5");
	}

	@Test
	public void testMgrWholeMergeOperationShouldBeRolledBackWhenOneOfUpdateOperationsFails() throws OHException {
		// given:
		Patient mergedPatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Visit visit = setupVisitAndAssignPatient(obsoletePatient);
		PatientExamination patientExamination = setupPatientExaminationAndAssignPatient(obsoletePatient);
		testPatientMergedEventListener.setShouldFail(true);

		// when:
		try {
			patientBrowserManager.mergePatient(mergedPatient, obsoletePatient);
		} catch (Exception e) {
		}

		// then:
		assertThatObsoletePatientWasNotDeletedAndIsTheActiveOne(mergedPatient);
		assertThatVisitIsStillAssignedToObsoletePatient(visit, obsoletePatient);
		assertThatExaminationIsStillAssignedToObsoletePatient(patientExamination, obsoletePatient);
		assertThatPatientMergedEventWasSent(mergedPatient, obsoletePatient);
	}

	@Test
	public void testMgrMergeValidationSexNotTheSame() {
		assertThatThrownBy(() -> {
			Patient patient1 = testPatient.setup(false);
			patient1.setSex('F');
			Patient mergedPatient = patientIoOperationRepository.saveAndFlush(patient1);

			Patient patient2 = testPatient.setup(false);
			patient2.setSex('M');
			Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(patient2);

			patientBrowserManager.mergePatient(mergedPatient, obsoletePatient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	public void testMgrMergeValidationPatient1PendingBills() {
		assertThatThrownBy(() -> {
			Patient patient1 = testPatient.setup(false);
			PriceList priceList = testPriceList.setup(false);
			Bill bill = testBill.setup(priceList, patient1, null, true);
			priceListIoOperationRepository.saveAndFlush(priceList);
			Patient mergedPatient = patientIoOperationRepository.saveAndFlush(patient1);
			accountingBillIoOperationRepository.saveAndFlush(bill);

			Patient patient2 = testPatient.setup(false);
			Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(patient2);

			patientBrowserManager.mergePatient(mergedPatient, obsoletePatient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting one validation error messages"));
	}

	@Test
	public void testMgrMergeValidationPatient2PendingBills() {
		assertThatThrownBy(() -> {
			Patient patient1 = testPatient.setup(false);
			Patient mergedPatient = patientIoOperationRepository.saveAndFlush(patient1);

			Patient patient2 = testPatient.setup(false);
			PriceList priceList = testPriceList.setup(false);
			Bill bill = testBill.setup(priceList, patient2, null, true);
			priceListIoOperationRepository.saveAndFlush(priceList);
			Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(patient2);
			accountingBillIoOperationRepository.saveAndFlush(bill);

			patientBrowserManager.mergePatient(mergedPatient, obsoletePatient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting one validation error messages"));
	}

	@Test
	public void testMgrMergeValidationPatient1Admitted() {
		assertThatThrownBy(() -> {
			Ward ward = testWard.setup(false, false);
			Patient patient1 = testPatient.setup(false);
			AdmissionType admissionType = testAdmissionType.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease diseaseIn = testDisease.setup(diseaseType, false);
			Disease diseaseOut1 = testDisease.setup(diseaseType, false);
			diseaseOut1.setCode("888");
			Admission admission = testAdmission.setup(ward, patient1, admissionType, diseaseIn, diseaseOut1,
					null, null, null, null, null, null, null, false);

			wardIoOperationRepository.saveAndFlush(ward);
			Patient mergedPatient = patientIoOperationRepository.saveAndFlush(patient1);
			admissionTypeIoOperationRepository.saveAndFlush(admissionType);
			diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
			diseaseIoOperationRepository.saveAndFlush(diseaseIn);
			diseaseIoOperationRepository.saveAndFlush(diseaseOut1);
			admissionIoOperationRepository.saveAndFlush(admission);

			Patient patient2 = testPatient.setup(false);
			Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(patient2);

			patientBrowserManager.mergePatient(mergedPatient, obsoletePatient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting one validation error messages"));
	}

	@Test
	public void testMgrMergeValidationPatient2Admitted() {
		assertThatThrownBy(() -> {
			Patient patient1 = testPatient.setup(false);
			Patient mergedPatient = patientIoOperationRepository.saveAndFlush(patient1);

			Ward ward = testWard.setup(false, false);
			Patient patient2 = testPatient.setup(false);
			AdmissionType admissionType = testAdmissionType.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease diseaseIn = testDisease.setup(diseaseType, false);
			Disease diseaseOut1 = testDisease.setup(diseaseType, false);
			diseaseOut1.setCode("888");
			Admission admission = testAdmission.setup(ward, patient2, admissionType, diseaseIn, diseaseOut1,
					null, null, null, null, null, null, null, false);

			wardIoOperationRepository.saveAndFlush(ward);
			Patient obsoletePatient = patientIoOperationRepository.saveAndFlush(patient2);
			admissionTypeIoOperationRepository.saveAndFlush(admissionType);
			diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
			diseaseIoOperationRepository.saveAndFlush(diseaseIn);
			diseaseIoOperationRepository.saveAndFlush(diseaseOut1);
			admissionIoOperationRepository.saveAndFlush(admission);

			patientBrowserManager.mergePatient(mergedPatient, obsoletePatient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting two validation error messages"));
	}

	private void assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(Patient mergedPatient, Patient obsoletePatient) {
		Patient mergedPatientResult = patientIoOperationRepository.findById(mergedPatient.getCode()).get();
		Patient obsoletePatientResult = patientIoOperationRepository.findById(obsoletePatient.getCode()).get();
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo('Y');
		assertThat(mergedPatientResult.getDeleted()).isEqualTo('N');
	}

	private void assertThatObsoletePatientWasNotDeletedAndIsTheActiveOne(Patient obsoletePatient) throws OHException {
		Patient obsoletePatientResult = patientIoOperationRepository.findById(obsoletePatient.getCode()).get();
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo('N');
	}

	private void assertThatVisitWasMovedFromObsoleteToMergedPatient(Visit visit, Patient mergedPatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.findById(visit.getVisitID()).get();
		assertThat(visitResult.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private void assertThatVisitIsStillAssignedToObsoletePatient(Visit visit, Patient obsoletePatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.findById(visit.getVisitID()).get();
		assertThat(visitResult.getPatient().getCode()).isEqualTo(obsoletePatient.getCode());
	}

	private void assertThatExaminationWasMovedFromObsoleteToMergedPatient(PatientExamination examination, Patient mergedPatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.findById(examination.getPex_ID()).get();
		assertThat(patientResult.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private void assertThatExaminationIsStillAssignedToObsoletePatient(PatientExamination patientExamination, Patient obsoletePatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.findById(patientExamination.getPex_ID()).get();
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
