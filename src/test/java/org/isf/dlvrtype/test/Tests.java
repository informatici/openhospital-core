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
package org.isf.dlvrtype.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.service.DeliveryTypeIoOperation;
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
	private static TestDeliveryType testDeliveryType;
	private static TestDeliveryTypeContext testDeliveryTypeContext;

    @Autowired
    DeliveryTypeIoOperation deliveryTypeIoOperation;
	
	
	@BeforeClass
    public static void setUpClass()  
    {			
		jpa = new DbJpaUtil();
    	testDeliveryType = new TestDeliveryType();
    	testDeliveryTypeContext = new TestDeliveryTypeContext();
    	
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
    	testDeliveryType = null;
    	testDeliveryTypeContext = null;

    	return;
    }
	
    
	@Test
	public void testDeliveryTypeGets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDeliveryType(false);
			_checkDeliveryTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testDeliveryTypeSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDeliveryType(true);
			_checkDeliveryTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	        
	@Test
	public void testIoGetDeliveryType() 
	{	
		try 
		{		
			String code = _setupTestDeliveryType(false);
			DeliveryType foundDeliveryType = (DeliveryType)jpa.find(DeliveryType.class, code); 
			ArrayList<DeliveryType> deliveryTypes = deliveryTypeIoOperation.getDeliveryType();
			
			assertEquals(foundDeliveryType.getDescription(), deliveryTypes.get(deliveryTypes.size()-1).getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoUpdateDeliveryType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestDeliveryType(false);
			DeliveryType foundDeliveryType = (DeliveryType)jpa.find(DeliveryType.class, code);
			jpa.flush();
			foundDeliveryType.setDescription("Update");
			result = deliveryTypeIoOperation.updateDeliveryType(foundDeliveryType);
			DeliveryType updateDeliveryType = (DeliveryType)jpa.find(DeliveryType.class, code);

			assertTrue(result);
			assertEquals("Update", updateDeliveryType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoNewDeliveryType() 
	{
		boolean result = false;
		
		
		try 
		{		
			DeliveryType deliveryType = testDeliveryType.setup(true);
			result = deliveryTypeIoOperation.newDeliveryType(deliveryType);

			assertTrue(result);
			_checkDeliveryTypeIntoDb(deliveryType.getCode());
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
			code = _setupTestDeliveryType(false);
			result = deliveryTypeIoOperation.isCodePresent(code);

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
	public void testIoDeleteDeliveryType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestDeliveryType(false);
			DeliveryType foundDeliveryType = (DeliveryType)jpa.find(DeliveryType.class, code); 
			result = deliveryTypeIoOperation.deleteDeliveryType(foundDeliveryType);
			assertTrue(result);
			result = deliveryTypeIoOperation.isCodePresent(code);
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
		testDeliveryTypeContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testDeliveryTypeContext.deleteNews(jpa);
        
        return;
    }
        
	private String _setupTestDeliveryType(
			boolean usingSet) throws OHException 
	{
		DeliveryType deliveryType;
		

    	jpa.beginTransaction();	
    	deliveryType = testDeliveryType.setup(usingSet);
		jpa.persist(deliveryType);
    	jpa.commitTransaction();
    	
		return deliveryType.getCode();
	}
		
	private void  _checkDeliveryTypeIntoDb(
			String code) throws OHException 
	{
		DeliveryType foundDeliveryType;
		

		foundDeliveryType = (DeliveryType)jpa.find(DeliveryType.class, code); 
		testDeliveryType.check(foundDeliveryType);
		
		return;
	}	
}