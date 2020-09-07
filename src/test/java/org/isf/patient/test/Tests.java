package org.isf.patient.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
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
public class Tests 
{	
	private static DbJpaUtil jpa;	
	private static TestPatient testPatient;
	private static TestPatientContext testPatientContext;

    @Autowired
    PatientIoOperations patientIoOperation;

    @BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testPatient = new TestPatient();
    	testPatientContext = new TestPatientContext();

        return;
    }

    @Before
    public void setUp() throws OHException
    {
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
    
	
	@Test
	public void testPatientGets() throws OHException 
	{
		Integer code = 0;
				

		try 
		{		
			code = _setupTestPatient(false);
			_checkPatientIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}

	@Test
	public void testPatientSets() 
	{
		Integer code = 0;
		
				
		try 
		{		
			code = _setupTestPatient(true);	
			_checkPatientIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testIoGetPatients() 
	{		
		try 
		{		
			_setupTestPatient(false);
			ArrayList<Patient> patients = patientIoOperation.getPatients();
			
			testPatient.check( patients.get(patients.size()-1));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetPatientsWithHeightAndWeight() 
	{	
		try 
		{		
			_setupTestPatient(false);
			// Pay attention that query return with PAT_ID descendant
			ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(null);

			testPatient.check(patients.get(0));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetPatientsByOneOfFieldsLikeFirstName() {
		try {
			// given:
			Integer code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code);

			// when:
			ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getFirstName());

			// then:
			testPatient.check(patients.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeSecondName() {
		try {
			// given:
			Integer code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code);

			// when:
			ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

			// then:
			testPatient.check(patients.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeNote() {
		try {
			// given:
			Integer code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code);

			// when:
			ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

			// then:
			testPatient.check(patients.get(0));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeTaxCode()	{
		try	{
			// given:
			Integer code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code);

			// when:
			ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getTaxCode());

			// then:
			testPatient.check(patients.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		return;
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeNotExistingStringShouldNotFindAnything()
	{
		try
		{
			Integer code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code);
			ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike("dupa");

			assertTrue(patients.isEmpty());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}

		return;
	}

	@Test
	public void testIoGetPatientFromName() 
	{	
		try 
		{	
			Integer code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code); 
			Patient patient = patientIoOperation.getPatient(foundPatient.getName());
			
			assertEquals(foundPatient.getName(), patient.getName());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetPatientFromCode() 
	{	
		try 
		{		
			Integer code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code); 
			Patient patient = patientIoOperation.getPatient(code);

			assertEquals(foundPatient.getName(), patient.getName());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetPatientAll() 
	{	
		Integer code = 0;
		
		
		try 
		{		
			code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code); 
			Patient patient = patientIoOperation.getPatientAll(code);
			
			
			assertEquals(foundPatient.getName(), patient.getName());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testNewPatient() throws OHException {
		Patient patient = testPatient.setup(true);
		assertNotNull(patientIoOperation.savePatient(patient));
	}
		
	@Test
	public void testUpdatePatientTrue() throws OHException {
		Integer code = _setupTestPatient(false);
		Patient patient = (Patient)jpa.find(Patient.class, code);
		jpa.flush();
		Patient result = patientIoOperation.savePatient(patient);

		assertNotNull(result);
	}
	
	@Test
	public void testDeletePatient() 
	{		
		try 
		{		
			Integer code = _setupTestPatient(false);
			Patient patient = (Patient)jpa.find(Patient.class, code); 
			boolean result = patientIoOperation.deletePatient(patient);
			Patient deletedPatient = _getDeletedPatient(code);

			assertTrue(result);
			assertEquals(code, deletedPatient.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}	
	
	@Test
	public void testIsPatientPresent() 
	{	
		try 
		{		
			Integer code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code); 
			boolean result = patientIoOperation.isPatientPresentByName(foundPatient.getName());

			assertTrue(result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}	
	
	@Test
	public void testGetNextPatientCode() 
	{
		Integer code = 0;
		Integer max = 0;
		
		
		try 
		{				
			code = _setupTestPatient(false);
			max = patientIoOperation.getNextPatientCode();			
			assertEquals(max, (code + 1), 0.1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
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

			// when:
			patientIoOperation.mergePatientHistory(mergedPatient, obsoletePatient);

			// then:
			assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(mergedPatient, obsoletePatient);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			testPatientContext.deleteNews(jpa); // we create 2 entries so additional deletion needed
		}
	}

	private void assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(Patient mergedPatient, Patient obsoletePatient) throws OHException {
		Patient mergedPatientResult = (Patient) jpa.find(Patient.class, mergedPatient.getCode());
		Patient obsoletePatientResult = (Patient) jpa.find(Patient.class, obsoletePatient.getCode());
		assertEquals("Y", obsoletePatientResult.getDeleted());
		assertEquals("N", mergedPatientResult.getDeleted());
	}

	private void _saveContext() throws OHException 
    {	
		testPatientContext.saveAll(jpa);
        		
        return;
    }
		
    private void _restoreContext() throws OHException 
    {
		testPatientContext.deleteNews(jpa);
        
        return;
    }
    	
	private Integer _setupTestPatient(
			boolean usingSet) throws OHException 
	{
		Patient patient;
	
		
    	jpa.beginTransaction();	
    	patient = testPatient.setup(usingSet);
		jpa.persist(patient);
    	jpa.commitTransaction();
				    	
		return patient.getCode();
	}
		
	private void _checkPatientIntoDb(
			Integer code) throws OHException 
	{
		Patient foundPatient; 
			

		foundPatient = (Patient)jpa.find(Patient.class, code); 
		testPatient.check(foundPatient);
		
		return;
	}
		
	@SuppressWarnings("unchecked")
	private Patient _getDeletedPatient(
			Integer Code) throws OHException 
	{	
		ArrayList<Object> params = new ArrayList<Object>();
		
		
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM PATIENT WHERE PAT_DELETED = 'Y' AND PAT_ID = ?", Patient.class, false);
		params.add(Code);
		jpa.setParameters(params, false);
		List<Patient> patients = (List<Patient>)jpa.getList();
		jpa.commitTransaction();
		
		return patients.get(0);
	}
}

