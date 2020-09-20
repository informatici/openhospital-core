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
package org.isf.medstockmovtype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperation;
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
	private static TestMovementType testMovementType;
	private static TestMovementTypeContext testMovementTypeContext;

    @Autowired
    MedicalStockMovementTypeIoOperation medicalStockMovementTypeIoOperation;
    
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testMovementType = new TestMovementType();
    	testMovementTypeContext = new TestMovementTypeContext();
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
	public void testMovementTypeGets()
	{
		String code = "";
			

		try 
		{		
			code = _setupTestMovementType(false);
			_checkMovementTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testMovementTypeSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestMovementType(true);
			_checkMovementTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetMovementType() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestMovementType(false);
			MovementType foundMovementType = (MovementType)jpa.find(MovementType.class, code); 
			ArrayList<MovementType> movementTypes = medicalStockMovementTypeIoOperation.getMedicaldsrstockmovType();
			
			assertThat(movementTypes.get(movementTypes.size() - 1).getDescription()).isEqualTo(foundMovementType.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateMovementType() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestMovementType(false);
			MovementType foundMovementType = (MovementType)jpa.find(MovementType.class, code); 
			foundMovementType.setDescription("Update");
			result = medicalStockMovementTypeIoOperation.updateMedicaldsrstockmovType(foundMovementType);
			MovementType updateMovementType = (MovementType)jpa.find(MovementType.class, code);

			assertThat(result).isTrue();
			assertThat(updateMovementType.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewMovementType() 
	{
		boolean result = false;
		
		
		try 
		{		
			MovementType movementType = testMovementType.setup(true);
			result = medicalStockMovementTypeIoOperation.newMedicaldsrstockmovType(movementType);

			assertThat(result).isTrue();
			_checkMovementTypeIntoDb(movementType.getCode());
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
			code = _setupTestMovementType(false);
			result = medicalStockMovementTypeIoOperation.isCodePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoDeleteMovementType() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestMovementType(false);
			MovementType foundMovementType = (MovementType)jpa.find(MovementType.class, code); 
			result = medicalStockMovementTypeIoOperation.deleteMedicaldsrstockmovType(foundMovementType);

			assertThat(result).isTrue();
			result = medicalStockMovementTypeIoOperation.isCodePresent(code);
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
		testMovementTypeContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testMovementTypeContext.deleteNews(jpa);
    }
        
	private String _setupTestMovementType(
			boolean usingSet) throws OHException 
	{
		MovementType movementType;
		

    	jpa.beginTransaction();	
    	movementType = testMovementType.setup(usingSet);
		jpa.persist(movementType);
    	jpa.commitTransaction();
    	
		return movementType.getCode();
	}
		
	private void  _checkMovementTypeIntoDb(
			String code) throws OHException 
	{
		MovementType foundMovementType;
		

		foundMovementType = (MovementType)jpa.find(MovementType.class, code); 
		testMovementType.check(foundMovementType);
	}	
}