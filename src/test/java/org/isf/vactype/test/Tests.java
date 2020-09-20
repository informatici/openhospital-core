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
package org.isf.vactype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VacTypeIoOperation;
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
	private static TestVaccineType testVaccineType;
	private static TestVaccineTypeContext testVaccineTypeContext;

    @Autowired
    VacTypeIoOperation vaccineTypeIoOperation;
    
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
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
    	testVaccineType = null;
    	testVaccineTypeContext = null;
    	
    	return;
    }
	
		
	@Test
	public void testVaccineTypeGets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestVaccineType(false);
			_checkVaccineTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testVaccineTypeSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestVaccineType(true);
			_checkVaccineTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetVaccineType() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestVaccineType(false);
			VaccineType foundVaccineType = (VaccineType)jpa.find(VaccineType.class, code); 
			ArrayList<VaccineType> vaccineTypes = vaccineTypeIoOperation.getVaccineType();
			
			assertThat(vaccineTypes.get(vaccineTypes.size() - 1).getDescription()).isEqualTo(foundVaccineType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateVaccineType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestVaccineType(false);
			VaccineType foundVaccineType = (VaccineType)jpa.find(VaccineType.class, code); 
			foundVaccineType.setDescription("Update");
			result = vaccineTypeIoOperation.updateVaccineType(foundVaccineType);
			VaccineType updateVaccineType = (VaccineType)jpa.find(VaccineType.class, code);

			assertThat(result).isTrue();
			assertThat(updateVaccineType.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewVaccineType() 
	{
		boolean result = false;
		
		
		try 
		{		
			VaccineType vaccineType = testVaccineType.setup(true);
			result = vaccineTypeIoOperation.newVaccineType(vaccineType);

			assertThat(result).isTrue();
			_checkVaccineTypeIntoDb(vaccineType.getCode());
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
			code = _setupTestVaccineType(false);
			result = vaccineTypeIoOperation.isCodePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeleteVaccineType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestVaccineType(false);
			VaccineType foundVaccineType = (VaccineType)jpa.find(VaccineType.class, code); 
			result = vaccineTypeIoOperation.deleteVaccineType(foundVaccineType);

			assertThat(result).isTrue();
			result = vaccineTypeIoOperation.isCodePresent(code);
			assertThat(result).isFalse();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	
	@Test
	public void testFindVaccineType() 
	{
		String code = "";
		VaccineType result;

		try 
		{		
			code = _setupTestVaccineType(false);
			result = vaccineTypeIoOperation.findVaccineType(code);
			
			assertThat(result).isNotNull();
			assertThat(result.getCode()).isEqualTo(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	private void _saveContext() throws OHException 
    {	
		testVaccineTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testVaccineTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestVaccineType(
			boolean usingSet) throws OHException 
	{
		VaccineType vaccineType;
		

    	jpa.beginTransaction();	
    	vaccineType = testVaccineType.setup(usingSet);
		jpa.persist(vaccineType);
    	jpa.commitTransaction();
    	
		return vaccineType.getCode();
	}
		
	private void  _checkVaccineTypeIntoDb(
			String code) throws OHException 
	{
		VaccineType foundVaccineType;
		

		foundVaccineType = (VaccineType)jpa.find(VaccineType.class, code); 
		testVaccineType.check(foundVaccineType);
	}	
}