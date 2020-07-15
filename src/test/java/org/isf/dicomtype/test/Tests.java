package org.isf.dicomtype.test;


import static org.junit.Assert.assertEquals;

import org.isf.dicomtype.model.DicomType;
import org.isf.dicom.service.DicomIoOperations;
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
	private static TestDicomType testDicomType;
	private static TestDicomTypeContext testDicomTypeContext;

    @Autowired
    DicomIoOperations dicomIoOperation;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testDicomType = new TestDicomType();
    	testDicomTypeContext = new TestDicomTypeContext();
    	
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
    	testDicomType = null;
    	testDicomTypeContext = null;

    	return;
    }
	
		
	@Test
	public void testDicomTypeGets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDicomType(false);
			_checkDicomTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			assertEquals(true, false);
		}
				
		return;
	}
	
	
	@Test
	public void testDicomTypeSets()
	{
		String code = "";
			

		try 
		{		
			code = _setupTestDicomType(true);
			_checkDicomTypeIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			assertEquals(true, false);
		}
		
		return;
	}
	
	
	private void _saveContext() throws OHException 
    {	
		testDicomTypeContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testDicomTypeContext.deleteNews(jpa);
        
        return;
    }
        
	private String _setupTestDicomType(
			boolean usingSet) throws OHException 
	{
		DicomType dicomType;
		

    	jpa.beginTransaction();	
    	dicomType = testDicomType.setup(usingSet);
		jpa.persist(dicomType);
    	jpa.commitTransaction();
    	
		return dicomType.getDicomTypeID();
	}
		
	private void  _checkDicomTypeIntoDb(
			String code) throws OHException 
	{
		DicomType foundDicomType;
		

		foundDicomType = (DicomType)jpa.find(DicomType.class, code); 
		testDicomType.check(foundDicomType);
		
		return;
	}	
}