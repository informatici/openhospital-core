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
package org.isf.medicalstockward;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

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
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MedicalWardId;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medicalstockward.service.MedicalStockWardIoOperations;
import org.isf.medicalstockward.service.MovementWardIoOperationRepository;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
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
import org.springframework.context.ApplicationEventPublisher;

class Tests extends OHCoreTestCase {

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
	MovementIoOperationRepository movementIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

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
	void testMedicalWardGets() throws Exception {
		MedicalWardId id = setupTestMedicalWard(false);
		checkMedicalWardIntoDb(id);
	}

	@Test
	void testMedicalWardSets() throws Exception {
		MedicalWardId id = setupTestMedicalWard(true);
		checkMedicalWardIntoDb(id);
	}

	@Test
	void testMovementWardGets() throws Exception {
		int id = setupTestMovementWard(false);
		checkMovementWardIntoDb(id);
	}

	@Test
	// FIX: rephrase this test because makes no sense
	void testTotalQuantityShouldFindMovementWardByWardCodeAndDates() throws Exception {
		// given:
		int code = setupTestMovementWard(false);
		MovementWard foundMovement = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		LocalDateTime startDate = foundMovement.getDate().minusDays(1);
		LocalDateTime endDate = foundMovement.getDate().plusDays(1);

		// when:
		List<MovementWard> wardMovementsToWard = medicalStockWardIoOperations.getWardMovementsToWard(foundMovement.getWard().getCode(), startDate, endDate);

		// then:
		assertThat(wardMovementsToWard).hasSize(1);
		assertThat(wardMovementsToWard.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	void testMovementWardSets() throws Exception {
		int id = setupTestMovementWard(true);
		checkMovementWardIntoDb(id);
	}

	@Test
	void testIoGetWardMovements() throws Exception {
		LocalDateTime now = TimeTools.getNow();
		LocalDateTime fromDate = now.withMonth(1).withDayOfMonth(1);
		LocalDateTime toDate = now.withMonth(3).withDayOfMonth(3);
		int code = setupTestMovementWard(false);
		MovementWard foundMovement = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		List<MovementWard> movements = medicalStockWardIoOperations.getWardMovements(
						foundMovement.getWard().getCode(),
						fromDate,
						toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	void testIoGetCurrentQuantityInWard() throws Exception {
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

		medicalStockWardIoOperations.newMovementWard(movementWard);
		Double quantity = (double) medicalStockWardIoOperations.getCurrentQuantityInWard(wardTo, medical);

		checkMovementWardIntoDb(movementWard.getCode());
		assertThat(movementWard.getQuantity()).isEqualTo(quantity);
	}

	@Test
	void testIoGetCurrentQuantityInWardNoWard() throws Exception {
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

		medicalStockWardIoOperations.newMovementWard(movementWard);
		Double quantity = (double) medicalStockWardIoOperations.getCurrentQuantityInWard(null, medical);

		checkMovementWardIntoDb(movementWard.getCode());
		assertThat(movementWard.getQuantity()).isEqualTo(quantity);
	}

	@Test
	void testIoNewMovementWard() throws Exception {
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

		medicalStockWardIoOperations.newMovementWard(movementWard);
		checkMovementWardIntoDb(movementWard.getCode());
	}

	@Test
	void testIoNewMovementWardWithMedicalWardDefined() throws Exception {
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

		MedicalWard medicalWard = new MedicalWard(ward, medical, 10.0f, 5.0f, lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);

		MedicalWard medicalWardTo = new MedicalWard(wardTo, medical, 10.0f, 15.0f, lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWardTo);

		medicalStockWardIoOperations.newMovementWard(movementWard);
		checkMovementWardIntoDb(movementWard.getCode());
	}

	@Test
	void testIoNewMovementWardWithMedicalWardDefinedNoWardTo() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");
		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, null, ward, lot, false);

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		MedicalWard medicalWard = new MedicalWard(ward, medical, 10.0f, 5.0f, lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);

		MedicalWard medicalWardTo = new MedicalWard(wardTo, medical, 10.0f, 15.0f, lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWardTo);

		medicalStockWardIoOperations.newMovementWard(movementWard);
		checkMovementWardIntoDb(movementWard.getCode());
	}

	@Test
	void testIoNewMovementWardArrayList() throws Exception {
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

		medicalStockWardIoOperations.newMovementWard(movementWards);

		Double quantity = (double) medicalStockWardIoOperations.getCurrentQuantityInWard(wardTo, medical);
		checkMovementWardIntoDb(movementWard.getCode());
		assertThat(movementWard.getQuantity()).isEqualTo(quantity);
	}

	@Test
	void testIoNewMovementWardArrayListWardToNull() throws Exception {
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

		medicalStockWardIoOperations.newMovementWard(movementWards);
	}

	@Test
	void testIoUpdateMovementWard() throws Exception {
		int code = setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		foundMovementWard.setDescription("Update");
		MovementWard updatedMovementWard = medicalStockWardIoOperations.updateMovementWard(foundMovementWard);
		assertThat(updatedMovementWard.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoDeleteMovementWard() throws Exception {
		int code = setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		medicalStockWardIoOperations.deleteMovementWard(foundMovementWard);
		assertThat(movementWardIoOperationRepository.findById(code)).isNotPresent();
	}

	@Test
	void testIoGetMedicalsWard() throws Exception {
		MedicalWardId code = setupTestMedicalWard(false);
		MedicalWard foundMedicalWard = medicalStockWardIoOperationRepository.findOneWhereCodeAndMedical(code.getWard().getCode(), code.getMedical().getCode());
		List<MedicalWard> medicalWards = medicalStockWardIoOperations.getMedicalsWard(foundMedicalWard.getWard().getCode().charAt(0), true);
		assertThat(medicalWards.get(0).getQty()).isCloseTo(foundMedicalWard.getIn_quantity() - foundMedicalWard.getOut_quantity(), offset(0.1));
	}

	@Test
	void testIoGetMedicalsWardStripEmptyLots() throws Exception {
		MedicalWardId code = setupTestMedicalWard(false);
		MedicalWard medicalWard = medicalStockWardIoOperationRepository.findOneWhereCodeAndMedical(code.getWard().getCode(), code.getMedical().getCode());
		medicalWard.setIn_quantity(10.0f);
		medicalWard.setOut_quantity(10.0f);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
		List<MedicalWard> medicalWards = medicalStockWardIoOperations.getMedicalsWard(medicalWard.getWard().getCode().charAt(0), true);
		assertThat(medicalWards).isEmpty();
	}

	@Test
	void testIoGetWardMovementsToPatient() throws Exception {
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

		medicalStockWardIoOperations.newMovementWard(movementWard);

		List<MovementWard> movementWards = medicalStockWardIoOperations.getWardMovementsToPatient(patient.getCode());
		assertThat(movementWards).hasSize(1);
		assertThat(movementWards.get(0).getDescription()).isEqualTo(movementWard.getDescription());
	}

	@Test
	void testIoGetMedicalsWardTotalQuantity() throws Exception {
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

		medicalStockWardIoOperations.newMovementWard(movementWard);

		List<MedicalWard> medicalWards = medicalStockWardIoOperations.getMedicalsWardTotalQuantity('X');
		assertThat(medicalWards).hasSize(1);
		assertThat(medicalWards.get(0).getWard().getCode()).isEqualTo("X");
	}

	@Test
	void testIoListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		Patient mergedPatient = setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(foundMovementWard.getPatient(), mergedPatient));

		// then:
		MovementWard result = movementWardIoOperationRepository.findById(id).orElse(null);
		assertThat(result).isNotNull();
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	@Test
	void testMgrGetMedicalsWard() throws Exception {
		MedicalWardId code = setupTestMedicalWard(false);
		MedicalWard foundMedicalWard = medicalStockWardIoOperationRepository.findOneWhereCodeAndMedical(code.getWard().getCode(), code.getMedical().getCode());
		List<MedicalWard> medicalWards = movWardBrowserManager.getMedicalsWard(foundMedicalWard.getWard().getCode().charAt(0), true);
		assertThat(medicalWards.get(0).getQty()).isCloseTo(foundMedicalWard.getIn_quantity() - foundMedicalWard.getOut_quantity(), offset(0.1));
	}

	@Test
	void testMgrGetCurrentQuantityInWard() throws Exception {
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
		assertThat(movementWard.getQuantity()).isEqualTo(quantity);
	}

	@Test
	void testMgrGetMovementWardWithQualifiers() throws Exception {
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
	void testMgrGetWardMovementsToWard() throws Exception {
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
	void testMgrNewMovementWard() throws Exception {
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
	void testMgrNewMovementWardError() throws Exception {
		assertThatThrownBy(() -> movWardBrowserManager.newMovementWard(new ArrayList<>()))
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrNewMovementWardArrayList() throws Exception {
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
		assertThat(movementWard.getQuantity()).isEqualTo(quantity);
	}

	@Test
	void testMgrNewMovementWardArrayListWardToNull() throws Exception {
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
	void testMgrUpdateMovementWard() throws Exception {
		int code = setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		foundMovementWard.setDescription("Update");
		MovementWard updatedMovementWard = movWardBrowserManager.updateMovementWard(foundMovementWard);
		assertThat(updatedMovementWard.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrGetMovementToPatient() throws Exception {
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
	void testMgrGetMedicalsWardTotalQuantity() throws Exception {
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

		List<MedicalWard> medicalWards = movWardBrowserManager.getMedicalsWardTotalQuantity('X');
		assertThat(medicalWards).hasSize(1);
		assertThat(medicalWards.get(0).getWard().getCode()).isEqualTo("X");
	}

	@Test
	void testMgrConvertMovementWardForPrintByDate() throws Exception {
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
	void testMgrConvertMovementWardForPrintByMedical() throws Exception {
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
	void testMgrConvertMovementForPrintByDate() throws Exception {
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
	void testMgrConvertMovementForPrintByMedical() throws Exception {
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
	void testMgrConvertWardDrugs() throws Exception {
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
	void testMgrValidationDescriptionEmptyIsPatient() throws Exception {
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
	void testMgrValidationDescriptionEmptyNotIsPatient() throws Exception {
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
	void testMgrValidationMedicalNull() throws Exception {
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
	void testMedicalWardIdEquals() throws Exception {
		MedicalType medicalType1 = testMedicalType.setup(false);
		Medical medical1 = testMedical.setup(medicalType1, false);
		Ward ward1 = testWard.setup(false);
		Lot lot1 = testLot.setup(medical1, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType1);
		medicalsIoOperationRepository.saveAndFlush(medical1);
		wardIoOperationRepository.saveAndFlush(ward1);
		lotIoOperationRepository.saveAndFlush(lot1);
		MedicalWard medicalWard1 = testMedicalWard.setup(medical1, ward1, lot1, false);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard1);
		MedicalWardId medicalWardId1 = medicalWard1.getId();

		MedicalType medicalType2 = testMedicalType.setup(false);
		medicalType2.setCode("ABCDE");
		Medical medical2 = testMedical.setup(medicalType2, false);
		medical2.setDescription("Medical 2");
		Ward ward2 = testWard.setup(false);
		ward2.setCode("Y");
		ward2.setDescription("Ward 2");
		Lot lot2 = testLot.setup(medical2, false);
		lot2.setCode("abc");
		medicalTypeIoOperationRepository.saveAndFlush(medicalType2);
		medicalsIoOperationRepository.saveAndFlush(medical2);
		wardIoOperationRepository.saveAndFlush(ward2);
		lotIoOperationRepository.saveAndFlush(lot2);
		MedicalWard medicalWard2 = testMedicalWard.setup(medical2, ward2, lot2, false);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard2);
		MedicalWardId medicalWardId2 = medicalWard2.getId();

		assertThat(medicalWardId1).isEqualTo(medicalWardId1);
		assertThat(medicalWardId1)
						.isNotEqualTo("someString")
						.isNotNull();

		// medical doesn't match
		assertThat(medicalWardId1).isNotEqualTo(medicalWardId2);

		// ward doesn't match
		medicalWardId2.setMedical(medicalWardId1.getMedical());
		assertThat(medicalWardId1).isNotEqualTo(medicalWardId2);

		// lot doesn't match
		medicalWardId2.setWard(medicalWardId1.getWard());
		assertThat(medicalWardId1).isNotEqualTo(medicalWardId2);

		// everything matches
		medicalWardId2.setLot(medicalWardId1.getLot());
		assertThat(medicalWardId1).isEqualTo(medicalWardId2);
	}

	@Test
	void testMedicalWardIdHashCode() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(medical, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		lotIoOperationRepository.saveAndFlush(lot);
		MedicalWard medicalWard = testMedicalWard.setup(medical, ward, lot, false);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
		MedicalWardId medicalWardId = medicalWard.getId();

		assertThat(medicalWardId.hashCode()).isPositive();
	}

	@Test
	void testMovementWardConstructor() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);

		assertThat(new MovementWard(ward, LocalDateTime.of(1, 1, 1, 0, 0, 0), true, patient, 32, 150.0f, "description", medical, 100.0d, "kilo")).isNotNull();
	}

	@Test
	void testMovementWardConstructorWithLot() throws Exception {
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

		assertThat(new MovementWard(ward, LocalDateTime.of(1, 1, 1, 0, 0, 0), true, patient, 32, 150.0f, "description", medical, 100.0d, "kilo", lot))
						.isNotNull();
	}

	@Test
	void testMovementWardConstructorShort() throws Exception {
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

		assertThat(new MovementWard(TimeTools.getNow(), ward, lot, "description", medical, 100.0d, "kilo")).isNotNull();
	}

	@Test
	void testMovementWardEquals() throws Exception {
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

		MovementWard movementWard1 = new MovementWard(TimeTools.getNow(), ward, lot, "description", medical, 100.0d, "kilo");
		MovementWard movementWard2 = new MovementWard(TimeTools.getNow(), ward, lot, "description", medical, 100.0d, "kilo");
		movementWard2.setCode(-1);

		assertThat(movementWard1).isEqualTo(movementWard1);
		assertThat(movementWard1)
						.isNotEqualTo("someString")
						.isNotEqualTo(movementWard2);

		// set the codes equal
		movementWard2.setCode(movementWard1.getCode());
		assertThat(movementWard1).isEqualTo(movementWard2);
	}

	@Test
	void testMovementWardHashCode() throws Exception {

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

		MovementWard movementWard = new MovementWard(TimeTools.getNow(), ward, lot, "description", medical, 100.0d, "kilo");
		movementWard.setCode(1);

		// generate hashCode
		int hashCode = movementWard.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1);

		// use existing value
		assertThat(movementWard.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testMedicalWardConstructorWith2Parameters() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		Medical medical = testMedical.setup(medicalType, true);
		assertThat(new MedicalWard(medical, 10.0d)).isNotNull();
	}

	@Test
	void testMedicalWardConstructorWith3Parameters() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, true);
		Lot lot = testLot.setup(medical, true);
		MedicalWard medicalWard = new MedicalWard(medical, 10.0d, lot);
		assertThat(medicalWard.getLot()).isEqualTo(lot);
	}

	@Test
	void testMedicalWardSetId() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, true);
		Ward ward = testWard.setup(true);
		MedicalWard medicalWard = new MedicalWard(medical, 10.0d, lot);
		medicalWard.setId(ward, medical, lot);
		assertThat(medicalWard.getId()).isEqualTo(new MedicalWardId(ward, medical, lot));
	}

	@Test
	void testMedicalWardGetLot() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, true);
		Ward ward = testWard.setup(true);
		MedicalWard medicalWard = new MedicalWard(medical, 10.0d, lot);
		assertThat(medicalWard.getLot()).isEqualTo(lot);
	}

	@Test
	void testMedicalWardCompareTo() throws Exception {
		MedicalType medicalType1 = testMedicalType.setup(true);
		Medical medical1 = testMedical.setup(medicalType1, false);
		Lot lot1 = testLot.setup(medical1, true);
		Ward ward1 = testWard.setup(true);
		MedicalWard medicalWard1 = new MedicalWard(medical1, 10.0d, lot1);

		MedicalType medicalType2 = testMedicalType.setup(true);
		Medical medical2 = testMedical.setup(medicalType2, false);
		Lot lot2 = testLot.setup(medical2, true);
		lot2.setCode("second");
		Ward ward2 = testWard.setup(true);
		MedicalWard medicalWard2 = new MedicalWard(medical2, 10.0d, lot2);

		assertThat(medicalWard1.compareTo(1)).isZero();
		assertThat(medicalWard1.compareTo(medicalWard2)).isZero();

		medicalWard2.getMedical().setDescription("some new description");
		assertThat(medicalWard1.compareTo(medicalWard2)).isPositive();

		medicalWard1.getMedical().setDescription("some new description");
		medicalWard2.getMedical().setDescription("ZZZZZdescription");
		assertThat(medicalWard1.compareTo(medicalWard2)).isNegative();
	}

	@Test
	void testMedicalWardEquals() throws Exception {
		MedicalType medicalType1 = testMedicalType.setup(true);
		Medical medical1 = testMedical.setup(medicalType1, false);
		Lot lot1 = testLot.setup(medical1, true);
		Ward ward1 = testWard.setup(true);
		MedicalWard medicalWard1 = new MedicalWard(medical1, 10.0d, lot1);

		MedicalType medicalType2 = testMedicalType.setup(true);
		Medical medical2 = testMedical.setup(medicalType2, false);
		Lot lot2 = testLot.setup(medical2, true);
		lot2.setCode("second");
		Ward ward2 = testWard.setup(true);
		MedicalWard medicalWard2 = new MedicalWard(medical2, 10.0d, lot2);

		assertThat(medicalWard1)
						.isEqualTo(medicalWard1)
						.isNotNull()
						.isNotEqualTo("some String")
						.isNotEqualTo(medicalWard2);

		medicalWard2.setMedical(medicalWard1.getMedical());
		assertThat(medicalWard1).isEqualTo(medicalWard2);
	}

	@Test
	void testMedicalWardHashCode() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(medical, false);

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		lotIoOperationRepository.saveAndFlush(lot);
		MedicalWard medicalWard = testMedicalWard.setup(medical, ward, lot, false);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);

		int hashCode = medicalWard.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + medicalWard.getMedical().getCode());
		// get computed value
		assertThat(medicalWard.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testDeleteLastMovementWard() throws Exception {
		int code = setupTestMovementWardWithMedicalWard(false);
		Optional<MovementWard> lastMovementWard = movementWardIoOperationRepository.findById(code);
		assertThat(lastMovementWard).isPresent();
		movWardBrowserManager.deleteLastMovementWard(lastMovementWard.get());
		Optional<MovementWard> movement = movementWardIoOperationRepository.findById(code);
		assertThat(movement).isNotPresent();
	}

	@Test
	void testDeleteLastMovementWardDenied() throws Exception {
		int code = setupTestMovementWardWithMedicalWard(false);
		MovementWard firstmovementWard = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(firstmovementWard).isNotNull();
		Medical medical = firstmovementWard.getMedical();
		Ward ward = firstmovementWard.getWard();
		Lot lot = firstmovementWard.getLot();
		Patient patient = firstmovementWard.getPatient();
		int age = patient.getAge();
		LocalDateTime date = LocalDateTime.now();
		MovementWard secondMovementWard = new MovementWard(date, ward, lot, "newDescription", medical, 10.0, "newUnits");
		Ward wardTo = new Ward("C", "description", "telephone", "fax", "email", 5, 2, 1, false, false, true, true);
		wardIoOperationRepository.saveAndFlush(wardTo);
		secondMovementWard.setWardTo(wardTo);
		MovementWard thirdMovementWard = new MovementWard(date.plusMinutes(1), wardTo, lot, "newDescription", medical, -10.0, "newUnits");
		thirdMovementWard.setWardFrom(ward);
		MovementWard fourthMovementWard = new MovementWard(wardTo, date.plusMinutes(2), true, patient, age, 50.5f, "description", medical, 5.0, "units", null,
						null, lot);
		movementWardIoOperationRepository.saveAndFlush(secondMovementWard);
		movementWardIoOperationRepository.saveAndFlush(thirdMovementWard);
		movementWardIoOperationRepository.saveAndFlush(fourthMovementWard);
		assertThatThrownBy(() -> movWardBrowserManager.deleteLastMovementWard(firstmovementWard))
						.isInstanceOf(OHServiceException.class);
		List<MovementWard> latestMovementWardList = movementWardIoOperationRepository.findByWardMedicalAndLotAfterOrSameDate(wardTo.getCode(),
						medical.getCode(), lot.getCode(), secondMovementWard.getDate());
		assertThat(latestMovementWardList.size()).isEqualTo(2);
		assertThatThrownBy(() -> movWardBrowserManager.deleteLastMovementWard(secondMovementWard))
						.isInstanceOf(OHServiceException.class);
	}

	private Patient setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
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

	private void checkMedicalWardIntoDb(MedicalWardId id) throws OHException {
		MedicalWard foundMedicalWard = medicalStockWardIoOperationRepository.findOneWhereCodeAndMedical(id.getWard().getCode(), id.getMedical().getCode());
		testMedicalWard.check(foundMedicalWard);
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
		medicalWard.setOut_quantity(0);
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