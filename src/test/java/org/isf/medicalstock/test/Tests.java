/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicals.test.TestMedical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medicalstock.service.MedicalStockIoOperations.MovementOrder;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medicalstockward.model.MedicalWard;
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
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests extends OHCoreTestCase {

	@ClassRule
	public static final SpringClassRule scr = new SpringClassRule();

	@Rule
	public final SpringMethodRule smr = new SpringMethodRule();

	private static TestLot testLot;
	private static TestMovement testMovement;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestMovementType testMovementType;
	private static TestWard testWard;
	private static TestSupplier testSupplier;

	@Autowired
	MedicalStockIoOperations medicalStockIoOperation;
	@Autowired
	MovBrowserManager movBrowserManager;
	@Autowired
	MovStockInsertingManager movStockInsertingManager;
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

	public Tests(boolean in, boolean out, boolean toward) {
		GeneralData.AUTOMATICLOT_IN = in;
		GeneralData.AUTOMATICLOT_OUT = out;
		GeneralData.AUTOMATICLOTWARD_TOWARD = toward;
	}

	@BeforeClass
	public static void setUpClass() {
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
	}

	@AfterClass
	public static void tearDownClass() {
		testLot = null;
		testMovement = null;
		testMedical = null;
		testMedicalType = null;
		testMovementType = null;
		testWard = null;
		testSupplier = null;
	}

	@Parameterized.Parameters(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	public static Collection<Object[]> automaticlot() {
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
	public void testIoGetLotsByMedicalEmptyLot() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);

		foundMovement.setQuantity(0);
		movementIoOperationRepository.saveAndFlush(foundMovement);

		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(foundMovement.getMedical());
		assertThat(lots).isEmpty();
	}

	@Test
	public void testIoNewAutomaticDischargingMovement() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		boolean result = medicalStockIoOperation.newAutomaticDischargingMovement(foundMovement);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoNewAutomaticDischargingMovementLotQuantityLessMovementQuantity() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		foundMovement.getLot().setMainStoreQuantity(10);
		foundMovement.setQuantity(100);
		assertThat(medicalStockIoOperation.newAutomaticDischargingMovement(foundMovement)).isTrue();
	}

	@Test
	public void testIoNewAutomaticDischargingMovementDifferentLots() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);

		Medical medical = foundMovement.getMedical();
		MovementType movementType = foundMovement.getType();
		Ward ward = foundMovement.getWard();
		Supplier supplier = foundMovement.getSupplier();
		Lot lot2 = testLot.setup(medical, false); //we are going to create a second lot
		lot2.setCode("second");
		Movement newMovement = new Movement(
				medical,
				movementType,
				null,
				lot2,
				new GregorianCalendar(),
				7, // new lot with 10 quantity
				supplier,
				"newReference");
		medicalStockIoOperation.newMovement(newMovement);

		MovementType dischargeMovementType = testMovementType.setup(false); //prepare discharge movement
		dischargeMovementType.setCode("discharge");
		dischargeMovementType.setType("-");
		medicalStockMovementTypeIoOperationRepository.saveAndFlush(dischargeMovementType);

		Movement dischargeMovement = new Movement(
				medical,
				dischargeMovementType,
				ward,
				null, // automatic lot selection
				new GregorianCalendar(),
				15,    // quantity of 15 should use first lot of 10 + second lot of 5
				null,
				"newReference2");
		boolean automaticLotMode = GeneralData.AUTOMATICLOT_OUT;
		GeneralData.AUTOMATICLOT_OUT = true;
		medicalStockIoOperation.newAutomaticDischargingMovement(dischargeMovement);
		GeneralData.AUTOMATICLOT_OUT = automaticLotMode;

		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(medical);
		assertThat(lots).hasSize(1); // first lot should be 0 quantity and stripped by the list
	}

	@Test
	public void testIoNewMovementOutGoingLots() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		Medical medical = foundMovement.getMedical();
		MovementType medicalType = foundMovement.getType();
		medicalType.setType("-");
		Ward ward = foundMovement.getWard();
		Supplier supplier = foundMovement.getSupplier();
		Lot lot = foundMovement.getLot();
		Movement newMovement = new Movement(
				medical,
				medicalType,
				ward,
				lot,
				new GregorianCalendar(),
				10, // new lot with 10 quantitye
				supplier,
				"newReference");
		assertThat(medicalStockIoOperation.newMovement(newMovement)).isTrue();
	}

	@Test
	public void testIoNewMovementOutGoingLotsWardNull() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		Medical medical = foundMovement.getMedical();
		MovementType medicalType = foundMovement.getType();
		medicalType.setType("-");
		Supplier supplier = foundMovement.getSupplier();
		Lot lot = foundMovement.getLot();
		Movement newMovement = new Movement(
				medical,
				medicalType,
				null,
				lot,
				new GregorianCalendar(),
				10, // new lot with 10 quantitye
				supplier,
				"newReference");
		assertThat(medicalStockIoOperation.newMovement(newMovement)).isTrue();
	}

	@Test
	public void testIoNewMovement() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		boolean result = medicalStockIoOperation.newMovement(foundMovement);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoNewMovementAutomaticLotMode() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		boolean automaticLotMode = GeneralData.AUTOMATICLOT_IN;
		GeneralData.AUTOMATICLOT_IN = true;
		assertThat(medicalStockIoOperation.newMovement(foundMovement)).isTrue();
		GeneralData.AUTOMATICLOT_IN = automaticLotMode;
	}

	@Test
	public void testIoNewMovementUpdateMedicalWardQuantityMedicalWardFound() throws Exception {
		int code = _setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findOne(code);
		MovementType medicalType = movement.getType();
		medicalType.setType("-");
		MedicalWard medicalWard = new MedicalWard(movement.getWard(), movement.getMedical(), 0, 0, movement.getLot());
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
		Movement newMovement = new Movement(movement.getMedical(), medicalType, movement.getWard(), movement.getLot(),
				new GregorianCalendar(), 10, movement.getSupplier(), "newReference");
		assertThat(medicalStockIoOperation.newMovement(newMovement)).isTrue();
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
		List<Movement> movements = medicalStockIoOperation.getMovements();
		assertThat(movements).isNotEmpty();
		assertThat(foundMovement.getCode()).isEqualTo(movements.get(0).getCode());
	}

	@Test
	public void testIoGetMovementsWithParameters() throws Exception {
		GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = medicalStockIoOperation.getMovements(
				foundMovement.getWard().getCode(),
				fromDate,
				toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testIoGetMovementsWihAllParameters() throws Exception {
		GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = medicalStockIoOperation.getMovements(
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
	public void testIoGetMovementForPrintDateOrder() throws Exception {
		GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
		MovementOrder order = MovementOrder.DATE;
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = medicalStockIoOperation.getMovementForPrint(
				foundMovement.getMedical().getDescription(),
				foundMovement.getMedical().getType().getCode(),
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
	public void testIoGetMovementForPrintWardOrderateWard() throws Exception {
		GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
		MovementOrder order = MovementOrder.WARD;
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = medicalStockIoOperation.getMovementForPrint(
				foundMovement.getMedical().getDescription(),
				foundMovement.getMedical().getType().getCode(),
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
	public void testIoGetMovementForPrintPharmaceuticalTypeOrder() throws Exception {
		GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
		MovementOrder order = MovementOrder.PHARMACEUTICAL_TYPE;
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = medicalStockIoOperation.getMovementForPrint(
				foundMovement.getMedical().getDescription(),
				foundMovement.getMedical().getType().getCode(),
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
	public void testIoGetMovementForPrintTypeOrder() throws Exception {
		GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
		MovementOrder order = MovementOrder.TYPE;
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = medicalStockIoOperation.getMovementForPrint(
				foundMovement.getMedical().getDescription(),
				foundMovement.getMedical().getType().getCode(),
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
		_setupTestMovement(false);
		List<Movement> movements = medicalStockIoOperation.getMovements();
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
		List<Movement> movements = medicalStockIoOperation.getMovementsByReference(foundMovement.getRefNo());
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testMgrGetMovements() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = movBrowserManager.getMovements();
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testMgrGetMovementsWithParameters() throws Exception {
		GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = movBrowserManager.getMovements(foundMovement.getWard().getCode(), fromDate, toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testMgrGetMovementsByReference() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = movBrowserManager.getMovementsByReference("notThere");
		assertThat(movements).isEmpty();
		movements = movBrowserManager.getMovementsByReference(foundMovement.getRefNo());
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testMgrGetMovementsWihAllParameters() throws Exception {
		GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
				foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, toDate, fromDate, toDate, fromDate, toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testMgrGetMovementsCheckMovParameters() {
		assertThatThrownBy(() ->
		{
			GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
			GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
			int code = _setupTestMovement(false);
			Movement foundMovement = movementIoOperationRepository.findOne(code);
			movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
					foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, null, fromDate, toDate, fromDate, toDate);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrGetMovementsCheckLotPrepParameters() {
		assertThatThrownBy(() ->
		{
			GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
			GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
			int code = _setupTestMovement(false);
			Movement foundMovement = movementIoOperationRepository.findOne(code);
			movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
					foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, toDate, null, toDate, fromDate, toDate);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrGetMovementsCheckLotDueParameters() {
		assertThatThrownBy(() ->
		{
			GregorianCalendar fromDate = new GregorianCalendar(2000, 1, 1);
			GregorianCalendar toDate = new GregorianCalendar(2000, 3, 3);
			int code = _setupTestMovement(false);
			Movement foundMovement = movementIoOperationRepository.findOne(code);
			movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
					foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, toDate, fromDate, toDate, fromDate, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrGetMovementsWihAllParametersNull() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Movement> movements = movBrowserManager.getMovements(null, null, foundMovement.getWard().getCode(), null,
				null, null, null, null, null, null);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testMgrGetLotsByMedical() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		List<Lot> lots = movStockInsertingManager.getLotByMedical(foundMovement.getMedical());
		assertThat(lots).hasSize(1);
		assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
	}

	@Test
	public void testMgrGetLotsByMedicalNull() throws Exception {
		assertThat(movStockInsertingManager.getLotByMedical(null)).isEmpty();
	}

	@Test
	public void testMgrAlertCriticalQuantityUnderLimit() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		assertThat(movStockInsertingManager.alertCriticalQuantity(foundMovement.getMedical(), 100)).isTrue();
	}

	@Test
	public void testMgrAlertCriticalQuantityOverLimit() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		assertThat(movStockInsertingManager.alertCriticalQuantity(foundMovement.getMedical(), -50)).isFalse();
	}

	@Test
	public void testMgrGetLastMovementDate() throws Exception {
		_setupTestMovement(false);
		List<Movement> movements = medicalStockIoOperation.getMovements();
		GregorianCalendar gregorianCalendar = movStockInsertingManager.getLastMovementDate();
		assertThat(gregorianCalendar).isEqualTo(movements.get(0).getDate());
	}

	@Test
	public void testMgrRefNoExists() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		assertThat(movStockInsertingManager.refNoExists(foundMovement.getRefNo())).isTrue();
		assertThat(movStockInsertingManager.refNoExists("notThere")).isFalse();
	}

	@Test
	public void testMgrPrepareChargingMovement() throws Exception {
		int code = _setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findOne(code);
		ArrayList<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		assertThat(movStockInsertingManager.newMultipleChargingMovements(movements, "refNo")).isTrue();
	}

	@Test
	public void testMgrPrepareChargingMovementBadRefNumber() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			movement.setRefNo(null);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHDataValidationException) e).getMessages().size() == 2), "Expecting two validation errors")
				);
	}

	@Test
	public void testMgrPrepareDischargingMovement() throws Exception {
		int code = _setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findOne(code);
		ArrayList<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		assertThat(movStockInsertingManager.newMultipleDischargingMovements(movements, "refNo")).isTrue();
	}

	@Test
	public void testMgrPrepareDischargingMovementBadRefNumber() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			movement.setRefNo(null);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleDischargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
				);
	}

	@Test
	public void testMgrPrepareDischargingMovementIsAutomaticLotOut() throws Exception {
		int code = _setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findOne(code);
		ArrayList<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		boolean isAutomaticLot_Out = GeneralData.AUTOMATICLOT_OUT;
		GeneralData.AUTOMATICLOT_OUT = true;
		assertThat(movStockInsertingManager.newMultipleDischargingMovements(movements, "refNo")).isTrue();
		GeneralData.AUTOMATICLOT_OUT = isAutomaticLot_Out;
	}

	@Test
	public void testMgrStoreLot() throws Exception {
		int code = _setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findOne(code);
		assertThat(movStockInsertingManager.storeLot(movement.getLot().getCode(), movement.getLot(), movement.getMedical())).isTrue();
	}

	@Test
	public void testMgrValidateMovementMoveDateAfterToday() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			movement.setDate(new GregorianCalendar(2099, 1, 1));
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, "refNo");
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
				);
	}

	@Test
	public void testMgrValidateMovementDateBeforeLastDate() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			GregorianCalendar todayPlusAYear = new GregorianCalendar();
			todayPlusAYear.add(GregorianCalendar.YEAR, 1);
			movement.setDate(todayPlusAYear);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, "refNo");
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
				);
	}

	@Test
	public void testMgrValidateReferenceNumber() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
				);
	}

	@Test
	public void testMgrValidateNullSupplier() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			movement.getType().setType("+");
			movement.setSupplier(null);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 3), "Expecting two validation errors")
				);
	}

	@Test
	public void testMgrValidateNullWard() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			movement.getType().setType("-");
			movement.setWard(null);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@Test
	public void testMgrValidateQuantityZero() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			movement.setQuantity(0);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 3), "Expecting three validation errors")
				);
	}

	@Test
	public void testMgrValidateNullMedical() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			movement.setMedical(null);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 3), "Expecting three validation errors")
				);
	}

	@Test
	public void testMgrValidateNullMovementType() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			movement.setType(null);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 3), "Expecting three validation errors")
				);
	}

	@Test
	public void testMgrValidateLotRefersToAnotherMedical() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			movement.getMedical().setCode(-99);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
				);
	}

	@Test
	public void testMgrValidateLotWithCostZero() {
		assertThatThrownBy(() ->
		{
			boolean lotWithCost = GeneralData.LOTWITHCOST;
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			Lot lot = movement.getLot();
			lot.setCost(new BigDecimal(0.));
			lotIoOperationRepository.saveAndFlush(lot);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			GeneralData.LOTWITHCOST = true;
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
			GeneralData.LOTWITHCOST = lotWithCost;
		})
				.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@Test
	public void testMgrValidateLotCodeTooLong() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			Lot lot = movement.getLot();
			lot.setCode("thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong");
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHDataValidationException) e).getMessages().size() == 2), "Expecting two validation errors")
				);
	}

	@Test
	public void testMgrValidateLotDueDateNull() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			Lot lot = movement.getLot();
			lot.setDueDate(null);
			lotIoOperationRepository.saveAndFlush(lot);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@Test
	public void testMgrValidateLotPrepationDateNull() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			Lot lot = movement.getLot();
			lot.setPreparationDate(null);
			lotIoOperationRepository.saveAndFlush(lot);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@Test
	public void testMgrValidateLotPreparationDateAfterDueDate() {
		assertThatThrownBy(() ->
		{
			int code = _setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findOne(code);
			Lot lot = movement.getLot();
			lot.setPreparationDate(new GregorianCalendar(99, 1, 1));
			lot.setDueDate(new GregorianCalendar(1, 1, 1));
			lotIoOperationRepository.saveAndFlush(lot);
			ArrayList<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
				.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@Test
	public void testLotToString() throws Exception {
		String code = _setupTestLot(true);
		Lot lot = lotIoOperationRepository.findOne(code);
		lot.setCode(null);
		// TODO: if resource bundles are available this string test needs to change
		assertThat(lot).hasToString("angal.medicalstock.nolot.txt");
		lot.setCode(code);
		assertThat(lot).hasToString(code);
	}

	@Test
	public void testLotIsValidLot() throws Exception {
		String code = _setupTestLot(true);
		Lot lot = lotIoOperationRepository.findOne(code);
		assertThat(lot.isValidLot()).isTrue();
		lot.setCode("thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong");
		assertThat(lot.isValidLot()).isFalse();
	}

	@Test
	public void testLotEquals() throws Exception {
		String code = _setupTestLot(true);
		Lot lot = lotIoOperationRepository.findOne(code);
		assertThat(lot.equals(lot)).isTrue();
		assertThat(lot)
				.isNotNull()
				.isNotEqualTo("someString");

		Lot lot2 = new Lot(null);
		assertThat(lot).isNotEqualTo(lot2);
		assertThat(lot2).isNotEqualTo(lot);

		lot2.setCode(lot.getCode());
		assertThat(lot2).isNotEqualTo(lot);
		assertThat(lot).isNotEqualTo(lot2);

		lot2.setCost(new BigDecimal(1));
		assertThat(lot2).isNotEqualTo(lot);
		assertThat(lot).isNotEqualTo(lot2);

		lot2.setCost(lot.getCost());
		assertThat(lot2).isNotEqualTo(lot);
		assertThat(lot).isNotEqualTo(lot2);

		lot2.setDueDate(lot.getDueDate());
		assertThat(lot2).isNotEqualTo(lot);
		assertThat(lot).isNotEqualTo(lot2);

		lot2.setPreparationDate(lot.getPreparationDate());
		assertThat(lot2).isEqualTo(lot);
		assertThat(lot).isEqualTo(lot2);

		lot2.setMainStoreQuantity(1);
		assertThat(lot2).isNotEqualTo(lot);
		assertThat(lot).isNotEqualTo(lot2);

		lot2.setMainStoreQuantity(lotIoOperationRepository.getMainStoreQuantity(lot));
		assertThat(lot2).isEqualTo(lot);
		assertThat(lot).isEqualTo(lot2);
	}

	@Test
	public void testLotHashCode() {
		Lot lot = new Lot("aCode", new GregorianCalendar(), new GregorianCalendar());
		int hashCode = lot.hashCode();
		assertThat(hashCode).isPositive();
		// use computed value
		assertThat(lot.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testMovementToString() throws Exception {
		int code = _setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findOne(code);
		// TODO: if resource bundles are available this string test needs to change
		assertThat(movement).hasToString("angal.movement.tostring.fmt.txt");
	}

	@Test
	public void testMovementEquals() throws Exception {
		int code = _setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findOne(code);
		assertThat(movement.equals(movement)).isTrue();
		assertThat(movement)
				.isNotNull()
				.isNotEqualTo("someString");

		Movement movement2 = new Movement();
		movement2.setCode(-99);
		assertThat(movement).isNotEqualTo(movement2);
		movement2.setCode(movement.getCode());
		assertThat(movement).isEqualTo(movement2);
	}

	@Test
	public void testMovementHashCode() throws Exception {
		int code = _setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findOne(code);
		int hashCode = movement.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + code);
		// use computed value
		assertThat(movement.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testMovementGetOrigin() throws Exception {
		int code = _setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findOne(code);
		assertThat(movement.getSupplier()).isEqualTo(movement.getOrigin());
	}

	private String _setupTestLot(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		return lot.getCode();
	}

	private void _checkLotIntoDb(String code) {
		Lot foundLot = lotIoOperationRepository.findOne(code);
		testLot.check(foundLot);
	}

	private int _setupTestMovement(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		MovementType movementType = testMovementType.setup(false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(medical, false);
		Supplier supplier = testSupplier.setup(false);
		Movement movement = testMovement.setup(medical, movementType, ward, lot, supplier, usingSet);
		supplierIoOperationRepository.saveAndFlush(supplier);
		wardIoOperationRepository.saveAndFlush(ward);
		medicalStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		movementIoOperationRepository.saveAndFlush(movement);
		return movement.getCode();
	}

	private void _checkMovementIntoDb(int code) {
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		testMovement.check(foundMovement);
	}
}
