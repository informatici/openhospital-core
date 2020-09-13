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
package org.isf.opetype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperation;
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
	private static TestOperationType testOperationType;
	private static TestOperationTypeContext testOperationTypeContext;

    @Autowired
    OperationTypeIoOperation operationTypeIoOperation;
    
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testOperationType = new TestOperationType();
    	testOperationTypeContext = new TestOperationTypeContext();
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

    }
	
		
	@Test
	public void testOperationTypeGets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestOperationType(false);
			_checkOperationTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testOperationTypeSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestOperationType(true);
			_checkOperationTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetOperationType() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestOperationType(false);
			OperationType foundOperationType = (OperationType)jpa.find(OperationType.class, code); 
			ArrayList<OperationType> operationTypes = operationTypeIoOperation.getOperationType();
			
			assertThat(operationTypes.get(operationTypes.size() - 1).getDescription()).isEqualTo(foundOperationType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateOperationType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestOperationType(false);
			OperationType foundOperationType = (OperationType)jpa.find(OperationType.class, code); 
			foundOperationType.setDescription("Update");
			result = operationTypeIoOperation.updateOperationType(foundOperationType);
			OperationType updateOperationType = (OperationType)jpa.find(OperationType.class, code);

			assertThat(result).isTrue();
			assertThat(updateOperationType.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewOperationType() 
	{
		boolean result = false;
		
		
		try 
		{		
			OperationType operationType = testOperationType.setup(true);
			result = operationTypeIoOperation.newOperationType(operationType);

			assertThat(result).isTrue();
			_checkOperationTypeIntoDb(operationType.getCode());
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
			code = _setupTestOperationType(false);
			result = operationTypeIoOperation.isCodePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeleteOperationType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestOperationType(false);
			OperationType foundOperationType = (OperationType)jpa.find(OperationType.class, code); 
			result = operationTypeIoOperation.deleteOperationType(foundOperationType);

			assertThat(result).isTrue();
			result = operationTypeIoOperation.isCodePresent(code);
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
		testOperationTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testOperationTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestOperationType(
			boolean usingSet) throws OHException 
	{
		OperationType operationType;
		

    	jpa.beginTransaction();	
    	operationType = testOperationType.setup(usingSet);
		jpa.persist(operationType);
    	jpa.commitTransaction();
    	
		return operationType.getCode();
	}
		
	private void  _checkOperationTypeIntoDb(
			String code) throws OHException 
	{
		OperationType foundOperationType;
		

		foundOperationType = (OperationType)jpa.find(OperationType.class, code); 
		testOperationType.check(foundOperationType);
	}	
}