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
package org.isf.medicalstockward.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medicalstockward.TestMedicalWard;
import org.isf.medicalstockward.TestMovementWard;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MedicalWardId;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalStockWardIoOperationsTest extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestWard testWard;
	private static TestMedicalWard testMedicalWard;
	private static TestPatient testPatient;
	private static TestMovementWard testMovementWard;
	private static TestLot testLot;

	@Autowired
	MedicalStockWardIoOperations medicalStockWardIoOperations;
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
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	MovementIoOperationRepository movementIoOperationRepository;
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
		testLot = new TestLot();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	// FIX: rephrase this test because makes no sense
	void getWardMovementsToWard_shouldReturnMovement_whenDateInRange() throws Exception {
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
	void getWardMovements_shouldReturnMovement_whenDateInRange() throws Exception {
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
	void getCurrentQuantityInWard_shouldReturnQuantityMovedToWard() throws Exception {
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
		assertThat(movementWard.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(quantity));
	}

	@Test
	void getCurrentQuantityInWard_shouldReturnQuantityOfAllWards_whenWardIsNull() throws Exception {
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
		assertThat(movementWard.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(quantity));
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

		medicalStockWardIoOperations.newMovementWard(movementWard);
		checkMovementWardIntoDb(movementWard.getCode());
	}

	@Test
	void newMovementWard_shouldSaveMovementWard_whenMedicalWardsExist() throws Exception {
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

		MedicalWard medicalWard = new MedicalWard(ward, medical, 10, new BigDecimal("5.0"), lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);

		MedicalWard medicalWardTo = new MedicalWard(wardTo, medical, 10, new BigDecimal("15.0"), lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWardTo);

		medicalStockWardIoOperations.newMovementWard(movementWard);
		checkMovementWardIntoDb(movementWard.getCode());
	}

	@Test
	void newMovementWard_shouldSaveMovementWard_whenMedicalWardsExistAndWardToIsNull() throws Exception {
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

		MedicalWard medicalWard = new MedicalWard(ward, medical, 10, new BigDecimal("5.0"), lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);

		MedicalWard medicalWardTo = new MedicalWard(wardTo, medical, 10, new BigDecimal("15.0"), lot);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWardTo);

		medicalStockWardIoOperations.newMovementWard(movementWard);
		checkMovementWardIntoDb(movementWard.getCode());
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

		medicalStockWardIoOperations.newMovementWard(movementWards);

		Double quantity = (double) medicalStockWardIoOperations.getCurrentQuantityInWard(wardTo, medical);
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

		medicalStockWardIoOperations.newMovementWard(movementWards);
	}

	@Test
	void updateMovementWard_shouldSaveNewDescription() throws Exception {
		int code = setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		foundMovementWard.setDescription("Update");
		MovementWard updatedMovementWard = medicalStockWardIoOperations.updateMovementWard(foundMovementWard);
		assertThat(updatedMovementWard.getDescription()).isEqualTo("Update");
	}

	@Test
	void deleteMovementWard_shouldRemoveMovementWard() throws Exception {
		int code = setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		medicalStockWardIoOperations.deleteMovementWard(foundMovementWard);
		assertThat(movementWardIoOperationRepository.findById(code)).isNotPresent();
	}

	@Test
	void getMedicalsWard_shouldSetQtyToInMinusOutQuantity() throws Exception {
		MedicalWardId code = setupTestMedicalWard(false);
		MedicalWard foundMedicalWard = medicalStockWardIoOperationRepository.findOneWhereCodeAndMedical(code.getWard().getCode(), code.getMedical().getCode());
		List<MedicalWard> medicalWards = medicalStockWardIoOperations.getMedicalsWard(foundMedicalWard.getWard().getCode(), true);
		assertThat(medicalWards.get(0).getQty())
			.isEqualByComparingTo(BigDecimal.valueOf(foundMedicalWard.getIn_quantity()).subtract(foundMedicalWard.getOut_quantity()));
	}

	@Test
	void getMedicalsWard_shouldReturnEmpty_whenQtyIsZeroAndStripeEmptyIsTrue() throws Exception {
		MedicalWardId code = setupTestMedicalWard(false);
		MedicalWard medicalWard = medicalStockWardIoOperationRepository.findOneWhereCodeAndMedical(code.getWard().getCode(), code.getMedical().getCode());
		medicalWard.setIn_quantity(10);
		medicalWard.setOut_quantity(new BigDecimal("10.0"));
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
		List<MedicalWard> medicalWards = medicalStockWardIoOperations.getMedicalsWard(medicalWard.getWard().getCode(), true);
		assertThat(medicalWards).isEmpty();
	}

	@Test
	void getWardMovementsToPatient_shouldReturnMovementsOfPatient() throws Exception {
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

		medicalStockWardIoOperations.newMovementWard(movementWard);

		List<MedicalWard> medicalWards = medicalStockWardIoOperations.getMedicalsWardTotalQuantity("X");
		assertThat(medicalWards).hasSize(1);
		assertThat(medicalWards.get(0).getWard().getCode()).isEqualTo("X");
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

	private void checkMovementWardIntoDb(int id) throws OHException {
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		testMovementWard.check(foundMovementWard);
	}

}
