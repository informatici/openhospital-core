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
package org.isf.examination.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.examination.TestPatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PatientExaminationTest extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestPatientExamination testPatientExamination;

	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testPatient = new TestPatient();
		testPatientExamination = new TestPatientExamination();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		int id = setupTestPatientExamination(false);
		checkPatientExaminationIntoDb(id);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int id = setupTestPatientExamination(true);
		checkPatientExaminationIntoDb(id);
	}

	@Test
	void equalsHashCodeCompareTo_shouldUsePatientExaminationFields() throws Exception {
		int code = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(code).orElse(null);
		assertThat(patientExamination).isNotNull();
		Patient patient = testPatient.setup(false);
		PatientExamination patientExamination2 = testPatientExamination.setup(patient, false);
		patientExamination2.setPex_ID(-1);
		assertThat(patientExamination).isEqualTo(patientExamination);
		assertThat(patientExamination)
				.isNotEqualTo(patientExamination2)
				.isNotEqualTo("xyzzy");
		patientExamination2.setPex_ID(patientExamination.getPex_ID());
		assertThat(patientExamination).isEqualTo(patientExamination2);
		assertThat(patientExamination.compareTo(patientExamination2)).isZero();

		assertThat(patientExamination.hashCode()).isPositive();
	}

	@Test
	void getBMI_shouldReturnZero_whenHeightIsNull() throws Exception {
		int code = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(code).orElse(null);
		assertThat(patientExamination).isNotNull();
		assertThat(patientExamination.getBMI()).isPositive();
		patientExamination.setPex_height(null);
		assertThat(patientExamination.getBMI()).isZero();
	}

	@Test
	void settersAndGetters_shouldUpdateHgtDiuresisAndDescriptions() throws Exception {
		int code = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(code).orElse(null);
		assertThat(patientExamination).isNotNull();

		int hgt = patientExamination.getPex_hgt();
		patientExamination.setPex_hgt(-1);
		assertThat(patientExamination.getPex_hgt()).isEqualTo(-1);
		patientExamination.setPex_hgt(hgt);

		int diuresis = patientExamination.getPex_diuresis();
		patientExamination.setPex_diuresis(-1);
		assertThat(patientExamination.getPex_diuresis()).isEqualTo(-1);
		patientExamination.setPex_diuresis(diuresis);

		String desc = patientExamination.getPex_diuresis_desc();
		patientExamination.setPex_diuresis_desc("new description");
		assertThat(patientExamination.getPex_diuresis_desc()).isEqualTo("new description");
		patientExamination.setPex_diuresis_desc(desc);

		desc = patientExamination.getPex_bowel_desc();
		patientExamination.setPex_bowel_desc("new description too");
		assertThat(patientExamination.getPex_bowel_desc()).isEqualTo("new description too");
		patientExamination.setPex_bowel_desc(desc);
	}

	private int setupTestPatientExamination(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PatientExamination patientExamination = testPatientExamination.setup(patient, usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(patientExamination);
		return patientExamination.getPex_ID();
	}

	private void checkPatientExaminationIntoDb(int id) throws OHException {
		PatientExamination foundPatientExamination = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPatientExamination).isNotNull();
		testPatientExamination.check(foundPatientExamination);
		testPatient.check(foundPatientExamination.getPatient());
	}

}
