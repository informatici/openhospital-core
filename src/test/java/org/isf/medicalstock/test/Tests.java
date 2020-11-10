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
package org.isf.medicalstock.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.generaldata.GeneralData;
import org.isf.medicals.model.Medical;
import org.isf.medicals.test.TestMedical;
import org.isf.medicals.test.TestMedicalContext;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medicalstock.service.MedicalStockIoOperations.MovementOrder;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.test.TestMovementType;
import org.isf.medstockmovtype.test.TestMovementTypeContext;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.test.TestMedicalType;
import org.isf.medtype.test.TestMedicalTypeContext;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.test.TestSupplier;
import org.isf.supplier.test.TestSupplierContext;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.test.TestWard;
import org.isf.ward.test.TestWardContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests  
{
	private static DbJpaUtil jpa;
	private static TestLot testLot;
	private static TestLotContext testLotContext;
	private static TestMovement testMovement;
	private static TestMovementContext testMovementContext;
	private static TestMedical testMedical;
	private static TestMedicalContext testMedicalContext;
	private static TestMedicalType testMedicalType;
	private static TestMedicalTypeContext testMedicalTypeContext;
	private static TestMovementType testMovementType;
	private static TestMovementTypeContext testMovementTypeContext;
	private static TestWard testWard;
	private static TestWardContext testWardContext;
	private static TestSupplier testSupplier;
	private static TestSupplierContext testSupplierContext;

    @Autowired
    MedicalStockIoOperations medicalStockIoOperation;

    public Tests(boolean in, boolean out, boolean toward) {
	    GeneralData.AUTOMATICLOT_IN = in;
	    GeneralData.AUTOMATICLOT_OUT = out;
	    GeneralData.AUTOMATICLOTWARD_TOWARD = toward;
    }

	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testLot = new TestLot();
    	testLotContext = new TestLotContext();
    	testMovement = new TestMovement();
    	testMovementContext = new TestMovementContext();
    	testMedical = new TestMedical();
    	testMedicalContext = new TestMedicalContext();
    	testMedicalType = new TestMedicalType();
    	testMedicalTypeContext = new TestMedicalTypeContext();
    	testMovementType = new TestMovementType();
    	testMovementTypeContext = new TestMovementTypeContext();
    	testWard = new TestWard();
    	testWardContext = new TestWardContext();
    	testSupplier = new TestSupplier();
    	testSupplierContext = new TestSupplierContext();
    }

    @Before
    public void setUp() throws OHException
    {
        jpa.open();
        
        testLot.setup(false);

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
    	testLot = null;
    	testLotContext = null;
    	testMovement = null;
    	testMovementContext = null;
    	testMedical = null;
    	testMedicalContext = null;
    	testMedicalType = null;
    	testMedicalTypeContext = null;
    	testMovementType = null;
    	testMovementTypeContext = null;
    	testWard = null;
    	testWardContext = null;
    	testSupplier = null;
    	testSupplierContext = null;
    }

	@Parameterized.Parameters(name ="Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	public static Collection automaticlot() {
		return Arrays.asList(new Object[][] {
				{ false, false, false },
				{ false, false, true },
				{ false, true, false },
				{ false, true, true },
				{ true, false, false },
				{ true, false, true },
				{ true, true, false },
				{ true, true, true }
		});
	}

	@Test
	public void testLotGets() 
	{
		String code = "";
			
		
		try 
		{		
			code = _setupTestLot(false);
			_checkLotIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testLotSets() 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestLot(true);
			_checkLotIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testMovementGets() 
	{
		int code = 0;
			
		
		try 
		{		
			code = _setupTestMovement(false);
			_checkMovementIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testMovementSets() 
	{
		int code = 0;
			

		try 
		{		
			code = _setupTestMovement(true);
			_checkMovementIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetMedicalsFromLot() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			List<Integer> medicalIds = medicalStockIoOperation.getMedicalsFromLot(foundMovement.getLot().getCode());

			assertThat(medicalIds.get(0)).isEqualTo(foundMovement.getMedical().getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetLotsByMedical() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			List<Lot> lots = medicalStockIoOperation.getLotsByMedical(foundMovement.getMedical());

			assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewAutomaticDischargingMovement() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			boolean result = medicalStockIoOperation.newAutomaticDischargingMovement(foundMovement);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewAutomaticDischargingMovementDifferentLots() 
	{
		int code = 0;
		
		try 
		{	
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code);
			//medicalStockIoOperation.newMovement(foundMovement);
			
			Medical medical = foundMovement.getMedical();
			MovementType medicalType = foundMovement.getType();
			Ward ward = foundMovement.getWard();
			Supplier supplier = foundMovement.getSupplier();
			Lot lot = foundMovement.getLot(); //we are going to charge same lot
			Movement newMovement = new Movement(
					medical,
					medicalType, 
					ward, 
					lot, 
					new GregorianCalendar(),
					10, // new lot with 10 quantitye
					supplier, 
					"newReference");
			medicalStockIoOperation.newMovement(newMovement);
			
			Movement dischargeMovement = new Movement(
					medical,
					medicalType,
					ward,
					null, // automatic lot selection
					new GregorianCalendar(),
					15,	// quantity of 15 should use first lot of 10 + second lot of 5
					null,
					"newReference2");
			medicalStockIoOperation.newAutomaticDischargingMovement(dischargeMovement);
			
			ArrayList<Lot> lots = medicalStockIoOperation.getLotsByMedical(medical);
			assertThat(lots).hasSize(1); // first lot should be 0 quantity and stripped by the list
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewMovement() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			boolean result = medicalStockIoOperation.newMovement(foundMovement);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoPrepareChargingMovement() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			boolean result = medicalStockIoOperation.prepareChargingMovement(foundMovement);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoPrepareDischargingMovement() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			boolean result = medicalStockIoOperation.prepareDischargingMovement(foundMovement);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}	
	
	@Test
	public void testIoLotExists() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestLot(false);
			result = medicalStockIoOperation.lotExists(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}	
	
	@Test
	public void testIoGetMovements() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			ArrayList<Movement> movements = medicalStockIoOperation.getMovements();

			//assertEquals(foundMovement.getCode(), movements.get(0).getCode());
			assertThat(movements).isNotEmpty();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetMovementsWithParameters()
	{
		int code = 0;
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar fromDate = new GregorianCalendar(now.get(Calendar.YEAR), 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(now.get(Calendar.YEAR), 3, 3);
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			ArrayList<Movement> movements = medicalStockIoOperation.getMovements(
					foundMovement.getWard().getCode(),
					fromDate,
					toDate);

			assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetMovementsWihAllParameters() 
	{
		int code = 0;
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar fromDate = new GregorianCalendar(now.get(Calendar.YEAR), 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(now.get(Calendar.YEAR), 3, 3);
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			ArrayList<Movement> movements = medicalStockIoOperation.getMovements(
					foundMovement.getMedical().getCode(),
					foundMovement.getMedical().getType().getCode(),
					foundMovement.getWard().getCode(), 
					foundMovement.getType().getCode(),
					fromDate, 
					toDate,
					fromDate, 
					toDate,
					fromDate, 
					toDate);

			assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetMovementForPrint() 
	{
		int code = 0;
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar fromDate = new GregorianCalendar(now.get(Calendar.YEAR), 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(now.get(Calendar.YEAR), 3, 3);
		MovementOrder order = MedicalStockIoOperations.MovementOrder.DATE;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			ArrayList<Movement> movements = medicalStockIoOperation.getMovementForPrint(												
												foundMovement.getMedical().getDescription(), 
												null,
												foundMovement.getWard().getCode(),
												foundMovement.getType().getCode(),
												fromDate,
												toDate,
												foundMovement.getLot().getCode(),
												order
											);

			assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}	
	
	@Test
	public void testIoGetLastMovementDate() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			ArrayList<Movement> movements = medicalStockIoOperation.getMovements();
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			GregorianCalendar gc = medicalStockIoOperation.getLastMovementDate();

			assertThat(gc).isEqualTo(movements.get(0).getDate());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoRefNoExists() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			boolean result = medicalStockIoOperation.refNoExists(foundMovement.getRefNo());

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetMovementsByReference() 
	{
		int code = 0;
		
		
		try 
		{		
			code = _setupTestMovement(false);
			Movement foundMovement = (Movement)jpa.find(Movement.class, code); 
			ArrayList<Movement> movements = medicalStockIoOperation.getMovementsByReference(foundMovement.getRefNo() );

			assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	
	private void _saveContext() throws OHException 
    {	
		testLotContext.saveAll(jpa);
		testMovementContext.saveAll(jpa);
    	testMedicalContext.saveAll(jpa);
    	testMedicalTypeContext.saveAll(jpa);
    	testMovementTypeContext.saveAll(jpa);
    	testWardContext.saveAll(jpa);
    	testSupplierContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testMovementContext.deleteNews(jpa);
		testMedicalContext.deleteNews(jpa);
		testLotContext.deleteNews(jpa);
    	testMedicalTypeContext.deleteNews(jpa);
    	testMovementTypeContext.deleteNews(jpa);
    	testWardContext.deleteNews(jpa);
    	testSupplierContext.deleteNews(jpa);
    }
    
	private String _setupTestLot(
			boolean usingSet) throws OHException 
	{
		Lot lot;
		
	
		jpa.beginTransaction();	
		lot = testLot.setup(usingSet);
		jpa.persist(lot);
		jpa.commitTransaction();
		
		return lot.getCode();
	}
		
	private void  _checkLotIntoDb(
			String code) throws OHException 
	{
		Lot foundLot;
		
	
		foundLot = (Lot)jpa.find(Lot.class, code); 
		testLot.check(foundLot);
	}	
    
	private int _setupTestMovement(
			boolean usingSet) throws OHException 
	{
		Movement movement;
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		MovementType movementType = testMovementType.setup(false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(false);
		Supplier supplier = testSupplier.setup(false);
				
	
		jpa.beginTransaction();	
		movement = testMovement.setup(medical, movementType, ward, lot, supplier, usingSet);
		jpa.persist(supplier);
		jpa.persist(lot);
		jpa.persist(ward);
		jpa.persist(medicalType);
		jpa.persist(medical);
		jpa.persist(movementType);
		jpa.persist(movement);
		jpa.commitTransaction();
		
		return movement.getCode();
	}
		
	private void  _checkMovementIntoDb(
			int code) throws OHException 
	{
		Movement foundMovement;
		
	
		foundMovement = (Movement)jpa.find(Movement.class, code); 
		testMovement.check(foundMovement);
	}	
}