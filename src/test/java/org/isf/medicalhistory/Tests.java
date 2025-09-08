/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medicalhistory;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.util.Assert;
import org.isf.OHCoreTestCase;
import org.isf.encounter.TestEncounter;
import org.isf.encounter.manager.EncounterBrowserManager;
import org.isf.encounter.model.Encounter;
import org.isf.encounter.model.EncounterStatus;
import org.isf.medicalhistory.manager.MedicalHistoryBrowsingManager;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.medicalhistory.service.MedicalHistoryIoOperationRepository;
import org.isf.medicalhistory.service.MedicalHistoryIoOperations;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class Tests extends OHCoreTestCase {

	private static TestMedicalHistory testMedicalHistory;
	private static TestPatient testPatient = new TestPatient();
	private static TestEncounter testEncounter = new TestEncounter();

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@Autowired
	MedicalHistoryBrowsingManager manager;

	@Autowired
	MedicalHistoryIoOperations ioOperations;

	@Autowired
	MedicalHistoryIoOperationRepository repository;

	@Autowired
	EncounterBrowserManager encounterBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testPatient = new TestPatient();
		testMedicalHistory = new TestMedicalHistory();
	}

	@Test
	void testServiceAddMedicalHistory() throws Exception {
		MedicalHistory medicalHistory = setupTestMedicalHistory(null);
		assertThat(medicalHistory).isNotNull();

		MedicalHistory medHist = ioOperations.add(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		assertThat(medHist.getId()).isEqualTo(medicalHistory.getId());
	}

	@Test
	void testManagerAddMedicalHistory() throws Exception {
		MedicalHistory medicalHistory = setupTestMedicalHistory(null);
		assertThat(medicalHistory).isNotNull();

		MedicalHistory medHist = manager.add(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		assertThat(medHist.getId()).isEqualTo(medicalHistory.getId());
	}

	@Test
	void testServiceUpdateMedicalHistory() throws Exception {
		MedicalHistory medicalHistory = setupTestMedicalHistory(null);
		assertThat(medicalHistory).isNotNull();

		MedicalHistory medHist = ioOperations.add(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		medicalHistory.setOtherFamilyPathologies("yes");
		medicalHistory.setAllergyPrecision("allergic to sugar");

		manager.update(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		assertThat(medHist.getOtherFamilyPathologies()).isEqualTo("yes");
		assertThat(medicalHistory.getAllergyPrecision()).isEqualTo("allergic to sugar");
	}

	@Test
	void testManagerUpdateMedicalHistory() throws Exception {
		MedicalHistory medicalHistory = setupTestMedicalHistory(null);
		assertThat(medicalHistory).isNotNull();

		MedicalHistory medHist = manager.add(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		medicalHistory.setOtherFamilyPathologies("yes");
		medicalHistory.setAllergyPrecision("allergic to sugar");

		manager.update(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		assertThat(medHist.getOtherFamilyPathologies()).isEqualTo("yes");
		assertThat(medicalHistory.getAllergyPrecision()).isEqualTo("allergic to sugar");
	}

	@Test
	void testServiceGetMedicalHistoryById() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);

		MedicalHistory medHistory = ioOperations.getMedicalHistoryById(medicalHistories.get(0).getId());

		assertThat(medicalHistories.get(0)).isEqualTo(medHistory);
	}

	@Test
	void testManagerGetMedicalHistoryById() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);

		MedicalHistory medHistory = manager.getMedicalHistoryById(medicalHistories.get(0).getId());

		assertThat(medicalHistories.get(0)).isEqualTo(medHistory);
	}

	@Test
	void testManagerGetAllMedicalHistoryByPatientCode() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(1);
		repository.saveAllAndFlush(medicalHistories);
		MedicalHistory medHistory = manager.getMedicalHistoriesByPatientCode(medicalHistories.get(0).getPatient().getCode());

		assertThat(medHistory).isNotNull();
		assertThat(medHistory.getPatient().getCode()).isEqualTo(medicalHistories.get(0).getPatient().getCode());
	}

	@Test
	void testServiceGetAllMedicalHistories() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);

		List<MedicalHistory> medicalHistories1 = setupTestMedicalHistories(5);
		repository.saveAllAndFlush(medicalHistories1);

		List<MedicalHistory> medHistories = ioOperations.getAll();

		assertThat(medHistories).hasSize(8);
	}

	@Test
	void testManagerGetAllMedicalHistory() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);

		List<MedicalHistory> medicalHistories1 = setupTestMedicalHistories(5);
		repository.saveAllAndFlush(medicalHistories1);

		List<MedicalHistory> medHistories = manager.getAll();

		assertThat(medHistories).hasSize(8);
	}

	@Test
	void testMgrGetMedicalHistoriesByEncounter() throws Exception {
		String code = setupEncounter(false);

		Encounter encounter = encounterBrowserManager.getEncountersByCode(code);
		assertThat(encounter).isNotNull();

		encounter.setClosedAt(LocalDateTime.now().plusDays(1));
		encounterBrowserManager.saveEncounter(encounter);

		repository.flush();

		MedicalHistory medicalHistory = setupTestMedicalHistory(encounter.getPatient());

		repository.flush();

		List<MedicalHistory> medicalHistories = manager.getMedicalHistoriesForEncounter(encounter);
		assertThat(medicalHistories).isNotNull();
		assertThat(medicalHistories).hasSize(1);
		assertThat(medicalHistories.get(0).getPatient()).isEqualTo(encounter.getPatient());
	}

	private MedicalHistory setupTestMedicalHistory(Patient patient) throws Exception {
		if (patient == null) {
			Patient patientToSave = testPatient.setup(false);
			patient = patientIoOperationRepository.saveAndFlush(patientToSave);

			patientIoOperationRepository.flush();
		}

		MedicalHistory medicalHistory = testMedicalHistory.createMedicalHistory(patient);
		MedicalHistory savedHistory = repository.saveAndFlush(medicalHistory);

		repository.flush();

		return savedHistory;
	}

	private List<MedicalHistory> setupTestMedicalHistories(int length) throws Exception {
		List<MedicalHistory> histories = new ArrayList<>();

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);

		for (int i = 0; i < length; i++) {
			MedicalHistory medicalHistory = testMedicalHistory.createMedicalHistory(patient);
			histories.add(medicalHistory);
		}

		return histories;
	}

	private String setupEncounter(boolean usingSet) throws OHException, OHServiceException {
		Patient patient = testPatient.setup(false);
		Patient patientSaved = patientIoOperationRepository.saveAndFlush(patient);
		assertThat(patientSaved).isNotNull();
		assertThat(patientSaved.getCode()).isNotNull();

		Encounter encounter = testEncounter.setup(false);
		encounter.setPatient(patientSaved);
		if (encounter.getStatus() == null) {
			encounter.setStatus(EncounterStatus.ACTIVE);
		}
		encounter = encounterBrowserManager.saveEncounter(encounter);
		assertThat(encounter).isNotNull();
		assertThat(encounter.getCode()).isNotNull();

		return encounter.getCode();
	}
}
