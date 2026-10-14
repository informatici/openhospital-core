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
package org.isf.therapy.model;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.isf.therapy.service.TherapyIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TherapyRowTest extends OHCoreTestCase {

	private static TestTherapy testTherapyRow;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestPatient testPatient;

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
	void constructor_shouldSetFields() throws Exception {
		int id = setupTestTherapyRow(false);
		checkTherapyRowIntoDb(id);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int id = setupTestTherapyRow(true);
		checkTherapyRowIntoDb(id);
	}

	@Test
	void toString_shouldUseTherapyRowFields() throws Exception {
		int id = setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow)
						.isNotNull()
						.hasToString(Integer.toString(foundTherapyRow.getMedical()) + " - 10 9.9/11/12");
	}

	@Test
	void equals_shouldUseTherapyId() throws Exception {
		int id = setupTestTherapyRow(true);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow)
						.isNotNull()
						.isNotEqualTo("someString");

		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		TherapyRow therapyRow2 = testTherapyRow.setup(patient, medical, true);

		assertThat(foundTherapyRow).isNotEqualTo(therapyRow2);
		therapyRow2.setTherapyID(foundTherapyRow.getTherapyID());
		assertThat(foundTherapyRow).isEqualTo(therapyRow2);
	}

	@Test
	void hashCode_shouldUseTherapyId() throws Exception {
		int id = setupTestTherapyRow(true);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		foundTherapyRow.setTherapyID(99);
		// compute
		int hashCode = foundTherapyRow.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 99);
		// use computed value
		assertThat(foundTherapyRow.hashCode()).isEqualTo(hashCode);
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
