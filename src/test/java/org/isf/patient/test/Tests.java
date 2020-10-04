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
package org.isf.patient.test;

import static org.assertj.core.api.Assertions.assertThat;
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
    }

    @Before
    public void setUp() throws OHException
    {
        jpa.open();
        
        _saveContext();
    }
        
    @After
    public void tearDown() throws Exception 
    {
        _restoreContext();   
        
        jpa.flush();
        jpa.close();
    }
    
    @AfterClass
    public static void tearDownClass() throws OHException 
    {

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
	public void testIoGetPatientsByOneOfFieldsLikeMiddleOfFirstName() {
		try {
			// given:
			Integer code = _setupTestPatient(false);
			Patient foundPatient = (Patient)jpa.find(Patient.class, code);

			// when:
			ArrayList<Patient> patients = patientIoOperation
					.getPatientsByOneOfFieldsLike(foundPatient.getFirstName().substring(1, foundPatient.getFirstName().length() - 2));

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

			assertThat(patients).isEmpty();
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
			
			assertThat(patient.getName()).isEqualTo(foundPatient.getName());
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

			assertThat(patient.getName()).isEqualTo(foundPatient.getName());
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
			
			
			assertThat(patient.getName()).isEqualTo(foundPatient.getName());
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
		assertThat(patientIoOperation.savePatient(patient)).isNotNull();
	}
		
	@Test
	public void testUpdatePatientTrue() throws OHException {
		Integer code = _setupTestPatient(false);
		Patient patient = (Patient)jpa.find(Patient.class, code);
		jpa.flush();
		Patient result = patientIoOperation.savePatient(patient);

		assertThat(result).isNotNull();
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

			assertThat(result).isTrue();
			assertThat(deletedPatient.getCode()).isEqualTo(code);
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

			assertThat(result).isTrue();
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
			assertThat((code + 1)).isEqualTo(max);
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
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo("Y");
		assertThat(mergedPatientResult.getDeleted()).isEqualTo("N");
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
	}
		
	@SuppressWarnings("unchecked")
	private Patient _getDeletedPatient(
			Integer Code) throws OHException 
	{	
		ArrayList<Object> params = new ArrayList<>();
		
		
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM PATIENT WHERE PAT_DELETED = 'Y' AND PAT_ID = ?", Patient.class, false);
		params.add(Code);
		jpa.setParameters(params, false);
		List<Patient> patients = (List<Patient>)jpa.getList();
		jpa.commitTransaction();
		
		return patients.get(0);
	}
}

