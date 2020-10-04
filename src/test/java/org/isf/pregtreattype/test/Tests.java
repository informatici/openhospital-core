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
package org.isf.pregtreattype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperation;
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
	private static TestPregnantTreatmentType testPregnantTreatmentType;
	private static TestPregnantTreatmentTypeContext testPregnantTreatmentTypeContext;

    @Autowired
    PregnantTreatmentTypeIoOperation pregnantTreatmentTypeIoOperation;
    
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testPregnantTreatmentType = new TestPregnantTreatmentType();
    	testPregnantTreatmentTypeContext = new TestPregnantTreatmentTypeContext();
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
	public void testPregnantTreatmentTypeGets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestPregnantTreatmentType(false);
			_checkPregnantTreatmentTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testPregnantTreatmentTypeSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestPregnantTreatmentType(true);
			_checkPregnantTreatmentTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetPregnantTreatmentType() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestPregnantTreatmentType(false);
			PregnantTreatmentType foundPregnantTreatmentType = (PregnantTreatmentType)jpa.find(PregnantTreatmentType.class, code); 
			ArrayList<PregnantTreatmentType> pregnantTreatmentTypes = pregnantTreatmentTypeIoOperation.getPregnantTreatmentType();

            for (PregnantTreatmentType pregnantTreatmentType : pregnantTreatmentTypes) {
                if (pregnantTreatmentType.getCode().equals(code)) {
                    assertThat(pregnantTreatmentType.getDescription()).isEqualTo(foundPregnantTreatmentType.getDescription());
                }
            }
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdatePregnantTreatmentType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestPregnantTreatmentType(false);
			PregnantTreatmentType foundPregnantTreatmentType = (PregnantTreatmentType)jpa.find(PregnantTreatmentType.class, code); 
			foundPregnantTreatmentType.setDescription("Update");
			result = pregnantTreatmentTypeIoOperation.updatePregnantTreatmentType(foundPregnantTreatmentType);
			PregnantTreatmentType updatePregnantTreatmentType = (PregnantTreatmentType)jpa.find(PregnantTreatmentType.class, code);

			assertThat(result).isTrue();
			assertThat(updatePregnantTreatmentType.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewPregnantTreatmentType() 
	{
		boolean result = false;
		
		
		try 
		{		
			PregnantTreatmentType pregnantTreatmentType = testPregnantTreatmentType.setup(true);
			result = pregnantTreatmentTypeIoOperation.newPregnantTreatmentType(pregnantTreatmentType);

			assertThat(result).isTrue();
			_checkPregnantTreatmentTypeIntoDb(pregnantTreatmentType.getCode());
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
			code = _setupTestPregnantTreatmentType(false);
			result = pregnantTreatmentTypeIoOperation.isCodePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeletePregnantTreatmentType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestPregnantTreatmentType(false);
			PregnantTreatmentType foundPregnantTreatmentType = (PregnantTreatmentType)jpa.find(PregnantTreatmentType.class, code); 
			result = pregnantTreatmentTypeIoOperation.deletePregnantTreatmentType(foundPregnantTreatmentType);

			assertThat(result).isTrue();
			result = pregnantTreatmentTypeIoOperation.isCodePresent(code);
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
		testPregnantTreatmentTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testPregnantTreatmentTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestPregnantTreatmentType(
			boolean usingSet) throws OHException 
	{
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setDescription("Test Description");
		

    	jpa.beginTransaction();	
    	pregnantTreatmentType = testPregnantTreatmentType.setup(usingSet);
		jpa.persist(pregnantTreatmentType);
    	jpa.commitTransaction();
    	
		return pregnantTreatmentType.getCode();
	}
		
	private void  _checkPregnantTreatmentTypeIntoDb(
			String code) throws OHException 
	{
		PregnantTreatmentType foundPregnantTreatmentType;
		

		foundPregnantTreatmentType = (PregnantTreatmentType)jpa.find(PregnantTreatmentType.class, code); 
		testPregnantTreatmentType.check(foundPregnantTreatmentType);
	}	
}