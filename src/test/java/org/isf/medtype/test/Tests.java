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
package org.isf.medtype.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperation;
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
	private static TestMedicalType testMedicalType;
	private static TestMedicalTypeContext testMedicalTypeContext;

    @Autowired
    MedicalTypeIoOperation medicalTypeIoOperation;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testMedicalType = new TestMedicalType();
    	testMedicalTypeContext = new TestMedicalTypeContext();
    	
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
	public void testMedicalTypeGets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestMedicalType(false);
			_checkMedicalTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testMedicalTypeSets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestMedicalType(true);
			_checkMedicalTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetMedicalType() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestMedicalType(false);
			MedicalType foundMedicalType = (MedicalType)jpa.find(MedicalType.class, code); 
			ArrayList<MedicalType> medicalTypes = medicalTypeIoOperation.getMedicalTypes();
			
			assertEquals(foundMedicalType.getDescription(), medicalTypes.get(medicalTypes.size()-1).getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoUpdateMedicalType()
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestMedicalType(false);
			MedicalType foundMedicalType = (MedicalType)jpa.find(MedicalType.class, code); 
			foundMedicalType.setDescription("Update");
			result = medicalTypeIoOperation.updateMedicalType(foundMedicalType);
			MedicalType updateMedicalType = (MedicalType)jpa.find(MedicalType.class, code);

			assertTrue(result);
			assertEquals("Update", updateMedicalType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoNewMedicalType() 
	{
		boolean result = false;
		
		
		try 
		{		
			MedicalType medicalType = testMedicalType.setup(true);
			result = medicalTypeIoOperation.newMedicalType(medicalType);

			assertTrue(result);
			_checkMedicalTypeIntoDb(medicalType.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}

	@Test
	public void testIoIsCodePresent() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestMedicalType(false);
			result = medicalTypeIoOperation.isCodePresent(code);

			assertTrue(result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}

		assertTrue(result);
		
		return;
	}

	@Test
	public void testIoDeleteMedicalType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestMedicalType(false);
			MedicalType foundMedicalType = (MedicalType)jpa.find(MedicalType.class, code); 
			result = medicalTypeIoOperation.deleteMedicalType(foundMedicalType);

			assertTrue(result);
			result = medicalTypeIoOperation.isCodePresent(code);
			assertFalse(result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	
	private void _saveContext() throws OHException 
    {	
		testMedicalTypeContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testMedicalTypeContext.deleteNews(jpa);
        
        return;
    }
        
	private String _setupTestMedicalType(
			boolean usingSet) throws OHException 
	{
		MedicalType medicalType;
		

    	jpa.beginTransaction();	
    	medicalType = testMedicalType.setup(usingSet);
		jpa.persist(medicalType);
    	jpa.commitTransaction();
    	
		return medicalType.getCode();
	}
		
	private void  _checkMedicalTypeIntoDb(
			String code) throws OHException 
	{
		MedicalType foundMedicalType;
		

		foundMedicalType = (MedicalType)jpa.find(MedicalType.class, code); 
		testMedicalType.check(foundMedicalType);
		
		return;
	}	
}