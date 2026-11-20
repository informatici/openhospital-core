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
package org.isf.patvac.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patvac.TestPatientVaccine;
import org.isf.patvac.model.PatientVaccine;
import org.isf.patvac.service.PatVacIoOperationRepository;
import org.isf.patvac.service.PatVacIoOperations;
import org.isf.utils.exception.OHServiceException;
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

class PatVacManagerTest extends OHCoreTestCase {

	private static TestPatientVaccine testPatientVaccine;
	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;
	private static TestPatient testPatient;

	@Autowired
	PatVacIoOperations patvacIoOperation;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	PatVacManager patVacManager;
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
	void getPatientVaccine_shouldReturnOnlyTodayPatientVaccine_whenMinusOneWeekIsFalse() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineType.setCode("A");
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		vaccine.setCode("ABC");
		Patient patient = testPatient.setup(false);
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

		// Today
		LocalDateTime now = TimeTools.getNow();
		patientVaccine.setVaccineDate(now);

		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		patientIoOperationRepository.saveAndFlush(patient);
		patVacIoOperationRepository.saveAndFlush(patientVaccine);

		VaccineType vaccineType2 = testVaccineType.setup(false);
		vaccineType2.setCode("X");
		Vaccine vaccine2 = testVaccine.setup(vaccineType2, false);
		vaccine2.setCode("CBA");
		Patient patient2 = testPatient.setup(false);
		PatientVaccine patientVaccine2 = testPatientVaccine.setup(patient2, vaccine2, true);

		// 8 days ago
		patientVaccine2.setVaccineDate(now.minusDays(8));

		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType2);
		vaccineIoOperationRepository.saveAndFlush(vaccine2);
		patientIoOperationRepository.saveAndFlush(patient2);
		patVacIoOperationRepository.saveAndFlush(patientVaccine2);

		List<PatientVaccine> patientVaccines = patVacManager.getPatientVaccine(false);
		assertThat(patientVaccines).hasSize(1);
		assertThat(patientVaccines.get(patientVaccines.size() - 1).getPatName()).isEqualTo(patientVaccine.getPatName());
	}

	@Test
	void getPatientVaccine_shouldReturnOnlyLastWeekPatientVaccine_whenMinusOneWeekIsTrue() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineType.setCode("A");
		vaccineType.setDescription("Type Description1");
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		vaccine.setCode("ABC");
		vaccine.setDescription("Description1");
		Patient patient = testPatient.setup(false);
		patient.setFirstName("firstName1");
		patient.setSecondName("secondName1");
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

		LocalDateTime now = TimeTools.getNow();
		patientVaccine.setVaccineDate(now.minusDays(3));

		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		patientIoOperationRepository.saveAndFlush(patient);
		patVacIoOperationRepository.saveAndFlush(patientVaccine);

		VaccineType vaccineType2 = testVaccineType.setup(false);
		vaccineType2.setCode("Z");
		vaccineType2.setDescription("Type Description2");
		Vaccine vaccine2 = testVaccine.setup(vaccineType2, false);
		vaccine2.setCode("CBA");
		vaccine2.setDescription("Description2");
		Patient patient2 = testPatient.setup(false);
		patient2.setFirstName("firstName2");
		patient2.setSecondName("secondName2");
		PatientVaccine patientVaccine2 = testPatientVaccine.setup(patient2, vaccine2, true);

		// 8 days ago
		patientVaccine2.setVaccineDate(now.minusDays(8));

		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType2);
		vaccineIoOperationRepository.saveAndFlush(vaccine2);
		patientIoOperationRepository.saveAndFlush(patient2);
		patVacIoOperationRepository.saveAndFlush(patientVaccine2);

		List<PatientVaccine> patientVaccines = patVacManager.getPatientVaccine(true);
		assertThat(patientVaccines).hasSize(1);
		assertThat(patientVaccines.get(patientVaccines.size() - 1).getPatName()).isEqualTo(patientVaccine.getPatName());
	}

	@Test
	void getPatientVaccine_shouldReturnPatientVaccine_whenAllFiltersMatch() throws Exception {
		int code = setupTestPatientVaccine(false);
		PatientVaccine foundPatientVaccine = patVacIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPatientVaccine).isNotNull();
		List<PatientVaccine> patientVaccines = patVacManager.getPatientVaccine(
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
		PatientVaccine updatedPatientVaccine = patVacManager.updatePatientVaccine(foundPatientVaccine);
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
		PatientVaccine result = patVacManager.newPatientVaccine(patientVaccine);
		assertThat(result).isNotNull();
		checkPatientVaccineIntoDb(patientVaccine.getCode());
	}

	@Test
	void deletePatientVaccine_shouldRemovePatientVaccine() throws Exception {
		int code = setupTestPatientVaccine(true);
		PatientVaccine foundPatientVaccine = patVacIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPatientVaccine).isNotNull();
		patVacManager.deletePatientVaccine(foundPatientVaccine);
		assertThat(patvacIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void getProgYear_shouldReturnMaxProgr_whenYearIsZeroOrGiven() throws Exception {
		int progYear;
		int foundProgYear = 0;
		setupTestPatientVaccine(true);
		List<PatientVaccine> patientVaccineList = patVacManager.getPatientVaccine(null, null, null, null, 'A', 0, 0);
		for (PatientVaccine patVac : patientVaccineList) {
			if (patVac.getProgr() > foundProgYear) {
				foundProgYear = patVac.getProgr();
			}
		}
		progYear = patVacManager.getProgYear(0);
		assertThat(progYear).isEqualTo(foundProgYear);

		progYear = patVacManager.getProgYear(1984); //TestPatientVaccine's year
		assertThat(progYear).isEqualTo(10);
	}

	@Test
	void newPatientVaccine_shouldThrow_whenVaccineDateIsNull() {
		assertThatThrownBy(() -> {
			VaccineType vaccineType = testVaccineType.setup(false);
			Vaccine vaccine = testVaccine.setup(vaccineType, false);
			Patient patient = testPatient.setup(false);
			PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

			patientVaccine.setVaccineDate(null);

			patVacManager.newPatientVaccine(patientVaccine);
		})
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newPatientVaccine_shouldThrow_whenProgrIsNegative() {
		assertThatThrownBy(() -> {
			VaccineType vaccineType = testVaccineType.setup(false);
			Vaccine vaccine = testVaccine.setup(vaccineType, false);
			Patient patient = testPatient.setup(false);
			PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

			patientVaccine.setProgr(-99);

			patVacManager.newPatientVaccine(patientVaccine);
		})
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newPatientVaccine_shouldThrow_whenVaccineIsNull() {
		assertThatThrownBy(() -> {
			VaccineType vaccineType = testVaccineType.setup(false);
			Vaccine vaccine = testVaccine.setup(vaccineType, false);
			Patient patient = testPatient.setup(false);
			PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

			patientVaccine.setVaccine(null);

			patVacManager.newPatientVaccine(patientVaccine);
		})
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newPatientVaccine_shouldThrow_whenPatientIsNull() {
		assertThatThrownBy(() -> {
			VaccineType vaccineType = testVaccineType.setup(false);
			Vaccine vaccine = testVaccine.setup(vaccineType, false);
			Patient patient = testPatient.setup(false);
			PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

			patientVaccine.setPatient(null);

			patVacManager.newPatientVaccine(patientVaccine);
		})
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newPatientVaccine_shouldThrow_whenPatientAndVaccineAreNotSavedAndFirstNameIsBlank() {
		assertThatThrownBy(() -> {
			VaccineType vaccineType = testVaccineType.setup(false);
			Vaccine vaccine = testVaccine.setup(vaccineType, false);
			Patient patient = testPatient.setup(false);
			PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

			patientVaccine.getPatient().setFirstName(" ");

			patVacManager.newPatientVaccine(patientVaccine);
		})
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newPatientVaccine_shouldThrow_whenPatientAndVaccineAreNotSavedAndSexIsBlank() {
		assertThatThrownBy(() -> {
			VaccineType vaccineType = testVaccineType.setup(false);
			Vaccine vaccine = testVaccine.setup(vaccineType, false);
			Patient patient = testPatient.setup(false);
			PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);

			patientVaccine.getPatient().setSex(' ');

			patVacManager.newPatientVaccine(patientVaccine);
		})
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
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