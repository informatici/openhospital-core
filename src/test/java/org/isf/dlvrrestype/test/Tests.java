package org.isf.dlvrrestype.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    	testDeliveryResultType = null;
    	testDeliveryResultTypeContext = null;

    	return;
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
				
		return;
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
		
		return;
	}
	
	@Test
	public void testIoGetDeliveryResultType() 
	{
		String code = "";
		
		try 
		{		
			code = _setupTestDeliveryResultType(false);
			DeliveryResultType foundDeliveryResultType = (DeliveryResultType)jpa.find(DeliveryResultType.class, code); 
			
			assertEquals("TestDescription", foundDeliveryResultType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
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

			assertTrue(result);
			assertEquals("Update", updateDeliveryResultType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoNewDeliveryResultType() 
	{
		boolean result = false;
		
		
		try 
		{		
			DeliveryResultType deliveryResultType = testDeliveryResultType.setup(true);
			result = deliveryResultTypeIoOperation.newDeliveryResultType(deliveryResultType);

			assertTrue(result);
			_checkDeliveryResultTypeIntoDb(deliveryResultType.getCode());
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
			code = _setupTestDeliveryResultType(false);
			result = deliveryResultTypeIoOperation.isCodePresent(code);

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
		testDeliveryResultTypeContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testDeliveryResultTypeContext.deleteNews(jpa);
        
        return;
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
		
		return;
	}	
}