/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 *
 * You should have received a copy of the GNU General License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.medicalstock;

import jakarta.validation.ValidationException;
import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
class Tests extends OHCoreTestCase {

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
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@DisplayName("Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@ParameterizedTest
	@MethodSource("automaticlot")
	@Retention(RetentionPolicy.RUNTIME)
	private @interface TestParameterized {

	}

	public static Stream<Arguments> automaticlot() {
		return Stream.of(
			Arguments.of(false, false, false),
			Arguments.of(false, false, true),
			Arguments.of(false, true, false),
			Arguments.of(false, true, true),
			Arguments.of(true, false, false),
			Arguments.of(true, false, true),
			Arguments.of(true, true, false),
			Arguments.of(true, true, true)
		);
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
	}

	@TestParameterized
	void testLotGets(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(false);
		checkLotIntoDb(code);
	}

	@TestParameterized
	void testLotSets(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		checkLotIntoDb(code);
	}

	@TestParameterized
	void testMovementGets(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		checkMovementIntoDb(code);
	}

	@TestParameterized
	void testMovementSets(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(true);
		checkMovementIntoDb(code);
	}

	@TestParameterized
	void testIoGetMedicalsFromLot(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Integer> medicalIds = medicalStockIoOperation.getMedicalsFromLot(foundMovement.getLot().getCode());
		assertThat(medicalIds.get(0)).isEqualTo(foundMovement.getMedical().getCode());
	}

	@TestParameterized
	void testIoGetLotsByMedical(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(foundMovement.getMedical());
		assertThat(lots).hasSize(1);
		assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
	}

	@TestParameterized
	void testIoGetLotsByMedicalEmptyLot(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();

		foundMovement.setQuantity(0);
		movementIoOperationRepository.saveAndFlush(foundMovement);

		List<Lot> lots = medicalStockIoOperation.getLotsByMedical(foundMovement.getMedical());
		assertThat(lots).isEmpty();
	}

	@TestParameterized
	void testIoNewAutomaticDischargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Movement> result = medicalStockIoOperation.newAutomaticDischargingMovement(foundMovement);
		assertThat(result).isNotEmpty();
	}

	@TestParameterized
	void testIoNewAutomaticDischargingMovementLotQuantityLessMovementQuantity(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		foundMovement.getLot().setMainStoreQuantity(10);
		foundMovement.setQuantity(100);
		assertThat(medicalStockIoOperation.newAutomaticDischargingMovement(foundMovement)).isNotNull();
	}

	@TestParameterized
	void testIoNewAutomaticDischargingMovementDifferentLots(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();

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
			TimeTools.getNow(),
			7, // new lot with 10 quantity
			supplier,
			"newReference");
		medicalStockIoOperation.newMovement(newMovement);

		MovementType dischargeMovementType = testMovementType.setup(false); //prepare discharge movement
		dischargeMovementType.setCode("discharge");
		dischargeMovementType.setType("-");
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(dischargeMovementType);

		Movement dischargeMovement = new Movement(
			medical,
			dischargeMovementType,
			ward,
			null, // automatic lot selection
			TimeTools.getNow(),
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

	@TestParameterized
	void testIoNewMovementOutGoingLots(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
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
			TimeTools.getNow(),
			10, // new lot with 10 quantitye
			supplier,
			"newReference");
		assertThat(medicalStockIoOperation.newMovement(newMovement)).isNotNull();
	}

	@TestParameterized
	void testIoNewMovementOutGoingLotsWardNull(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
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
			TimeTools.getNow(),
			10, // new lot with 10 quantitye
			supplier,
			"newReference");
		assertThat(medicalStockIoOperation.newMovement(newMovement)).isNotNull();
	}

	@TestParameterized
	void testIoNewMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		Movement result = medicalStockIoOperation.newMovement(foundMovement);
		assertThat(result).isNotNull();
	}

	@TestParameterized
	void testIoNewMovementAutomaticLotMode(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		boolean automaticLotMode = GeneralData.AUTOMATICLOT_IN;
		GeneralData.AUTOMATICLOT_IN = true;
		assertThat(medicalStockIoOperation.newMovement(foundMovement)).isNotNull();
		GeneralData.AUTOMATICLOT_IN = automaticLotMode;
	}

	@TestParameterized
	void testIoNewMovementUpdateMedicalWardQuantityMedicalWardFound(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).get();
		MovementType medicalType = movement.getType();
		medicalType.setType("-");
		MedicalWard medicalWard = new MedicalWard(movement.getWard(), movement.getMedical(), 0, 0, movement.getLot());
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
		Movement newMovement = new Movement(movement.getMedical(), medicalType, movement.getWard(), movement.getLot(),
			TimeTools.getNow(), 10, movement.getSupplier(), "newReference");
		assertThat(medicalStockIoOperation.newMovement(newMovement)).isNotNull();
	}

	@TestParameterized
	void testIoPrepareChargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		Movement result = medicalStockIoOperation.prepareChargingMovement(foundMovement);
		assertThat(result).isNotNull();
	}

	@TestParameterized
	void testIoPrepareDischargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		Movement result = medicalStockIoOperation.prepareDischargingMovement(foundMovement);
		assertThat(result).isNotNull();
	}

	@TestParameterized
	void testIoLotExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(false);
		boolean result = medicalStockIoOperation.lotExists(code);
		assertThat(result).isTrue();
	}

	@TestParameterized
	void testIoGetMovements(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = medicalStockIoOperation.getMovements();
		assertThat(movements).isNotEmpty();
		assertThat(foundMovement.getCode()).isEqualTo(movements.get(0).getCode());
	}

	@TestParameterized
	void testIoGetMovementsWithParameters(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = medicalStockIoOperation.getMovements(
			foundMovement.getWard().getCode(),
			fromDate,
			toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@TestParameterized
	void testIoGetMovementsWihAllParameters(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
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

	@TestParameterized
	void testIoGetMovementForPrintDateOrder(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		MovementOrder order = MovementOrder.DATE;
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
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

	@TestParameterized
	void testIoGetMovementForPrintWardOrderateWard(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		MovementOrder order = MovementOrder.WARD;
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
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

	@TestParameterized
	void testIoGetMovementForPrintPharmaceuticalTypeOrder(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		MovementOrder order = MovementOrder.PHARMACEUTICAL_TYPE;
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
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

	@TestParameterized
	void testIoGetMovementForPrintTypeOrder(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		MovementOrder order = MovementOrder.TYPE;
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
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

	@TestParameterized
	void testIoGetLastMovementDate(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		setupTestMovement(false);
		List<Movement> movements = medicalStockIoOperation.getMovements();
		LocalDateTime gc = medicalStockIoOperation.getLastMovementDate();
		assertThat(gc).isEqualTo(movements.get(0).getDate());
	}

	@TestParameterized
	void testIoRefNoExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		boolean result = medicalStockIoOperation.refNoExists(foundMovement.getRefNo());
		assertThat(result).isTrue();
	}

	@TestParameterized
	void testIoGetMovementsByReference(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = medicalStockIoOperation.getMovementsByReference(foundMovement.getRefNo());
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@TestParameterized
	void testMgrGetMovements(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = movBrowserManager.getMovements();
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@TestParameterized
	void testMgrGetMovementsWithParameters(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = movBrowserManager.getMovements(foundMovement.getWard().getCode(), fromDate, toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@TestParameterized
	void testMgrGetMovementsByReference(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = movBrowserManager.getMovementsByReference("notThere");
		assertThat(movements).isEmpty();
		movements = movBrowserManager.getMovementsByReference(foundMovement.getRefNo());
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@TestParameterized
	void testMgrGetMovementsWihAllParameters(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
			foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, toDate, fromDate, toDate, fromDate, toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@TestParameterized
	void testMgrGetMovementsCheckMovParameters(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
			LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
			int code = setupTestMovement(false);
			Movement foundMovement = movementIoOperationRepository.findById(code).get();
			movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
				foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, null, fromDate, toDate, fromDate, toDate);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
			);
	}

	@TestParameterized
	void testMgrGetMovementsCheckLotPrepParameters(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
			LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
			int code = setupTestMovement(false);
			Movement foundMovement = movementIoOperationRepository.findById(code).get();
			movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
				foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, toDate, null, toDate, fromDate, toDate);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
			);
	}

	@TestParameterized
	void testMgrGetMovementsCheckLotDueParameters(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
			LocalDateTime toDate = LocalDateTime.of(2000, 3, 3, 0, 0, 0);
			int code = setupTestMovement(false);
			Movement foundMovement = movementIoOperationRepository.findById(code).get();
			movBrowserManager.getMovements(foundMovement.getMedical().getCode(), foundMovement.getMedical().getType().getCode(),
				foundMovement.getWard().getCode(), foundMovement.getType().getCode(), fromDate, toDate, fromDate, toDate, fromDate, null);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
			);
	}

	@TestParameterized
	void testMgrGetMovementsWihAllParametersNull(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = movBrowserManager.getMovements(null, null, foundMovement.getWard().getCode(), null,
			null, null, null, null, null, null);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@TestParameterized
	void testMgrGetLotsByMedical(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		List<Lot> lots = movStockInsertingManager.getLotByMedical(foundMovement.getMedical());
		assertThat(lots).hasSize(1);
		assertThat(lots.get(0).getCode()).isEqualTo(foundMovement.getLot().getCode());
	}

	@TestParameterized
	void testMgrGetLotsByMedicalNull(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		assertThat(movStockInsertingManager.getLotByMedical(null)).isEmpty();
	}

	@TestParameterized
	void testMgrAlertCriticalQuantityUnderLimit(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		assertThat(movStockInsertingManager.alertCriticalQuantity(foundMovement.getMedical(), 100)).isTrue();
	}

	@TestParameterized
	void testMgrAlertCriticalQuantityOverLimit(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		assertThat(movStockInsertingManager.alertCriticalQuantity(foundMovement.getMedical(), -50)).isFalse();
	}

	@TestParameterized
	void testMgrGetLastMovementDate(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		setupTestMovement(false);
		List<Movement> movements = medicalStockIoOperation.getMovements();
		LocalDateTime LocalDateTime = movStockInsertingManager.getLastMovementDate();
		assertThat(LocalDateTime).isEqualTo(movements.get(0).getDate());
	}

	@TestParameterized
	void testMgrRefNoExists(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		assertThat(movStockInsertingManager.refNoExists(foundMovement.getRefNo())).isTrue();
		assertThat(movStockInsertingManager.refNoExists("notThere")).isFalse();
	}

	@TestParameterized
	void testMgrPrepareChargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		assertThat(movStockInsertingManager.newMultipleChargingMovements(movements, "refNo")).isNotEmpty();
	}

	@TestParameterized
	void testMgrPrepareChargingMovementBadRefNumber(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			movement.setRefNo(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHDataValidationException) e).getMessages().size() == 2), "Expecting two validation errors")
			);
	}

	@TestParameterized
	void testMgrPrepareDischargingMovement(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		assertThat(movStockInsertingManager.newMultipleDischargingMovements(movements, "refNo")).isNotEmpty();
	}

	@TestParameterized
	void testMgrPrepareDischargingMovementBadRefNumber(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			movement.setRefNo(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleDischargingMovements(movements, null);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
			);
	}

	@TestParameterized
	void testMgrPrepareDischargingMovementIsAutomaticLotOut(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).get();
		List<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		boolean isAutomaticLot_Out = GeneralData.AUTOMATICLOT_OUT;
		GeneralData.AUTOMATICLOT_OUT = true;
		assertThat(movStockInsertingManager.newMultipleDischargingMovements(movements, "refNo")).isNotEmpty();
		GeneralData.AUTOMATICLOT_OUT = isAutomaticLot_Out;
	}

	@TestParameterized
	void testMgrStoreLot(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).get();
		assertThat(movStockInsertingManager.storeLot(movement.getLot().getCode(), movement.getLot(), movement.getMedical())).isNotNull();
	}

	@TestParameterized
	void testMgrValidateMovementMoveDateAfterToday(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThrows((OHDataValidationException.class), () -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			movement.setDate(LocalDateTime.of(2099, 1, 1, 0, 0, 0));
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, "refNo");
		});
	}

	@TestParameterized
	void testMgrValidateMovementDateBeforeLastDate(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			LocalDateTime todayPlusAYear = LocalDateTime.of(TimeTools.getNow().getYear() + 2, 2, 2, 0, 0, 0, 0);
			movement.setDate(todayPlusAYear);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, "refNo");
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
			);
	}

	@TestParameterized
	void testMgrValidateReferenceNumber(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
			);
	}

	@TestParameterized
	void testMgrValidateNullSupplier(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			movement.getType().setType("+");
			movement.setSupplier(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHServiceException) e).getMessages().size() == 3), "Expecting two validation errors")
			);
	}

	@TestParameterized
	void testMgrValidateNullWard(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			movement.getType().setType("-");
			movement.setWard(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
			.isInstanceOf(OHDataValidationException.class);
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@TestParameterized
	void testMgrValidateQuantityZero(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			movement.setQuantity(0);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHServiceException) e).getMessages().size() == 3), "Expecting three validation errors")
			);
	}

	@TestParameterized
	void testMgrValidateNullMedical(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			movement.setMedical(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		}).isInstanceOf(OHDataValidationException.class);
	}

	@TestParameterized
	void testMgrValidateNullMovementType(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThrows((OHDataValidationException.class), () -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			movement.setType(null);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		});
	}

	@TestParameterized
	void testMgrValidateLotRefersToAnotherMedical(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			movement.getMedical().setCode(-99);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					(e -> ((OHServiceException) e).getMessages().size() == 2), "Expecting two validation errors")
			);
	}

	@TestParameterized
	void testMgrValidateLotWithCostZero(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThatThrownBy(() ->
		{
			boolean lotWithCost = GeneralData.LOTWITHCOST;
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
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

	@TestParameterized
	void testMgrValidateLotCodeTooLong(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThrows((OHDataValidationException.class), () -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			Lot lot = movement.getLot();
			lot.setCode("thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong");
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		});
	}

	@TestParameterized
	void testMgrValidateLotDueDateNull(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThrows((ValidationException.class), () -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			Lot lot = movement.getLot();
			lot.setDueDate(null);
			lotIoOperationRepository.saveAndFlush(lot);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		});

		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@TestParameterized
	void testMgrValidateLotPrepationDateNull(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThrows((ValidationException.class), () -> {

			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			Lot lot = movement.getLot();
			lot.setPreparationDate(null);
			lotIoOperationRepository.saveAndFlush(lot);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		});
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@TestParameterized
	void testMgrValidateLotPreparationDateAfterDueDate(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		assertThrows((OHDataValidationException.class), () -> {
			int code = setupTestMovement(false);
			Movement movement = movementIoOperationRepository.findById(code).get();
			Lot lot = movement.getLot();
			lot.setPreparationDate(LocalDateTime.of(99, 1, 1, 0, 0, 0));
			lot.setDueDate(LocalDateTime.of(1, 1, 1, 0, 0, 0));
			lotIoOperationRepository.saveAndFlush(lot);
			List<Movement> movements = new ArrayList<>(1);
			movements.add(movement);
			movStockInsertingManager.newMultipleChargingMovements(movements, null);
		});
		// NB: number of messages not checked because it varies dependent on GeneralData values
	}

	@TestParameterized
	void testLotToString(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		Lot lot = lotIoOperationRepository.findById(code).get();
		lot.setCode(null);
		// TODO: if resource bundles are available this string test needs to change
		assertThat(lot).hasToString("angal.medicalstock.nolot.txt");
		lot.setCode(code);
		assertThat(lot).hasToString(code);
	}

	@TestParameterized
	void testLotIsValidLot(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		Lot lot = lotIoOperationRepository.findById(code).get();
		assertThat(lot.isValidLot()).isTrue();
		lot.setCode("thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong");
		assertThat(lot.isValidLot()).isFalse();
	}

	@TestParameterized
	void testLotEquals(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		Lot lot = lotIoOperationRepository.findById(code).get();
		assertThat(lot).isEqualTo(lot);
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

	@TestParameterized
	void testLotHashCode(boolean in, boolean out, boolean toward) {
		setGeneralData(in, out, toward);
		Lot lot = new Lot("aCode", TimeTools.getNow(), TimeTools.getNow());
		int hashCode = lot.hashCode();
		assertThat(hashCode).isPositive();
		// use computed value
		assertThat(lot.hashCode()).isEqualTo(hashCode);
	}

	@TestParameterized
	void testMovementToString(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).get();
		// TODO: if resource bundles are available this string test needs to change
		assertThat(movement).hasToString("angal.movement.tostring.fmt.txt");
	}

	@TestParameterized
	void testMovementEquals(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).get();
		assertThat(movement).isEqualTo(movement);
		assertThat(movement)
			.isNotNull()
			.isNotEqualTo("someString");

		Movement movement2 = new Movement();
		movement2.setCode(-99);
		assertThat(movement).isNotEqualTo(movement2);
		movement2.setCode(movement.getCode());
		assertThat(movement).isEqualTo(movement2);
	}

	@TestParameterized
	void testMovementHashCode(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).get();
		int hashCode = movement.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + code);
		// use computed value
		assertThat(movement.hashCode()).isEqualTo(hashCode);
	}

	@TestParameterized
	void testMovementGetOrigin(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMovement(false);
		Movement movement = movementIoOperationRepository.findById(code).get();
		assertThat(movement.getSupplier()).isEqualTo(movement.getOrigin());
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
		Lot foundLot = lotIoOperationRepository.findById(code).get();
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
		supplierIoOperationRepository.saveAndFlush(supplier);
		wardIoOperationRepository.saveAndFlush(ward);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		movementIoOperationRepository.saveAndFlush(movement);
		return movement.getCode();
	}

	private void checkMovementIntoDb(int code) {
		Movement foundMovement = movementIoOperationRepository.findById(code).get();
		testMovement.check(foundMovement);
	}
}
