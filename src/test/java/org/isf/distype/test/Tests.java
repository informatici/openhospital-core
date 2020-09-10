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
package org.isf.distype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperation;
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
	private static TestDiseaseType testDiseaseType;
	private static TestDiseaseTypeContext testDiseaseTypeContext;

    @Autowired
    DiseaseTypeIoOperation diseaseTypeIoOperation;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
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
    	testDiseaseType = null;
    	testDiseaseTypeContext = null;
    }
	
		
	@Test
	public void testDiseaseTypeGets() 
	{
		String code = "";
			
		
		try 
		{		
			code = _setupTestDiseaseType(false);
			_checkDiseaseTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testDiseaseTypeSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDiseaseType(true);
			_checkDiseaseTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetDiseaseType()  
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestDiseaseType(false);
			DiseaseType foundDiseaseType = (DiseaseType)jpa.find(DiseaseType.class, code); 
			ArrayList<DiseaseType> diseaseTypes = diseaseTypeIoOperation.getDiseaseTypes();

			assertThat(diseaseTypes).contains(foundDiseaseType);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateDiseaseType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestDiseaseType(false);
			DiseaseType foundDiseaseType = (DiseaseType)jpa.find(DiseaseType.class, code);
			jpa.flush();
			foundDiseaseType.setDescription("Update");
			result = diseaseTypeIoOperation.updateDiseaseType(foundDiseaseType);
			DiseaseType updateDiseaseType = (DiseaseType)jpa.find(DiseaseType.class, code);

			assertThat(result).isTrue();
			assertThat(updateDiseaseType.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewDiseaseType() 
	{
		boolean result = false;
		
		
		try 
		{		
			DiseaseType diseaseType = testDiseaseType.setup(true);
			result = diseaseTypeIoOperation.newDiseaseType(diseaseType);

			assertThat(result).isTrue();
			_checkDiseaseTypeIntoDb(diseaseType.getCode());
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
			code = _setupTestDiseaseType(false);
			result = diseaseTypeIoOperation.isCodePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeleteDiseaseType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestDiseaseType(false);
			DiseaseType foundDiseaseType = (DiseaseType)jpa.find(DiseaseType.class, code); 
			result = diseaseTypeIoOperation.deleteDiseaseType(foundDiseaseType);

			result = diseaseTypeIoOperation.isCodePresent(code);
			assertThat(result).isFalse();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
		
	
	private void _saveContext() throws OHException 
    {	
		testDiseaseTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testDiseaseTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestDiseaseType(
			boolean usingSet) throws OHException 
	{
		DiseaseType diseaseType;
		

    	jpa.beginTransaction();	
    	diseaseType = testDiseaseType.setup(usingSet);
		jpa.persist(diseaseType);
    	jpa.commitTransaction();
    	
		return diseaseType.getCode();
	}
		
	private void  _checkDiseaseTypeIntoDb(
			String code) throws OHException 
	{
		DiseaseType foundDiseaseType;
		

		foundDiseaseType = (DiseaseType)jpa.find(DiseaseType.class, code); 
		testDiseaseType.check(foundDiseaseType);
	}	
}