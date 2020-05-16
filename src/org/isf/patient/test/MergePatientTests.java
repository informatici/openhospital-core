package org.isf.patient.test;

import org.isf.examination.model.PatientExamination;
import org.isf.examination.test.TestPatientExamination;
import org.isf.examination.test.TestPatientExaminationContext;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.model.Visit;
import org.isf.visits.test.TestVisit;
import org.isf.visits.test.TestVisitContext;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class MergePatientTests {
	private static DbJpaUtil jpa;
	private static TestPatient testPatient;
	private static TestPatientContext testPatientContext;
	private static TestPatientExamination testPatientExamination;
	private static TestPatientExaminationContext testPatientExaminationContext;
	private static TestVisit testVisit;
	private static TestVisitContext testVisitContext;

	@Autowired
	PatientIoOperations patientIoOperation;
	@Autowired
	TestPatientMergedEventListener testPatientMergedEventListener;

	@BeforeClass
	public static void setUpClass()	{
		jpa = new DbJpaUtil();
		testPatient = new TestPatient();
		testPatientContext = new TestPatientContext();
		testPatientExamination = new TestPatientExamination();
		testPatientExaminationContext = new TestPatientExaminationContext();
		testVisit = new TestVisit();
		testVisitContext = new TestVisitContext();
	}

	@Before
	public void setUp() throws OHException {
		jpa.open();
		_saveContext();
	}

	@After
	public void tearDown() throws Exception	{
		_restoreContext();
		jpa.flush();
		jpa.close();

		return;
	}

	@AfterClass
	public static void tearDownClass() throws OHException
	{
		return;
	}

	private void _saveContext() throws OHException {
		testPatientContext.saveAll(jpa);
		testPatientExaminationContext.saveAll(jpa);
		testVisitContext.saveAll(jpa);
	}

	private void _restoreContext() throws OHException {
		testPatientContext.deleteNews(jpa);
		testPatientExaminationContext.deleteNews(jpa);
		testVisitContext.deleteNews(jpa);
		testPatientMergedEventListener.setShouldFail(false);
	}

	public void testShouldRollbackWholeMergeWhenOneOperationFails() {

	}

	@Test
	public void testMergePatientHistory() throws OHException, OHServiceException {
		try {
			// given:
			jpa.beginTransaction();
			Patient mergedPatient = testPatient.setup(false);
			jpa.persist(mergedPatient);
			Patient obsoletePatient = testPatient.setup(false);
			jpa.persist(obsoletePatient);
			jpa.commitTransaction();
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
			assertEquals(true, false);
		}
	}

	@Test
	public void testWholeMergeOperationShouldBeRolledBackWhenOneOfUpdateOperationsFails() throws OHException, OHServiceException {
		try {
			// given:
			jpa.beginTransaction();
			Patient mergedPatient = testPatient.setup(false);
			jpa.persist(mergedPatient);
			Patient obsoletePatient = testPatient.setup(false);
			jpa.persist(obsoletePatient);
			jpa.commitTransaction();
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
			assertEquals(true, false);
		}
	}

	private void assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(Patient mergedPatient, Patient obsoletePatient) throws OHException {
		Patient mergedPatientResult = (Patient) jpa.find(Patient.class, mergedPatient.getCode());
		Patient obsoletePatientResult = (Patient) jpa.find(Patient.class, obsoletePatient.getCode());
		assertEquals("Y", obsoletePatientResult.getDeleted());
		assertEquals("N", mergedPatientResult.getDeleted());
	}

	private void assertThatObsoletePatientWasNotDeletedAndIsTheActiveOne(Patient obsoletePatient) throws OHException {
		Patient obsoletePatientResult = (Patient) jpa.find(Patient.class, obsoletePatient.getCode());
		assertEquals("N", obsoletePatientResult.getDeleted());
	}

	private void assertThatVisitWasMovedFromObsoleteToMergedPatient(Visit visit, Patient mergedPatient) throws OHException {
		Visit visitResult = (Visit) jpa.find(Visit.class, visit.getVisitID());
		assertEquals(mergedPatient.getCode(), visitResult.getPatient().getCode());
	}

	private void assertThatVisitIsStillAssignedToObsoletePatient(Visit visit, Patient obsoletePatient) throws OHException {
		Visit visitResult = (Visit) jpa.find(Visit.class, visit.getVisitID());
		assertEquals(obsoletePatient.getCode(), visitResult.getPatient().getCode());
	}

	private void assertThatExaminationWasMovedFromObsoleteToMergedPatient(PatientExamination examination, Patient mergedPatient) throws OHException {
		PatientExamination patientResult = (PatientExamination) jpa.find(PatientExamination.class, examination.getPex_ID());
		assertEquals(mergedPatient.getCode(), patientResult.getPatient().getCode());
	}

	private void assertThatExaminationIsStillAssignedToObsoletePatient(PatientExamination patientExamination, Patient obsoletePatient) throws OHException {
		PatientExamination patientResult = (PatientExamination) jpa.find(PatientExamination.class, patientExamination.getPex_ID());
		assertEquals(obsoletePatient.getCode(), patientResult.getPatient().getCode());
	}

	private void assertThatPatientMergedEventWasSent(Patient mergedPatient, Patient obsoletePatient) {
		assertEquals(mergedPatient.getCode(), testPatientMergedEventListener.getPatientMergedEvent().getMergedPatient().getCode());
		assertEquals(obsoletePatient.getCode(), testPatientMergedEventListener.getPatientMergedEvent().getObsoletePatient().getCode());
	}

	private Visit setupVisitAndAssignPatient(Patient patient) throws OHException {
		jpa.beginTransaction();
		Visit visit = testVisit.setup(patient, false);
		jpa.persist(visit);
		jpa.commitTransaction();
		return visit;
	}

	private PatientExamination setupPatientExaminationAndAssignPatient(Patient patient) throws OHException {
		jpa.beginTransaction();
		PatientExamination patientExamination = testPatientExamination.setup(patient, false);
		jpa.persist(patientExamination);
		jpa.commitTransaction();
		return patientExamination;
	}
}
