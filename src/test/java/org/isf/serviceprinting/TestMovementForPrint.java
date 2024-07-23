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
package org.isf.serviceprinting;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstockward.TestMovementWard;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.serviceprinting.print.MovementForPrint;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestMovementForPrint  extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestWard testWard;
	private static TestPatient testPatient;
	private static TestMovementWard testMovementWard;
	private static TestMovementType testMovementType;
	private static TestSupplier testSupplier;
	private static TestLot testLot;

	@Autowired
	MovWardBrowserManager movWardBrowserManager;
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
		testPatient = new TestPatient();
		testMovementType = new TestMovementType();
		testSupplier = new TestSupplier();
		testLot = new TestLot();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testMovementForPrintByDate() throws Exception {
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
		assertThat(movementForPrints)
						.extracting(MovementForPrint::getLot)
						.containsExactly("third", "second", "123456");
		assertThat(movementForPrints)
						.extracting(MovementForPrint::getQuantity)
						.containsExactly(10D, 10D, 10D);

		assertThat(movementForPrints.get(0).toString()).isEqualTo("Medical 3");
		assertThat(movementForPrints.get(0).compareTo(movementForPrints.get(1))).isEqualTo(1);
	}
}
