package org.isf.patient.test;

import org.isf.accounting.test.TestBill;
import org.isf.accounting.test.TestBillContext;
import org.isf.admission.test.TestAdmission;
import org.isf.admission.test.TestAdmissionContext;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.test.TestPatientExamination;
import org.isf.examination.test.TestPatientExaminationContext;
import org.isf.lab.test.TestLaboratory;
import org.isf.lab.test.TestLaboratoryContext;
import org.isf.medicalstockward.test.TestMovementWard;
import org.isf.medicalstockward.test.TestMovementWardContext;
import org.isf.opd.test.TestOpd;
import org.isf.opd.test.TestOpdContext;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperations;
import org.isf.patvac.test.TestPatientVaccine;
import org.isf.patvac.test.TestPatientVaccineContext;
import org.isf.priceslist.test.TestPriceList;
import org.isf.priceslist.test.TestPriceListContext;
import org.isf.therapy.test.TestTherapy;
import org.isf.therapy.test.TestTherapyContext;
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

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class MergePatientTests {
	private static DbJpaUtil jpa;
	private static TestPatient testPatient;
	private static TestPatientContext testPatientContext;
	private static TestAdmission testAdmission;
	private static TestAdmissionContext testAdmissionContext;
	private static TestPatientExamination testPatientExamination;
	private static TestPatientExaminationContext testPatientExaminationContext;
	private static TestLaboratory testLaboratory;
	private static TestLaboratoryContext testLaboratoryContext;
	private static TestOpd testOpd;
	private static TestOpdContext testOpdContext;
	private static TestBill testBill;
	private static TestBillContext testBillContext;
	private static TestPriceList testPriceList;
	private static TestPriceListContext testPriceListContext;
	private static TestMovementWard testMovementWard;
	private static TestMovementWardContext testMovementWardContext;
	private static TestTherapy testTherapy;
	private static TestTherapyContext testTherapyContext;
	private static TestVisit testVisit;
	private static TestVisitContext testVisitContext;
	private static TestPatientVaccine testPatientVaccine;
	private static TestPatientVaccineContext testPatientVaccineContext;

	@Autowired
	PatientIoOperations patientIoOperation;
	@Autowired
	TestPatientMergedEventListener testPatientMergedEventListener;

	@BeforeClass
	public static void setUpClass()	{
		jpa = new DbJpaUtil();
		testPatient = new TestPatient();
		testPatientContext = new TestPatientContext();
		testAdmission = new TestAdmission();
		testAdmissionContext = new TestAdmissionContext();
		testPatientExamination = new TestPatientExamination();
		testPatientExaminationContext = new TestPatientExaminationContext();
		testLaboratory = new TestLaboratory();
		testLaboratoryContext = new TestLaboratoryContext();
		testOpd = new TestOpd();
		testOpdContext = new TestOpdContext();
		testBill = new TestBill();
		testBillContext = new TestBillContext();
		testPriceList = new TestPriceList();
		testPriceListContext = new TestPriceListContext();
		testMovementWard = new TestMovementWard();
		testMovementWardContext = new TestMovementWardContext();
		testTherapy = new TestTherapy();
		testTherapyContext = new TestTherapyContext();
		testVisit = new TestVisit();
		testVisitContext = new TestVisitContext();
		testPatientVaccine = new TestPatientVaccine();
		testPatientVaccineContext = new TestPatientVaccineContext();
	}

	@Before
	public void setUp() throws OHException {
		jpa.open();

		_saveContext();

		return;
	}

	@After
	public void tearDown() throws Exception
	{
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
		//testAdmissionContext.saveAll(jpa);
		testPatientExaminationContext.saveAll(jpa);
		/*
		testLaboratoryContext.saveAll(jpa);
		testOpdContext.saveAll(jpa);
		testBillContext.saveAll(jpa);
		testPriceListContext.saveAll(jpa);
		testMovementWardContext.saveAll(jpa);
		testTherapyContext.saveAll(jpa);

		 */
		testVisitContext.saveAll(jpa);
		//testPatientVaccineContext.saveAll(jpa);
	}

	private void _restoreContext() throws OHException {
		testPatientContext.deleteNews(jpa);
		//testAdmissionContext.deleteNews(jpa);
		testPatientExaminationContext.deleteNews(jpa);
		/*
		testLaboratoryContext.deleteNews(jpa);
		testOpdContext.deleteNews(jpa);
		testBillContext.deleteNews(jpa);
		testPriceListContext.deleteNews(jpa);
		testMovementWardContext.deleteNews(jpa);
		testTherapyContext.deleteNews(jpa);

		 */
		testVisitContext.deleteNews(jpa);
		//testPatientVaccineContext.deleteNews(jpa);
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

	private void assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(Patient mergedPatient, Patient obsoletePatient) throws OHException {
		Patient mergedPatientResult = (Patient) jpa.find(Patient.class, mergedPatient.getCode());
		Patient obsoletePatientResult = (Patient) jpa.find(Patient.class, obsoletePatient.getCode());
		assertEquals("Y", obsoletePatientResult.getDeleted());
		assertEquals("N", mergedPatientResult.getDeleted());
	}

	private void assertThatVisitWasMovedFromObsoleteToMergedPatient(Visit visit, Patient mergedPatient) throws OHException {
		Visit visitResult = (Visit) jpa.find(Visit.class, visit.getVisitID());
		assertEquals(mergedPatient.getCode(), visitResult.getPatient().getCode());
	}

	private void assertThatExaminationWasMovedFromObsoleteToMergedPatient(PatientExamination examination, Patient mergedPatient) throws OHException {
		PatientExamination patientResult = (PatientExamination) jpa.find(PatientExamination.class, examination.getPex_ID());
		assertEquals(mergedPatient.getCode(), patientResult.getPatient().getCode());
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
