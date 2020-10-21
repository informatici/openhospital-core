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
package org.isf.medicalstockward.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.isf.medicals.model.Medical;
import org.isf.medicals.test.TestMedical;
import org.isf.medicals.test.TestMedicalContext;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.test.TestLot;
import org.isf.medicalstock.test.TestLotContext;
import org.isf.medicalstock.test.TestMovement;
import org.isf.medicalstock.test.TestMovementContext;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MedicalWardId;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperations;
import org.isf.medstockmovtype.test.TestMovementType;
import org.isf.medstockmovtype.test.TestMovementTypeContext;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.test.TestMedicalType;
import org.isf.medtype.test.TestMedicalTypeContext;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.test.TestPatient;
import org.isf.patient.test.TestPatientContext;
import org.isf.supplier.test.TestSupplier;
import org.isf.supplier.test.TestSupplierContext;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.isf.ward.test.TestWard;
import org.isf.ward.test.TestWardContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner; 

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests  
{
	private static DbJpaUtil jpa;
	private static TestMedical testMedical;
	private static TestMedicalContext testMedicalContext;
	private static TestMedicalType testMedicalType;
	private static TestMedicalTypeContext testMedicalTypeContext;
	private static TestWard testWard;
	private static TestWardContext testWardContext;
	private static TestMedicalWard testMedicalWard;
	private static TestMedicalWardContext testMedicalWardContext;
	private static TestPatient testPatient;
	private static TestPatientContext testPatientContext;
	private static TestMovementWard testMovementWard;
	private static TestMovementWardContext testMovementWardContext;
	private static TestMovement testMovement;
	private static TestMovementContext testMovementContext;
	private static TestMovementType testMovementType;
	private static TestMovementTypeContext testMovementTypeContext;
	private static TestSupplier testSupplier;
	private static TestSupplierContext testSupplierContext;
	private static TestLot testLot;
	private static TestLotContext testLotContext;

    @Autowired
    MedicalStockWardIoOperations medicalIoOperation;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testMedical = new TestMedical();
    	testMedicalContext = new TestMedicalContext();
    	testMedicalType = new TestMedicalType();
    	testMedicalTypeContext = new TestMedicalTypeContext();
    	testWard = new TestWard();
    	testWardContext = new TestWardContext();
    	testMedicalWard = new TestMedicalWard();
    	testMedicalWardContext = new TestMedicalWardContext();
    	testPatient = new TestPatient();
    	testPatientContext = new TestPatientContext();
    	testMovementWard = new TestMovementWard();
    	testMovementWardContext = new TestMovementWardContext();
    	testMovement = new TestMovement();
    	testMovementContext = new TestMovementContext();
    	testMovementType = new TestMovementType();
    	testMovementTypeContext = new TestMovementTypeContext();
    	testSupplier = new TestSupplier();
    	testSupplierContext = new TestSupplierContext();
    	testLot = new TestLot();
    	testLotContext = new TestLotContext();
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
    	testMedical = null;
    	testMedicalContext = null;
    	testMedicalType = null;
    	testMedicalTypeContext = null;
    	testWard = null;
    	testWardContext = null;
    	testMedicalWard = null;
    	testMedicalWardContext = null;
    	testPatient = null;
    	testPatientContext = null;
    	testMovementWard = null;
    	testMovementWardContext = null;
    	testMovement = null;
    	testMovementContext = null;
    	testMovementType = null;
    	testMovementTypeContext = null;
    	testSupplier = null;
    	testSupplierContext = null;
    	testLot = null;
    	testLotContext = null;
    }
	
	
	@Test
	public void testMedicalWardGets() 
	{
		MedicalWardId id = null;
			
		
		try 
		{		
			id = _setupTestMedicalWard(false);
			_checkMedicalWardIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testMedicalWardSets() 
	{
		MedicalWardId id = null;
			
		
		try 
		{		
			id = _setupTestMedicalWard(true);
			_checkMedicalWardIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testMovementWardGets() 
	{
		int id = 0;
			
		
		try 
		{		
			id = _setupTestMovementWard(false);
			_checkMovementWardIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testTotalQuantityShouldFindMovementWardByWardCodeAndDates() throws OHException, OHServiceException {
		// given:
		int code = _setupTestMovementWard(false);
		MovementWard foundMovement = (MovementWard)jpa.find(MovementWard.class, code);

		LocalDateTime startDate = foundMovement.getDate().minusDays(1);
		LocalDateTime endDate = foundMovement.getDate().plusDays(1);

		// when:
		ArrayList<MovementWard> wardMovementsToWard = medicalIoOperation.getWardMovementsToWard(
			foundMovement.getWard().getCode(), startDate, endDate
		);

		// then:
		assertThat(wardMovementsToWard).hasSize(1);
		assertThat(wardMovementsToWard.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testMovementWardSets() 
	{
		int id = 0;
			
		
		try 
		{		
			id = _setupTestMovementWard(true);
			_checkMovementWardIntoDb(id);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetWardMovements() 
	{
		int code = 0;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime fromDate = now.withMonth(1).withDayOfMonth(1);
		LocalDateTime toDate = now.withMonth(3).withDayOfMonth(3);
		
		
		try 
		{		
			code = _setupTestMovementWard(false);
			MovementWard foundMovement = (MovementWard)jpa.find(MovementWard.class, code); 
			ArrayList<MovementWard> movements = medicalIoOperation.getWardMovements(
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
	public void testIoGetCurrentQuantityInWard() 
	{	
		boolean result = false;
		
		try 
		{		
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical= testMedical.setup(medicalType, false);
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(false);
			Lot lot = testLot.setup(false);

			Ward wardTo = testWard.setup(false);
			wardTo.setCode("X");

			jpa.beginTransaction();	
			jpa.persist(medicalType);
			jpa.persist(medical);
			jpa.persist(ward);
			jpa.persist(wardTo);
			jpa.persist(patient);
			jpa.persist(lot);
			MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);
			jpa.persist(movementWard);
			jpa.commitTransaction();
			
			result = medicalIoOperation.newMovementWard(movementWard);
			Double quantity = (double) medicalIoOperation.getCurrentQuantityInWard(
					wardTo,
					medical);

			_checkMovementWardIntoDb(movementWard.getCode());
			assertThat(movementWard.getQuantity()).isEqualTo(quantity);
		} 
		catch (Exception e) 
		{
			fail();
		}
	}
	
	@Test
	public void testIoNewMovementWard() 
	{
		boolean result = false;
		
		
		try 
		{		
			MovementWard movementWard;
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical= testMedical.setup(medicalType, false);
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(false);
			Lot lot = testLot.setup(false);

			Ward wardTo = testWard.setup(false);
			wardTo.setCode("X");

			jpa.beginTransaction();	
			jpa.persist(medicalType);
			jpa.persist(medical);
			jpa.persist(ward);
			jpa.persist(wardTo);
			jpa.persist(patient);
			jpa.persist(lot);
			jpa.commitTransaction();
			movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);
			result = medicalIoOperation.newMovementWard(movementWard);
			Double quantity = (double) medicalIoOperation.getCurrentQuantityInWard(wardTo, medical);
			
			_checkMovementWardIntoDb(movementWard.getCode());

			assertThat(movementWard.getQuantity()).isEqualTo(quantity);
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testIoUpdateMovementWard()
	{
		int code = 0;
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestMovementWard(false);
			MovementWard foundMovementWard = (MovementWard)jpa.find(MovementWard.class, code); 
			foundMovementWard.setDescription("Update");
			result = medicalIoOperation.updateMovementWard(foundMovementWard);
			MovementWard updateMovementWard = (MovementWard)jpa.find(MovementWard.class, code);

			assertThat(result).isTrue();
			assertThat(updateMovementWard.getDescription()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}

	}

	@Test
	public void testIoDeleteMovementWard() 
	{
		int code = 0;
		boolean result = false;
		

		try 
		{		
			code = _setupTestMovementWard(false);
			MovementWard foundMovementWard = (MovementWard)jpa.find(MovementWard.class, code); 
			result = medicalIoOperation.deleteMovementWard(foundMovementWard);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetMedicalsWard()
	{
		MedicalWardId code = new MedicalWardId();
		
		
		try 
		{		
			code = _setupTestMedicalWard(false);
			MedicalWard foundMedicalWard = (MedicalWard)jpa.find(MedicalWard.class, code); 
			ArrayList<MedicalWard> medicalWards = medicalIoOperation.getMedicalsWard(foundMedicalWard.getWard().getCode().charAt(0), true);
			assertThat(medicalWards.get(0).getQty()).isCloseTo((double) (foundMedicalWard.getInQuantity() - foundMedicalWard.getOutQuantity()), offset(0.1));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() {
		try {
			// given:
			int id = _setupTestMovementWard(false);
			MovementWard found = (MovementWard) jpa.find(MovementWard.class, id);
			Patient mergedPatient = _setupTestPatient(false);

			// when:
			applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

			// then:
			MovementWard result = (MovementWard)jpa.find(MovementWard.class, id);
			assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private Patient _setupTestPatient(boolean usingSet) throws OHException	{
		jpa.beginTransaction();
		Patient patient = testPatient.setup(usingSet);
		jpa.persist(patient);
		jpa.commitTransaction();

		return patient;
	}

	
	private void _saveContext() throws OHException 
    {	
		testMovementWardContext.saveAll(jpa);
		testMedicalContext.saveAll(jpa);
		testMedicalTypeContext.saveAll(jpa);
		testWardContext.saveAll(jpa);
		testMedicalWardContext.saveAll(jpa);
		testPatientContext.saveAll(jpa);
		testMovementContext.saveAll(jpa);
    	testMovementTypeContext.saveAll(jpa);
    	testSupplierContext.saveAll(jpa);
		testLotContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testMovementWardContext.deleteNews(jpa);
		testMovementContext.deleteNews(jpa);
		testMedicalWardContext.deleteNews(jpa);
		testMedicalContext.deleteNews(jpa);
		testLotContext.deleteNews(jpa);
		testMedicalTypeContext.deleteNews(jpa);
		testWardContext.deleteNews(jpa);
		testPatientContext.deleteNews(jpa);
    	testMovementTypeContext.deleteNews(jpa);
    	testSupplierContext.deleteNews(jpa);
    }
    
	private MedicalWardId _setupTestMedicalWard(
			boolean usingSet) throws OHException 
	{
		MedicalWard medicalWard;
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical= testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(false);

	
		jpa.beginTransaction();	
		jpa.persist(medicalType);
		jpa.persist(medical);
		jpa.persist(ward);
		jpa.persist(lot);
		medicalWard = testMedicalWard.setup(medical, ward, lot, usingSet);
		jpa.persist(medicalWard);
		jpa.commitTransaction();
		
		return medicalWard.getId();
	}
		
	private void  _checkMedicalWardIntoDb(
			MedicalWardId id) throws OHException 
	{
		MedicalWard foundMedicalWard;
		
	
		foundMedicalWard = (MedicalWard)jpa.find(MedicalWard.class, id); 
		testMedicalWard.check(foundMedicalWard);
	}	
    
	private int _setupTestMovementWard(
			boolean usingSet) throws OHException 
	{
		MovementWard movementWard;
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical= testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(false);

	
		jpa.beginTransaction();	
		jpa.persist(medicalType);
		jpa.persist(medical);
		jpa.persist(ward);
		jpa.persist(patient);
		jpa.persist(lot);
		movementWard = testMovementWard.setup(ward, patient, medical, ward, ward, lot, usingSet);
		jpa.persist(movementWard);
		jpa.commitTransaction();
		
		return movementWard.getCode();
	}
		
	private void  _checkMovementWardIntoDb(
			int id) throws OHException 
	{
		MovementWard foundMovementWard;
		
	
		foundMovementWard = (MovementWard)jpa.find(MovementWard.class, id); 
		testMovementWard.check(foundMovementWard);
	}	
}