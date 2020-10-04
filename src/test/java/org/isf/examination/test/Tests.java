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
package org.isf.examination.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.test.TestPatient;
import org.isf.patient.test.TestPatientContext;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests 
{
	private static DbJpaUtil jpa;
	private static TestPatient testPatient;
	private static TestPatientExamination testPatientExamination;
	private static TestPatientContext testPatientContext;
	private static TestPatientExaminationContext testPatientExaminationContext;

    @Autowired
    ExaminationOperations examinationOperations;
    @Autowired
	ApplicationEventPublisher applicationEventPublisher;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testPatient = new TestPatient();
    	testPatientExamination = new TestPatientExamination();
    	testPatientContext = new TestPatientContext();
    	testPatientExaminationContext = new TestPatientExaminationContext();
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
    	testPatient = null;
    	testPatientExamination = null;
    	testPatientContext = null;
    	testPatientExaminationContext = null;
    }
	
		
	@Test
	public void testPatientExaminationGets() 
	{
		int id = 0;
			

		try 
		{		
			id = _setupTestPatientExamination(false);
			_checkPatientExaminationIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testPatientExaminationSets() 
	{
		int id = 0;
			

		try 
		{		
			id = _setupTestPatientExamination(true);
			_checkPatientExaminationIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testGetFromLastPatientExamination()
	{		
		try 
		{		
			Patient	patient = testPatient.setup(false);		
			PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);	

			
			PatientExamination patientExamination = examinationOperations.getFromLastPatientExamination(lastPatientExamination);
			testPatientExamination.check(patientExamination);
			testPatient.check(patientExamination.getPatient());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testSaveOrUpdate() 
	{
		int id = 0;
			

		try 
		{		
			id = _setupTestPatientExamination(false);	
			PatientExamination patientExamination = (PatientExamination)jpa.find(PatientExamination.class, id); 
			Integer pex_hr = patientExamination.getPex_hr();
			patientExamination.setPex_hr(pex_hr + 1);
			examinationOperations.saveOrUpdate(patientExamination);
			assertThat(patientExamination.getPex_hr()).isEqualTo((Integer) (pex_hr + 1));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testGetByID() 
	{
		int id = 0;
			

		try 
		{		
			id = _setupTestPatientExamination(false);	
			PatientExamination patientExamination = examinationOperations.getByID(id);
			testPatientExamination.check(patientExamination);
			testPatient.check(patientExamination.getPatient());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}	

	@Test
	public void testGetLastByPatID()
	{				
		try 
		{		
			jpa.beginTransaction();				
			Patient	patient = testPatient.setup(false);		
			PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);	
			jpa.persist(patient);
			jpa.persist(lastPatientExamination);
			jpa.commitTransaction();
			PatientExamination foundExamination = examinationOperations.getLastByPatID(patient.getCode());
			
			_checkPatientExaminationIntoDb(foundExamination.getPex_ID());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}	

	@Test
	public void testGetLastNByPatID()
	{				
		try 
		{		
			jpa.beginTransaction();				
			Patient	patient = testPatient.setup(false);		
			PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);	
			jpa.persist(patient);
			jpa.persist(lastPatientExamination);
			jpa.commitTransaction();
			ArrayList<PatientExamination> foundExamination = examinationOperations.getLastNByPatID(patient.getCode(), 1);
			
			_checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}	

	@Test
	public void testGetByPatID()
	{	
		try 
		{		
			jpa.beginTransaction();				
			Patient	patient = testPatient.setup(false);		
			PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);	
			jpa.persist(patient);
			jpa.persist(lastPatientExamination);
			jpa.commitTransaction();
			ArrayList<PatientExamination> foundExamination = examinationOperations.getByPatID(patient.getCode());
			
			_checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() {
		try {
			// given:
			int id = _setupTestPatientExamination(false);
			PatientExamination found = (PatientExamination) jpa.find(PatientExamination.class, id);
			Patient mergedPatient = _setupTestPatient(false);

			// when:
			applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

			// then:
			PatientExamination result = (PatientExamination)jpa.find(PatientExamination.class, id);
			assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private Patient _setupTestPatient(boolean usingSet) throws OHException {
		jpa.beginTransaction();
		Patient patient = testPatient.setup(usingSet);
		jpa.persist(patient);
		jpa.commitTransaction();

		return patient;
	}

	private void _saveContext() throws OHException 
    {	
		testPatientContext.saveAll(jpa);
		testPatientExaminationContext.saveAll(jpa);
    }
		
    private void _restoreContext() throws OHException 
    {
    	testPatientExaminationContext.deleteNews(jpa);
		testPatientContext.deleteNews(jpa);
    }
        
	private int _setupTestPatientExamination(
			boolean usingSet) throws OHException 
	{
		PatientExamination patientExamination;
		Patient	patient = testPatient.setup(false);
	

    	jpa.beginTransaction();	
    	patientExamination = testPatientExamination.setup(patient, usingSet);
		jpa.persist(patient);
		jpa.persist(patientExamination);
    	jpa.commitTransaction();
		
		return patientExamination.getPex_ID();
	}
		
	private void _checkPatientExaminationIntoDb(
			int id) throws OHException 
	{
		PatientExamination foundPatientExamination;
		

		foundPatientExamination = (PatientExamination)jpa.find(PatientExamination.class, id); 
		testPatientExamination.check(foundPatientExamination);
		testPatient.check(foundPatientExamination.getPatient());
	}
}