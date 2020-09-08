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
package org.isf.operation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.operation.model.Operation;
import org.isf.operation.service.OperationIoOperations;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.test.TestOperationType;
import org.isf.opetype.test.TestOperationTypeContext;
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
	private static TestOperation testOperation;
	private static TestOperationType testOperationType;
	private static TestOperationContext testOperationContext;
	private static TestOperationTypeContext testOperationTypeContext;
	
    @Autowired
    OperationIoOperations operationIoOperations;
    
    
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testOperation = new TestOperation();
    	testOperationType = new TestOperationType();
    	testOperationContext = new TestOperationContext();
    	testOperationTypeContext = new TestOperationTypeContext();
    	
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
    	testOperation = null;
    	testOperationType = null;
    	testOperationContext = null;
    	testOperationTypeContext = null;

    	return;
    }
	
		
	@Test
	public void testOperationGets() throws OHException 
	{
		String code = "";
	
		
		try 
		{		
			code = _setupTestOperation(false);
			_checkOperationIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testOperationSets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestOperation(true);
			_checkOperationIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetOperations() throws OHException 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestOperation(false);
			Operation foundOperation = (Operation)jpa.find(Operation.class, code); 
			ArrayList<Operation> operations = operationIoOperations.getOperation(foundOperation.getDescription());
			
			assertEquals(foundOperation.getDescription(), operations.get(0).getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}

	@Test
	public void testIoGetOperationsShouldFindOperationsWithoutProvidingDescription() throws OHException
	{
		String code = "";


		try
		{
			// given:
			code = _setupTestOperation(false);
			Operation foundOperation = (Operation)jpa.find(Operation.class, code);

			// when:
			ArrayList<Operation> operations = operationIoOperations.getOperation(foundOperation.getDescription());

			// then:
			assertFalse(operations.isEmpty());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}

		return;
	}
	
	@Test
	public void testIoNewOperation() throws OHException 
	{
		OperationType operationType = testOperationType.setup(false);
		boolean result = false;
		
		
		try 
		{		
			jpa.beginTransaction();	
			Operation operation = testOperation.setup(operationType, true);
			jpa.persist(operationType);
			jpa.commitTransaction();
			result = operationIoOperations.newOperation(operation);

			assertTrue(result);
			_checkOperationIntoDb(operation.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}

	public void testIoUpdateOperation() throws OHException 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestOperation(false);
			Operation foundOperation = (Operation)jpa.find(Operation.class, code);
			jpa.flush();
			int lock = foundOperation.getLock();
			foundOperation.setDescription("Update");
			result = operationIoOperations.updateOperation(foundOperation);
			Operation updateOperation = (Operation)jpa.find(Operation.class, code);

			assertTrue(result);
			assertEquals("Update", updateOperation.getDescription());
			assertEquals(lock + 1, updateOperation.getLock().intValue());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoDeleteOperation() throws OHException 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestOperation(false);
			Operation foundOperation = (Operation)jpa.find(Operation.class, code); 
			result = operationIoOperations.deleteOperation(foundOperation);

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
	public void testIoIsCodePresent() throws OHException 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestOperation(false);
			result = operationIoOperations.isCodePresent(code);

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
	public void testIoIsDescriptionPresent() throws OHException 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestOperation(false);
			Operation foundOperation = (Operation)jpa.find(Operation.class, code); 
			result = operationIoOperations.isDescriptionPresent(foundOperation.getDescription(), foundOperation.getType().getCode());

			assertTrue(result);
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
		testOperationContext.saveAll(jpa);
		testOperationTypeContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testOperationContext.deleteNews(jpa);
		testOperationTypeContext.deleteNews(jpa);
        
        return;
    }
        
	private String _setupTestOperation(
			boolean usingSet) throws OHException 
	{
		Operation operation;
		OperationType operationType = testOperationType.setup(false);
		

    	jpa.beginTransaction();	
    	operation = testOperation.setup(operationType, usingSet);
    	jpa.persist(operationType);
		jpa.persist(operation);
    	jpa.commitTransaction();
    	
		return operation.getCode();
	}
		
	private void  _checkOperationIntoDb(
			String code) throws OHException 
	{
		Operation foundOperation;
		

		foundOperation = (Operation)jpa.find(Operation.class, code); 
		testOperation.check(foundOperation);
		
		return;
	}	
}