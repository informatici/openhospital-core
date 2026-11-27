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
package org.isf.medicalstockward.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstockward.TestMedicalWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalWardIdTest extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestWard testWard;
	private static TestMedicalWard testMedicalWard;
	private static TestLot testLot;

	@Autowired
	MedicalStockWardIoOperationRepository medicalStockWardIoOperationRepository;
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
		testLot = new TestLot();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void equals_shouldCompareWardMedicalAndLot() throws Exception {
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
	void hashCode_shouldReturnPositiveValue() throws Exception {
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

}
