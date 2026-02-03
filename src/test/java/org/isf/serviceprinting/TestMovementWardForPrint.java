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
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstockward.TestMovementWard;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.serviceprinting.print.MovementWardForPrint;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestMovementWardForPrint extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestWard testWard;
	private static TestPatient testPatient;
	private static TestMovementWard testMovementWard;
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
		testLot = new TestLot();
		testMovementWard = new TestMovementWard();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testMovementWardForPrint() throws Exception {
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
		assertThat(movementWardForPrints)
						.extracting(MovementWardForPrint::getCode)
						.containsExactly(0,0,0);
		assertThat(movementWardForPrints)
						.extracting(MovementWardForPrint::getQuantity)
						.containsExactly(46.0, 46.0, 46.0);
		assertThat(movementWardForPrints)
						.extracting(MovementWardForPrint::getLot)
						.containsExactly("123456", "123456", "123456");
		assertThat(movementWardForPrints)
						.extracting(MovementWardForPrint::getUnits)
						.containsExactly("TestUni", "TestUni", "TestUni");
		assertThat(movementWardForPrints)
						.extracting(MovementWardForPrint::getPatient)
						.containsExactly(false, false, false);

		assertThat(movementWardForPrints.get(0).compareTo(movementWardForPrints.get(1))).isEqualTo(1);
	}
}
