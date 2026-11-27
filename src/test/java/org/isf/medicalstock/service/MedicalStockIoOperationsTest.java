/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medicalstock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.TestMedicalStock;
import org.isf.medicalstock.TestMovement;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.MedicalStock;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.MedicalStockIoOperations.MovementOrder;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
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
class MedicalStockIoOperationsTest extends OHCoreTestCase {

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
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	MedicalStockWardIoOperationRepository medicalStockWardIoOperationRepository;
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

	@BeforeEach
	void setUp() throws OHException {
		cleanH2InMemoryDb();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void getMedicalsFromLot_shouldReturnMedicalCodeOfLotMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Integer> medicalIds = medicalStockIoOperation.getMedicalsFromLot(foundMovement.getLot().getCode());
		assertThat(medicalIds.get(0)).isEqualTo(foundMovement.getMedical().getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void getLotsByMedical_shouldReturnLotOfMedical_whenRemoveEmptyIsTrue(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(foundMovement.getMedical(), true);
		assertThat(lots).hasSize(1);
		assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void getLotsByMedical_shouldReturnEmpty_whenLotIsEmptyAndRemoveEmptyIsTrue(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();

		foundMovement.setQuantity(0);
		movementIoOperationRepository.saveAndFlush(foundMovement);

		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(foundMovement.getMedical(), true);
		assertThat(lots).isEmpty();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void newAutomaticDischargingMovement_shouldSaveOneMovement_whenOneLotCoversQuantity(boolean in, boolean out, boolean toward) throws Exception {
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
	void newAutomaticDischargingMovement_shouldThrow_whenQuantityExceedsMedicalTotalQuantity(boolean in, boolean out, boolean toward) throws Exception {
		assertThatThrownBy(() -> {
			setGeneralData(in, out, toward);
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			movement.getLot().setMainStoreQuantity(10);
			movement.setQuantity(100);
			medicalStockIoOperation.newAutomaticDischargingMovement(movement);
		})
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting one validation errors"));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void newAutomaticDischargingMovement_shouldLeaveOneLotWithStock_whenQuantitySpansTwoLots(boolean in, boolean out, boolean toward) throws Exception {
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

		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(medical, true);
		assertThat(lots).hasSize(1); // first lot should be 0 quantity and stripped by the list
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void newMovement_shouldSaveDischargingMovement(boolean in, boolean out, boolean toward) throws Exception {
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
	void newMovement_shouldSaveDischargingMovement_whenWardIsNull(boolean in, boolean out, boolean toward) throws Exception {
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
	void newMovement_shouldSaveChargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		Movement newMovement = medicalStockIoOperation.newMovement(foundMovement);
		checkMovementIntoDb(newMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void newMovement_shouldSaveChargingMovement_whenAutomaticLotInIsTrue(boolean in, boolean out, boolean toward) throws Exception {
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
	void newMovement_shouldSaveDischargingMovement_whenMedicalWardExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		MovementType movementType = movement.getType();
		movementType.setType("-");
		MedicalWard medicalWard = new MedicalWard(movement.getWard(), movement.getMedical(), 0, BigDecimal.ZERO, movement.getLot());
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
	void prepareChargingMovement_shouldSaveMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		Movement preparedMovement = medicalStockIoOperation.prepareChargingMovement(foundMovement);
		checkMovementIntoDb(preparedMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void prepareDischargingMovement_shouldSaveMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		Movement preparedMovement = medicalStockIoOperation.prepareDischargingMovement(foundMovement);
		checkMovementIntoDb(preparedMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void lotExists_shouldReturnTrue_whenCodeExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(false);
		boolean result = medicalStockIoOperation.lotExists(code);
		assertThat(result).isTrue();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void getMovements_shouldReturnSavedMovement(boolean in, boolean out, boolean toward) throws Exception {
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
	void getMovements_shouldReturnMovement_whenWardAndDateRangeMatch(boolean in, boolean out, boolean toward) throws Exception {
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
	void getMovements_shouldReturnMovement_whenAllFiltersMatch(boolean in, boolean out, boolean toward) throws Exception {
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
	void getMovementForPrint_shouldReturnMovement_whenOrderIsDate(boolean in, boolean out, boolean toward) throws Exception {
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
	void getMovementForPrint_shouldReturnMovement_whenOrderIsWard(boolean in, boolean out, boolean toward) throws Exception {
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
	void getMovementForPrint_shouldReturnMovement_whenOrderIsPharmaceuticalType(boolean in, boolean out, boolean toward) throws Exception {
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
	void getMovementForPrint_shouldReturnMovement_whenOrderIsType(boolean in, boolean out, boolean toward) throws Exception {
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
	void getLastMovementDate_shouldReturnDateOfLastMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		setupTestMovement(false);
		List<Movement> movements = medicalStockIoOperation.getMovements();
		LocalDateTime gc = medicalStockIoOperation.getLastMovementDate();
		assertThat(gc).isEqualTo(movements.get(0).getDate());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void refNoExists_shouldReturnTrue_whenRefNoExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		boolean result = medicalStockIoOperation.refNoExists(foundMovement.getRefNo());
		assertThat(result).isTrue();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void getMovementsByReference_shouldReturnMovement_whenRefNoExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = medicalStockIoOperation.getMovementsByReference(foundMovement.getRefNo());
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void updateMedicalStockTable_shouldUpdateBalance_whenDateIsSameAsLastBalanceDate() throws Exception {
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
	void updateMedicalStockTable_shouldInsertRecordAndUpdatePrevious_whenDateIsAfterLastBalanceDate() throws Exception {
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
	void updateMedicalStockTable_shouldReturnBalanceEqualToQuantity_whenMedicalHasNoStockRecord() throws Exception {

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
	void updateMedicalStockTable_shouldThrow_whenMedicalHasNoStockRecordAndQuantityIsNegative() throws Exception {
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
					throw e.getCause();
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

}
