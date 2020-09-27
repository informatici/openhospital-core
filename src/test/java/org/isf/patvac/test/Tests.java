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
package org.isf.patvac.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.test.TestPatient;
import org.isf.patient.test.TestPatientContext;
import org.isf.patvac.model.PatientVaccine;
import org.isf.patvac.service.PatVacIoOperations;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.test.TestVaccine;
import org.isf.vaccine.test.TestVaccineContext;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.test.TestVaccineType;
import org.isf.vactype.test.TestVaccineTypeContext;
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
	private static TestPatientVaccine testPatientVaccine;
	private static TestPatientVaccineContext testPatientVaccineContext;
	private static TestVaccine testVaccine;
	private static TestVaccineContext testVaccineContext;
	private static TestVaccineType testVaccineType;
	private static TestVaccineTypeContext testVaccineTypeContext;
	private static TestPatient testPatient;
	private static TestPatientContext testPatientContext;

    @Autowired
    PatVacIoOperations patvacIoOperation;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testPatientVaccine = new TestPatientVaccine();
    	testPatientVaccineContext = new TestPatientVaccineContext();
    	testPatient = new TestPatient();
    	testPatientContext = new TestPatientContext();
    	testVaccine = new TestVaccine();
    	testVaccineContext = new TestVaccineContext();
    	testVaccineType = new TestVaccineType();
    	testVaccineTypeContext = new TestVaccineTypeContext();
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
    	testPatientVaccine = null;
    	testPatientVaccineContext = null;
    	testPatient = null;
    	testPatientContext = null;
    	testVaccine = null;
    	testVaccineContext = null;
    	testVaccineType = null;
    	testVaccineTypeContext = null;
    }
	
		
	@Test
	public void testPatientVaccineGets() throws OHException 
	{
		int code = 0;
			

		try 
		{		
			code = _setupTestPatientVaccine(false);
			_checkPatientVaccineIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testPatientVaccineSets() 
	{
		int code = 0;
			

		try 
		{		
			code = _setupTestPatientVaccine(true);
			_checkPatientVaccineIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetPatientVaccine() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestPatientVaccine(false);
			PatientVaccine foundPatientVaccine = (PatientVaccine)jpa.find(PatientVaccine.class, code); 
			ArrayList<PatientVaccine> patientVaccines = patvacIoOperation.getPatientVaccine(
					foundPatientVaccine.getVaccine().getVaccineType().getCode(),
					foundPatientVaccine.getVaccine().getCode(),
					foundPatientVaccine.getVaccineDate(),
					foundPatientVaccine.getVaccineDate(),
					foundPatientVaccine.getPatient().getSex(),
					foundPatientVaccine.getPatient().getAge(),
					foundPatientVaccine.getPatient().getAge());
			
			assertThat(patientVaccines.get(patientVaccines.size() - 1).getPatName()).isEqualTo(foundPatientVaccine.getPatName());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdatePatientVaccine() 
	{
		int code = 0;
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestPatientVaccine(false);
			PatientVaccine foundPatientVaccine = (PatientVaccine)jpa.find(PatientVaccine.class, code); 
			foundPatientVaccine.setPatName("Update");
			result = patvacIoOperation.updatePatientVaccine(foundPatientVaccine);
			PatientVaccine updatePatientVaccine = (PatientVaccine)jpa.find(PatientVaccine.class, code);

			assertThat(result).isTrue();
			assertThat(updatePatientVaccine.getPatName()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewPatientVaccine() 
	{
		boolean result = false;
		
		
		try 
		{		
	    	jpa.beginTransaction();	
	    	Patient patient = testPatient.setup(false);
	    	VaccineType vaccineType = testVaccineType.setup(false);
	    	Vaccine vaccine = testVaccine.setup(vaccineType, false);
	    	jpa.persist(patient);
	    	jpa.persist(vaccineType);
	    	jpa.persist(vaccine);
			jpa.commitTransaction();
	    	
			PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);
			result = patvacIoOperation.newPatientVaccine(patientVaccine);

			assertThat(result).isTrue();
			_checkPatientVaccineIntoDb(patientVaccine.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeletePatientVaccine() 
	{
		int code = 0;
		boolean result = false;
		

		try 
		{		
			code = _setupTestPatientVaccine(false);
			PatientVaccine foundPatientVaccine = (PatientVaccine)jpa.find(PatientVaccine.class, code); 
			result = patvacIoOperation.deletePatientVaccine(foundPatientVaccine);

			assertThat(result).isTrue();
			result = patvacIoOperation.isCodePresent(code);
			assertThat(result).isFalse();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetProgYear() 
	{
		int prog_year = 0;
		int found_prog_year = 0;

		try 
		{		
			_setupTestPatientVaccine(false);
			List<PatientVaccine> patientVaccineList = patvacIoOperation.getPatientVaccine(null, null, null, null, 'A', 0, 0);
			for (PatientVaccine patVac : patientVaccineList) {
				if (patVac.getProgr() > found_prog_year) found_prog_year = patVac.getProgr();
			}
			prog_year = patvacIoOperation.getProgYear(0);
			assertThat(prog_year).isEqualTo(found_prog_year);
			
			prog_year = patvacIoOperation.getProgYear(1984); //TestPatientVaccine's year
			assertThat(prog_year).isEqualTo(10);
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
			int id = _setupTestPatientVaccine(false);
			PatientVaccine found = (PatientVaccine) jpa.find(PatientVaccine.class, id);
			Patient mergedPatient = _setupTestPatient(false);

			// when:
			applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

			// then:
			PatientVaccine result = (PatientVaccine)jpa.find(PatientVaccine.class, id);
			assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private Patient _setupTestPatient(boolean usingSet) throws OHException	{
		jpa.beginTransaction();
		Patient patient = testPatient.setup(usingSet);
		jpa.persist(patient);
		jpa.commitTransaction();

		return patient;
	}
	
	
	private void _saveContext() throws OHException 
    {	
		testPatientVaccineContext.saveAll(jpa);
		testVaccineTypeContext.saveAll(jpa);
		testVaccineContext.saveAll(jpa);
		testPatientContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testPatientVaccineContext.deleteNews(jpa);
		testPatientContext.deleteNews(jpa);
		testVaccineContext.deleteNews(jpa);
		testVaccineTypeContext.deleteNews(jpa);
    }
        
	private int _setupTestPatientVaccine(
			boolean usingSet) throws OHException 
	{
		PatientVaccine patientVaccine;
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		Patient patient = testPatient.setup(false);
		

    	jpa.beginTransaction();	
    	patientVaccine = testPatientVaccine.setup(patient, vaccine, usingSet);
		jpa.persist(vaccineType);
		jpa.persist(vaccine);
		jpa.persist(patient);
		jpa.persist(patientVaccine);
    	jpa.commitTransaction();
    	
		return patientVaccine.getCode();
	}
		
	private void  _checkPatientVaccineIntoDb(
			int code) throws OHException 
	{
		PatientVaccine foundPatientVaccine;
		

		foundPatientVaccine = (PatientVaccine)jpa.find(PatientVaccine.class, code); 
		testPatientVaccine.check(foundPatientVaccine);
	}	
}