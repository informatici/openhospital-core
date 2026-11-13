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
package org.isf.examination.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.examination.TestPatientExamination;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.model.PatientExamination;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExaminationOperationsTest extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestPatientExamination testPatientExamination;

	@Autowired
	ExaminationOperations examinationOperations;
	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
	@Autowired
	ExaminationBrowserManager examinationBrowserManager;
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
	void getFromLastPatientExamination_shouldCopyLastExaminationValues() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		PatientExamination patientExamination = examinationOperations.getFromLastPatientExamination(lastPatientExamination);
		testPatientExamination.check(patientExamination);
		testPatient.check(patientExamination.getPatient());
	}

	@Test
	void saveOrUpdate_shouldKeepUpdatedHeartRate() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(patientExamination).isNotNull();
		Integer pex_hr = patientExamination.getPex_hr();
		patientExamination.setPex_hr(pex_hr + 1);
		examinationOperations.saveOrUpdate(patientExamination);
		assertThat(patientExamination.getPex_hr()).isEqualTo((Integer) (pex_hr + 1));
	}

	@Test
	void saveOrUpdate_shouldSnapshotSexAndAgeOnlyOnInsert() throws Exception {
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		PatientExamination patientExamination = testPatientExamination.setup(patient, false);

		// a new examination goes through the operations layer, which snapshots the patient's sex and age
		PatientExamination saved = examinationOperations.saveOrUpdate(patientExamination);
		int expectedAge = (int) ChronoUnit.YEARS.between(patient.getBirthDate(), saved.getPex_date().toLocalDate());
		assertThat(saved.getSex()).isEqualTo(String.valueOf(patient.getSex()));
		assertThat(saved.getAge()).isEqualTo(expectedAge);

		// the snapshot is immutable: updating an existing examination must not refresh it from the patient
		saved.setSex("M");
		saved.setAge(999);
		PatientExamination updated = examinationOperations.saveOrUpdate(saved);
		assertThat(updated.getSex()).isEqualTo("M");
		assertThat(updated.getAge()).isEqualTo(999);
	}

	@Test
	void getByID_shouldReturnExamination() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationOperations.getByID(id);
		testPatientExamination.check(patientExamination);
		testPatient.check(patientExamination.getPatient());
	}

	@Test
	void getLastByPatID_shouldReturnLastExaminationOfPatient() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		PatientExamination foundExamination = examinationOperations.getLastByPatID(patient.getCode());
		checkPatientExaminationIntoDb(foundExamination.getPex_ID());
	}

	@Test
	void getLastNByPatID_shouldReturnLastExaminationsOfPatient() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		List<PatientExamination> foundExamination = examinationOperations.getLastNByPatID(patient.getCode(), 1);
		checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}
	
	@Test
	void getLastNByPatIDPageable_shouldReturnLastExaminationsOfPatient() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		PagedResponse<PatientExamination> foundExamination = examinationOperations.getLastNByPatIDPageable(patient.getCode(), 1);
		checkPatientExaminationIntoDb(foundExamination.getData().get(0).getPex_ID());
	}

	@Test
	void getByPatID_shouldReturnExaminationsOfPatient() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		List<PatientExamination> foundExamination = examinationOperations.getByPatID(patient.getCode());
		checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}

	@Test
	void remove_shouldDeleteExaminations() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationBrowserManager.getByID(id);
		List<PatientExamination> patexList = new ArrayList<>(1);
		patexList.add(patientExamination);
		examinationOperations.remove(patexList);
		assertThat(examinationIoOperationRepository.findById(id)).isEmpty();
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
