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
package org.isf.disease.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperations;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.test.TestDiseaseType;
import org.isf.distype.test.TestDiseaseTypeContext;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
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
	private static TestDisease testDisease;
	private static TestDiseaseType testDiseaseType;
	private static TestDiseaseContext testDiseaseContext;
	private static TestDiseaseTypeContext testDiseaseTypeContext;

    @Autowired
    DiseaseIoOperations diseaseIoOperation;
    
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testDisease = new TestDisease();
    	testDiseaseType = new TestDiseaseType();
    	testDiseaseContext = new TestDiseaseContext();
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
    	testDisease = null;
    	testDiseaseType = null;
    	testDiseaseContext = null;
    	testDiseaseTypeContext = null;
    }
	
		
	@Test
	public void testDiseaseGets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDisease(false);
			_checkDiseaseIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testDiseaseSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDisease(true);
			_checkDiseaseIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetDiseaseByCode()  
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestDisease(false);
			Disease foundDisease = diseaseIoOperation.getDiseaseByCode(Integer.parseInt(code));
			
			testDisease.check(foundDisease);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetDiseases() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestDisease(false);
			Disease foundDisease = (Disease)jpa.find(Disease.class, code); 

			ArrayList<Disease> diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, false, false);
			assertThat(diseases).contains(foundDisease);
			
			diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, false, false);
			assertThat(diseases).doesNotContain(foundDisease);
			foundDisease.setOpdInclude(true);
			jpa.beginTransaction();	
			jpa.persist(foundDisease);
			jpa.commitTransaction();
			diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, false, false);
			assertThat(diseases).contains(foundDisease);

			foundDisease = (Disease)jpa.find(Disease.class, code);
			diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, false);
			assertThat(diseases).doesNotContain(foundDisease);
			foundDisease.setOpdInclude(true);
			foundDisease.setIpdInInclude(true);
			jpa.beginTransaction();	
			jpa.persist(foundDisease);
			jpa.commitTransaction();
			diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, false);
			assertThat(diseases).contains(foundDisease);

			foundDisease = (Disease)jpa.find(Disease.class, code);
			diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, true);
			assertThat(diseases).doesNotContain(foundDisease);
			foundDisease.setOpdInclude(true);
			foundDisease.setIpdInInclude(true);
			foundDisease.setIpdOutInclude(true);
			jpa.beginTransaction();	
			jpa.persist(foundDisease);
			jpa.commitTransaction();
			diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, true);
			assertThat(diseases).contains(foundDisease);
			
			diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, true, true);
			assertThat(diseases).contains(foundDisease);
			
			diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, false, true);
			assertThat(diseases).contains(foundDisease);
			
			diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, true, false);
			assertThat(diseases).contains(foundDisease);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewDisease() 
	{
		boolean result = false; 
		
		
		try 
		{		
			DiseaseType diseaseType = testDiseaseType.setup(false);

			
			jpa.beginTransaction();	
			Disease disease = testDisease.setup(diseaseType, true);
			jpa.persist(diseaseType);
			jpa.commitTransaction();
			result = diseaseIoOperation.newDisease(disease);

			assertThat(result).isTrue();
			_checkDiseaseIntoDb(disease.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateDisease()
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
 			code = _setupTestDisease(false);
 			Disease foundDisease = (Disease)jpa.find(Disease.class, code); 
			jpa.flush();
 			foundDisease.setDescription("Update");
 			result = diseaseIoOperation.updateDisease(foundDisease);
			jpa.open();
 			Disease updateDisease = (Disease)jpa.find(Disease.class, code);

			assertThat(result).isTrue();
			assertThat(updateDisease.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoHasDiseaseModified() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestDisease(false);
			Disease foundDisease = (Disease)jpa.find(Disease.class, code);
			jpa.flush();
			result = diseaseIoOperation.deleteDisease(foundDisease);
			jpa.open();
			assertThat(result).isTrue();
			assertThat(foundDisease.getIpdInInclude()).isFalse();
			assertThat(foundDisease.getIpdOutInclude()).isFalse();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoDeleteDisease() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestDisease(false);
			Disease foundDisease = (Disease)jpa.find(Disease.class, code);
			jpa.flush();
			result = diseaseIoOperation.deleteDisease(foundDisease);
			jpa.open();
			assertThat(result).isTrue();
			assertThat(foundDisease.getIpdInInclude()).isFalse();
			assertThat(foundDisease.getIpdOutInclude()).isFalse();
			assertThat(foundDisease.getOpdInclude()).isFalse();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoIsCodePresent() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestDisease(false);
			result = diseaseIoOperation.isCodePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoIsDescriptionPresent() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestDisease(false);
			Disease foundDisease = (Disease)jpa.find(Disease.class, code); 
			result = diseaseIoOperation.isDescriptionPresent(foundDisease.getDescription(), foundDisease.getType().getCode());

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	
	private void _saveContext() throws OHException 
    {	
		testDiseaseContext.saveAll(jpa);
		testDiseaseTypeContext.saveAll(jpa);
		testDiseaseContext.addMissingKey(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testDiseaseContext.deleteNews(jpa);
		testDiseaseTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestDisease(
			boolean usingSet) throws OHException 
	{
		Disease disease;
		DiseaseType diseaseType = testDiseaseType.setup(false);
		

    	jpa.beginTransaction();	
    	disease = testDisease.setup(diseaseType, usingSet);
    	jpa.persist(diseaseType);
		jpa.persist(disease);
    	jpa.commitTransaction();
    	
		return disease.getCode();
	}
		
	private void  _checkDiseaseIntoDb(
			String code) throws OHException 
	{
		Disease foundDisease;
		

		foundDisease = (Disease)jpa.find(Disease.class, code); 
		testDisease.check(foundDisease);
	}	
}