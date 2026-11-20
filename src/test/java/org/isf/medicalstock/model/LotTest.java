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
package org.isf.medicalstock.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class LotTest extends OHCoreTestCase {

	private static TestLot testLot;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;

	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;

	static Stream<Arguments> automaticlot() {
		return Stream.of(
			Arguments.of(false, false, false),
			Arguments.of(false, false, true),
			Arguments.of(false, true, false),
			Arguments.of(false, true, true),
			Arguments.of(true, false, false),
			Arguments.of(true, false, true),
			Arguments.of(true, true, false),
			Arguments.of(true, true, true));
	}

	private static void setGeneralData(boolean in, boolean out, boolean toward) {
		GeneralData.AUTOMATICLOT_IN = in;
		GeneralData.AUTOMATICLOT_OUT = out;
		GeneralData.AUTOMATICLOTWARD_TOWARD = toward;
	}

	@BeforeAll
	static void setUpClass() {
		testLot = new TestLot();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
	}

	@AfterAll
	static void tearDownClass() {
		testLot = null;
		testMedical = null;
		testMedicalType = null;
	}

	@BeforeEach
	void setUp() throws OHException {
		cleanH2InMemoryDb();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void constructor_shouldSetFields(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(false);
		checkLotIntoDb(code);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void setters_shouldSetFields(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		checkLotIntoDb(code);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void toString_shouldReturnNoLotMessageForNullCodeAndCodeOtherwise(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		Lot lot = lotIoOperationRepository.findById(code).orElse(null);
		assertThat(lot).isNotNull();
		lot.setCode(null);
		// TODO: if resource bundles are available this string test needs to change
		assertThat(lot).hasToString("angal.medicalstock.nolot.txt");
		lot.setCode(code);
		assertThat(lot).hasToString(code);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void isValidLot_shouldReturnTrueOnlyForCodeWithinMaxLength(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		Lot lot = lotIoOperationRepository.findById(code).orElse(null);
		assertThat(lot).isNotNull();
		assertThat(lot.isValidLot()).isTrue();
		lot.setCode("thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong_thisIsWayTooLong");
		assertThat(lot.isValidLot()).isFalse();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void equals_shouldCompareCodeCostDatesAndMainStoreQuantity(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		String code = setupTestLot(true);
		Lot lot = lotIoOperationRepository.findById(code).orElse(null);
		assertThat(lot)
			.isEqualTo(lot)
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

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, true);
		medicalsIoOperationRepository.saveAndFlush(medical);
		Lot lot = new Lot(medical, "aCode", TimeTools.getNow(), TimeTools.getNow(), new BigDecimal(10.10));
		int hashCode = lot.hashCode();
		assertThat(hashCode).isPositive();
		// use computed value
		assertThat(lot.hashCode()).isEqualTo(hashCode);
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
		Lot foundLot = lotIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLot).isNotNull();
		testLot.check(foundLot);
	}

}
