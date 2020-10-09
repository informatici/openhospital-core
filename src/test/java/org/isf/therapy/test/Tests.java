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
package org.isf.therapy.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.medicals.model.Medical;
import org.isf.medicals.test.TestMedical;
import org.isf.medicals.test.TestMedicalContext;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.test.TestMedicalType;
import org.isf.medtype.test.TestMedicalTypeContext;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.test.TestPatient;
import org.isf.patient.test.TestPatientContext;
import org.isf.therapy.model.TherapyRow;
import org.isf.therapy.service.TherapyIoOperations;
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
	private static TestTherapy testTherapyRow;
	private static TestTherapyContext testTherapyRowContext;
	private static TestMedical testMedical;
	private static TestMedicalContext testMedicalContext;
	private static TestMedicalType testMedicalType;
	private static TestMedicalTypeContext testMedicalTypeContext;
	private static TestPatient testPatient;
	private static TestPatientContext testPatientContext;

    @Autowired
    private TherapyIoOperations therapyIoOperation;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testTherapyRow = new TestTherapy();
    	testTherapyRowContext = new TestTherapyContext();
    	testPatient = new TestPatient();
    	testPatientContext = new TestPatientContext();
    	testMedical = new TestMedical();
    	testMedicalContext = new TestMedicalContext();
    	testMedicalType = new TestMedicalType();
    	testMedicalTypeContext = new TestMedicalTypeContext();
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
    	testTherapyRow = null;
    	testTherapyRowContext = null;
    	testPatient = null;
    	testPatientContext = null;
    	testMedical = null;
    	testMedicalContext = null;
    	testMedicalType = null;
    	testMedicalTypeContext = null;
    }
	
		
	@Test
	public void testTherapyRowGets() 
	{
		int id = 0;
			

		try 
		{		
			id = _setupTestTherapyRow(false);
			_checkTherapyRowIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testTherapyRowSets() 
	{
		int id = 0;
			

		try 
		{		
			id = _setupTestTherapyRow(true);
			_checkTherapyRowIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetTherapyRow() 
	{
		int id = 0;
		
		
		try
		{
			id = _setupTestTherapyRow(false);
			TherapyRow foundTherapyRow = (TherapyRow) jpa.find(TherapyRow.class, id);
			ArrayList<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(foundTherapyRow.getPatient().getCode());

			assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoGetTherapyRowWithZeroAsIdentifierProvided() {
		try	{
			// given:
			int id = _setupTestTherapyRow(false);
			TherapyRow foundTherapyRow = (TherapyRow)jpa.find(TherapyRow.class, id);

			// when:
			ArrayList<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(0);

			// then:
			assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testIoNewTherapyRow() 
	{
		int id = 0;		
							
		
		try 
		{		
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			Patient patient = testPatient.setup(false);
			jpa.beginTransaction();	
			jpa.persist(medicalType);
			jpa.persist(medical);
			jpa.persist(patient);
			jpa.commitTransaction();
			TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
			therapyIoOperation.newTherapy(therapyRow);
			
			_checkTherapyRowIntoDb(therapyRow.getTherapyID());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeleteTherapyRow()
	{
		int id = 0;
		boolean result = false;


		try
		{
			id = _setupTestTherapyRow(false);
			TherapyRow foundTherapyRow = (TherapyRow)jpa.find(TherapyRow.class, id);
			result = therapyIoOperation.deleteAllTherapies(foundTherapyRow.getPatient());

			assertThat(result).isTrue();
			result = therapyIoOperation.isCodePresent(id);
			assertThat(result).isFalse();
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
			int id = _setupTestTherapyRow(false);
			TherapyRow found = (TherapyRow) jpa.find(TherapyRow.class, id);
			Patient obsoletePatient = found.getPatient();
			Patient mergedPatient = _setupTestPatient(false);

			// when:
			applicationEventPublisher.publishEvent(new PatientMergedEvent(obsoletePatient, mergedPatient));

			// then:
			TherapyRow result = (TherapyRow)jpa.find(TherapyRow.class, id);
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
		testMedicalContext.saveAll(jpa);
		testMedicalTypeContext.saveAll(jpa);
		testPatientContext.saveAll(jpa);
		testTherapyRowContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testTherapyRowContext.deleteNews(jpa);
		testPatientContext.deleteNews(jpa);
		testMedicalContext.deleteNews(jpa);
		testMedicalTypeContext.deleteNews(jpa);
    }
        
	private int _setupTestTherapyRow(
			boolean usingSet) throws OHException 
	{
		TherapyRow therapyRow;
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
					

    	jpa.beginTransaction();	
    	jpa.persist(medicalType);
    	jpa.persist(medical);
    	jpa.persist(patient);
    	therapyRow = testTherapyRow.setup(patient, medical, usingSet);
		jpa.persist(therapyRow);
    	jpa.commitTransaction();
    	
		return therapyRow.getTherapyID();
	}
		
	private void  _checkTherapyRowIntoDb(
			int id) throws OHException 
	{
		TherapyRow foundTherapyRow;
		

		foundTherapyRow = (TherapyRow)jpa.find(TherapyRow.class, id); 
		testTherapyRow.check(foundTherapyRow);
	}	
}