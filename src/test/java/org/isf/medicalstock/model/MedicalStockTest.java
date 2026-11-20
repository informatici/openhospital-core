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

import java.util.stream.Stream;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestMedicalStock;
import org.isf.medicalstock.service.MedicalStockIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MedicalStockTest extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestMedicalStock testMedicalStock;

	@Autowired
	MedicalStockIoOperationRepository medicalStockIoOperationRepository;
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
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testMedicalStock = new TestMedicalStock();
	}

	@AfterAll
	static void tearDownClass() {
		testMedical = null;
		testMedicalType = null;
		testMedicalStock = null;
	}

	@BeforeEach
	void setUp() throws OHException {
		cleanH2InMemoryDb();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void constructor_shouldSetFields(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMedicalStock(false);
		checkMedicalStockIntoDb(code);
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void setters_shouldSetFields(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		int code = setupTestMedicalStock(true);
		checkMedicalStockIntoDb(code);
	}

	private int setupTestMedicalStock(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		MedicalStock medicalStock = testMedicalStock.setup(medical, usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		medicalStockIoOperationRepository.saveAndFlush(medicalStock);
		return medicalStock.getCode();
	}

	private void checkMedicalStockIntoDb(int code) {
		MedicalStock foundMedicalStock = medicalStockIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedicalStock).isNotNull();
		testMedicalStock.check(foundMedicalStock);
	}

}
