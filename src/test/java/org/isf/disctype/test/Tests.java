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
package org.isf.disctype.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperation;
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
	private static TestDischargeType testDischargeType;
	private static TestDischargeTypeContext testDischargeTypeContext;

    @Autowired
    DischargeTypeIoOperation dischargeTypeIoOperation;
    
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testDischargeType = new TestDischargeType();
    	testDischargeTypeContext = new TestDischargeTypeContext();
    	
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
	public void testDischargeTypeGets()
	{
		String code = "";

		
		try 
		{		
			code = _setupTestDischargeType(false);
			_checkDischargeTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testDischargeTypeSets()
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDischargeType(true);
			_checkDischargeTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetDischargeType() 
	{
		String code = "";
		

		try 
		{		
			code = _setupTestDischargeType(false);
			DischargeType foundDischargeType = (DischargeType)jpa.find(DischargeType.class, code); 
			ArrayList<DischargeType> dischargeTypes = dischargeTypeIoOperation.getDischargeType();
			
			assertEquals(foundDischargeType.getDescription(), dischargeTypes.get(dischargeTypes.size()-1).getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
    
    @Test
	public void testIoNewDischargeType()  
	{
		boolean result = false;
		
		
		try 
		{		
			DischargeType dischargeType = testDischargeType.setup(true);
			result = dischargeTypeIoOperation.newDischargeType(dischargeType);

			assertTrue(result);
			_checkDischargeTypeIntoDb(dischargeType.getCode());
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
			code = _setupTestDischargeType(false);
			result = dischargeTypeIoOperation.isCodePresent(code);

			assertTrue(result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
   
	@Test
	public void testIoDeleteDischargeType()
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestDischargeType(false);
			DischargeType foundDischargeType = (DischargeType)jpa.find(DischargeType.class, code); 
			result = dischargeTypeIoOperation.deleteDischargeType(foundDischargeType);

			assertTrue(result);
			result = dischargeTypeIoOperation.isCodePresent(code);
			assertFalse(result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	 
	@Test
	public void testIoUpdateDischargeType()
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestDischargeType(false);
			DischargeType foundDischargeType = (DischargeType)jpa.find(DischargeType.class, code); 
			foundDischargeType.setDescription("Update");
			result = dischargeTypeIoOperation.updateDischargeType(foundDischargeType);
			DischargeType updateDischargeType = (DischargeType)jpa.find(DischargeType.class, code);

			assertTrue(result);
			assertEquals("Update", updateDischargeType.getDescription());
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
		testDischargeTypeContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testDischargeTypeContext.deleteNews(jpa);
        
        return;
    }
        
	private String _setupTestDischargeType(
			boolean usingSet) throws OHException 
	{
		DischargeType dischargeType;
		

    	jpa.beginTransaction();	
    	dischargeType = testDischargeType.setup(usingSet);
		jpa.persist(dischargeType);
    	jpa.commitTransaction();
    	
		return dischargeType.getCode();
	}
		
	private void  _checkDischargeTypeIntoDb(
			String code) throws OHException 
	{
		DischargeType foundDischargeType;
		

		foundDischargeType = (DischargeType)jpa.find(DischargeType.class, code); 
		testDischargeType.check(foundDischargeType);
		
		return;
	}	
}