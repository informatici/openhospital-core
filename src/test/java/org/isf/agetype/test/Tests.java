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
package org.isf.agetype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.agetype.model.AgeType;
import org.isf.agetype.service.AgeTypeIoOperations;
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
	private static TestAgeType testAgeType;
	private static TestAgeTypeContext testAgeTypeContext;

    @Autowired
    AgeTypeIoOperations ageTypeIoOperations;
    
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testAgeType = new TestAgeType();
    	testAgeTypeContext = new TestAgeTypeContext();
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
    	testAgeType = null;
    	testAgeTypeContext = null;
    }
	
		
	@Test
	public void testAgeTypeGets()
	{
		String code = "";
			

		try 
		{		
			code = _setupTestAgeType(false);
			_checkAgeTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testAgeTypeSets()
	{
		String code = "";
			

		try 
		{		
			code = _setupTestAgeType(true);
			_checkAgeTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetAgeType() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestAgeType(false);
			AgeType foundAgeType = (AgeType)jpa.find(AgeType.class, code); 
			ArrayList<AgeType> ageTypes = ageTypeIoOperations.getAgeType();
			
			assertThat(ageTypes.get(ageTypes.size() - 1).getDescription()).isEqualTo(foundAgeType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateAgeType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestAgeType(false);
			AgeType foundAgeType = (AgeType)jpa.find(AgeType.class, code); 
			foundAgeType.setFrom(4);
			foundAgeType.setTo(40);
			ArrayList<AgeType> ageTypes = new ArrayList<>();
			ageTypes.add(foundAgeType);
			result = ageTypeIoOperations.updateAgeType(ageTypes);
			AgeType updateAgeType = (AgeType)jpa.find(AgeType.class, code);

			assertThat(result).isTrue();
			assertThat(updateAgeType.getFrom()).isEqualTo(4);
			assertThat(updateAgeType.getTo()).isEqualTo(40);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testIoGetAgeTypeByCode()
	{	
		String code = "";
		
		try 
		{		
			code = _setupTestAgeType(false);
			AgeType ageType = (AgeType)jpa.find(AgeType.class, code); 
			AgeType foundAgeType = ageTypeIoOperations.getAgeTypeByCode(9);
			
			assertThat(foundAgeType.getFrom()).isEqualTo(ageType.getFrom());
			assertThat(foundAgeType.getTo()).isEqualTo(ageType.getTo());
			assertThat(foundAgeType.getDescription()).isEqualTo(ageType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	
	private void _saveContext() throws OHException 
    {	
		testAgeTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testAgeTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestAgeType(
			boolean usingSet) throws OHException 
	{
		AgeType ageType;
		

    	jpa.beginTransaction();	
    	ageType = testAgeType.setup(usingSet);
		jpa.persist(ageType);
    	jpa.commitTransaction();
    	
		return ageType.getCode();
	}
		
	private void  _checkAgeTypeIntoDb(
			String code) throws OHException 
	{
		AgeType foundAgeType;
		

		foundAgeType = (AgeType)jpa.find(AgeType.class, code); 
		testAgeType.check(foundAgeType);
	}	
}