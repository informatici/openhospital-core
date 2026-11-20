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
package org.isf.patvac.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patvac.TestPatientVaccine;
import org.isf.patvac.model.PatientVaccine;
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
import org.springframework.transaction.annotation.Transactional;

class PatVacIoOperationsTest extends OHCoreTestCase {

	private static TestPatientVaccine testPatientVaccine;
	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;
	private static TestPatient testPatient;

	@Autowired
	PatVacIoOperations patvacIoOperation;
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
	void getPatientVaccine_shouldReturnTodayPatientVaccine_whenMinusOneWeekIsFalse() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		Patient patient = testPatient.setup(false);
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

		patientVaccine.setVaccineDate(TimeTools.getNow());

		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		patientIoOperationRepository.saveAndFlush(patient);
		patVacIoOperationRepository.saveAndFlush(patientVaccine);

		List<PatientVaccine> patientVaccines = patvacIoOperation.getPatientVaccine(false);
		assertThat(patientVaccines.get(patientVaccines.size() - 1).getPatName()).isEqualTo(patientVaccine.getPatName());
	}

	@Test
	void getPatientVaccine_shouldReturnLastWeekPatientVaccine_whenMinusOneWeekIsTrue() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		Patient patient = testPatient.setup(false);
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

		LocalDateTime date = TimeTools.getNow().minusDays(3);
		patientVaccine.setVaccineDate(date);

		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		patientIoOperationRepository.saveAndFlush(patient);
		patVacIoOperationRepository.saveAndFlush(patientVaccine);

		List<PatientVaccine> patientVaccines = patvacIoOperation.getPatientVaccine(true);
		assertThat(patientVaccines.get(patientVaccines.size() - 1).getPatName()).isEqualTo(patientVaccine.getPatName());
	}

	@Test
	void getPatientVaccine_shouldReturnPatientVaccine_whenAllFiltersMatch() throws Exception {
		int code = setupTestPatientVaccine(false);
		PatientVaccine foundPatientVaccine = patVacIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPatientVaccine).isNotNull();
		List<PatientVaccine> patientVaccines = patvacIoOperation.getPatientVaccine(
				foundPatientVaccine.getVaccine().getVaccineType().getCode(),
				foundPatientVaccine.getVaccine().getCode(),
				foundPatientVaccine.getVaccineDate(),
				foundPatientVaccine.getVaccineDate(),
				foundPatientVaccine.getPatient().getSex(),
				foundPatientVaccine.getPatient().getAge(),
				foundPatientVaccine.getPatient().getAge());
		assertThat(patientVaccines.get(patientVaccines.size() - 1).getPatName()).isEqualTo(foundPatientVaccine.getPatName());
	}

	@Test
	@Transactional // requires active session because of lazy loading patient
	void updatePatientVaccine_shouldSaveNewVaccineDate() throws Exception {
		int code = setupTestPatientVaccine(false);
		PatientVaccine foundPatientVaccine = patVacIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPatientVaccine).isNotNull();
		LocalDateTime newDate = TimeTools.getNow();
		foundPatientVaccine.setVaccineDate(newDate);
		PatientVaccine updatedPatientVaccine = patvacIoOperation.updatePatientVaccine(foundPatientVaccine);
		assertThat(updatedPatientVaccine.getVaccineDate()).isEqualTo(newDate);
	}

	@Test
	void newPatientVaccine_shouldSavePatientVaccine() throws Exception {
		Patient patient = testPatient.setup(false);
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		patientIoOperationRepository.saveAndFlush(patient);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);
		PatientVaccine result = patvacIoOperation.newPatientVaccine(patientVaccine);
		assertThat(result).isNotNull();
		checkPatientVaccineIntoDb(patientVaccine.getCode());
	}

	@Test
	void deletePatientVaccine_shouldRemovePatientVaccine() throws Exception {
		int code = setupTestPatientVaccine(false);
		PatientVaccine foundPatientVaccine = patVacIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPatientVaccine).isNotNull();
		patvacIoOperation.deletePatientVaccine(foundPatientVaccine);
		assertThat(patvacIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void getProgYear_shouldReturnMaxProgr_whenYearIsZeroOrGiven() throws Exception {
		int progYear;
		int foundProgYear = 0;
		setupTestPatientVaccine(true);
		List<PatientVaccine> patientVaccineList = patvacIoOperation.getPatientVaccine(null, null, null, null, 'A', 0, 0);
		for (PatientVaccine patVac : patientVaccineList) {
			if (patVac.getProgr() > foundProgYear) {
				foundProgYear = patVac.getProgr();
			}
		}
		progYear = patvacIoOperation.getProgYear(0);
		assertThat(progYear).isEqualTo(foundProgYear);

		progYear = patvacIoOperation.getProgYear(1984); //TestPatientVaccine's year
		assertThat(progYear).isEqualTo(10);
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
