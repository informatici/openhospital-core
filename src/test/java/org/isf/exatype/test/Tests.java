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
package org.isf.exatype.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperation;
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
	private static TestExamType testExamType;
	private static TestExamTypeContext testExamTypeContext;

    @Autowired
    ExamTypeIoOperation examTypeIoOperation;

    
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testExamType = new TestExamType();
    	testExamTypeContext = new TestExamTypeContext();
    	
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
    	testExamType = null;
    	testExamTypeContext = null;

    	return;
    }
	
		
	@Test
	public void testExamTypeGets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestExamType(false);
			_checkExamTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testExamTypeSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestExamType(true);
			_checkExamTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetExamType()
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestExamType(false);
			ExamType foundExamType = (ExamType)jpa.find(ExamType.class, code); 
			ArrayList<ExamType> examTypes = examTypeIoOperation.getExamType();

			assertTrue(examTypes.contains(foundExamType));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoUpdateExamType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestExamType(false);
			ExamType foundExamType = (ExamType)jpa.find(ExamType.class, code); 
			foundExamType.setDescription("Update");
			result = examTypeIoOperation.updateExamType(foundExamType);
			ExamType updateExamType = (ExamType)jpa.find(ExamType.class, code);

			assertTrue(result);
			assertEquals("Update", updateExamType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoNewExamType()
	{
		boolean result = false;
		
		
		try 
		{		
			ExamType examType = testExamType.setup(true);
			result = examTypeIoOperation.newExamType(examType);

			assertTrue(result);
			_checkExamTypeIntoDb(examType.getCode());
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
			code = _setupTestExamType(false);
			result = examTypeIoOperation.isCodePresent(code);

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
	public void testIoDeleteExamType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestExamType(false);
			ExamType foundExamType = (ExamType)jpa.find(ExamType.class, code); 
			result = examTypeIoOperation.deleteExamType(foundExamType);
			assertTrue(result);

			result = examTypeIoOperation.isCodePresent(code);
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
		testExamTypeContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testExamTypeContext.deleteNews(jpa);
        
        return;
    }
        
	private String _setupTestExamType(
			boolean usingSet) throws OHException 
	{
		ExamType examType;
		

    	jpa.beginTransaction();	
    	examType = testExamType.setup(usingSet);
		jpa.persist(examType);
    	jpa.commitTransaction();
    	
		return examType.getCode();
	}
		
	private void  _checkExamTypeIntoDb(
			String code) throws OHException 
	{
		ExamType foundExamType;
		

		foundExamType = (ExamType)jpa.find(ExamType.class, code); 
		testExamType.check(foundExamType);
		
		return;
	}	
}