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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

<<<<<<< HEAD
import javax.persistence.EntityManager;

import org.isf.generaldata.GeneralData;
=======
import org.isf.OHCoreTestCase;
>>>>>>> develop
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicals.test.TestMedical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medicalstock.service.MedicalStockIoOperations.MovementOrder;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medicalstockward.service.MovementWardIoOperationRepository;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperationRepository;
import org.isf.medstockmovtype.test.TestMovementType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.medtype.test.TestMedicalType;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.supplier.test.TestSupplier;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
<<<<<<< HEAD
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests  
{
	@ClassRule
	public static final SpringClassRule scr = new SpringClassRule();

	@Rule
	public final SpringMethodRule smr = new SpringMethodRule();

=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class Tests extends OHCoreTestCase {

>>>>>>> develop
	private static DbJpaUtil jpa;
	private static TestLot testLot;
	private static TestMovement testMovement;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestMovementType testMovementType;
	private static TestWard testWard;
	private static TestSupplier testSupplier;

	@Autowired
<<<<<<< HEAD
	private EntityManager entityManager;

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
    }
        
    @After
    public void tearDown() throws Exception 
    {
        cleanH2InMemoryDB();
        
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
=======
	MedicalStockIoOperations medicalStockIoOperation;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	MedicalStockWardIoOperationRepository medicalStockWardIoOperationRepository;
	@Autowired
	MovementWardIoOperationRepository movementWardIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	MovementIoOperationRepository movementIoOperationRepository;
	@Autowired
	MedicalStockMovementTypeIoOperationRepository medicalStockMovementTypeIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@BeforeClass
	public static void setUpClass() {
		jpa = new DbJpaUtil();
		testLot = new TestLot();
		testMovement = new TestMovement();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testMovementType = new TestMovementType();
		testWard = new TestWard();
		testSupplier = new TestSupplier();
	}

	@Before
	public void setUp() throws OHException {
		cleanH2InMemoryDb();
		jpa.open();
		testLot.setup(false);
	}

	@After
	public void tearDown() throws Exception
>>>>>>> develop
	{
		jpa.flush();
		jpa.close();
	}

	@Test
	public void testLotGets() throws Exception {
		String code = _setupTestLot(false);
		_checkLotIntoDb(code);
	}

	@Test
	public void testLotSets() throws Exception {
		String code = _setupTestLot(true);
		_checkLotIntoDb(code);
	}

	@Test
	public void testMovementGets() throws Exception {
		int code = _setupTestMovement(false);
		_checkMovementIntoDb(code);
	}

	@Test
	public void testMovementSets() throws Exception {
		int code = _setupTestMovement(true);
		_checkMovementIntoDb(code);
	}

	@Test
	public void testIoGetMedicalsFromLot() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Integer> medicalIds = medicalStockIoOperation.getMedicalsFromLot(foundMovement.getLot().getCode());
		assertThat(medicalIds.get(0)).isEqualTo(foundMovement.getMedical().getCode());
	}

	@Test
	public void testIoGetLotsByMedical() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(foundMovement.getMedical());
		assertThat(lots).hasSize(1);
		assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
	}

	@Test
<<<<<<< HEAD
	public void testIoNewAutomaticDischargingMovementDifferentLots() throws Exception
	{
		int code = 0;
		code = _setupTestMovement(false);
		Movement foundMovement = (Movement) jpa.find(Movement.class, code);
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
				15,    // quantity of 15 should use first lot of 10 + second lot of 5
				null,
				"newReference2");
		medicalStockIoOperation.newAutomaticDischargingMovement(dischargeMovement);

		ArrayList<Lot> lots = medicalStockIoOperation.getLotsByMedical(medical);
		assertThat(lots).hasSize(1); // first lot should be 0 quantity and stripped by the list
=======
	public void testIoNewAutomaticDischargingMovement() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		boolean result = medicalStockIoOperation.newAutomaticDischargingMovement(foundMovement);
		assertThat(result).isTrue();
>>>>>>> develop
	}

	@Test
	public void testIoNewAutomaticDischargingMovementDifferentLots() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
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
				15,    // quantity of 15 should use first lot of 10 + second lot of 5
				null,
				"newReference2");
		medicalStockIoOperation.newAutomaticDischargingMovement(dischargeMovement);

		ArrayList<Lot> lots = medicalStockIoOperation.getLotsByMedical(medical);
		assertThat(lots).hasSize(1); // first lot should be 0 quantity and stripped by the list
	}

	@Test
	public void testIoNewMovement() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		boolean result = medicalStockIoOperation.newMovement(foundMovement);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoPrepareChargingMovement() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		boolean result = medicalStockIoOperation.prepareChargingMovement(foundMovement);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoPrepareDischargingMovement() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		boolean result = medicalStockIoOperation.prepareDischargingMovement(foundMovement);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoLotExists() throws Exception {
		String code = _setupTestLot(false);
		boolean result = medicalStockIoOperation.lotExists(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoGetMovements() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		ArrayList<Movement> movements = medicalStockIoOperation.getMovements();
		//assertEquals(foundMovement.getCode(), movements.get(0).getCode());
		assertThat(movements).isNotEmpty();
	}

	@Test
	public void testIoGetMovementsWithParameters() throws Exception {
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar fromDate = new GregorianCalendar(now.get(Calendar.YEAR), 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(now.get(Calendar.YEAR), 3, 3);
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		ArrayList<Movement> movements = medicalStockIoOperation.getMovements(
				foundMovement.getWard().getCode(),
				fromDate,
				toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testIoGetMovementsWihAllParameters() throws Exception {
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar fromDate = new GregorianCalendar(now.get(Calendar.YEAR), 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(now.get(Calendar.YEAR), 3, 3);
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
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

	@Test
	public void testIoGetMovementForPrint() throws Exception {
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar fromDate = new GregorianCalendar(now.get(Calendar.YEAR), 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(now.get(Calendar.YEAR), 3, 3);
		MovementOrder order = MedicalStockIoOperations.MovementOrder.DATE;
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
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

	@Test
	public void testIoGetLastMovementDate() throws Exception {
		int code = _setupTestMovement(false);
		ArrayList<Movement> movements = medicalStockIoOperation.getMovements();
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		GregorianCalendar gc = medicalStockIoOperation.getLastMovementDate();
		assertThat(gc).isEqualTo(movements.get(0).getDate());
	}

	@Test
	public void testIoRefNoExists() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		boolean result = medicalStockIoOperation.refNoExists(foundMovement.getRefNo());
		assertThat(result).isTrue();
	}

	@Test
	public void testIoGetMovementsByReference() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		ArrayList<Movement> movements = medicalStockIoOperation.getMovementsByReference(foundMovement.getRefNo());
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}
<<<<<<< HEAD
	
    private void cleanH2InMemoryDB() throws OHException {
	    List<Object[]> show_tables = entityManager.createNativeQuery("SHOW TABLES").getResultList();
	    show_tables
			    .stream()
			    .map(result -> (String) result[0])
			    .forEach(this::truncateTable);
    }

    private void truncateTable(String name) {
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
		entityManager.createNativeQuery("TRUNCATE TABLE " + name).executeUpdate();
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
	}

	private String _setupTestLot(
			boolean usingSet) throws OHException 
	{
		Lot lot;
		
	
		jpa.beginTransaction();	
		lot = testLot.setup(usingSet);
		jpa.persist(lot);
		jpa.commitTransaction();
		
=======

	private String _setupTestLot(boolean usingSet) throws OHException {
		Lot lot = testLot.setup(usingSet);
		lotIoOperationRepository.saveAndFlush(lot);
>>>>>>> develop
		return lot.getCode();
	}

	private void _checkLotIntoDb(String code) throws OHException {
		Lot foundLot = lotIoOperationRepository.findOne(code);
		testLot.check(foundLot);
	}

	private int _setupTestMovement(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		MovementType movementType = testMovementType.setup(false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(false);
		Supplier supplier = testSupplier.setup(false);
		Movement movement = testMovement.setup(medical, movementType, ward, lot, supplier, usingSet);
		//	Should not this be the same as the jpa persist statement below?
		//  It appears not as
		//		supplierIoOperationRepository.saveAndFlush(supplier);
		//		lotIoOperationRepository.saveAndFlush(lot);
		//		wardIoOperationRepository.saveAndFlush(ward);
		//		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		//		medicalsIoOperationRepository.saveAndFlush(medical);
		//		medicalStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		//		movementIoOperationRepository.saveAndFlush(movement);
		jpa.beginTransaction();
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

	private void _checkMovementIntoDb(int code) throws OHException {
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		testMovement.check(foundMovement);
	}
}