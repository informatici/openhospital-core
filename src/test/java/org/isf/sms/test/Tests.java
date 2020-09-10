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
package org.isf.sms.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsOperations;
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
	private static TestSms testSms;
	private static TestSmsContext testSmsContext;

    @Autowired
    private SmsOperations smsIoOperation;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testSms = new TestSms();
    	testSmsContext = new TestSmsContext();
    	
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
	public void testSmsGets() 
	{
		int code = 0;
			
		
		try 
		{		
			code = _setupTestSms(false);
			_checksmsIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testSmsSets() 
	{
		int code = 0;
			

		try 
		{		
			code = _setupTestSms(true);
			_checksmsIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testSmsSaveOrUpdate() 
	{		
		boolean result = false;
		
		
		try 
		{		
			Sms sms = testSms.setup(true);
			result = smsIoOperation.saveOrUpdate(sms);

			assertTrue(result);
			_checksmsIntoDb(sms.getSmsId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testSmsGetByID() 
	{	
		int code = 0;
		
		
		try 
		{		
			code = _setupTestSms(false);
			Sms foundSms = smsIoOperation.getByID(code);
			
			_checksmsIntoDb(foundSms.getSmsId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testSmsGetAll() 
	{		
		int code = 0;
		Date smsDateStart = new GregorianCalendar(2011, 9, 6).getTime();
		Date smsDateEnd = new GregorianCalendar(2011, 9, 9).getTime();
		
		
		try 
		{		
			code = _setupTestSms(false);
			Sms foundSms = (Sms)jpa.find(Sms.class, code); 
			List<Sms> sms = smsIoOperation.getAll(smsDateStart, smsDateEnd);			
			
			assertEquals(foundSms.getSmsText(), sms.get(0).getSmsText());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testSmsGetList() 
	{	
		int code = 0;
		 
		
		try 
		{		
			code = _setupTestSms(false);
			Sms foundSms = (Sms)jpa.find(Sms.class, code); 
			List<Sms> sms = smsIoOperation.getList();			
			
			assertEquals(foundSms.getSmsText(), sms.get(0).getSmsText());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}

	@Test
	public void testIoDeleteSms() 
	{
		int code = 0;
		

		try 
		{		
			code = _setupTestSms(false);
			Sms foundSms = (Sms)jpa.find(Sms.class, code); 
			smsIoOperation.delete(foundSms);
			
			boolean result = smsIoOperation.isCodePresent(code);
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
	public void testIoDeleteByModuleModuleID() 
	{
		int code = 0;
		

		try 
		{		
			code = _setupTestSms(false);
			Sms foundSms = (Sms)jpa.find(Sms.class, code); 
			smsIoOperation.deleteByModuleModuleID(
					foundSms.getModule(), 
					foundSms.getModuleID());

			boolean result = smsIoOperation.isCodePresent(code);
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
		testSmsContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testSmsContext.deleteNews(jpa);
        
        return;
    }
    
	private int _setupTestSms(
			boolean usingSet) throws OHException 
	{
		Sms sms;
		
	
		jpa.beginTransaction();	
		sms = testSms.setup(usingSet);
		jpa.persist(sms);
		jpa.commitTransaction();
		
		return sms.getSmsId();
	}
		
	private void  _checksmsIntoDb(
			int code) throws OHException 
	{
		Sms foundSms;
		
	
		foundSms = (Sms)jpa.find(Sms.class, code); 
		testSms.check(foundSms);
		
		return;
	}	
}