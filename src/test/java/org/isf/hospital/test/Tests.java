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
package org.isf.hospital.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.isf.hospital.model.Hospital;
import org.isf.hospital.service.HospitalIoOperations;
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
	private static TestHospital testHospital;
	private static TestHospitalContext testHospitalContext;

    @Autowired
    HospitalIoOperations hospitalIoOperation;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testHospital = new TestHospital();
    	testHospitalContext = new TestHospitalContext();
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
    	testHospital = null;
    	testHospitalContext = null;
    }
	
	
	@Test
	public void testHospitalGets() 
	{
		String code = "";
			
		
		try 
		{		
			code = _setupTestHospital(false);
			_checkHospitalIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}


	
	@Test
	public void testHospitalSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestHospital(true);
			_checkHospitalIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateHospital()
	{
		String code = "";
		boolean result = false;


		try
		{
			code = _setupTestHospital(false);
			Hospital foundHospital = (Hospital)jpa.find(Hospital.class, code);
			jpa.flush();
			foundHospital.setDescription("Update");
			Hospital updateHospital = hospitalIoOperation.updateHospital(foundHospital);

			assertThat(updateHospital.getDescription()).isEqualTo("Update");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	
	private void _saveContext() throws OHException 
    {	
		testHospitalContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testHospitalContext.deleteNews(jpa);
    }
    
	private String _setupTestHospital(
			boolean usingSet) throws OHException 
	{
		Hospital hospital;
		
	
		jpa.beginTransaction();	
		hospital = testHospital.setup(usingSet);
		jpa.persist(hospital);
		jpa.commitTransaction();
		
		return hospital.getCode();
	}
		
	private void  _checkHospitalIntoDb(
			String code) throws OHException 
	{
		Hospital foundHospital;
		
	
		foundHospital = (Hospital)jpa.find(Hospital.class, code); 
		testHospital.check(foundHospital);
	}	
}