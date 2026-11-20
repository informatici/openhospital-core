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
package org.isf.medicalstock.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medicalstock.service.MovementIoOperationRepository;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MovStockInsertingManagerTest extends OHCoreTestCase {

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
	MovStockInsertingManager movStockInsertingManager;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
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
	void getLotByMedical_shouldReturnLotOfMedical_whenRemoveEmptyIsTrue(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Lot> lots = movStockInsertingManager.getLotByMedical(foundMovement.getMedical(), true);
		assertThat(lots).hasSize(1);
		assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void getLotByMedical_shouldReturnLotOfMedical_whenRemoveEmptyIsFalse(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Lot> lots = movStockInsertingManager.getLotByMedical(foundMovement.getMedical(), false);
		assertThat(lots).hasSize(1);
		assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void getLotByMedical_shouldReturnEmpty_whenMedicalIsNull(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		assertThat(movStockInsertingManager.getLotByMedical(null, true)).isEmpty();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void updateLot_shouldReturnLotsOfGivenList() throws Exception {
		String code1 = setupTestLot(false);
		Lot lot1 = lotIoOperationRepository.findById(code1).orElse(null);

		String code2 = setupTestLot(false);
		Lot lot2 = lotIoOperationRepository.findById(code2).orElse(null);
		String newCode = "456789";
		lot2.setCode(newCode);

		List<Lot> lots = Arrays.asList(lot1, lot2);
		List<Lot> updatedLots = movStockInsertingManager.updateLot(lots);

		// Assert the size and content of the updated list
		assertThat(updatedLots.size()).isEqualTo(2);
		assertThat(updatedLots).contains(lot1);
		assertThat(updatedLots).contains(lot2);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void alertCriticalQuantity_shouldReturnTrue_whenResidualIsBelowMinQty(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(movStockInsertingManager.alertCriticalQuantity(foundMovement.getMedical(), 100)).isTrue();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void alertCriticalQuantity_shouldReturnFalse_whenResidualIsAboveMinQty(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(movStockInsertingManager.alertCriticalQuantity(foundMovement.getMedical(), -50)).isFalse();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void getLastMovementDate_shouldReturnDateOfLastMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		setupTestMovement(false);
		List<Movement> movements = medicalStockIoOperation.getMovements();
		LocalDateTime localDateTimea = movStockInsertingManager.getLastMovementDate();
		assertThat(localDateTimea).isEqualTo(movements.get(0).getDate());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void refNoExists_shouldReturnTrueOnlyForExistingRefNo(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(movStockInsertingManager.refNoExists(foundMovement.getRefNo())).isTrue();
		assertThat(movStockInsertingManager.refNoExists("notThere")).isFalse();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void newMultipleChargingMovements_shouldReturnSavedMovement(boolean in, boolean out, boolean toward) throws Exception {
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
	void newMultipleChargingMovements_shouldThrow_whenRefNoIsNull() {
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
	void newMultipleDischargingMovements_shouldReturnSavedMovement(boolean in, boolean out, boolean toward) throws Exception {
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
	void newMultipleDischargingMovements_shouldThrow_whenRefNoIsNull() {
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
	void newMultipleDischargingMovements_shouldReturnSavedMovement_whenAutomaticLotOutIsTrue(boolean in, boolean out, boolean toward) throws Exception {
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
	void storeLot_shouldSaveLot(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(movement).isNotNull();
		Lot storedLot = movStockInsertingManager.storeLot(movement.getLot().getCode(), movement.getLot(), movement.getMedical());
		checkLotIntoDb(storedLot.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void newMultipleChargingMovements_shouldThrow_whenMovementDateIsInTheFuture() {
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
	void newMultipleChargingMovements_shouldThrow_whenMovementDateIsTwoYearsAhead() {
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
	void newMultipleChargingMovements_shouldThrow_whenRefNoAlreadyExists() {
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
	void newMultipleChargingMovements_shouldThrow_whenSupplierIsNull() {
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
	void newMultipleChargingMovements_shouldThrow_whenTypeIsDischargeAndWardIsNull() {
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
	void newMultipleChargingMovements_shouldThrow_whenQuantityIsZero() {
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
	void newMultipleChargingMovements_shouldThrow_whenMedicalIsNull() {
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
	void newMultipleChargingMovements_shouldThrow_whenTypeIsNull() {
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
	void newMultipleChargingMovements_shouldThrow_whenMedicalCodeIsChanged() {
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
	void newMultipleChargingMovements_shouldThrow_whenLotCostIsZeroAndLotWithCostIsTrue() {
		assertThatThrownBy(() -> {
			boolean lotWithCost = GeneralData.LOTWITHCOST;
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).orElse(null);
			assertThat(movement).isNotNull();
			Lot lot = movement.getLot();
			lot.setCost(BigDecimal.ZERO);
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
	void newMultipleChargingMovements_shouldThrow_whenLotCodeIsChanged() {
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
	void newMultipleChargingMovements_shouldThrow_whenRefNoAlreadyExistsAndLotDueDateIsNull() {
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
	void newMultipleChargingMovements_shouldThrow_whenRefNoAlreadyExistsAndLotPreparationDateIsNull() {
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
	void newMultipleChargingMovements_shouldThrow_whenRefNoAlreadyExistsAndLotPreparationDateIsAfterDueDate() {
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

	@Test
	void updateLot_shouldThrow_whenCostIsNegative() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, false);
		lot.setCost(new BigDecimal("-1"));
		assertThatThrownBy(() -> movStockInsertingManager.updateLot(lot))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void updateLot_shouldThrow_whenDueDateIsBeforePreparationDate() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, false);
		lot.setDueDate(lot.getPreparationDate().minusDays(1));
		assertThatThrownBy(() -> movStockInsertingManager.updateLot(lot))
			.isInstanceOf(OHDataValidationException.class);
	}

}
