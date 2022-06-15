/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.patient.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.isf.patient.data.PatientHelper.PATIENT_DATA_TABLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.isf.OHCoreTestCase5;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.model.Patient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientServiceTests extends OHCoreTestCase5 {

	private static PatientHelper createPatient;

	@Autowired
	private PatientIoOperations patientIoOperation;
	@Autowired
	private PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	public void beforeAll() {
		createPatient = new PatientHelper();
	}

	@BeforeEach
	public void beforeEach() {
		cleanH2InMemoryDb();
	}

	@Test
	void getPatients() throws Exception {
		createPatient.createPatient();
		List<Patient> patients = patientIoOperation.getPatients();
		createPatient.checkPatientInDb(patients.get(patients.size() - 1).getCode());
	}

	@Test
	void getPatientsPageable() throws Exception {
		createPatient.createPatient(15);
		List<Patient> patients = patientIoOperation.getPatients(PageRequest.of(0, 10));
		assertThat(patients).hasSize(10);
		createPatient.checkPatientInDb(patients.get(patients.size() - 1).getCode());

		patients = patientIoOperation.getPatients(PageRequest.of(1, 10));
		assertThat(patients).hasSize(5);
	}

	@Test
	void getPatientsByOneOfFieldsLike() throws Exception {
		createPatient.createPatient();
		// Pay attention that query return with PAT_ID descendant
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(null);
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeFirstName() throws Exception {
		// given:
		List<Integer> codes = createPatient.createPatient(7);
		Patient foundPatient = patientIoOperation.getPatient(codes.get(3));

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getFirstName());

		// then:
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeMiddleOfFirstName() throws Exception {
		// given:
		List<Integer> codes = createPatient.createPatient(5);
		Patient foundPatient = patientIoOperation.getPatient(codes.get(2));

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(
				foundPatient.getFirstName().substring(1, foundPatient.getFirstName().length() - 2));

		// then:
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeSecondName() throws Exception {
		// given:
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(4));

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

		// then:
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeNote() throws Exception {
		// given:
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(8));

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

		// then:
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeTaxCode() throws Exception {
		// given:
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(2));

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getTaxCode());

		// then:
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeNotExistingStringShouldNotFindAnything() throws Exception {
		createPatient.createPatient();
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike("dupaxyzzy");
		assertThat(patients).isEmpty();
	}

	@Test
	void getPatientFromName() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(2));
		Patient patient = patientIoOperation.getPatient(foundPatient.getName());
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void getPatientFromNameDoesNotExist() throws Exception {
		assertThat(patientIoOperation.getPatient("someUnusualNameThatWillNotBeFound")).isNull();
	}

	@Test
	void getPatientFromCode() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient patient = patientIoOperation.getPatient(codes.get(2));
		assertThat(patient.getName()).isEqualTo(PATIENT_DATA_TABLE[2].getName());
	}

	@Test
	void getPatientFromCodeDoesNotExist() throws Exception {
		assertThat(patientIoOperation.getPatient(-987654321)).isNull();
	}

	@Test
	void getPatientAll() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(0));
		Patient patient = patientIoOperation.getPatientAll(codes.get(0));
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void getPatientsByParams() throws Exception {
		createPatient.createPatient();
		Map<String, Object> params = new HashMap<>();
		params.put("firstName", PATIENT_DATA_TABLE[2].getFirstName());
		params.put("birthDate", PATIENT_DATA_TABLE[2].getBirthDate().atStartOfDay());
		params.put("address", PATIENT_DATA_TABLE[2].getAddress());
		List<Patient> patients = patientIoOperation.getPatients(params);
		assertThat(patients.size()).isPositive();
	}

	@Test
	void saveNewPatient() {
		Patient patient = PATIENT_DATA_TABLE[17];
		assertThat(patientIoOperation.savePatient(patient)).isNotNull();
	}

	@Test
	void updatePatient() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		int code = codes.get(6);
		Patient patient = patientIoOperation.getPatient(code);
		patient.setFirstName("someNewFirstName");
		assertThat(patientIoOperation.updatePatient(patient)).isTrue();
		Patient updatedPatient = patientIoOperation.getPatient(code);
		assertThat(updatedPatient.getFirstName()).isEqualTo(patient.getFirstName());
	}

	@Test
	void deletePatient() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient patient = patientIoOperation.getPatient(codes.get(1));
		assertThat(patientIoOperation.deletePatient(patient)).isTrue();
	}

	@Test
	void patientIsPresent() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(8));
		assertThat(patientIoOperation.isPatientPresentByName(foundPatient.getName())).isTrue();
	}

	@Test
	void patientIsNotPresent() throws Exception {
		createPatient.createPatient();
		assertThat(patientIoOperation.isPatientPresentByName("someNameWeAreSureDoesNotExist")).isFalse();
	}

	@Test
	void getNextPatientCode() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		int max = patientIoOperation.getNextPatientCode();
		assertThat((codes.size() + 1)).isEqualTo(max);
	}

	@Test
	void codeIsPresent() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		assertThat(patientIoOperation.isCodePresent(codes.get(18))).isTrue();
	}

	@Test
	void codeIsNotPresent() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		assertThat(patientIoOperation.isCodePresent(-987)).isFalse();
	}

	//	@Test
	//	void testMergePatientHistory() throws Exception {
	//		// given:
	//		Patient mergedPatient = testPatient.setup(false);
	//		Patient obsoletePatient = testPatient.setup(false);
	//		patientIoOperationRepository.saveAndFlush(mergedPatient);
	//		patientIoOperationRepository.saveAndFlush(obsoletePatient);
	//
	//		// when:
	//		patientIoOperation.mergePatientHistory(mergedPatient, obsoletePatient);
	//
	//		// then:
	//		assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(mergedPatient, obsoletePatient);
	//	}

	private void assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(Patient mergedPatient, Patient obsoletePatient) {
		Patient mergedPatientResult = patientIoOperationRepository.findById(mergedPatient.getCode()).get();
		Patient obsoletePatientResult = patientIoOperationRepository.findById(obsoletePatient.getCode()).get();
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo("Y");
		assertThat(mergedPatientResult.getDeleted()).isEqualTo("N");
	}

}
