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
package org.isf.patient.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.utils.exception.OHException;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class PatientIoOperationsTest extends OHCoreTestCase {

	private static TestPatient testPatient;

	@Autowired
	PatientIoOperations patientIoOperation;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		testPatient = new TestPatient();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getPatients_shouldReturnSavedPatient() throws Exception {
		setupTestPatient(false);
		List<Patient> patients = patientIoOperation.getPatients();
		testPatient.check(patients.get(patients.size() - 1));
	}
	
	@Test
	void getPatientsPageable_shouldReturnPageWithSavedPatient() throws Exception {
		setupTestPatient(false); 
		PagedResponse<Patient> patients = patientIoOperation.getPatientsPageable(createPageRequest());
		testPatient.check(patients.getData().get(patients.getData().size() - 1)); 
	}
	 
	private Pageable createPageRequest() {
		return PageRequest.of(0, 10); // Page size 10
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldReturnSavedPatient_whenKeywordIsNull() throws Exception {
		setupTestPatient(false);
		// Pay attention that query return with PAT_ID descendant
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(null);
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsFirstName() throws Exception {
		// given:
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getFirstName());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsPartOfFirstName() throws Exception {
		// given:
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(
				foundPatient.getFirstName().substring(1, foundPatient.getFirstName().length() - 2));

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsSecondName() throws Exception {
		// given:
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsSecondNameAndNoteIsNull() throws Exception {
		// given:
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsTaxCode() throws Exception {
		// given:
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getTaxCode());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldReturnEmpty_whenKeywordMatchesNoPatient() throws Exception {
		setupTestPatient(false);
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike("dupa");
		assertThat(patients).isEmpty();
	}

	@Test
	void getPatient_shouldReturnPatient_whenNameExists() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientIoOperation.getPatient(foundPatient.getName());
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void getPatient_shouldReturnNull_whenNameDoesNotExist() throws Exception {
		assertThat(patientIoOperation.getPatient("someUnusualNameThatWillNotBeFound")).isNull();
	}

	@Test
	void getPatient_shouldReturnPatient_whenCodeExists() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientIoOperation.getPatient(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void getPatient_shouldReturnNull_whenCodeDoesNotExist() throws Exception {
		assertThat(patientIoOperation.getPatient(-987654321)).isNull();
	}

	@Test
	void getPatientAll_shouldReturnPatient_whenCodeExists() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientIoOperation.getPatientAll(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void getPatients_shouldFindPatient_whenFirstNameBirthDateAndAddressMatch() throws Exception {
		setupTestPatient(false);
		Map<String, Object> params = new HashMap<>();
		params.put("firstName", "TestFirstName");
		params.put("birthDate", LocalDateTime.of(1984, Calendar.AUGUST, 14, 0, 0, 0));
		params.put("address", "TestAddress");
		List<Patient> patients = patientIoOperation.getPatients(params);
		assertThat(patients).isNotEmpty();
	}

	@Test
	void savePatient_shouldSaveNewPatient() throws Exception {
		Patient patient = testPatient.setup(true);
		assertThat(patientIoOperation.savePatient(patient)).isNotNull();
	}

	@Test
	void updatePatient_shouldSaveNewFirstName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patient.setFirstName("someNewFirstName");
		Patient updatedPatient = patientIoOperation.updatePatient(patient);
		assertThat(updatedPatient.getFirstName()).isEqualTo(patient.getFirstName());
	}

	@Test
	void deletePatient_shouldMakePatientNotFoundByName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patientIoOperation.deletePatient(patient);
		Patient deletedPatient = patientIoOperation.getPatient(patient.getName());
		assertThat(deletedPatient).isNull();
	}

	@Test
	void deletePatient_shouldSetDeletedFlagAndDeletedDate() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		assertThat(patient.getDeletedDate()).isNull();

		patientIoOperation.deletePatient(patient);

		Patient deleted = patientIoOperationRepository.findById(code).orElse(null);
		assertThat(deleted).isNotNull();
		assertThat(deleted.getDeleted()).isEqualTo('Y');
		assertThat(deleted.getDeletedDate()).isNotNull();
	}

	@Test
	void isPatientPresentByName_shouldReturnTrue_whenNameExists() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		boolean result = patientIoOperation.isPatientPresentByName(foundPatient.getName());
		assertThat(result).isTrue();
	}

	@Test
	void getNextPatientCode_shouldReturnMaxCodePlusOne() throws Exception {
		Integer code = setupTestPatient(false);
		Integer max = patientIoOperation.getNextPatientCode();
		assertThat((code + 1)).isEqualTo(max);
	}

	@Test
	void isCodePresent_shouldReturnTrueOnlyForExistingCode() throws Exception {
		Integer code = setupTestPatient(false);
		assertThat(patientIoOperation.isCodePresent(code)).isTrue();
		assertThat(patientIoOperation.isCodePresent(-987)).isFalse();
	}

	@Test
	void countAllActivePatients_shouldCountSavedPatient() throws Exception {
		assertThat(patientIoOperation.countAllActivePatients()).isZero();
		setupTestPatient(true);
		assertThat(patientIoOperation.countAllActivePatients()).isOne();
	}

	@Test
	void mergePatientHistory_shouldMarkObsoletePatientDeleted() throws Exception {
		// given:
		Patient mergedPatient = testPatient.setup(false);
		Patient obsoletePatient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(mergedPatient);
		patientIoOperationRepository.saveAndFlush(obsoletePatient);

		// when:
		patientIoOperation.mergePatientHistory(mergedPatient, obsoletePatient);

		// then:
		assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(mergedPatient, obsoletePatient);
	}
	
	@Test
	void getPatientByCodes_shouldReturnBothPatients_whenTwoCodesAreGiven() throws Exception {
		// given:
		Integer firstcode = setupTestPatientWithPhoto(false);
		Integer secondcode = setupTestPatient(false);
		List<Integer> codes = List.of(firstcode, secondcode);

		// when:
		List<Patient> patients = patientIoOperation.getPatientByCodes(codes);

		// then:
		assertThat(patients).isNotNull();
		assertThat(patients).isNotEmpty();
		assertThat(patients.size()).isEqualTo(2);
	}

	private void assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(Patient mergedPatient, Patient obsoletePatient) throws OHException {
		Patient mergedPatientResult = patientIoOperationRepository.findById(mergedPatient.getCode()).orElse(null);
		assertThat(mergedPatientResult).isNotNull();
		Patient obsoletePatientResult = patientIoOperationRepository.findById(obsoletePatient.getCode()).orElse(null);
		assertThat(obsoletePatientResult).isNotNull();
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo('Y');
		assertThat(mergedPatientResult.getDeleted()).isEqualTo('N');
	}

	private Integer setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient.getCode();
	}
	
	private Integer setupTestPatientWithPhoto(boolean usingSet) throws OHException, IOException {
		Patient patient = testPatient.setup(usingSet);
		PatientProfilePhoto patientProfilePhoto = new PatientProfilePhoto();

		File file = new File(getClass().getResource("patient.jpg").getFile());
		byte[] bytes = Files.readAllBytes(file.toPath());
		patientProfilePhoto.setPhoto(bytes);
		patient.setPatientProfilePhoto(patientProfilePhoto);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient.getCode();
	}

}
