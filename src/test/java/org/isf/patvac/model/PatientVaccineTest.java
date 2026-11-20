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
package org.isf.patvac.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patvac.TestPatientVaccine;
import org.isf.patvac.service.PatVacIoOperationRepository;
import org.isf.utils.time.TimeTools;
import org.isf.vaccine.TestVaccine;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PatientVaccineTest extends OHCoreTestCase {

	private static TestPatientVaccine testPatientVaccine;
	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;
	private static TestPatient testPatient;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	VaccineIoOperationRepository vaccineIoOperationRepository;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;
	@Autowired
	PatVacIoOperationRepository patVacIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testPatientVaccine = new TestPatientVaccine();
		testPatient = new TestPatient();
		testVaccine = new TestVaccine();
		testVaccineType = new TestVaccineType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		int code = setupTestPatientVaccine(false);
		checkPatientVaccineIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int code = setupTestPatientVaccine(true);
		checkPatientVaccineIntoDb(code);
	}

	@Test
	void hashCode_shouldBeComputedFromFields_whenFieldsAreZeroOrNull() {
		PatientVaccine patientVaccine = new PatientVaccine(0, 0, null, new Patient(), null, 0);
		patientVaccine.setPatient(null);
		assertThat(patientVaccine.hashCode()).isEqualTo(31 * 31 * 31 * 31 * 31);
	}

	@Test
	void equals_shouldCompareFields() {
		PatientVaccine patientVaccine1 = new PatientVaccine(0, 0, null, new Patient(), null, 0);
		PatientVaccine patientVaccine2 = new PatientVaccine(0, 0, null, new Patient(), null, 0);

		assertThat(patientVaccine1)
				.isNotNull()
				.isEqualTo(patientVaccine1)
				.isNotEqualTo("someString");

		patientVaccine2.setCode(-99);
		assertThat(patientVaccine1).isNotEqualTo(patientVaccine2);

		patientVaccine2.setCode(patientVaccine1.getCode());
		patientVaccine2.setPatient(null);
		assertThat(patientVaccine1).isNotEqualTo(patientVaccine2);

		patientVaccine1.setPatient(null);
		patientVaccine2.setPatient(new Patient());
		patientVaccine2.getPatient().setCode(1);
		assertThat(patientVaccine1).isNotEqualTo(patientVaccine2);

		patientVaccine1.setPatient(new Patient());
		patientVaccine1.getPatient().setCode(1);
		assertThat(patientVaccine1).isEqualTo(patientVaccine2);

		patientVaccine2.setProgr(-99);
		assertThat(patientVaccine1).isNotEqualTo(patientVaccine2);

		patientVaccine2.setProgr(patientVaccine1.getProgr());
		patientVaccine2.setVaccine(new Vaccine());
		patientVaccine2.getVaccine().setCode("Z");
		patientVaccine2.getVaccine().setDescription("desciption");
		patientVaccine2.getVaccine().setVaccineType(new VaccineType());
		patientVaccine2.getVaccine().getVaccineType().setCode("A");
		patientVaccine2.getVaccine().getVaccineType().setDescription("description");
		assertThat(patientVaccine1).isNotEqualTo(patientVaccine2);

		patientVaccine1.setVaccine(new Vaccine());
		patientVaccine1.getVaccine().setCode("Z");
		patientVaccine1.getVaccine().setDescription("desciption");
		patientVaccine1.getVaccine().setVaccineType(new VaccineType());
		patientVaccine1.getVaccine().getVaccineType().setCode("A");
		patientVaccine1.getVaccine().getVaccineType().setDescription("description");
		assertThat(patientVaccine1).isEqualTo(patientVaccine2);

		patientVaccine1.setVaccineDate(TimeTools.getNow());
		patientVaccine2.setVaccineDate(null);
		assertThat(patientVaccine1).isNotEqualTo(patientVaccine2);

		patientVaccine2.setVaccineDate(patientVaccine1.getVaccineDate());
		patientVaccine1.setVaccineDate(null);
		assertThat(patientVaccine1).isNotEqualTo(patientVaccine2);

		patientVaccine2.setVaccineDate(null);
		assertThat(patientVaccine1).isEqualTo(patientVaccine2);
	}

	private int setupTestPatientVaccine(boolean usingSet) throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		Patient patient = testPatient.setup(false);
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, usingSet);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		patientIoOperationRepository.saveAndFlush(patient);
		patVacIoOperationRepository.saveAndFlush(patientVaccine);
		return patientVaccine.getCode();
	}

	private void checkPatientVaccineIntoDb(int code) {
		PatientVaccine foundPatientVaccine = patVacIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPatientVaccine).isNotNull();
		testPatientVaccine.check(foundPatientVaccine);
	}

}
