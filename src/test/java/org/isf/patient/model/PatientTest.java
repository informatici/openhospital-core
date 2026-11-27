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
package org.isf.patient.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.opd.TestOpd;
import org.isf.opd.model.Opd;
import org.isf.patient.TestPatient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PatientTest extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestOpd testOpd;

	@Autowired
	PatientIoOperations patientIoOperation;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		testPatient = new TestPatient();
		testOpd = new TestOpd();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		Integer code = setupTestPatient(false);
		checkPatientIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		Integer code = setupTestPatient(true);
		checkPatientIntoDb(code);
	}

	@Test
	void constructorAndSetDeleted_shouldSetSexFromOpdAndUpdateDeleted() throws Exception {
		Opd opd = testOpd.setup(null, null, null, null, false);
		Patient patient = new Patient(opd);

		assertThat(patient.getSex()).isEqualTo('F');
		assertThat(patient.getCode()).isNull();
		assertThat(patient.getBirthDate()).isNull();

		assertThat(patient.getDeleted()).isEqualTo('N');
		patient.setDeleted('Y');
		assertThat(patient.getDeleted()).isEqualTo('Y');
	}

	@Test
	void constructorAndSetLock_shouldSetCodeAndSexAndUpdateLock() {
		Patient patient = new Patient(99, "firstName", "secondName", "name", null, 99, " ", 'F', "address",
				"city", "nextOfKin", "noPhone", "note", "motherName", ' ', "fatherName", ' ',
				"bloodType", ' ', ' ', "personalCode", "maritalStatus", "profession");

		assertThat(patient.getCode()).isEqualTo(99);
		assertThat(patient.getSex()).isEqualTo('F');
		assertThat(patient.getBirthDate()).isNull();

		assertThat(patient.getLock()).isZero();
		patient.setLock(99);
		assertThat(patient.getLock()).isEqualTo(99);
	}

	@Test
	void getSearchString_shouldUseCodeNamesCityAddressTelephoneAndTaxCode() throws Exception {
		Patient patient = testPatient.setup(false);
		patient.setCode(1);
		assertThat(patient.getSearchString()).isEqualTo("1 testfirstname testsecondname testcity testaddress TestTelephone testtaxcode ");
	}

	@Test
	void getInformations_shouldJoinCityAddressTelephoneNoteAndTaxCode() throws Exception {
		Patient patient = testPatient.setup(false);
		patient.setNote("someNote");
		assertThat(patient.getInformations()).isEqualTo("TestCity - TestAddress - TestTelephone - someNote - TestTaxCode");
	}

	@Test
	void equals_shouldCompareCodes() throws Exception {
		Patient patient = testPatient.setup(false);
		assertThat(patient)
				.isEqualTo(patient)
				.isNotNull()
				.isNotEqualTo("someString");
		Patient patient2 = testPatient.setup(true);
		patient.setCode(1);
		patient.setCode(2);
		assertThat(patient).isNotEqualTo(patient2);
		patient2.setCode(patient.getCode());
		assertThat(patient).isEqualTo(patient2);
	}

	@Test
	void hashCode_shouldBeComputedFromCode() throws Exception {
		Patient patient = testPatient.setup(false);
		patient.setCode(1);
		// compute
		int hashCode = patient.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1);
		// check stored value
		assertThat(patient.hashCode()).isEqualTo(hashCode);

		Patient patent2 = testPatient.setup(true);
		patent2.setCode(null);
		assertThat(patent2.hashCode()).isEqualTo(23 * 133);
	}

	private Integer setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient.getCode();
	}

	private void checkPatientIntoDb(Integer code) throws OHServiceException {
		Patient foundPatient = patientIoOperation.getPatient(code);
		testPatient.check(foundPatient);
	}

}
