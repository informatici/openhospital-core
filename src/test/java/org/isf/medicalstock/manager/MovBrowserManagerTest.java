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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import org.isf.medicalstock.service.MovementLogIoOperationRepository;
import org.isf.medicalstockward.model.MovementWard;
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
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MovBrowserManagerTest extends OHCoreTestCase {

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
	LotIoOperationRepository lotIoOperationRepository;
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
	MovementLogIoOperationRepository movementLogIoOperationRepository;
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
	void getMovements_shouldReturnSavedMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<Movement> movements = movBrowserManager.getMovements();
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
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
		List<Movement> movements = movBrowserManager.getMovements(foundMovement.getWard().getCode(), fromDate, toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void getMovementsByReference_shouldReturnMovementOnlyForExistingRefNo(boolean in, boolean out, boolean toward) throws Exception {
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
	void getMovements_shouldReturnMovement_whenAllFiltersMatch(boolean in, boolean out, boolean toward) throws Exception {
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
	void getMovements_shouldThrow_whenMovToIsNull() {
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
	void getMovements_shouldThrow_whenLotPrepFromIsNull() {
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
	void getMovements_shouldThrow_whenLotDueToIsNull() {
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
	void getMovements_shouldReturnSavedMovement_whenAllFiltersButWardAreNull(boolean in, boolean out, boolean toward) throws Exception {
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
	void deleteLastMovement_shouldRemoveChargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Optional<Movement> movement = movementIoOperationRepository.findById(code);
		assertThat(movement).isPresent();
		movBrowserManager.deleteLastMovement(movement.get(), null);
		Optional<Movement> movement2 = movementIoOperationRepository.findById(code);
		assertThat(movement2).isNotPresent();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void deleteLastMovement_shouldThrow_whenWardMovementFollows(boolean in, boolean out, boolean toward) throws Exception {
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
		MovementWard movementWard = new MovementWard(TimeTools.getNow(), ward, lot, "newDescription", medical, new BigDecimal("10.0"), "newUnits");
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
		assertThrows(OHServiceException.class, () -> movBrowserManager.deleteLastMovement(lastMovement, null));
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void deleteLastMovement_shouldRemoveMovementAndLogReason_whenNoWardMovementFollows(boolean in, boolean out, boolean toward) throws Exception {
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
		MovementWard movementWard = new MovementWard(TimeTools.getDateToday0(), ward, lot, "newDescription", medical, new BigDecimal("10.0"), "newUnits");
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
		assertThrows(OHServiceException.class, () -> movBrowserManager.deleteLastMovement(lastMovement, null));

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
		movBrowserManager.deleteLastMovement(movement2, "wrong entry");
		Optional<Movement> followingMovement2 = movementIoOperationRepository.findById(code2);
		assertThat(followingMovement2).isNotPresent();
		// OP-1388: the deletion must be logged together with the reason
		assertThat(movementLogIoOperationRepository.findAll())
			.anyMatch(movementLog -> movementLog.getMovementCode() == code2 && "wrong entry".equals(movementLog.getReason()));
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

}
