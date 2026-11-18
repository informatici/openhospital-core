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
package org.isf.therapy.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.menu.model.User;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.sessionaudit.model.UserSession;
import org.isf.therapy.TestTherapy;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TherapyIoOperationsTest extends OHCoreTestCase {

	private static TestTherapy testTherapyRow;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestPatient testPatient;

	@Autowired
	TherapyIoOperations therapyIoOperation;
	@Autowired
	TherapyIoOperationRepository therapyIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		UserSession.setUser(new User("TestUser", null, "testpass", "test"));
		testTherapyRow = new TestTherapy();
		testPatient = new TestPatient();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getTherapyRows_shouldReturnPatientRows_whenPatientCodeIsGiven() throws Exception {
		int id = setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		List<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(foundTherapyRow.getPatient().getCode());
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	@Test
	void getTherapyRows_shouldReturnAllRows_whenPatientCodeIsZero() throws Exception {
		// given:
		int id = setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();

		// when:
		List<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(0);

		// then:
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	@Test
	void newTherapy_shouldSaveTherapyRow() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
		TherapyRow newTherapyRow = therapyIoOperation.newTherapy(therapyRow);
		checkTherapyRowIntoDb(newTherapyRow.getTherapyID());
	}

	@Test
	void deleteAllTherapies_shouldDeletePatientTherapies() throws Exception {
		int id = setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		therapyIoOperation.deleteAllTherapies(foundTherapyRow.getPatient());
		assertThat(therapyIoOperation.isCodePresent(id)).isFalse();
	}

	@Test
	void getTherapyRows_shouldEndWithSavedRow_whenPatientCodeIsGiven() throws Exception {
		int id = setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		List<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(foundTherapyRow.getPatient().getCode());
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	private int setupTestTherapyRow(boolean usingSet) throws OHException {
		TherapyRow therapyRow;
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, true);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		therapyRow = testTherapyRow.setup(patient, medical, usingSet);
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		return therapyRow.getTherapyID();
	}

	private void checkTherapyRowIntoDb(int id) throws OHException {
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		testTherapyRow.check(foundTherapyRow);
	}

}
