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
package org.isf.supplier.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierOperations;
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
	private static TestSupplier testSupplier;
	private static TestSupplierContext testSupplierContext;

    @Autowired
    private SupplierOperations supplierIoOperation;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testSupplier = new TestSupplier();
    	testSupplierContext = new TestSupplierContext();
    	
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
    	testSupplier = null;
    	testSupplierContext = null;

    	return;
    }
	
	
	@Test
	public void testSupplierGets() 
	{
		int code = 0;
			
		
		try 
		{		
			code = _setupTestSupplier(false);
			_checkSupplierIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	@Test
	public void testSupplierSets() 
	{
		int code = 0;
			

		try 
		{		
			code = _setupTestSupplier(true);
			_checkSupplierIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testSupplierSaveOrUpdate() 
	{		
		boolean result = false;
		
		
		try 
		{		
			Supplier supplier = testSupplier.setup(true);
			result = supplierIoOperation.saveOrUpdate(supplier);

			assertTrue(result);
			_checkSupplierIntoDb(supplier.getSupId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testSupplierGetByID() 
	{	
		int code = 0;
		
		
		try 
		{		
			code = _setupTestSupplier(false);
			Supplier foundSupplier = supplierIoOperation.getByID(code);
			
			_checkSupplierIntoDb(foundSupplier.getSupId());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testSupplierGetAll() 
	{		
		int code = 0;
		
		
		try 
		{		
			code = _setupTestSupplier(false);
			Supplier foundSupplier = (Supplier)jpa.find(Supplier.class, code); 
			List<Supplier> suppliers = supplierIoOperation.getAll();

			assertTrue(suppliers.contains(foundSupplier));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	@Test
	public void testSupplierGetList() 
	{	
		int code = 0;
		
		
		try 
		{		
			code = _setupTestSupplier(false);
			Supplier foundSupplier = (Supplier)jpa.find(Supplier.class, code); 
			List<Supplier> suppliers = supplierIoOperation.getList();

			assertTrue(suppliers.contains(foundSupplier));
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
		testSupplierContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testSupplierContext.deleteNews(jpa);
        
        return;
    }
    
	private int _setupTestSupplier(
			boolean usingSet) throws OHException 
	{
		Supplier supplier;
		
	
		jpa.beginTransaction();	
		supplier = testSupplier.setup(usingSet);
		jpa.persist(supplier);
		jpa.commitTransaction();
		
		return supplier.getSupId();
	}
		
	private void  _checkSupplierIntoDb(
			int code) throws OHException 
	{
		Supplier foundSupplier;
		
	
		foundSupplier = (Supplier)jpa.find(Supplier.class, code); 
		testSupplier.check(foundSupplier);
		
		return;
	}	
}