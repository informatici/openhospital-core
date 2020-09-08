package org.isf.patient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.examination.test.TestPatientExamination;
import org.isf.examination.test.TestPatientExaminationContext;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.service.PatientIoOperations;
import org.isf.patient.test.TestPatient;
import org.isf.patient.test.TestPatientContext;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.test.TestVisit;
import org.isf.visits.test.TestVisitContext;
import org.junit.After;
import org.junit.AfterClass;
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
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
	@Autowired
	VisitsIoOperationRepository visitsIoOperationRepository;
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
        testPatientMergedEventListener.setShouldFail(false);
		
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
    
    private void _saveContext() throws OHException 
    {	
		testPatientContext.saveAll(jpa);
		testPatientExaminationContext.saveAll(jpa);
		testVisitContext.saveAll(jpa);
        		
        return;
    }
		
    private void _restoreContext() throws OHException 
    {
    	testVisitContext.deleteNews(jpa);
    	testPatientExaminationContext.deleteNews(jpa);
    	testPatientContext.deleteNews(jpa);
        
        return;
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
		Patient mergedPatientResult = patientIoOperationRepository.findOne(mergedPatient.getCode());
		Patient obsoletePatientResult = patientIoOperationRepository.findOne(obsoletePatient.getCode());
		assertEquals("Y", obsoletePatientResult.getDeleted());
		assertEquals("N", mergedPatientResult.getDeleted());
	}

	private void assertThatObsoletePatientWasNotDeletedAndIsTheActiveOne(Patient obsoletePatient) throws OHException {
		Patient obsoletePatientResult = patientIoOperationRepository.findOne(obsoletePatient.getCode());
		assertEquals("N", obsoletePatientResult.getDeleted());
	}

	private void assertThatVisitWasMovedFromObsoleteToMergedPatient(Visit visit, Patient mergedPatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.findOne(visit.getVisitID());
		assertEquals(mergedPatient.getCode(), visitResult.getPatient().getCode());
	}

	private void assertThatVisitIsStillAssignedToObsoletePatient(Visit visit, Patient obsoletePatient) throws OHException {
		Visit visitResult = visitsIoOperationRepository.findOne(visit.getVisitID());
		assertEquals(obsoletePatient.getCode(), visitResult.getPatient().getCode());
	}

	private void assertThatExaminationWasMovedFromObsoleteToMergedPatient(PatientExamination examination, Patient mergedPatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.findOne(examination.getPex_ID());
		assertEquals(mergedPatient.getCode(), patientResult.getPatient().getCode());
	}

	private void assertThatExaminationIsStillAssignedToObsoletePatient(PatientExamination patientExamination, Patient obsoletePatient) throws OHException {
		PatientExamination patientResult = examinationIoOperationRepository.findOne(patientExamination.getPex_ID());
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
