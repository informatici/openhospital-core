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
package org.isf.admtype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperation;
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
	private static TestAdmissionType testAdmissionType;
	private static TestAdmissionTypeContext testAdmissionTypeContext;

    @Autowired
    AdmissionTypeIoOperation admissionTypeIoOperation;
    
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testAdmissionType = new TestAdmissionType();
    	testAdmissionTypeContext = new TestAdmissionTypeContext();
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
    	testAdmissionType = null;
    	testAdmissionTypeContext = null;
    }
	
		
	@Test
	public void testAdmissionTypeGets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestAdmissionType(false);
			_checkAdmissionTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testAdmissionTypeSets()
	{
		String code = "";
			

		try 
		{		
			code = _setupTestAdmissionType(true);
			_checkAdmissionTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetAdmissionType() 
	{		
		try 
		{		
			String code = _setupTestAdmissionType(false);
			AdmissionType foundAdmissionType = (AdmissionType)jpa.find(AdmissionType.class, code); 
			ArrayList<AdmissionType> admissionTypes = admissionTypeIoOperation.getAdmissionType();
			
			assertThat(admissionTypes.get(admissionTypes.size() - 1).getDescription()).isEqualTo(foundAdmissionType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateAdmissionType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestAdmissionType(false);
			AdmissionType foundAdmissionType = (AdmissionType)jpa.find(AdmissionType.class, code);
			jpa.flush();
			foundAdmissionType.setDescription("Update");
			result = admissionTypeIoOperation.updateAdmissionType(foundAdmissionType);
			AdmissionType updateAdmissionType = (AdmissionType)jpa.find(AdmissionType.class, code);

			assertThat(result).isTrue();
			assertThat(updateAdmissionType.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewAdmissionType() 
	{
		boolean result = false;
		
		
		try 
		{		
			AdmissionType admissionType = testAdmissionType.setup(true);
			result = admissionTypeIoOperation.newAdmissionType(admissionType);

			assertThat(result).isTrue();
			_checkAdmissionTypeIntoDb(admissionType.getCode());
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
			code = _setupTestAdmissionType(false);
			result = admissionTypeIoOperation.isCodePresent(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}

		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteAdmissionType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestAdmissionType(false);
			AdmissionType foundAdmissionType = (AdmissionType)jpa.find(AdmissionType.class, code); 
			result = admissionTypeIoOperation.deleteAdmissionType(foundAdmissionType);

			assertThat(result).isTrue();
			result = admissionTypeIoOperation.isCodePresent(code);
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
		testAdmissionTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testAdmissionTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestAdmissionType(
			boolean usingSet) throws OHException 
	{
		AdmissionType admissionType;
		

    	jpa.beginTransaction();	
    	admissionType = testAdmissionType.setup(usingSet);
		jpa.persist(admissionType);
    	jpa.commitTransaction();
    	
		return admissionType.getCode();
	}
		
	private void  _checkAdmissionTypeIntoDb(
			String code) throws OHException 
	{
		AdmissionType foundAdmissionType;
		

		foundAdmissionType = (AdmissionType)jpa.find(AdmissionType.class, code); 
		testAdmissionType.check(foundAdmissionType);
	}	
}