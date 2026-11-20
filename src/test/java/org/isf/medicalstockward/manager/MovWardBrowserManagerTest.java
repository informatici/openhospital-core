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
package org.isf.medicalstockward.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstockward.TestMedicalWard;
import org.isf.medicalstockward.TestMovementWard;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MedicalWardId;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medicalstockward.service.MedicalStockWardIoOperations;
import org.isf.medicalstockward.service.MovementWardIoOperationRepository;
import org.isf.medicalstockward.service.MovementWardLogIoOperationRepository;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.serviceprinting.print.MedicalWardForPrint;
import org.isf.serviceprinting.print.MovementForPrint;
import org.isf.serviceprinting.print.MovementWardForPrint;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MovWardBrowserManagerTest extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestWard testWard;
	private static TestMedicalWard testMedicalWard;
	private static TestPatient testPatient;
	private static TestMovementWard testMovementWard;
	private static TestMovementType testMovementType;
	private static TestSupplier testSupplier;
	private static TestLot testLot;

	@Autowired
	MedicalStockWardIoOperations medicalStockWardIoOperations;
	@Autowired
	MedicalStockWardIoOperationRepository medicalStockWardIoOperationRepository;
	@Autowired
	MovWardBrowserManager movWardBrowserManager;
	@Autowired
	MovementWardIoOperationRepository movementWardIoOperationRepository;
	@Autowired
	MovementWardLogIoOperationRepository movementWardLogIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testWard = new TestWard();
		testMedicalWard = new TestMedicalWard();
		testPatient = new TestPatient();
		testMovementWard = new TestMovementWard();
		testMovementType = new TestMovementType();
		testSupplier = new TestSupplier();
		testLot = new TestLot();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getMedicalsWard_shouldSetQtyToInMinusOutQuantity() throws Exception {
		MedicalWardId code = setupTestMedicalWard(false);
		MedicalWard foundMedicalWard = medicalStockWardIoOperationRepository.findOneWhereCodeAndMedical(code.getWard().getCode(), code.getMedical().getCode());
		List<MedicalWard> medicalWards = movWardBrowserManager.getMedicalsWard(foundMedicalWard.getWard().getCode(), true);
		assertThat(medicalWards.get(0).getQty())
			.isEqualByComparingTo(BigDecimal.valueOf(foundMedicalWard.getIn_quantity()).subtract(foundMedicalWard.getOut_quantity()));
	}

	@Test
	void newMovementWard_shouldSaveMovementWardAndAddQuantityToWardTo() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");
		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		movWardBrowserManager.newMovementWard(movementWard);
		Double quantity = (double) medicalStockWardIoOperations.getCurrentQuantityInWard(wardTo, medical);

		checkMovementWardIntoDb(movementWard.getCode());
		assertThat(movementWard.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(quantity));
	}

	@Test
	void getMovementWard_shouldReturnMovement_whenDateInRange() throws Exception {
		LocalDateTime now = TimeTools.getNow();
		LocalDateTime fromDate = LocalDateTime.of(now.getYear(), 1, 1, 0, 0, 0, 0);
		LocalDateTime toDate = LocalDateTime.of(now.getYear(), 3, 3, 0, 0, 0, 0);
		int code = setupTestMovementWard(false);
		MovementWard foundMovement = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<MovementWard> movements = movWardBrowserManager.getMovementWard(foundMovement.getWard().getCode(), fromDate, toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	void getWardMovementsToWard_shouldReturnMovement_whenDateInRange() throws Exception {
		int code = setupTestMovementWard(false);
		MovementWard foundMovement = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		LocalDateTime startDate = foundMovement.getDate().minusDays(1);
		LocalDateTime endDate = foundMovement.getDate().plusDays(1);
		List<MovementWard> wardMovementsToWard = movWardBrowserManager.getWardMovementsToWard(foundMovement.getWard().getCode(), startDate, endDate);
		assertThat(wardMovementsToWard).hasSize(1);
		assertThat(wardMovementsToWard.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	void newMovementWard_shouldSaveMovementWard() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");
		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		movWardBrowserManager.newMovementWard(movementWard);
		checkMovementWardIntoDb(movementWard.getCode());
	}

	@Test
	void newMovementWard_shouldThrow_whenTransferQuantityIsFractional() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");
		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);
		// A ward-to-ward transfer (wardTo set) must be a whole number: the destination ward is credited through
		// the integer MDSRWRD_IN_QTI column, so a fractional quantity would be truncated on the incoming side.
		movementWard.setQuantity(new BigDecimal("2.5"));

		assertThatThrownBy(() -> movWardBrowserManager.newMovementWard(movementWard))
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMovementWard_shouldThrow_whenListIsEmpty() throws Exception {
		assertThatThrownBy(() -> movWardBrowserManager.newMovementWard(new ArrayList<>()))
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMovementWard_shouldSaveMovementWardAndAddQuantityToWardTo_whenListIsGiven() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);

		List<MovementWard> movementWards = new ArrayList<>();
		movementWards.add(movementWard);

		movWardBrowserManager.newMovementWard(movementWards);

		Double quantity = (double) movWardBrowserManager.getCurrentQuantityInWard(wardTo, medical);
		checkMovementWardIntoDb(movementWard.getCode());
		assertThat(movementWard.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(quantity));
	}

	@Test
	void newMovementWard_shouldNotThrow_whenListIsGivenAndWardToIsNull() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, null, null, lot, false);

		List<MovementWard> movementWards = new ArrayList<>();
		movementWards.add(movementWard);

		movWardBrowserManager.newMovementWard(movementWards);
	}

	@Test
	void updateMovementWard_shouldSaveNewDescription() throws Exception {
		int code = setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		foundMovementWard.setDescription("Update");
		MovementWard updatedMovementWard = movWardBrowserManager.updateMovementWard(foundMovementWard);
		assertThat(updatedMovementWard.getDescription()).isEqualTo("Update");
	}

	@Test
	void getMovementToPatient_shouldReturnMovementsOfPatient() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);

		movWardBrowserManager.newMovementWard(movementWard);

		List<MovementWard> movementWards = movWardBrowserManager.getMovementToPatient(patient);
		assertThat(movementWards).hasSize(1);
		assertThat(movementWards.get(0).getDescription()).isEqualTo(movementWard.getDescription());
	}

	@Test
	void getMedicalsWardTotalQuantity_shouldReturnMedicalWardOfWard() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);

		movWardBrowserManager.newMovementWard(movementWard);

		List<MedicalWard> medicalWards = movWardBrowserManager.getMedicalsWardTotalQuantity("X");
		assertThat(medicalWards).hasSize(1);
		assertThat(medicalWards.get(0).getWard().getCode()).isEqualTo("X");
	}

	@Test
	void convertMovementWardForPrint_shouldSortByDateDescending() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);

		ward.setDescription("Ward 1");

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");
		wardTo.setDescription("Ward 2");

		Ward wardFrom = testWard.setup(false);
		wardFrom.setCode("Y");
		wardFrom.setDescription("Ward 3");

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		wardIoOperationRepository.saveAndFlush(wardFrom);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		MovementWard movementWard1 = testMovementWard.setup(ward, patient, medical, wardTo, wardFrom, lot, false);
		movementWard1.setDate(LocalDateTime.of(99, 1, 1, 0, 0, 0));
		MovementWard movementWard2 = testMovementWard.setup(wardTo, patient, medical, ward, wardFrom, lot, false);
		movementWard2.setDate(LocalDateTime.of(188, 1, 1, 0, 0, 0));
		MovementWard movementWard3 = testMovementWard.setup(wardFrom, patient, medical, wardTo, ward, lot, false);
		movementWard3.setDate(LocalDateTime.of(277, 11, 1, 0, 0, 0));

		List<MovementWard> movementWards = new ArrayList<>();
		movementWards.add(movementWard1);
		movementWards.add(movementWard2);
		movementWards.add(movementWard3);

		List<MovementWardForPrint> movementWardForPrints = movWardBrowserManager.convertMovementWardForPrint(movementWards);
		assertThat(movementWardForPrints)
						.extracting(MovementWardForPrint::getWard)
						.containsExactly("Ward 3", "Ward 2", "Ward 1");
	}

	@Test
	void convertMovementWardForPrint_shouldSortByMedical_whenDatesAreEqual() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical1 = testMedical.setup(medicalType, false);
		medical1.setDescription("Medical 1");
		Medical medical2 = testMedical.setup(medicalType, false);
		medical2.setDescription("Medical 2");
		Medical medical3 = testMedical.setup(medicalType, false);
		medical3.setDescription("Medical 3");
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical1, false);

		ward.setDescription("Ward 1");

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");
		wardTo.setDescription("Ward 2");

		Ward wardFrom = testWard.setup(false);
		wardFrom.setCode("Y");
		wardFrom.setDescription("Ward 3");

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical1);
		medicalsIoOperationRepository.saveAndFlush(medical2);
		medicalsIoOperationRepository.saveAndFlush(medical3);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		wardIoOperationRepository.saveAndFlush(wardFrom);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		MovementWard movementWard1 = testMovementWard.setup(ward, patient, medical1, wardTo, wardFrom, lot, false);
		MovementWard movementWard2 = testMovementWard.setup(wardTo, patient, medical2, ward, wardFrom, lot, false);
		MovementWard movementWard3 = testMovementWard.setup(wardFrom, patient, medical3, wardTo, ward, lot, false);

		List<MovementWard> movementWards = new ArrayList<>();
		movementWards.add(movementWard3);
		movementWards.add(movementWard2);
		movementWards.add(movementWard1);

		List<MovementWardForPrint> movementWardForPrints = movWardBrowserManager.convertMovementWardForPrint(movementWards);
		assertThat(movementWardForPrints)
						.extracting(MovementWardForPrint::getWard)
						.containsExactly("Ward 1", "Ward 2", "Ward 3");
	}

	@Test
	void convertMovementForPrint_shouldSortByDateDescending() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical1 = testMedical.setup(medicalType, false);
		medical1.setDescription("Medical 1");
		Medical medical2 = testMedical.setup(medicalType, false);
		medical2.setDescription("Medical 2");
		Medical medical3 = testMedical.setup(medicalType, false);
		medical3.setDescription("Medical 3");
		Patient patient = testPatient.setup(false);
		Lot lot1 = testLot.setup(medical1, false);
		Lot lot2 = testLot.setup(medical2, false);
		lot2.setCode("second");
		Lot lot3 = testLot.setup(medical3, false);
		lot3.setCode("third");

		MovementType movementType1 = testMovementType.setup(false);
		MovementType movementType2 = testMovementType.setup(false);
		movementType2.setCode("ABCDZZ");
		MovementType movementType3 = testMovementType.setup(false);
		movementType3.setCode("ZABCDZ");

		Ward ward = testWard.setup(false);
		ward.setDescription("Ward 1");

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");
		wardTo.setDescription("Ward 2");

		Ward wardFrom = testWard.setup(false);
		wardFrom.setCode("Y");
		wardFrom.setDescription("Ward 3");

		Supplier supplier = testSupplier.setup(false);

		Movement movement1 = new Movement(medical1, movementType1, ward, lot1, LocalDateTime.of(2, 2, 2, 0, 0, 0), 10, supplier, "TestRef");
		movement1.setDate(LocalDateTime.of(99, 1, 1, 0, 0, 0));
		Movement movement2 = new Movement(medical2, movementType2, wardTo, lot2, LocalDateTime.of(12, 2, 2, 0, 0, 0), 10, supplier, "TestRef");
		movement2.setDate(LocalDateTime.of(199, 1, 1, 0, 0, 0));
		Movement movement3 = new Movement(medical3, movementType3, wardFrom, lot3, LocalDateTime.of(22, 2, 2, 0, 0, 0), 10, supplier, "TestRef");
		movement3.setDate(LocalDateTime.of(299, 1, 1, 0, 0, 0));

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical1);
		medicalsIoOperationRepository.saveAndFlush(medical2);
		medicalsIoOperationRepository.saveAndFlush(medical3);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType1);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType2);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType3);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		wardIoOperationRepository.saveAndFlush(wardFrom);
		patientIoOperationRepository.saveAndFlush(patient);
		supplierIoOperationRepository.saveAndFlush(supplier);
		lotIoOperationRepository.saveAndFlush(lot1);
		lotIoOperationRepository.saveAndFlush(lot2);
		lotIoOperationRepository.saveAndFlush(lot3);

		List<Movement> movements = new ArrayList<>();
		movements.add(movement2);
		movements.add(movement1);
		movements.add(movement3);

		List<MovementForPrint> movementForPrints = movWardBrowserManager.convertMovementForPrint(movements);
		assertThat(movementForPrints)
						.extracting(MovementForPrint::getWard)
						.containsExactly("Ward 3", "Ward 2", "Ward 1");
	}

	@Test
	void convertMovementForPrint_shouldSortByMedical_whenDatesAreEqual() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical1 = testMedical.setup(medicalType, false);
		medical1.setDescription("Medical 1");
		Medical medical2 = testMedical.setup(medicalType, false);
		medical2.setDescription("Medical 2");
		Medical medical3 = testMedical.setup(medicalType, false);
		medical3.setDescription("Medical 3");
		Patient patient = testPatient.setup(false);
		Lot lot1 = testLot.setup(medical1, false);
		Lot lot2 = testLot.setup(medical2, false);
		lot2.setCode("second");
		Lot lot3 = testLot.setup(medical3, false);
		lot3.setCode("third");

		MovementType movementType1 = testMovementType.setup(false);
		MovementType movementType2 = testMovementType.setup(false);
		movementType2.setCode("ABCDZZ");
		MovementType movementType3 = testMovementType.setup(false);
		movementType3.setCode("ZABCDZ");

		Ward ward = testWard.setup(false);
		ward.setDescription("Ward 1");

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");
		wardTo.setDescription("Ward 2");

		Ward wardFrom = testWard.setup(false);
		wardFrom.setCode("Y");
		wardFrom.setDescription("Ward 3");

		Supplier supplier = testSupplier.setup(false);

		Movement movement1 = new Movement(medical1, movementType1, ward, lot1, LocalDateTime.of(2, 2, 2, 0, 0, 0), 10, supplier, "TestRef");
		movement1.setDate(LocalDateTime.of(99, 1, 1, 0, 0, 0));
		Movement movement2 = new Movement(medical2, movementType2, wardTo, lot2, LocalDateTime.of(12, 2, 2, 0, 0, 0), 10, supplier, "TestRef");
		movement2.setDate(LocalDateTime.of(99, 1, 1, 0, 0, 0));
		Movement movement3 = new Movement(medical3, movementType3, wardFrom, lot3, LocalDateTime.of(22, 2, 2, 0, 0, 0), 10, supplier, "TestRef");
		movement3.setDate(LocalDateTime.of(99, 1, 1, 0, 0, 0));

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical1);
		medicalsIoOperationRepository.saveAndFlush(medical2);
		medicalsIoOperationRepository.saveAndFlush(medical3);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType1);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType2);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType3);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		wardIoOperationRepository.saveAndFlush(wardFrom);
		patientIoOperationRepository.saveAndFlush(patient);
		supplierIoOperationRepository.saveAndFlush(supplier);
		lotIoOperationRepository.saveAndFlush(lot1);
		lotIoOperationRepository.saveAndFlush(lot2);
		lotIoOperationRepository.saveAndFlush(lot3);

		List<Movement> movements = new ArrayList<>();
		movements.add(movement2);
		movements.add(movement1);
		movements.add(movement3);

		List<MovementForPrint> movementForPrints = movWardBrowserManager.convertMovementForPrint(movements);
		assertThat(movementForPrints)
						.extracting(MovementForPrint::getWard)
						.containsExactly("Ward 1", "Ward 2", "Ward 3");
	}

	@Test
	void convertWardDrugs_shouldSortByMedicalDescription() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical1 = testMedical.setup(medicalType, false);
		medical1.setDescription("first description");
		Medical medical2 = testMedical.setup(medicalType, false);
		medical2.setDescription("the second description");
		Medical medical3 = testMedical.setup(medicalType, false);
		medical3.setDescription("a description");
		Lot lot1 = testLot.setup(medical1, false);
		Lot lot2 = testLot.setup(medical2, false);
		lot2.setCode("second");
		Lot lot3 = testLot.setup(medical3, false);
		lot3.setCode("third");

		Ward ward1 = testWard.setup(false);
		ward1.setDescription("Ward 1");

		Ward ward2 = testWard.setup(false);
		ward2.setCode("X");
		ward2.setDescription("Ward 2");

		Ward ward3 = testWard.setup(false);
		ward3.setCode("Y");
		ward3.setDescription("Ward 3");

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical1);
		medicalsIoOperationRepository.saveAndFlush(medical2);
		medicalsIoOperationRepository.saveAndFlush(medical3);
		wardIoOperationRepository.saveAndFlush(ward1);
		wardIoOperationRepository.saveAndFlush(ward2);
		wardIoOperationRepository.saveAndFlush(ward3);
		lotIoOperationRepository.saveAndFlush(lot1);
		lotIoOperationRepository.saveAndFlush(lot2);
		lotIoOperationRepository.saveAndFlush(lot3);
		MedicalWard medicalWard1 = testMedicalWard.setup(medical1, ward1, lot1, false);
		MedicalWard medicalWard2 = testMedicalWard.setup(medical2, ward2, lot2, false);
		MedicalWard medicalWard3 = testMedicalWard.setup(medical3, ward3, lot3, false);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard1);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard2);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard3);

		List<MedicalWard> medicalWards = new ArrayList<>();
		medicalWards.add(medicalWard2);
		medicalWards.add(medicalWard3);
		medicalWards.add(medicalWard1);

		List<MedicalWardForPrint> medicalWardForPrints = movWardBrowserManager.convertWardDrugs(ward3, medicalWards);
		assertThat(medicalWardForPrints).hasSize(3);
		assertThat(medicalWardForPrints.get(0).getMedical().getDescription()).isEqualTo("a description");
		assertThat(medicalWardForPrints.get(1).getMedical().getDescription()).isEqualTo("first description");
		assertThat(medicalWardForPrints.get(2).getMedical().getDescription()).isEqualTo("the second description");
	}

	@Test
	void newMovementWard_shouldThrow_whenDescriptionIsEmptyAndPatientFlagIsTrue() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(false);
			Lot lot = testLot.setup(medical, false);

			Ward wardTo = testWard.setup(false);
			wardTo.setCode("X");
			MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);

			medicalTypeIoOperationRepository.saveAndFlush(medicalType);
			medicalsIoOperationRepository.saveAndFlush(medical);
			wardIoOperationRepository.saveAndFlush(ward);
			wardIoOperationRepository.saveAndFlush(wardTo);
			patientIoOperationRepository.saveAndFlush(patient);
			lotIoOperationRepository.saveAndFlush(lot);

			movementWard.setDescription("");
			movementWard.setPatient(true);
			movWardBrowserManager.newMovementWard(movementWard);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMovementWard_shouldThrow_whenDescriptionIsEmptyAndPatientFlagIsFalse() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(false);
			Lot lot = testLot.setup(medical, false);

			Ward wardTo = testWard.setup(false);
			wardTo.setCode("X");
			MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);

			medicalTypeIoOperationRepository.saveAndFlush(medicalType);
			medicalsIoOperationRepository.saveAndFlush(medical);
			wardIoOperationRepository.saveAndFlush(ward);
			wardIoOperationRepository.saveAndFlush(wardTo);
			patientIoOperationRepository.saveAndFlush(patient);
			lotIoOperationRepository.saveAndFlush(lot);

			movementWard.setDescription("");
			movementWard.setPatient(false);
			movWardBrowserManager.newMovementWard(movementWard);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMovementWard_shouldThrow_whenMedicalIsNull() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(false);
			Lot lot = testLot.setup(medical, false);

			Ward wardTo = testWard.setup(false);
			wardTo.setCode("X");
			MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);

			medicalTypeIoOperationRepository.saveAndFlush(medicalType);
			medicalsIoOperationRepository.saveAndFlush(medical);
			wardIoOperationRepository.saveAndFlush(ward);
			wardIoOperationRepository.saveAndFlush(wardTo);
			patientIoOperationRepository.saveAndFlush(patient);
			lotIoOperationRepository.saveAndFlush(lot);

			movementWard.setMedical(null);
			movWardBrowserManager.newMovementWard(movementWard);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void deleteLastMovementWard_shouldRemoveMovementWardAndLogReason() throws Exception {
		int code = setupTestMovementWardWithMedicalWard(false);
		Optional<MovementWard> lastMovementWard = movementWardIoOperationRepository.findById(code);
		assertThat(lastMovementWard).isPresent();
		movWardBrowserManager.deleteLastMovementWard(lastMovementWard.get(), "wrong entry");
		Optional<MovementWard> movement = movementWardIoOperationRepository.findById(code);
		assertThat(movement).isNotPresent();
		// OP-1388: the deletion must be logged together with the reason
		assertThat(movementWardLogIoOperationRepository.findAll())
			.anyMatch(movementWardLog -> movementWardLog.getMovementWardCode() == code && "wrong entry".equals(movementWardLog.getReason()));
	}

	@Test
	void deleteLastMovementWard_shouldThrow_whenMovementIsNotLastOrMedicalWasUsedInWardTo() throws Exception {
		int code = setupTestMovementWardWithMedicalWard(false);
		MovementWard firstmovementWard = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(firstmovementWard).isNotNull();
		Medical medical = firstmovementWard.getMedical();
		Ward ward = firstmovementWard.getWard();
		Lot lot = firstmovementWard.getLot();
		Patient patient = firstmovementWard.getPatient();
		int age = patient.getAge();
		LocalDateTime date = LocalDateTime.now();
		MovementWard secondMovementWard = new MovementWard(date, ward, lot, "newDescription", medical, new BigDecimal("10.0"), "newUnits");
		Ward wardTo = new Ward("C", "description", "telephone", "fax", "email", 5, 2, 1, false, false, true, true);
		wardIoOperationRepository.saveAndFlush(wardTo);
		secondMovementWard.setWardTo(wardTo);
		MovementWard thirdMovementWard = new MovementWard(date.plusMinutes(1), wardTo, lot, "newDescription", medical, new BigDecimal("-10.0"), "newUnits");
		thirdMovementWard.setWardFrom(ward);
		MovementWard fourthMovementWard = new MovementWard(wardTo, date.plusMinutes(2), true, patient, age, 50.5f, "description",
						medical, new BigDecimal("5.0"), "units", null,
						null, lot);
		movementWardIoOperationRepository.saveAndFlush(secondMovementWard);
		movementWardIoOperationRepository.saveAndFlush(thirdMovementWard);
		movementWardIoOperationRepository.saveAndFlush(fourthMovementWard);
		assertThatThrownBy(() -> movWardBrowserManager.deleteLastMovementWard(firstmovementWard, null))
						.isInstanceOf(OHServiceException.class);
		List<MovementWard> latestMovementWardList = movementWardIoOperationRepository.findByWardMedicalAndLotAfterOrSameDate(wardTo.getCode(),
						medical.getCode(), lot.getCode(), secondMovementWard.getDate());
		assertThat(latestMovementWardList.size()).isEqualTo(2);
		assertThatThrownBy(() -> movWardBrowserManager.deleteLastMovementWard(secondMovementWard, null))
						.isInstanceOf(OHServiceException.class);
	}

	private MedicalWardId setupTestMedicalWard(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(medical, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		lotIoOperationRepository.saveAndFlush(lot);
		MedicalWard medicalWard = testMedicalWard.setup(medical, ward, lot, usingSet);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
		return medicalWard.getId();
	}

	private int setupTestMovementWard(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);
		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, ward, ward, lot, usingSet);
		movementWardIoOperationRepository.saveAndFlush(movementWard);
		return movementWard.getCode();
	}

	private int setupTestMovementWardWithMedicalWard(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);
		MedicalWard medicalWard = testMedicalWard.setup(medical, ward, lot, false);
		medicalWard.setOut_quantity(new BigDecimal("0"));
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, null, null, lot, usingSet);
		movementWardIoOperationRepository.saveAndFlush(movementWard);
		return movementWard.getCode();
	}

	private void checkMovementWardIntoDb(int id) throws OHException {
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		testMovementWard.check(foundMovementWard);
	}

}