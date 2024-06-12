/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.medicalstock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.MedicalStock;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medicalstock.service.MedicalStockIoOperations.MovementOrder;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medicalstockward.service.MovementWardIoOperationRepository;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class Tests extends OHCoreTestCase {

	private static TestLot testLot;
	private static TestMovement testMovement;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestMovementType testMovementType;
	private static TestWard testWard;
	private static TestSupplier testSupplier;
	private static TestMedicalStock testMedicalStock;

	@Autowired
	MedicalStockIoOperations medicalStockIoOperation;
	@Autowired
	MedicalStockIoOperationRepository medicalStockIoOperationRepository;
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
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	static Stream<Arguments> automaticlot() {
		return Stream.of(
						Arguments.of(false, false, false),
						Arguments.of(false, false, true),
						Arguments.of(false, true, false),
						Arguments.of(false, true, true),
						Arguments.of(true, false, false),
						Arguments.of(true, false, true),
						Arguments.of(true, true, false),
						Arguments.of(true, true, true));
	}

	private static void setGeneralData(boolean in, boolean out, boolean toward) {
		GeneralData.AUTOMATICLOT_IN = in;
		GeneralData.AUTOMATICLOT_OUT = out;
		GeneralData.AUTOMATICLOTWARD_TOWARD = toward;
	}

	@BeforeAll
	static void setUpClass() {
		testLot = new TestLot();
		testMovement = new TestMovement();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testMovementType = new TestMovementType();
		testWard = new TestWard();
		testSupplier = new TestSupplier();
		testMedicalStock = new TestMedicalStock();
	}

	@BeforeEach
	void setUp() throws OHException {
		cleanH2InMemoryDb();
	}

	@AfterAll
	static void tearDownClass() {
		testLot = null;
		testMovement = null;
		testMedical = null;
		testMedicalType = null;
		testMovementType = null;
		testWard = null;
		testSupplier = null;
		testMedicalStock = null;
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testLotGets(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(false);
		checkLotIntoDb(code);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testLotSets(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		checkLotIntoDb(code);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMovementGets(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		checkMovementIntoDb(code);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMovementSets(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(true);
		checkMovementIntoDb(code);
	}

	void testMedicalStockGets() throws Exception {
		int code = setupTestMedicalStock(false);
		checkMedicalStockIntoDb(code);
	}

	void testMedicalStockSets() throws Exception {
		int code = setupTestMedicalStock(true);
		checkMedicalStockIntoDb(code);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetMedicalsFromLot(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Integer> medicalIds = medicalStockIoOperation.getMedicalsFromLot(foundMovement.getLot().getCode());
		assertThat(medicalIds.get(0)).isEqualTo(foundMovement.getMedical().getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetLotsByMedical(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(foundMovement.getMedical());
		assertThat(lots).hasSize(1);
		assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetLotsByMedicalEmptyLot(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();

		foundMovement.setQuantity(0);
		movementIoOperationRepository.saveAndFlush(foundMovement);

		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(foundMovement.getMedical());
		assertThat(lots).isEmpty();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoNewAutomaticDischargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		medicalStockIoOperation.newAutomaticDischargingMovement(movement);
		List<Movement> movementsByRefNo = medicalStockIoOperation.getMovementsByReference(movement.getRefNo());
		assertThat(movementsByRefNo).hasSize(2);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoNewAutomaticDischargingMovementLotQuantityLessMovementQuantity(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		movement.getLot().setMainStoreQuantity(10);
		movement.setQuantity(100);
		medicalStockIoOperation.newAutomaticDischargingMovement(movement);
		List<Movement> movementsByRefNo = medicalStockIoOperation.getMovementsByReference(movement.getRefNo());
		assertThat(movementsByRefNo).hasSize(2);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoNewAutomaticDischargingMovementDifferentLots(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();

		Medical medical = foundMovement.getMedical();
		MovementType movementType = foundMovement.getType();
		Ward ward = foundMovement.getWard();
		Supplier supplier = foundMovement.getSupplier();
		Lot lot2 = testLot.setup(medical, false); // we are going to create a second lot
		lot2.setCode("second");
		Movement newMovement = new Movement(
						medical,
						movementType,
						null,
						lot2,
						TimeTools.getNow(),
						7, // new lot with 10 quantity
						supplier,
						"newReference");
		medicalStockIoOperation.newMovement(newMovement);

		MovementType dischargeMovementType = testMovementType.setup(false); // prepare discharge movement
		dischargeMovementType.setCode("discharge");
		dischargeMovementType.setType("-");
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(dischargeMovementType);

		Movement dischargeMovement = new Movement(
						medical,
						dischargeMovementType,
						ward,
						null, // automatic lot selection
						TimeTools.getNow(),
						15, // quantity of 15 should use first lot of 10 + second lot of 5
						null,
						"newReference2");
		boolean automaticLotMode = GeneralData.AUTOMATICLOT_OUT;
		GeneralData.AUTOMATICLOT_OUT = true;
		medicalStockIoOperation.newAutomaticDischargingMovement(dischargeMovement);
		GeneralData.AUTOMATICLOT_OUT = automaticLotMode;

		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(medical);
		assertThat(lots).hasSize(1); // first lot should be 0 quantity and stripped by the list
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoNewMovementOutGoingLots(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		Medical medical = movement.getMedical();
		MovementType medicalType = movement.getType();
		medicalType.setType("-");
		Ward ward = movement.getWard();
		Supplier supplier = movement.getSupplier();
		Lot lot = movement.getLot();
		Movement newMovement = new Movement(
						medical,
						medicalType,
						ward,
						lot,
						TimeTools.getNow(),
						10, // new lot with 10 quantity
						supplier,
						"newReference");
		Movement storedMovement = medicalStockIoOperation.newMovement(newMovement);
		Movement foundMovement = movementIoOperationRepository.findById(storedMovement.getCode()).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(foundMovement.getQuantity()).isEqualTo(10);
		assertThat(foundMovement.getRefNo()).isEqualTo("newReference");
		assertThat(foundMovement.getType().getType()).isEqualTo("-");
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoNewMovementOutGoingLotsWardNull(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		Medical medical = movement.getMedical();
		MovementType medicalType = movement.getType();
		medicalType.setType("-");
		Supplier supplier = movement.getSupplier();
		Lot lot = movement.getLot();
		Movement newMovement = new Movement(
						medical,
						medicalType,
						null,
						lot,
						TimeTools.getNow(),
						10, // new lot with 10 quantity
						supplier,
						"newReference");
		Movement storedMovement = medicalStockIoOperation.newMovement(newMovement);
		Movement foundMovement = movementIoOperationRepository.findById(storedMovement.getCode()).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(foundMovement.getQuantity()).isEqualTo(10);
		assertThat(foundMovement.getRefNo()).isEqualTo("newReference");
		assertThat(foundMovement.getType().getType()).isEqualTo("-");
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoNewMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		Movement newMovement = medicalStockIoOperation.newMovement(foundMovement);
		checkMovementIntoDb(newMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoNewMovementAutomaticLotMode(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		boolean automaticLotMode = GeneralData.AUTOMATICLOT_IN;
		GeneralData.AUTOMATICLOT_IN = true;
		Movement newMovement = medicalStockIoOperation.newMovement(foundMovement);
		checkMovementIntoDb(newMovement.getCode());
		GeneralData.AUTOMATICLOT_IN = automaticLotMode;
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoNewMovementUpdateMedicalWardQuantityMedicalWardFound(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		MovementType movementType = movement.getType();
		movementType.setType("-");
		MedicalWard medicalWard = new MedicalWard(movement.getWard(), movement.getMedical(), 0, 0, movement.getLot());
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
		Movement newMovement = new Movement(movement.getMedical(), movementType, movement.getWard(), movement.getLot(),
						TimeTools.getNow(), 10, movement.getSupplier(), "newReference");
		Movement storedMovement = medicalStockIoOperation.newMovement(newMovement);
		Movement foundMovement = movementIoOperationRepository.findById(storedMovement.getCode()).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(foundMovement.getQuantity()).isEqualTo(10);
		assertThat(foundMovement.getRefNo()).isEqualTo("newReference");
		assertThat(foundMovement.getType().getType()).isEqualTo("-");
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoPrepareChargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		Movement preparedMovement = medicalStockIoOperation.prepareChargingMovement(foundMovement);
		checkMovementIntoDb(preparedMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoPrepareDischargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		Movement preparedMovement = medicalStockIoOperation.prepareDischargingMovement(foundMovement);
		checkMovementIntoDb(preparedMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoLotExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(false);
		boolean result = medicalStockIoOperation.lotExists(code);
		assertThat(result).isTrue();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetMovements(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = medicalStockIoOperation.getMovements();
		assertThat(movements).isNotEmpty();
		assertThat(foundMovement.getCode()).isEqualTo(movements.get(0).getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetMovementsWithParameters(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = medicalStockIoOperation.getMovements(
						foundMovement.getWard().getCode(),
						fromDate,
						toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetMovementsWihAllParameters(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
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

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetMovementForPrintDateOrder(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		MovementOrder order = MovementOrder.DATE;
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = medicalStockIoOperation.getMovementForPrint(
						foundMovement.getMedical().getDescription(),
						foundMovement.getMedical().getType().getCode(),
						foundMovement.getWard().getCode(),
						foundMovement.getType().getCode(),
						fromDate,
						toDate,
						foundMovement.getLot().getCode(),
						order);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetMovementForPrintWardOrderateWard(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		MovementOrder order = MovementOrder.WARD;
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = medicalStockIoOperation.getMovementForPrint(
						foundMovement.getMedical().getDescription(),
						foundMovement.getMedical().getType().getCode(),
						foundMovement.getWard().getCode(),
						foundMovement.getType().getCode(),
						fromDate,
						toDate,
						foundMovement.getLot().getCode(),
						order);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetMovementForPrintPharmaceuticalTypeOrder(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		MovementOrder order = MovementOrder.PHARMACEUTICAL_TYPE;
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = medicalStockIoOperation.getMovementForPrint(
						foundMovement.getMedical().getDescription(),
						foundMovement.getMedical().getType().getCode(),
						foundMovement.getWard().getCode(),
						foundMovement.getType().getCode(),
						fromDate,
						toDate,
						foundMovement.getLot().getCode(),
						order);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetMovementForPrintTypeOrder(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		MovementOrder order = MovementOrder.TYPE;
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = medicalStockIoOperation.getMovementForPrint(
						foundMovement.getMedical().getDescription(),
						foundMovement.getMedical().getType().getCode(),
						foundMovement.getWard().getCode(),
						foundMovement.getType().getCode(),
						fromDate,
						toDate,
						foundMovement.getLot().getCode(),
						order);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetLastMovementDate(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		setupTestMovement(false);
		List<Movement> movements = medicalStockIoOperation.getMovements();
		LocalDateTime gc = medicalStockIoOperation.getLastMovementDate();
		assertThat(gc).isEqualTo(movements.get(0).getDate());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoRefNoExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		boolean result = medicalStockIoOperation.refNoExists(foundMovement.getRefNo());
		assertThat(result).isTrue();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoGetMovementsByReference(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = medicalStockIoOperation.getMovementsByReference(foundMovement.getRefNo());
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetMovements(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = movBrowserManager.getMovements();
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetMovementsWithParameters(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = movBrowserManager.getMovements(foundMovement.getWard().getCode(), fromDate, toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetMovementsByReference(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = movBrowserManager.getMovementsByReference("notThere");
		assertThat(movements).isEmpty();
		movements = movBrowserManager.getMovementsByReference(foundMovement.getRefNo());
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetMovementsWihAllParameters(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
						foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, toDate, fromDate, toDate, fromDate, toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetMovementsCheckMovParameters() {
		assertThatThrownBy(() -> {
			LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
			LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
			int code = setupTestMovement(false);
			Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(foundMovement).isNotNull();
			movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
							foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, null, fromDate, toDate, fromDate, toDate);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetMovementsCheckLotPrepParameters() {
		assertThatThrownBy(() -> {
			LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
			LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
			int code = setupTestMovement(false);
			Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(foundMovement).isNotNull();
			movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
							foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, toDate, null, toDate, fromDate, toDate);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetMovementsCheckLotDueParameters() {
		assertThatThrownBy(() -> {
			LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
			LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
			int code = setupTestMovement(false);
			Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(foundMovement).isNotNull();
			movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
							foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, toDate, fromDate, toDate, fromDate, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetMovementsWihAllParametersNull(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = movBrowserManager.getMovements(null, null, foundMovement.getWard().getCode(), null,
						null, null, null, null, null, null);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetLotsByMedical(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Lot> lots = movStockInsertingManager.getLotByMedical(foundMovement.getMedical());
		assertThat(lots).hasSize(1);
		assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetLotsByMedicalNull(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		assertThat(movStockInsertingManager.getLotByMedical(null)).isEmpty();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrAlertCriticalQuantityUnderLimit(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(movStockInsertingManager.alertCriticalQuantity(foundMovement.getMedical(), 100)).isTrue();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrAlertCriticalQuantityOverLimit(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(movStockInsertingManager.alertCriticalQuantity(foundMovement.getMedical(), -50)).isFalse();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrGetLastMovementDate(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		setupTestMovement(false);
		List<Movement> movements = medicalStockIoOperation.getMovements();
		LocalDateTime localDateTimea = movStockInsertingManager.getLastMovementDate();
		assertThat(localDateTimea).isEqualTo(movements.get(0).getDate());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrRefNoExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(movStockInsertingManager.refNoExists(foundMovement.getRefNo())).isTrue();
		assertThat(movStockInsertingManager.refNoExists("notThere")).isFalse();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrPrepareChargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		List<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		List<Movement> inserted = movStockInsertingManager.newMultipleChargingMovements(movements, "refNo");
		assertThat(inserted).hasSize(1);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrPrepareChargingMovementBadRefNumber() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.setRefNo(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHDataValidationException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrPrepareDischargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		List<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		List<Movement> newDischarging = movStockInsertingManager.newMultipleDischargingMovements(movements, "refNo");
		assertThat(newDischarging).hasSize(1);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrPrepareDischargingMovementBadRefNumber() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.setRefNo(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleDischargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrPrepareDischargingMovementIsAutomaticLotOut(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		List<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		boolean automaticlotOut = GeneralData.AUTOMATICLOT_OUT;
		GeneralData.AUTOMATICLOT_OUT = true;
		List<Movement> inserting = movStockInsertingManager.newMultipleDischargingMovements(movements, "refNo");
		assertThat(inserting).hasSize(1);
		GeneralData.AUTOMATICLOT_OUT = automaticlotOut;
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrStoreLot(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		Lot storedLot = movStockInsertingManager.storeLot(movement.getLot().getCode(), movement.getLot(), movement.getMedical());
		checkLotIntoDb(storedLot.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateMovementMoveDateAfterToday() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.setDate(LocalDateTime.of(2099, 1, 1, 0, 0, 0));
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, "refNo");
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateMovementDateBeforeLastDate() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			LocalDateTime todayPlusAYear = LocalDateTime.of(TimeTools.getNow().getYear() + 2, 2, 2, 0, 0, 0, 0);
			movement.setDate(todayPlusAYear);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, "refNo");
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateReferenceNumber() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateNullSupplier() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.getType().setType("+");
			movement.setSupplier(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 3, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateNullWard() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.getType().setType("-");
			movement.setWard(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateQuantityZero() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.setQuantity(0);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 3, "Expecting three validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateNullMedical() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.setMedical(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 2, "Expecting two validation errors: "));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateNullMovementType() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.setType(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateLotRefersToAnotherMedical() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.getMedical().setCode(-99);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHServiceException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateLotWithCostZero() {
		assertThatThrownBy(() -> {
			boolean lotWithCost = GeneralData.LOTWITHCOST;
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			Lot lot = movement.getLot();
			lot.setCost(new BigDecimal(0.0));
			lotIoOperationRepository.saveAndFlush(lot);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			GeneralData.LOTWITHCOST = true;
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
			GeneralData.LOTWITHCOST = lotWithCost;
		})
						.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateLotCodeTooLong() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			Lot lot = movement.getLot();
			lot.setCode("thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong");
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class)
						.has(
										new Condition<Throwable>(
														e -> ((OHDataValidationException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateLotDueDateNull() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			Lot lot = movement.getLot();
			lot.setDueDate(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateLotPrepationDateNull() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			Lot lot = movement.getLot();
			lot.setPreparationDate(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMgrValidateLotPreparationDateAfterDueDate() {
		assertThatThrownBy(() -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			Lot lot = movement.getLot();
			lot.setPreparationDate(LocalDateTime.of(99, 1, 1, 0, 0, 0));
			lot.setDueDate(LocalDateTime.of(1, 1, 1, 0, 0, 0));
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
						.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testLotToString(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		Lot lot = lotIoOperationRepository.findById(code).orElse(null);
		assertThat(lot).isNotNull();
		lot.setCode(null);
		// TODO: if resource bundles are available this string test needs to change
		assertThat(lot).hasToString("angal.medicalstock.nolot.txt");
		lot.setCode(code);
		assertThat(lot).hasToString(code);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testLotIsValidLot(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		Lot lot = lotIoOperationRepository.findById(code).orElse(null);
		assertThat(lot).isNotNull();
		assertThat(lot.isValidLot()).isTrue();
		lot.setCode("thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong");
		assertThat(lot.isValidLot()).isFalse();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testLotEquals(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		Lot lot = lotIoOperationRepository.findById(code).orElse(null);
		assertThat(lot)
						.isEqualTo(lot)
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

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testLotHashCode() {
		Lot lot = new Lot("aCode", TimeTools.getNow(), TimeTools.getNow());
		int hashCode = lot.hashCode();
		assertThat(hashCode).isPositive();
		// use computed value
		assertThat(lot.hashCode()).isEqualTo(hashCode);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMovementToString(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		// TODO: if resource bundles are available this string test needs to change
		assertThat(movement).hasToString("angal.movement.tostring.fmt.txt");
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMovementEquals(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement)
						.isEqualTo(movement)
						.isNotNull()
						.isNotEqualTo("someString");

		Movement movement2 = new Movement();
		movement2.setCode(-99);
		assertThat(movement).isNotEqualTo(movement2);
		movement2.setCode(movement.getCode());
		assertThat(movement).isEqualTo(movement2);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMovementHashCode(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		int hashCode = movement.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + code);
		// use computed value
		assertThat(movement.hashCode()).isEqualTo(hashCode);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testMovementGetOrigin(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		assertThat(movement.getSupplier()).isEqualTo(movement.getOrigin());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testDeleteLastMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Optional<Movement> movement = movementIoOperationRepository.findById(code);
		assertThat(movement).isPresent();
		movBrowserManager.deleteLastMovement(movement.get());
		Optional<Movement> movement2 = movementIoOperationRepository.findById(code);
		assertThat(movement2).isNotPresent();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testDeleteLastMovementDenied(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		Medical medical = movement.getMedical();
		MovementType medicalType = movement.getType();
		medicalType.setType("-");
		medicalType = medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(medicalType);
		Ward ward = movement.getWard();
		Lot lot = movement.getLot();
		Movement newMovement = new Movement(
						medical,
						medicalType,
						ward,
						lot,
						TimeTools.getDateToday0(),
						10,
						null,
						"newReference");
		Movement storedMovement = medicalStockIoOperation.newMovement(newMovement);
		MovementWard movementWard = new MovementWard(TimeTools.getNow(), ward, lot, "newDescription", medical, 10.0, "newUnits");
		MovementWard saveMovWard = movementWardIoOperationRepository.saveAndFlush(movementWard);
		MovementWard foundMovWard = movementWardIoOperationRepository.findById(saveMovWard.getCode()).orElse(null);
		assertThat(foundMovWard).isNotNull();
		Movement lastMovement = movementIoOperationRepository.findById(storedMovement.getCode()).orElse(null);
		assertThat(lastMovement).isNotNull();
		assertThat(lastMovement.getType().getType()).isEqualTo("-");
		assertThat(lastMovement.getMedical()).isEqualTo(foundMovWard.getMedical());
		List<MovementWard> movWards = movementWardIoOperationRepository.findByWardMedicalAndLotAfterOrSameDate(ward.getCode(), medical.getCode(), lot.getCode(),
						storedMovement.getDate());
		assertThat(movWards).hasSizeGreaterThan(0);
		assertThrows(OHServiceException.class, () -> movBrowserManager.deleteLastMovement(lastMovement));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testDeleteLastMovementWithPriorMovements(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		Medical medical = movement.getMedical();
		MovementType medicalType = movement.getType();
		medicalType.setType("-");
		medicalType = medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(medicalType);
		Ward ward = movement.getWard();
		Lot lot = movement.getLot();
		Movement newMovement = new Movement(
						medical,
						medicalType,
						ward,
						lot,
						TimeTools.getDateToday0(),
						10,
						null,
						"newReference");
		Movement storedMovement = medicalStockIoOperation.newMovement(newMovement);
		MovementWard movementWard = new MovementWard(TimeTools.getDateToday0(), ward, lot, "newDescription", medical, 10.0, "newUnits");
		MovementWard saveMovWard = movementWardIoOperationRepository.saveAndFlush(movementWard);
		MovementWard foundMovWard = movementWardIoOperationRepository.findById(saveMovWard.getCode()).orElse(null);
		assertThat(foundMovWard).isNotNull();
		Movement lastMovement = movementIoOperationRepository.findById(storedMovement.getCode()).orElse(null);
		assertThat(lastMovement).isNotNull();
		assertThat(lastMovement.getType().getType()).isEqualTo("-");
		assertThat(lastMovement.getMedical()).isEqualTo(foundMovWard.getMedical());
		List<MovementWard> movWards = movementWardIoOperationRepository.findByWardMedicalAndLotAfterOrSameDate(ward.getCode(), medical.getCode(), lot.getCode(),
						storedMovement.getDate());
		assertThat(movWards).hasSizeGreaterThan(0);
		assertThrows(OHServiceException.class, () -> movBrowserManager.deleteLastMovement(lastMovement));

		Movement newMovement2 = new Movement(
						medical,
						medicalType,
						ward,
						lot,
						TimeTools.getNow(),
						10,
						null,
						"newReference2");
		Movement storedMovement2 = medicalStockIoOperation.newMovement(newMovement2);
		int code2 = storedMovement2.getCode();
		Optional<Movement> followingMovement = movementIoOperationRepository.findById(code2);
		assertThat(followingMovement).isPresent();
		Movement movement2 = followingMovement.get();
		movWards = movementWardIoOperationRepository.findByWardMedicalAndLotAfterOrSameDate(movement2.getWard().getCode(),
						movement2.getMedical().getCode(),
						movement2.getLot().getCode(),
						movement2.getDate());
		assertThat(movWards).isEmpty();
		movBrowserManager.deleteLastMovement(movement2);
		Optional<Movement> followingMovement2 = movementIoOperationRepository.findById(code2);
		assertThat(followingMovement2).isNotPresent();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoUpdateMedicalStockTableSameDate() throws Exception {
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		Medical medical = movement.getMedical();
		LocalDateTime dateTime = movement.getDate();
		int quantity = movement.getQuantity();
		int remainQuantity = quantity - quantity / 2; // to overcome tests with not even quantities

		MedicalStockIoOperations medicalStockIoOperation = new MedicalStockIoOperations(movementIoOperationRepository, lotIoOperationRepository,
						medicalsIoOperationRepository, medicalStockIoOperationRepository, medicalStockWardIoOperationRepository);

		Method method = medicalStockIoOperation.getClass().getDeclaredMethod("updateMedicalStockTable", Medical.class, LocalDate.class, int.class);
		method.setAccessible(true);
		assertThat((MedicalStock) method.invoke(medicalStockIoOperation, medical, dateTime.toLocalDate(), -quantity / 2)).extracting("balance")
						.isEqualTo(remainQuantity);
		assertThat((MedicalStock) method.invoke(medicalStockIoOperation, medical, dateTime.toLocalDate(), -remainQuantity)).extracting("balance")
						.isEqualTo(0);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoUpdateMedicalStockTableDifferentDate() throws Exception {
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		Medical medical = movement.getMedical();
		int quantity = movement.getQuantity();

		MedicalStockIoOperations medicalStockIoOperation = new MedicalStockIoOperations(movementIoOperationRepository, lotIoOperationRepository,
						medicalsIoOperationRepository, medicalStockIoOperationRepository, medicalStockWardIoOperationRepository);

		Method method = medicalStockIoOperation.getClass().getDeclaredMethod("updateMedicalStockTable", Medical.class, LocalDate.class, int.class);
		method.setAccessible(true);
		LocalDate newDate = LocalDate.now();
		int days = TimeTools.getDaysBetweenDates(movement.getDate().toLocalDate(), newDate, true);
		assertThat((MedicalStock) method.invoke(medicalStockIoOperation, medical, newDate, quantity)).extracting("balance").isEqualTo(quantity * 2);
		List<MedicalStock> medicalStockList = medicalStockIoOperationRepository.findByMedicalCodeOrderByBalanceDateDesc(medical.getCode());
		assertThat(medicalStockList.size()).isEqualTo(2);
		// previous record updated
		assertThat(medicalStockList.get(1).getBalance()).isEqualTo(quantity);
		assertThat(medicalStockList.get(1).getNextMovDate()).isEqualTo(newDate);
		assertThat(medicalStockList.get(1).getDays()).isEqualTo(days);
		// new record
		assertThat(medicalStockList.get(0).getNextMovDate()).isNull();
		assertThat(medicalStockList.get(0).getDays()).isNull();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoUpdateMedicalStockTableEmptyTable() throws Exception {

		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		LocalDate newDate = LocalDate.now();
		int quantity = 10;

		MedicalStockIoOperations medicalStockIoOperation = new MedicalStockIoOperations(movementIoOperationRepository, lotIoOperationRepository,
						medicalsIoOperationRepository, medicalStockIoOperationRepository, medicalStockWardIoOperationRepository);

		Method method = medicalStockIoOperation.getClass().getDeclaredMethod("updateMedicalStockTable", Medical.class, LocalDate.class, int.class);
		method.setAccessible(true);
		assertThat((MedicalStock) method.invoke(medicalStockIoOperation, medical, newDate, quantity)).extracting("balance").isEqualTo(quantity);

	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testIoUpdateMedicalStockTableEmptyTableWithNegativeQuantity() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			medicalTypeIoOperationRepository.saveAndFlush(medicalType);
			medicalsIoOperationRepository.saveAndFlush(medical);
			LocalDate newDate = LocalDate.now();
			int quantity = -10;

			MedicalStockIoOperations medicalStockIoOperation = new MedicalStockIoOperations(movementIoOperationRepository, lotIoOperationRepository,
							medicalsIoOperationRepository, medicalStockIoOperationRepository, medicalStockWardIoOperationRepository);

			Method method = medicalStockIoOperation.getClass().getDeclaredMethod("updateMedicalStockTable", Medical.class, LocalDate.class, int.class);
			method.setAccessible(true);
			try {
				method.invoke(medicalStockIoOperation, medical, newDate, quantity);
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof OHServiceException) {
					throw (OHServiceException) e.getCause();
				} else {
					throw e;
				}
			}
		})
						.isInstanceOf(OHServiceException.class);
	}

	private String setupTestLot(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		return lot.getCode();
	}

	private void checkLotIntoDb(String code) {
		Lot foundLot = lotIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLot).isNotNull();
		testLot.check(foundLot);
	}

	private int setupTestMovement(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		MovementType movementType = testMovementType.setup(false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(medical, false);
		Supplier supplier = testSupplier.setup(false);
		Movement movement = testMovement.setup(medical, movementType, ward, lot, supplier, usingSet);
		MedicalStock medicalStock = testMedicalStock.setup(movement);
		supplierIoOperationRepository.saveAndFlush(supplier);
		wardIoOperationRepository.saveAndFlush(ward);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		movementIoOperationRepository.saveAndFlush(movement);
		medicalStockIoOperationRepository.saveAndFlush(medicalStock);
		return movement.getCode();
	}

	private void checkMovementIntoDb(int code) {
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		testMovement.check(foundMovement);
	}

	private int setupTestMedicalStock(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		MedicalStock medicalStock = testMedicalStock.setup(medical, false);
		return medicalStock.getCode();
	}

	private void checkMedicalStockIntoDb(int code) {
		MedicalStock foundMedicalStock = medicalStockIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalStock).isNotNull();
		testMedicalStock.check(foundMedicalStock);
	}
}
