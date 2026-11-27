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
package org.isf.patient.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PatientBrowserManagerTest extends OHCoreTestCase {

	private static TestPatient testPatient;

	@Autowired
	PatientIoOperations patientIoOperation;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	PatientBrowserManager patientBrowserManager;

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
	void getPatient_shouldReturnSavedPatient() throws Exception {
		setupTestPatient(false);
		List<Patient> patients = patientBrowserManager.getPatient();
		testPatient.check(patients.get(patients.size() - 1));
	}

	@Test
	void getPatientsPageable_shouldSplitFifteenPatientsIntoPagesOfTenAndFive() throws Exception {
		for (int idx = 0; idx < 15; idx++) {
			setupTestPatient(false);
		}

		// First page of 10
		PagedResponse<Patient> patients = patientBrowserManager.getPatientsPageable(0, 10);
		assertThat(patients.getData()).hasSize(10);
		testPatient.check(patients.getData().get(patients.getData().size() - 1));

		// Go get the next page or 10
		patients = patientBrowserManager.getPatientsPageable(1, 10);
		assertThat(patients.getData()).hasSize(5);
	}

	@Test
	void getPatients_shouldFindPatient_whenFirstNameBirthDateAndAddressMatch() throws Exception {
		setupTestPatient(false);
		Map<String, Object> params = new HashMap<>();
		params.put("firstName", "TestFirstName");
		params.put("birthDate", LocalDateTime.of(1984, Calendar.AUGUST, 14, 0, 0, 0));
		params.put("address", "TestAddress");
		List<Patient> patients = patientBrowserManager.getPatients(params);
		assertThat(patients).isNotEmpty();
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldReturnSavedPatient_whenKeywordIsNull() throws Exception {
		setupTestPatient(false);
		// Pay attention that query return with PAT_ID descendant
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(null);
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsFirstName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getFirstName());
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsPartOfFirstName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(
				foundPatient.getFirstName().substring(1, foundPatient.getFirstName().length() - 2));
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsSecondName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsSecondNameAndNoteIsNull() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldFindPatient_whenKeywordIsTaxCode() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getTaxCode());
		testPatient.check(patients.get(0));
	}

	@Test
	void getPatientsByOneOfFieldsLike_shouldReturnEmpty_whenKeywordMatchesNoPatient() throws Exception {
		setupTestPatient(false);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike("dupa");
		assertThat(patients).isEmpty();
	}

	@Test
	void retrievePatientProfilePhoto_shouldReturnProfilePhotoWithNullPhoto() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		PatientProfilePhoto patientProfilePhoto = patientBrowserManager.retrievePatientProfilePhoto(foundPatient);
		assertThat(patientProfilePhoto).isNotNull();
		assertThat(patientProfilePhoto.getPhoto()).isNull();
	}

	@Test
	void getPatientById_shouldReturnPatient_whenCodeExists() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientBrowserManager.getPatientById(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void getPatientById_shouldReturnNull_whenCodeDoesNotExist() throws Exception {
		assertThat(patientBrowserManager.getPatientById(-987654321)).isNull();
	}

	@Test
	void getPatientAll_shouldReturnPatient_whenCodeExists() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientBrowserManager.getPatientAll(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void savePatient_shouldSaveNewPatient() throws Exception {
		Patient patient = testPatient.setup(true);
		assertThat(patientBrowserManager.savePatient(patient)).isNotNull();
	}

	@Test
	void savePatient_shouldSaveNewFirstName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patient.setFirstName("someNewFirstName");
		Patient updatedPatient = patientBrowserManager.savePatient(patient);
		assertThat(updatedPatient).isNotNull();
		assertThat(updatedPatient.getFirstName()).isEqualTo(patient.getFirstName());
	}

	@Test
	void deletePatient_shouldMakePatientNotFoundByName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patientBrowserManager.deletePatient(patient);
		Patient deletedPatient = patientIoOperation.getPatient(patient.getName());
		assertThat(deletedPatient).isNull();
	}

	@Test
	void deletePatient_shouldMakePatientNotFoundByName_whenPatientExists() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patientBrowserManager.deletePatient(patient);
		Patient deletedPatient = patientIoOperation.getPatient(patient.getName());
		assertThat(deletedPatient).isNull();
	}

	@Test
	void isNamePresent_shouldReturnTrue_whenNameExists() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		assertThat(patientBrowserManager.isNamePresent(foundPatient.getName())).isTrue();
	}

	@Test
	void isNamePresent_shouldReturnFalse_whenNameDoesNotExist() throws Exception {
		assertThat(patientBrowserManager.isNamePresent("someNameWeAreSureDoesNotExist")).isFalse();
	}

	@Test
	void getNextPatientCode_shouldReturnMaxCodePlusOne() throws Exception {
		Integer code = setupTestPatient(false);
		Integer max = patientBrowserManager.getNextPatientCode();
		assertThat((code + 1)).isEqualTo(max);
	}

	@Test
	void getCities_shouldReturnCityOfSavedPatient() throws  Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<String> cities = patientBrowserManager.getCities();
		assertThat(cities).isNotEmpty();
		assertThat(cities.get(0)).isEqualTo(foundPatient.getCity());
	}

	@Test
	void getCities_shouldReturnEmpty_whenNoPatientIsSaved() throws  Exception {
		List<String> cities = patientBrowserManager.getCities();
		assertThat(cities).isEmpty();
	}

	@Test
	void getMaritalList_shouldReturnNonEmptyList() throws Exception {
		resetHashMaps();
		String[] maritalDescriptionList = patientBrowserManager.getMaritalList();
		assertThat(maritalDescriptionList).isNotEmpty();
	}

	@Test
	void getMaritalTranslated_shouldReturnMatchingDescriptionOrUnknown() throws Exception {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getMaritalTranslated(null)).isEqualTo("angal.patient.maritalstatusunknown.txt");
		assertThat(patientBrowserManager.getMaritalTranslated("someKeyNotInTheList")).isEqualTo("angal.patient.maritalstatusunknown.txt");
		assertThat(patientBrowserManager.getMaritalTranslated("married")).isEqualTo("angal.patient.maritalstatusmarried.txt");
	}

	@Test
	void getMaritalKey_shouldReturnMatchingKeyOrUndefined() throws Exception {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getMaritalKey(null)).isEqualTo("undefined");
		assertThat(patientBrowserManager.getMaritalKey("someKeyNotInTheList")).isEqualTo("undefined");
		assertThat(patientBrowserManager.getMaritalKey("angal.patient.maritalstatusmarried.txt")).isEqualTo("married");
	}

	@Test
	void getProfessionList_shouldReturnNonEmptyList() throws Exception {
		resetHashMaps();
		String[] maritalDescriptionList = patientBrowserManager.getProfessionList();
		assertThat(maritalDescriptionList).isNotEmpty();
	}

	@Test
	void getProfessionTranslated_shouldReturnMatchingDescriptionOrUnknown() throws Exception {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getProfessionTranslated(null)).isEqualTo("angal.patient.profession.unknown.txt");
		assertThat(patientBrowserManager.getProfessionTranslated("someKeyNotInTheList")).isEqualTo("angal.patient.profession.unknown.txt");
		assertThat(patientBrowserManager.getProfessionTranslated("mining")).isEqualTo("angal.patient.profession.mining.txt");
	}

	@Test
	void getProfessionKey_shouldReturnMatchingKeyOrUndefined() throws Exception {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getProfessionKey(null)).isEqualTo("undefined");
		assertThat(patientBrowserManager.getProfessionKey("someKeyNotInTheList")).isEqualTo("undefined");
		assertThat(patientBrowserManager.getProfessionKey("angal.patient.profession.mining.txt")).isEqualTo("mining");
	}

	@Test
	void savePatient_shouldThrow_whenFirstNameIsEmpty() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setFirstName("");

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void savePatient_shouldThrow_whenFirstNameIsNull() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setFirstName(null);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void savePatient_shouldThrow_whenSecondNameIsEmpty() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setSecondName("");

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void savePatient_shouldThrow_whenSecondNameIsNull() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setSecondName(null);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void savePatient_shouldThrow_whenBirthDateIsNull() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setBirthDate(null);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void savePatient_shouldThrow_whenBirthDateIsMoreThan200YearsAgo() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setBirthDate(LocalDate.of(999, 1, 1));

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void savePatient_shouldThrow_whenBirthDateIsNullAndAgeIsNegative() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setBirthDate(null);
			patient.setAge(-1);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void savePatient_shouldThrow_whenBirthDateIsNullAndAgeIsOver200() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setBirthDate(null);
			patient.setAge(201);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void savePatient_shouldThrow_whenSexIsBlank() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setSex(' ');

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	private void resetHashMaps() throws Exception {
		Field diuresisDescriptionHashMap = patientBrowserManager.getClass().getDeclaredField("maritalHashMap");
		diuresisDescriptionHashMap.setAccessible(true);
		diuresisDescriptionHashMap.set(patientBrowserManager, null);

		Field bowelDescriptionHashMap = patientBrowserManager.getClass().getDeclaredField("professionHashMap");
		bowelDescriptionHashMap.setAccessible(true);
		bowelDescriptionHashMap.set(patientBrowserManager, null);
	}

	private Integer setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient.getCode();
	}

}
