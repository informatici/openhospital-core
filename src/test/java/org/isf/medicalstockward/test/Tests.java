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
package org.isf.medicalstockward.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.isf.OHCoreTestCase;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicals.test.TestMedical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medicalstock.test.TestLot;
import org.isf.medicalstock.test.TestMovement;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MedicalWardId;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medicalstockward.service.MedicalStockWardIoOperations;
import org.isf.medicalstockward.service.MovementWardIoOperationRepository;
import org.isf.medstockmovtype.test.TestMovementType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.medtype.test.TestMedicalType;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.supplier.test.TestSupplier;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class Tests extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestWard testWard;
	private static TestMedicalWard testMedicalWard;
	private static TestPatient testPatient;
	private static TestMovementWard testMovementWard;
	private static TestMovement testMovement;
	private static TestMovementType testMovementType;
	private static TestSupplier testSupplier;
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
	SupplierIoOperationRepository supplierIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@BeforeClass
	public static void setUpClass() {
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testWard = new TestWard();
		testMedicalWard = new TestMedicalWard();
		testPatient = new TestPatient();
		testMovementWard = new TestMovementWard();
		testMovement = new TestMovement();
		testMovementType = new TestMovementType();
		testSupplier = new TestSupplier();
		testLot = new TestLot();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testMedicalWardGets() throws Exception {
		MedicalWardId id = _setupTestMedicalWard(false);
		_checkMedicalWardIntoDb(id);
	}

	@Test
	public void testMedicalWardSets() throws Exception {
		MedicalWardId id = _setupTestMedicalWard(true);
		_checkMedicalWardIntoDb(id);
	}

	@Test
	public void testMovementWardGets() throws Exception {
		int id = _setupTestMovementWard(false);
		_checkMovementWardIntoDb(id);
	}

	@Test
	public void testTotalQuantityShouldFindMovementWardByWardCodeAndDates() throws Exception {
		// given:
		int code = _setupTestMovementWard(false);
		MovementWard foundMovement = movementWardIoOperationRepository.findOne(code);
		DateTime startDate = new DateTime(foundMovement.getDate()).minusDays(1);
		DateTime endDate = new DateTime(foundMovement.getDate()).plusDays(1);

		// when:
		ArrayList<MovementWard> wardMovementsToWard = medicalStockWardIoOperations.getWardMovementsToWard(
				foundMovement.getWard().getCode(), startDate.toGregorianCalendar(), endDate.toGregorianCalendar());

		// then:
		assertThat(wardMovementsToWard).hasSize(1);
		assertThat(wardMovementsToWard.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testMovementWardSets() throws Exception {
		int id = _setupTestMovementWard(true);
		_checkMovementWardIntoDb(id);
	}

	@Test
	public void testIoGetWardMovements() throws Exception {
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar fromDate = new GregorianCalendar(now.get(Calendar.YEAR), 1, 1);
		GregorianCalendar toDate = new GregorianCalendar(now.get(Calendar.YEAR), 3, 3);
		int code = _setupTestMovementWard(false);
		MovementWard foundMovement = movementWardIoOperationRepository.findOne(code);
		ArrayList<MovementWard> movements = medicalStockWardIoOperations.getWardMovements(
				foundMovement.getWard().getCode(),
				fromDate,
				toDate);
		assertThat(movements.get(0).getCode()).isEqualTo(foundMovement.getCode());
	}

	@Test
	public void testIoGetCurrentQuantityInWard() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");
		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		boolean result = medicalStockWardIoOperations.newMovementWard(movementWard);
		Double quantity = (double) medicalStockWardIoOperations.getCurrentQuantityInWard(
				wardTo,
				medical);

		_checkMovementWardIntoDb(movementWard.getCode());
		assertThat(movementWard.getQuantity()).isEqualTo(quantity);
	}

	@Test
	public void testIoNewMovementWard() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(false);

		Ward wardTo = testWard.setup(false);
		wardTo.setCode("X");

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		wardIoOperationRepository.saveAndFlush(wardTo);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);

		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, wardTo, null, lot, false);
		boolean result = medicalStockWardIoOperations.newMovementWard(movementWard);
		Double quantity = (double) medicalStockWardIoOperations.getCurrentQuantityInWard(wardTo, medical);
		_checkMovementWardIntoDb(movementWard.getCode());
		assertThat(movementWard.getQuantity()).isEqualTo(quantity);
	}

	@Test
	public void testIoUpdateMovementWard() throws Exception {
		int code = _setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findOne(code);
		foundMovementWard.setDescription("Update");
		boolean result = medicalStockWardIoOperations.updateMovementWard(foundMovementWard);
		assertThat(result).isTrue();
		MovementWard updateMovementWard = movementWardIoOperationRepository.findOne(code);
		assertThat(updateMovementWard.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoDeleteMovementWard() throws Exception {
		int code = _setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findOne(code);
		boolean result = medicalStockWardIoOperations.deleteMovementWard(foundMovementWard);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoGetMedicalsWard() throws Exception {
		MedicalWardId code = _setupTestMedicalWard(false);
		MedicalWard foundMedicalWard = medicalStockWardIoOperationRepository.findOneWhereCodeAndMedical(code.getWard().getCode(), code.getMedical().getCode());
		ArrayList<MedicalWard> medicalWards = medicalStockWardIoOperations.getMedicalsWard(foundMedicalWard.getWard().getCode().charAt(0), true);
		assertThat(medicalWards.get(0).getQty()).isCloseTo(foundMedicalWard.getInQuantity() - foundMedicalWard.getOutQuantity(), offset(0.1));
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = _setupTestMovementWard(false);
		MovementWard found = movementWardIoOperationRepository.findOne(id);
		Patient mergedPatient = _setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		MovementWard result = movementWardIoOperationRepository.findOne(id);
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private Patient _setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private MedicalWardId _setupTestMedicalWard(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		lotIoOperationRepository.saveAndFlush(lot);
		MedicalWard medicalWard = testMedicalWard.setup(medical, ward, lot, usingSet);
		medicalStockWardIoOperationRepository.saveAndFlush(medicalWard);
		return medicalWard.getId();
	}

	private void _checkMedicalWardIntoDb(MedicalWardId id) throws OHException {
		MedicalWard foundMedicalWard = medicalStockWardIoOperationRepository.findOneWhereCodeAndMedical(id.getWard().getCode(), id.getMedical().getCode());
		testMedicalWard.check(foundMedicalWard);
	}

	private int _setupTestMovementWard(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);
		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, ward, ward, lot, usingSet);
		movementWardIoOperationRepository.saveAndFlush(movementWard);
		return movementWard.getCode();
	}

	private void _checkMovementWardIntoDb(int id) throws OHException {
		MovementWard foundMovementWard = movementWardIoOperationRepository.findOne(id);
		testMovementWard.check(foundMovementWard);
	}
}