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
package org.isf.opd.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;

import org.isf.disease.model.Disease;
import org.isf.disease.test.TestDisease;
import org.isf.disease.test.TestDiseaseContext;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.test.TestDiseaseType;
import org.isf.distype.test.TestDiseaseTypeContext;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperations;
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
	private static TestOpd testOpd;
	private static TestOpdContext testOpdContext;
	private static TestPatient testPatient;
	private static TestPatientContext testPatientContext;
	private static TestDiseaseType testDiseaseType;
	private static TestDiseaseTypeContext testDiseaseTypeContext;
	private static TestDisease testDisease;
	private static TestDiseaseContext testDiseaseContext;

    @Autowired
    OpdIoOperations opdIoOperation;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testOpd = new TestOpd();
    	testOpdContext = new TestOpdContext();
    	testPatient = new TestPatient();
    	testPatientContext = new TestPatientContext();
    	testDisease = new TestDisease();
    	testDiseaseContext = new TestDiseaseContext();
    	testDiseaseType = new TestDiseaseType();
    	testDiseaseTypeContext = new TestDiseaseTypeContext();
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
    	//jpa.destroy();
    	testOpd = null;
    	testOpdContext = null;
    	testPatient = null;
    	testPatientContext = null;
    	testDisease = null;
    	testDiseaseContext = null;
    	testDiseaseType = null;
    	testDiseaseTypeContext = null;
    }
	
		
	@Test
	public void testOpdGets() 
	{
		int code = 0;
			

		try 
		{		
			code = _setupTestOpd(false);
			_checkOpdIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testOpdSets() 
	{
		int code = 0;
			

		try 
		{		
			code = _setupTestOpd(true);
			_checkOpdIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetOpd()
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestOpd(false);
			Opd foundOpd = (Opd)jpa.find(Opd.class, code); 
			ArrayList<Opd> opds = opdIoOperation.getOpdList(
					foundOpd.getDisease().getType().getCode(), 
					foundOpd.getDisease().getCode(),
					foundOpd.getVisitDate(),
					foundOpd.getVisitDate(),
					foundOpd.getAge()-1,
					foundOpd.getAge()+1,
					foundOpd.getSex(),
					foundOpd.getNewPatient());			
						
			assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewOpd() throws OHException
	{
		Patient	patient = testPatient.setup(false); 
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		boolean result = false;

    	
		try 
		{		
			jpa.beginTransaction();	
			jpa.persist(patient);
			jpa.persist(diseaseType);
			jpa.persist(disease);
			jpa.commitTransaction();

			Opd opd = testOpd.setup(patient, disease, false);
	    	opd.setDate(new Date((new java.util.Date()).getTime()));
			result = opdIoOperation.newOpd(opd);

			assertThat(result).isTrue();
			_checkOpdIntoDb(opd.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateOpd() 
	{
		int code = 0;
		Opd result = null;
		
		
		try 
		{		
			code = _setupTestOpd(false);
			Opd foundOpd = (Opd)jpa.find(Opd.class, code); 
			jpa.flush();
			foundOpd.setNote("Update");
			result = opdIoOperation.updateOpd(foundOpd);
			jpa.open();
			Opd updateOpd = (Opd)jpa.find(Opd.class, code);

			assertThat(result).isNotNull();
			assertThat(updateOpd.getNote()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeleteOpd() 
	{
		int code = 0;
		boolean result = false;
		

		try 
		{		
			code = _setupTestOpd(false);
			Opd foundOpd = (Opd)jpa.find(Opd.class, code); 
			result = opdIoOperation.deleteOpd(foundOpd);

			assertThat(result).isTrue();
			result = opdIoOperation.isCodePresent(code);
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
		int code = 0;
		int progYear = 0;
		

		try 
		{		
			code = _setupTestOpd(false);
			progYear = opdIoOperation.getProgYear(0);

			Opd foundOpd = (Opd)jpa.find(Opd.class, code); 
			assertThat(progYear).isEqualTo(foundOpd.getProgYear());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnTrueWhenOpdWithGivenOPDProgressiveYearAndVisitYearExists() {
		try	{
			// given:
			int code = _setupTestOpd(false);
			Opd foundOpd = (Opd)jpa.find(Opd.class, code);

			// when:
			Calendar cal = Calendar.getInstance();
			cal.setTime(foundOpd.getVisitDate());
			int foundOpdVisitYear = cal.get(Calendar.YEAR);
			Boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), foundOpdVisitYear);

			// then:
			assertThat(result).isTrue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnTrueWhenOpdNumExistsAndVisitYearIsNotProvided()	{
		try	{
			// given:
			int code = _setupTestOpd(false);
			Opd foundOpd = (Opd)jpa.find(Opd.class, code);

			// when:
			Boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), 0);

			// then:
			assertThat(result).isTrue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnFalseWhenOpdNumExistsAndVisitYearIsIncorrect() {
		try	{
			// given:
			int code = _setupTestOpd(false);
			Opd foundOpd = (Opd)jpa.find(Opd.class, code);

			// when:
			Boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), 1488);

			// then:
			assertThat(result).isFalse();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIoGetLastOpd()  
	{
		int code = 0;
		

		try 
		{		
			code = _setupTestOpd(false);
			Opd foundOpd = (Opd)jpa.find(Opd.class, code); 
			Opd lastOpd = opdIoOperation.getLastOpd(foundOpd.getPatient().getCode());

			assertThat(lastOpd.getCode()).isEqualTo(foundOpd.getCode());
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
			int id = _setupTestOpd(false);
			Opd found = (Opd) jpa.find(Opd.class, id);
			Patient mergedPatient = _setupTestPatient(false);

			// when:
			applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

			// then:
			Opd result = (Opd)jpa.find(Opd.class, id);
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
		testOpdContext.saveAll(jpa);
		testPatientContext.saveAll(jpa);
		testDiseaseContext.saveAll(jpa);
		testDiseaseTypeContext.saveAll(jpa);
		testDiseaseContext.addMissingKey(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testOpdContext.deleteNews(jpa);
		testPatientContext.deleteNews(jpa);
		testDiseaseContext.deleteNews(jpa);
		testDiseaseTypeContext.deleteNews(jpa);
    }
        
	private int _setupTestOpd(
			boolean usingSet) throws OHException 
	{
		Opd opd;
		Patient	patient = testPatient.setup(false); 
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		

    	jpa.beginTransaction();	
    	opd = testOpd.setup(patient, disease, usingSet);
    	opd.setDate(new Date((new java.util.Date()).getTime()));
    	jpa.persist(patient);
    	jpa.persist(diseaseType);
    	jpa.persist(disease);
		jpa.persist(opd);
    	jpa.commitTransaction();
    	
		return opd.getCode();
	}
		
	private void  _checkOpdIntoDb(
			int code) throws OHException 
	{
		Opd foundOpd;
		

		foundOpd = (Opd)jpa.find(Opd.class, code); 
		testOpd.check(foundOpd);
	}	
}