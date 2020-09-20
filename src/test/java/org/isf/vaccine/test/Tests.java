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
package org.isf.vaccine.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperations;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests  
{
	private static DbJpaUtil jpa;
	private static TestVaccine testVaccine;
	private static TestVaccineContext testVaccineContext;
	private static TestVaccineType testVaccineType;
	private static TestVaccineTypeContext testVaccineTypeContext;

    @Autowired
    VaccineIoOperations vaccineIoOperation;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
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
    	testVaccine = null;
    	testVaccineContext = null;
    	testVaccineType = null;
    	testVaccineTypeContext = null;
    }
	
		
	@Test
	public void testVaccineGets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestVaccine(false);
			_checkVaccineIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testVaccineSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestVaccine(true);
			_checkVaccineIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetVaccineShouldFindByTypeCode() 	{
		try {
			String code = _setupTestVaccine(false);
			Vaccine foundVaccine = (Vaccine)jpa.find(Vaccine.class, code); 
			ArrayList<Vaccine> vaccines = vaccineIoOperation.getVaccine(foundVaccine.getVaccineType().getCode());
			
			assertThat(vaccines.get(vaccines.size() - 1).getDescription()).isEqualTo(foundVaccine.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetVaccineShouldFindAllVaccinesWhenNoCodeProvided() {
		try {
			// given:
			String code = _setupTestVaccine(false);
			Vaccine foundVaccine = (Vaccine)jpa.find(Vaccine.class, code);

			// when:
			ArrayList<Vaccine> vaccines = vaccineIoOperation.getVaccine(null);

			// then:
			assertThat(vaccines).isNotEmpty();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testIoUpdateVaccine() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestVaccine(false);
			Vaccine foundVaccine = (Vaccine)jpa.find(Vaccine.class, code);
			jpa.flush();
			foundVaccine.setDescription("Update");
			result = vaccineIoOperation.updateVaccine(foundVaccine);
			Vaccine updateVaccine = (Vaccine)jpa.find(Vaccine.class, code);

			assertThat(result).isTrue();
			assertThat(updateVaccine.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewVaccine() 
	{
		boolean result = false;
		
		
		try 
		{		
	    	jpa.beginTransaction();	
	    	VaccineType vaccineType = testVaccineType.setup(false);
	    	jpa.persist(vaccineType);
			jpa.commitTransaction();
	    	
			Vaccine vaccine = testVaccine.setup(vaccineType, true);
			result = vaccineIoOperation.newVaccine(vaccine);

			assertThat(result).isTrue();
			_checkVaccineIntoDb(vaccine.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeleteVaccine() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestVaccine(false);
			Vaccine foundVaccine = (Vaccine)jpa.find(Vaccine.class, code); 
			result = vaccineIoOperation.deleteVaccine(foundVaccine);

			assertThat(result).isTrue();
			result = vaccineIoOperation.isCodePresent(code);
			assertThat(result).isFalse();
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
			code = _setupTestVaccine(false);
			result = vaccineIoOperation.isCodePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	
	@Test
	public void testFindVaccine() 
	{
		String code = "";
		Vaccine result;

		try 
		{		
			code = _setupTestVaccine(false);
			result = vaccineIoOperation.findVaccine(code);
			
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
		testVaccineContext.saveAll(jpa);
		testVaccineTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testVaccineContext.deleteNews(jpa);
		testVaccineTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestVaccine(
			boolean usingSet) throws OHException 
	{
		Vaccine vaccine;
		VaccineType vaccineType = testVaccineType.setup(false);
		

    	jpa.beginTransaction();	
    	vaccine = testVaccine.setup(vaccineType, usingSet);
		jpa.persist(vaccineType);
		jpa.persist(vaccine);
    	jpa.commitTransaction();
    	
		return vaccine.getCode();
	}
		
	private void  _checkVaccineIntoDb(
			String code) throws OHException 
	{
		Vaccine foundVaccine;
		

		foundVaccine = (Vaccine)jpa.find(Vaccine.class, code); 
		testVaccine.check(foundVaccine);
	}	
}