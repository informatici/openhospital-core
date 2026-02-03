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

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstockward.TestMedicalWard;
import org.isf.medicalstockward.TestMovementWard;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.serviceprinting.print.MedicalWardForPrint;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestMedicalWardForPrint extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestWard testWard;
	private static TestMedicalWard testMedicalWard;
	private static TestPatient testPatient;
	private static TestMovementWard testMovementWard;
	private static TestLot testLot;

	@Autowired
	MedicalStockWardIoOperationRepository medicalStockWardIoOperationRepository;
	@Autowired
	MovWardBrowserManager movWardBrowserManager;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testWard = new TestWard();
		testMedicalWard = new TestMedicalWard();
		testPatient = new TestPatient();
		testLot = new TestLot();
		testMovementWard = new TestMovementWard();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testMedicalWardForPrint() throws Exception {
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

		assertThat(medicalWardForPrints.get(0).getWard().getDescription()).isEqualTo("Ward 3");
		assertThat(medicalWardForPrints.get(1).getCode()).isEqualTo("TP1");
		assertThat(medicalWardForPrints.get(2).getMedical().getDescription()).isEqualTo("the second description");
		assertThat(medicalWardForPrints.get(0).getQty()).isEqualTo(0.0D);
		assertThat(medicalWardForPrints.get(1).getPackets()).isZero();
	}

}
