package org.isf.vaccine.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperations;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.test.TestVaccineType;
import org.isf.vactype.test.TestVaccineTypeContext;
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
	private static TestVaccine testVaccine;
	private static TestVaccineContext testVaccineContext;
	private static TestVaccineType testVaccineType;
	private static TestVaccineTypeContext testVaccineTypeContext;

    @Autowired
    VaccineIoOperations vaccineIoOperation;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testVaccine = new TestVaccine();
    	testVaccineContext = new TestVaccineContext();
    	testVaccineType = new TestVaccineType();
    	testVaccineTypeContext = new TestVaccineTypeContext();
    	
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
    	testVaccine = null;
    	testVaccineContext = null;
    	testVaccineType = null;
    	testVaccineTypeContext = null;

    	return;
    }
	
		
	@Test
	public void testVaccineGets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestVaccine(false);
			_checkVaccineIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testVaccineSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestVaccine(true);
			_checkVaccineIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoGetVaccineShouldFindByTypeCode() 	{
		try {
			String code = _setupTestVaccine(false);
			Vaccine foundVaccine = (Vaccine)jpa.find(Vaccine.class, code); 
			ArrayList<Vaccine> vaccines = vaccineIoOperation.getVaccine(foundVaccine.getVaccineType().getCode());
			
			assertEquals(foundVaccine.getDescription(), vaccines.get(vaccines.size()-1).getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetVaccineShouldFindAllVaccinesWhenNoCodeProvided() {
		try {
			// given:
			String code = _setupTestVaccine(false);
			Vaccine foundVaccine = (Vaccine)jpa.find(Vaccine.class, code);

			// when:
			ArrayList<Vaccine> vaccines = vaccineIoOperation.getVaccine(null);

			// then:
			assertFalse(vaccines.isEmpty());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testIoUpdateVaccine() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestVaccine(false);
			Vaccine foundVaccine = (Vaccine)jpa.find(Vaccine.class, code);
			jpa.flush();
			foundVaccine.setDescription("Update");
			result = vaccineIoOperation.updateVaccine(foundVaccine);
			Vaccine updateVaccine = (Vaccine)jpa.find(Vaccine.class, code);

			assertTrue(result);
			assertEquals("Update", updateVaccine.getDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testIoNewVaccine() 
	{
		boolean result = false;
		
		
		try 
		{		
	    	jpa.beginTransaction();	
	    	VaccineType vaccineType = testVaccineType.setup(false);
	    	jpa.persist(vaccineType);
			jpa.commitTransaction();
	    	
			Vaccine vaccine = testVaccine.setup(vaccineType, true);
			result = vaccineIoOperation.newVaccine(vaccine);

			assertTrue(result);
			_checkVaccineIntoDb(vaccine.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}

	@Test
	public void testIoDeleteVaccine() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestVaccine(false);
			Vaccine foundVaccine = (Vaccine)jpa.find(Vaccine.class, code); 
			result = vaccineIoOperation.deleteVaccine(foundVaccine);

			assertTrue(result);
			result = vaccineIoOperation.isCodePresent(code);
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
	public void testIoIsCodePresent() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestVaccine(false);
			result = vaccineIoOperation.isCodePresent(code);

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
	public void testFindVaccine() 
	{
		String code = "";
		Vaccine result;

		try 
		{		
			code = _setupTestVaccine(false);
			result = vaccineIoOperation.findVaccine(code);
			
			assertNotNull(result);
			assertEquals(code,result.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	
	private void _saveContext() throws OHException 
    {	
		testVaccineContext.saveAll(jpa);
		testVaccineTypeContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testVaccineContext.deleteNews(jpa);
		testVaccineTypeContext.deleteNews(jpa);
        
        return;
    }
        
	private String _setupTestVaccine(
			boolean usingSet) throws OHException 
	{
		Vaccine vaccine;
		VaccineType vaccineType = testVaccineType.setup(false);
		

    	jpa.beginTransaction();	
    	vaccine = testVaccine.setup(vaccineType, usingSet);
		jpa.persist(vaccineType);
		jpa.persist(vaccine);
    	jpa.commitTransaction();
    	
		return vaccine.getCode();
	}
		
	private void  _checkVaccineIntoDb(
			String code) throws OHException 
	{
		Vaccine foundVaccine;
		

		foundVaccine = (Vaccine)jpa.find(Vaccine.class, code); 
		testVaccine.check(foundVaccine);
		
		return;
	}	
}