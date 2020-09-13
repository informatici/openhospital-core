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
package org.isf.dlvrrestype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.service.DeliveryResultTypeIoOperation;
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
	private static TestDeliveryResultType testDeliveryResultType;
	private static TestDeliveryResultTypeContext testDeliveryResultTypeContext;
	
    @Autowired
    DeliveryResultTypeIoOperation deliveryResultTypeIoOperation;
    
    
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testDeliveryResultType = new TestDeliveryResultType();
    	testDeliveryResultTypeContext = new TestDeliveryResultTypeContext();
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
    	testDeliveryResultType = null;
    	testDeliveryResultTypeContext = null;
    }
	
		
	@Test
	public void testDeliveryResultTypeGets()
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDeliveryResultType(false);
			_checkDeliveryResultTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testDeliveryResultTypeSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDeliveryResultType(true);
			_checkDeliveryResultTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetDeliveryResultType() 
	{
		String code = "";
		
		try 
		{		
			code = _setupTestDeliveryResultType(false);
			DeliveryResultType foundDeliveryResultType = (DeliveryResultType)jpa.find(DeliveryResultType.class, code); 
			
			assertThat(foundDeliveryResultType.getDescription()).isEqualTo("TestDescription");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateDeliveryResultType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestDeliveryResultType(false);
			DeliveryResultType foundDeliveryResultType = (DeliveryResultType)jpa.find(DeliveryResultType.class, code); 
			foundDeliveryResultType.setDescription("Update");
			result = deliveryResultTypeIoOperation.updateDeliveryResultType(foundDeliveryResultType);
			DeliveryResultType updateDeliveryResultType = (DeliveryResultType)jpa.find(DeliveryResultType.class, code);

			assertThat(result).isTrue();
			assertThat(updateDeliveryResultType.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewDeliveryResultType() 
	{
		boolean result = false;
		
		
		try 
		{		
			DeliveryResultType deliveryResultType = testDeliveryResultType.setup(true);
			result = deliveryResultTypeIoOperation.newDeliveryResultType(deliveryResultType);

			assertThat(result).isTrue();
			_checkDeliveryResultTypeIntoDb(deliveryResultType.getCode());
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
			code = _setupTestDeliveryResultType(false);
			result = deliveryResultTypeIoOperation.isCodePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeleteDeliveryResultType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestDeliveryResultType(false);
			DeliveryResultType foundDeliveryResultType = (DeliveryResultType)jpa.find(DeliveryResultType.class, code); 
			result = deliveryResultTypeIoOperation.deleteDeliveryResultType(foundDeliveryResultType);
			
			result = deliveryResultTypeIoOperation.isCodePresent(code);
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
		testDeliveryResultTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testDeliveryResultTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestDeliveryResultType(
			boolean usingSet) throws OHException 
	{
		DeliveryResultType deliveryResultType;
		

    	jpa.beginTransaction();	
    	deliveryResultType = testDeliveryResultType.setup(usingSet);
		jpa.persist(deliveryResultType);
    	jpa.commitTransaction();
    	
		return deliveryResultType.getCode();
	}
		
	private void  _checkDeliveryResultTypeIntoDb(
			String code) throws OHException 
	{
		DeliveryResultType foundDeliveryResultType;
		

		foundDeliveryResultType = (DeliveryResultType)jpa.find(DeliveryResultType.class, code); 
		testDeliveryResultType.check(foundDeliveryResultType);
	}	
}