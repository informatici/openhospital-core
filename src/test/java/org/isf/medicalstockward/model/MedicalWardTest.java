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

import java.math.BigDecimal;

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
import org.isf.utils.exception.OHException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalWardTest extends OHCoreTestCase {

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
	void constructor_shouldSetFields() throws Exception {
		MedicalWardId id = setupTestMedicalWard(false);
		checkMedicalWardIntoDb(id);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		MedicalWardId id = setupTestMedicalWard(true);
		checkMedicalWardIntoDb(id);
	}

	@Test
	void constructor_shouldCreateMedicalWard_whenMedicalAndQuantityGiven() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		Medical medical = testMedical.setup(medicalType, true);
		assertThat(new MedicalWard(medical, new BigDecimal("10"))).isNotNull();
	}

	@Test
	void constructor_shouldSetLot_whenMedicalQuantityAndLotGiven() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, true);
		Lot lot = testLot.setup(medical, true);
		MedicalWard medicalWard = new MedicalWard(medical, new BigDecimal("10"), lot);
		assertThat(medicalWard.getLot()).isEqualTo(lot);
	}

	@Test
	void setId_shouldSetWardMedicalAndLot() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, true);
		Ward ward = testWard.setup(true);
		MedicalWard medicalWard = new MedicalWard(medical, new BigDecimal("10"), lot);
		medicalWard.setId(ward, medical, lot);
		assertThat(medicalWard.getId()).isEqualTo(new MedicalWardId(ward, medical, lot));
	}

	@Test
	void getLot_shouldReturnLot() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, true);
		Ward ward = testWard.setup(true);
		MedicalWard medicalWard = new MedicalWard(medical, new BigDecimal("10"), lot);
		assertThat(medicalWard.getLot()).isEqualTo(lot);
	}

	@Test
	void compareTo_shouldCompareMedicalDescriptions() throws Exception {
		MedicalType medicalType1 = testMedicalType.setup(true);
		Medical medical1 = testMedical.setup(medicalType1, false);
		Lot lot1 = testLot.setup(medical1, true);
		Ward ward1 = testWard.setup(true);
		MedicalWard medicalWard1 = new MedicalWard(medical1, new BigDecimal("10"), lot1);

		MedicalType medicalType2 = testMedicalType.setup(true);
		Medical medical2 = testMedical.setup(medicalType2, false);
		Lot lot2 = testLot.setup(medical2, true);
		lot2.setCode("second");
		Ward ward2 = testWard.setup(true);
		MedicalWard medicalWard2 = new MedicalWard(medical2, new BigDecimal("10"), lot2);

		assertThat(medicalWard1.compareTo(1)).isZero();
		assertThat(medicalWard1.compareTo(medicalWard2)).isZero();

		medicalWard2.getMedical().setDescription("some new description");
		assertThat(medicalWard1.compareTo(medicalWard2)).isPositive();

		medicalWard1.getMedical().setDescription("some new description");
		medicalWard2.getMedical().setDescription("ZZZZZdescription");
		assertThat(medicalWard1.compareTo(medicalWard2)).isNegative();
	}

	@Test
	void equals_shouldCompareMedical() throws Exception {
		MedicalType medicalType1 = testMedicalType.setup(true);
		Medical medical1 = testMedical.setup(medicalType1, false);
		Lot lot1 = testLot.setup(medical1, true);
		Ward ward1 = testWard.setup(true);
		MedicalWard medicalWard1 = new MedicalWard(medical1, new BigDecimal("10"), lot1);

		MedicalType medicalType2 = testMedicalType.setup(true);
		Medical medical2 = testMedical.setup(medicalType2, false);
		Lot lot2 = testLot.setup(medical2, true);
		lot2.setCode("second");
		Ward ward2 = testWard.setup(true);
		MedicalWard medicalWard2 = new MedicalWard(medical2, new BigDecimal("10"), lot2);

		assertThat(medicalWard1)
						.isEqualTo(medicalWard1)
						.isNotNull()
						.isNotEqualTo("some String")
						.isNotEqualTo(medicalWard2);

		medicalWard2.setMedical(medicalWard1.getMedical());
		assertThat(medicalWard1).isEqualTo(medicalWard2);
	}

	@Test
	void hashCode_shouldBeComputedFromMedicalCode() throws Exception {
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

}
