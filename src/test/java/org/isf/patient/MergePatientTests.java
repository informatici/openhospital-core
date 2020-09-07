package org.isf.patient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.examination.test.TestPatientExamination;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.service.PatientIoOperations;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.test.TestVisit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class MergePatientTests {
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
	public static void setUpClass()	{
		testPatient = new TestPatient();
		testPatientExamination = new TestPatientExamination();
		testVisit = new TestVisit();
	}

	@Before
	public void setUp() throws OHException {
		visitsIoOperationRepository.deleteAll();
		examinationIoOperationRepository.deleteAll();
		patientIoOperationRepository.deleteAll();
		testPatientMergedEventListener.setShouldFail(false);
	}

	@Test
	public void testMergePatientHistory() throws OHException, OHServiceException {
		try {
			// given:
			Patient mergedPatient = patientIoOperationRepository.save(testPatient.setup(false));
			Patient obsoletePatient = patientIoOperationRepository.save(testPatient.setup(false));
			Visit visit = setupVisitAndAssignPatient(obsoletePatient);
			PatientExamination patientExamination = setupPatientExaminationAndAssignPatient(obsoletePatient);

			// when:
			patientIoOperation.mergePatientHistory(mergedPatient, obsoletePatient);

			// then:
			assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(mergedPatient, obsoletePatient);
			assertThatVisitWasMovedFromObsoleteToMergedPatient(visit, mergedPatient);
			assertThatExaminationWasMovedFromObsoleteToMergedPatient(patientExamination, mergedPatient);
			assertThatPatientMergedEventWasSent(mergedPatient, obsoletePatient);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testWholeMergeOperationShouldBeRolledBackWhenOneOfUpdateOperationsFails() throws OHException, OHServiceException {
		try {
			// given:
			Patient mergedPatient = patientIoOperationRepository.save(testPatient.setup(false));
			Patient obsoletePatient = patientIoOperationRepository.save(testPatient.setup(false));
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
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private void assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(Patient mergedPatient, Patient obsoletePatient) throws OHException {
		Patient mergedPatientResult = patientIoOperationRepository.getOne(mergedPatient.getCode());
		Patient obsoletePatientResult = patientIoOperationRepository.getOne(obsoletePatient.getCode());
		assertEquals("Y", obsoletePatientResult.getDeleted());
		assertEquals("N", mergedPatientResult.getDeleted());
	}

	private void assertThatObsoletePatientWasNotDeletedAndIsTheActiveOne(Patient obsoletePatient) throws OHException {
		Patient obsoletePatientResult = patientIoOperationRepository.getOne(obsoletePatient.getCode());
		assertEquals("N", obsoletePatientResult.getDeleted());
	}

	private void assertThatVisitWasMovedFromObsoleteToMergedPatient(Visit visit, Patient mergedPatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.getOne(visit.getVisitID());
		assertEquals(mergedPatient.getCode(), visitResult.getPatient().getCode());
	}

	private void assertThatVisitIsStillAssignedToObsoletePatient(Visit visit, Patient obsoletePatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.getOne(visit.getVisitID());
		assertEquals(obsoletePatient.getCode(), visitResult.getPatient().getCode());
	}

	private void assertThatExaminationWasMovedFromObsoleteToMergedPatient(PatientExamination examination, Patient mergedPatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.getOne(examination.getPex_ID());
		assertEquals(mergedPatient.getCode(), patientResult.getPatient().getCode());
	}

	private void assertThatExaminationIsStillAssignedToObsoletePatient(PatientExamination patientExamination, Patient obsoletePatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.getOne(patientExamination.getPex_ID());
		assertEquals(obsoletePatient.getCode(), patientResult.getPatient().getCode());
	}

	private void assertThatPatientMergedEventWasSent(Patient mergedPatient, Patient obsoletePatient) {
		assertEquals(mergedPatient.getCode(), testPatientMergedEventListener.getPatientMergedEvent().getMergedPatient().getCode());
		assertEquals(obsoletePatient.getCode(), testPatientMergedEventListener.getPatientMergedEvent().getObsoletePatient().getCode());
	}

	private Visit setupVisitAndAssignPatient(Patient patient) throws OHException {
		Visit visit = testVisit.setup(patient, false, null);
		visitsIoOperationRepository.save(visit);
		return visit;
	}

	private PatientExamination setupPatientExaminationAndAssignPatient(Patient patient) throws OHException {
		PatientExamination patientExamination = testPatientExamination.setup(patient, false);
		examinationIoOperationRepository.save(patientExamination);
		return patientExamination;
	}
}
