/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.opd.TestOpd;
import org.isf.opd.model.Opd;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class Tests extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestOpd testOpd;

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
		testOpd = new TestOpd();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testPatientGets() throws Exception {
		Integer code = setupTestPatient(false);
		checkPatientIntoDb(code);
	}

	@Test
	void testPatientSets() throws Exception {
		Integer code = setupTestPatient(true);
		checkPatientIntoDb(code);
	}

	@Test
	void testIoGetPatients() throws Exception {
		setupTestPatient(false);
		List<Patient> patients = patientIoOperation.getPatients();
		testPatient.check(patients.get(patients.size() - 1));
	}
	
	@Test
	void testIoGetPatientsPageable() throws Exception {
		setupTestPatient(false); 
		PagedResponse<Patient> patients = patientIoOperation.getPatientsPageable(createPageRequest());
		testPatient.check(patients.getData().get(patients.getData().size() - 1)); 
	}
	 
	private Pageable createPageRequest() {
		return PageRequest.of(0, 10); // Page size 10
	}

	@Test
	void testIoGetPatientsByOneOfFieldsLike() throws Exception {
		setupTestPatient(false);
		// Pay attention that query return with PAT_ID descendant
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(null);
		testPatient.check(patients.get(0));
	}

	@Test
	void testIoGetPatientsByOneOfFieldsLikeFirstName() throws Exception {
		// given:
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getFirstName());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	void testIoGetPatientsByOneOfFieldsLikeMiddleOfFirstName() throws Exception {
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
	void testIoGetPatientsByOneOfFieldsLikeSecondName() throws Exception {
		// given:
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	void testIoGetPatientsByOneOfFieldsLikeNote() throws Exception {
		// given:
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	void testIoGetPatientsByOneOfFieldsLikeTaxCode() throws Exception {
		// given:
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getTaxCode());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	void testIoGetPatientsByOneOfFieldsLikeNotExistingStringShouldNotFindAnything() throws Exception {
		setupTestPatient(false);
		List<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike("dupa");
		assertThat(patients).isEmpty();
	}

	@Test
	void testIoGetPatientFromName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientIoOperation.getPatient(foundPatient.getName());
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void testIoGetPatientFromNameDoesNotExist() throws Exception {
		assertThat(patientIoOperation.getPatient("someUnusualNameThatWillNotBeFound")).isNull();
	}

	@Test
	void testIoGetPatientFromCode() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientIoOperation.getPatient(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void testIoGetPatientFromCodeDoesNotExist() throws Exception {
		assertThat(patientIoOperation.getPatient(-987654321)).isNull();
	}

	@Test
	void testIoGetPatientAll() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientIoOperation.getPatientAll(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void testIoGetPatientsByParams() throws Exception {
		setupTestPatient(false);
		Map<String, Object> params = new HashMap<>();
		params.put("firstName", "TestFirstName");
		params.put("birthDate", LocalDateTime.of(1984, Calendar.AUGUST, 14, 0, 0, 0));
		params.put("address", "TestAddress");
		List<Patient> patients = patientIoOperation.getPatients(params);
		assertThat(patients).isNotEmpty();
	}

	@Test
	void testIoSaveNewPatient() throws Exception {
		Patient patient = testPatient.setup(true);
		assertThat(patientIoOperation.savePatient(patient)).isNotNull();
	}

	@Test
	void testIoUpdatePatient() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patient.setFirstName("someNewFirstName");
		Patient updatedPatient = patientIoOperation.updatePatient(patient);
		assertThat(updatedPatient.getFirstName()).isEqualTo(patient.getFirstName());
	}

	@Test
	void testIoDeletePatient() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patientIoOperation.deletePatient(patient);
		Patient deletedPatient = patientIoOperation.getPatient(patient.getName());
		assertThat(deletedPatient).isNull();
	}

	@Test
	void testIoIsPatientPresent() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		boolean result = patientIoOperation.isPatientPresentByName(foundPatient.getName());
		assertThat(result).isTrue();
	}

	@Test
	void testIoGetNextPatientCode() throws Exception {
		Integer code = setupTestPatient(false);
		Integer max = patientIoOperation.getNextPatientCode();
		assertThat((code + 1)).isEqualTo(max);
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		Integer code = setupTestPatient(false);
		assertThat(patientIoOperation.isCodePresent(code)).isTrue();
		assertThat(patientIoOperation.isCodePresent(-987)).isFalse();
	}

	@Test
	void testCountAllActivePatients() throws Exception {
		assertThat(patientIoOperation.countAllActivePatients()).isZero();
		setupTestPatient(true);
		assertThat(patientIoOperation.countAllActivePatients()).isOne();
	}

	@Test
	void testMergePatientHistory() throws Exception {
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
	void testMgrGetPatients() throws Exception {
		setupTestPatient(false);
		List<Patient> patients = patientBrowserManager.getPatient();
		testPatient.check(patients.get(patients.size() - 1));
	}

	@Test
	void testMgrGetPatientsPageable() throws Exception {
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
	void testMgrGetPatientsByParams() throws Exception {
		setupTestPatient(false);
		Map<String, Object> params = new HashMap<>();
		params.put("firstName", "TestFirstName");
		params.put("birthDate", LocalDateTime.of(1984, Calendar.AUGUST, 14, 0, 0, 0));
		params.put("address", "TestAddress");
		List<Patient> patients = patientBrowserManager.getPatients(params);
		assertThat(patients).isNotEmpty();
	}

	@Test
	void testMgrGetPatientsByOneOfFieldsLike() throws Exception {
		setupTestPatient(false);
		// Pay attention that query return with PAT_ID descendant
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(null);
		testPatient.check(patients.get(0));
	}

	@Test
	void testMgrGetPatientsByOneOfFieldsLikeFirstName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getFirstName());
		testPatient.check(patients.get(0));
	}

	@Test
	void testMgrGetPatientsByOneOfFieldsLikeMiddleOfFirstName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(
				foundPatient.getFirstName().substring(1, foundPatient.getFirstName().length() - 2));
		testPatient.check(patients.get(0));
	}

	@Test
	void testMgrGetPatientsByOneOfFieldsLikeSecondName() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());
		testPatient.check(patients.get(0));
	}

	@Test
	void testMgrGetPatientsByOneOfFieldsLikeNote() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());
		testPatient.check(patients.get(0));
	}

	@Test
	void testMgrGetPatientsByOneOfFieldsLikeTaxCode() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getTaxCode());
		testPatient.check(patients.get(0));
	}

	@Test
	void testMgrGetPatientsByOneOfFieldsLikeNotExistingStringShouldNotFindAnything() throws Exception {
		setupTestPatient(false);
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike("dupa");
		assertThat(patients).isEmpty();
	}

	@Test
	void testMgrRetrievePatientProfilePhoto() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		PatientProfilePhoto patientProfilePhoto = patientBrowserManager.retrievePatientProfilePhoto(foundPatient);
		assertThat(patientProfilePhoto).isNotNull();
		assertThat(patientProfilePhoto.getPhoto()).isNull();
	}

	@Test
	void testMgrGetPatientById() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientBrowserManager.getPatientById(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void testMgrGetPatientByIdDoesNotExist() throws Exception {
		assertThat(patientBrowserManager.getPatientById(-987654321)).isNull();
	}

	@Test
	void testMgrGetPatientAll() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientBrowserManager.getPatientAll(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void testMgrSaveNewPatient() throws Exception {
		Patient patient = testPatient.setup(true);
		assertThat(patientBrowserManager.savePatient(patient)).isNotNull();
	}

	@Test
	void testMgrUpdatePatient() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patient.setFirstName("someNewFirstName");
		Patient updatedPatient = patientBrowserManager.savePatient(patient);
		assertThat(updatedPatient).isNotNull();
		assertThat(updatedPatient.getFirstName()).isEqualTo(patient.getFirstName());
	}

	@Test
	void testMgrDeletePatient() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patientBrowserManager.deletePatient(patient);
		Patient deletedPatient = patientIoOperation.getPatient(patient.getName());
		assertThat(deletedPatient).isNull();
	}

	@Test
	void testMgrDeletePatientNotFound() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		patientBrowserManager.deletePatient(patient);
		Patient deletedPatient = patientIoOperation.getPatient(patient.getName());
		assertThat(deletedPatient).isNull();
	}

	@Test
	void testMgrIsNamePresent() throws Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		assertThat(patientBrowserManager.isNamePresent(foundPatient.getName())).isTrue();
	}

	@Test
	void testMgrIsNamePresentNotFound() throws Exception {
		assertThat(patientBrowserManager.isNamePresent("someNameWeAreSureDoesNotExist")).isFalse();
	}

	@Test
	void testMgrGetNextPatientCode() throws Exception {
		Integer code = setupTestPatient(false);
		Integer max = patientBrowserManager.getNextPatientCode();
		assertThat((code + 1)).isEqualTo(max);
	}

	@Test
	void testGetCities() throws  Exception {
		Integer code = setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		List<String> cities = patientBrowserManager.getCities();
		assertThat(cities).isNotEmpty();
		assertThat(cities.get(0)).isEqualTo(foundPatient.getCity());
	}

	@Test
	void testGetCitiesNoneFound() throws  Exception {
		List<String> cities = patientBrowserManager.getCities();
		assertThat(cities).isEmpty();
	}

	@Test
	void testMgrGetMaritalList() throws Exception {
		resetHashMaps();
		String[] maritalDescriptionList = patientBrowserManager.getMaritalList();
		assertThat(maritalDescriptionList).isNotEmpty();
	}

	@Test
	void testMgrGetMaritalTranslated() throws Exception {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getMaritalTranslated(null)).isEqualTo("angal.patient.maritalstatusunknown.txt");
		assertThat(patientBrowserManager.getMaritalTranslated("someKeyNotInTheList")).isEqualTo("angal.patient.maritalstatusunknown.txt");
		assertThat(patientBrowserManager.getMaritalTranslated("married")).isEqualTo("angal.patient.maritalstatusmarried.txt");
	}

	@Test
	void testMgrGetMaritalKey() throws Exception {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getMaritalKey(null)).isEqualTo("undefined");
		assertThat(patientBrowserManager.getMaritalKey("someKeyNotInTheList")).isEqualTo("undefined");
		assertThat(patientBrowserManager.getMaritalKey("angal.patient.maritalstatusmarried.txt")).isEqualTo("married");
	}

	@Test
	void testMgrGetProfessionList() throws Exception {
		resetHashMaps();
		String[] maritalDescriptionList = patientBrowserManager.getProfessionList();
		assertThat(maritalDescriptionList).isNotEmpty();
	}

	@Test
	void testMgrGetProfessionTranslated() throws Exception {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getProfessionTranslated(null)).isEqualTo("angal.patient.profession.unknown.txt");
		assertThat(patientBrowserManager.getProfessionTranslated("someKeyNotInTheList")).isEqualTo("angal.patient.profession.unknown.txt");
		assertThat(patientBrowserManager.getProfessionTranslated("mining")).isEqualTo("angal.patient.profession.mining.txt");
	}

	@Test
	void testMgrGetProfessionKey() throws Exception {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getProfessionKey(null)).isEqualTo("undefined");
		assertThat(patientBrowserManager.getProfessionKey("someKeyNotInTheList")).isEqualTo("undefined");
		assertThat(patientBrowserManager.getProfessionKey("angal.patient.profession.mining.txt")).isEqualTo("mining");
	}

	@Test
	void testMgrPatientValidationNoFirstName() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setFirstName("");

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrPatientValidationNullFirstName() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setFirstName(null);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrPatientValidationNoSecondName() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setSecondName("");

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrPatientValidationNullSecondName() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setSecondName(null);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrPatientValidationBirthDateNull() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setBirthDate(null);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrPatientValidationBirthDateTooFarInFuture() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setBirthDate(LocalDate.of(999, 1, 1));

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrPatientValidationAgeLessThanZero() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setBirthDate(null);
			patient.setAge(-1);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrPatientValidationAgeToHigh() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setBirthDate(null);
			patient.setAge(201);

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrPatientValidationSexEmpty() {
		assertThatThrownBy(() -> {
			Patient patient = testPatient.setup(true);

			patient.setSex(' ');

			patientBrowserManager.savePatient(patient);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
				(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testPatientOpdConstructor() throws Exception {
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
	void testPatientConstructor() {
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
	void testPatientGetSearchString() throws Exception {
		Patient patient = testPatient.setup(false);
		patient.setCode(1);
		assertThat(patient.getSearchString()).isEqualTo("1 testfirstname testsecondname testcity testaddress TestTelephone testtaxcode ");
	}

	@Test
	void testPatientGetInformations() throws Exception {
		Patient patient = testPatient.setup(false);
		patient.setNote("someNote");
		assertThat(patient.getInformations()).isEqualTo("TestCity - TestAddress - TestTelephone - someNote - TestTaxCode");
	}

	@Test
	void testPatientEquals() throws Exception {
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
	void testPatientHashCode() throws Exception {
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

	// This test requires access to the opencv java native library
	@Disabled
	@Test
	void testPatientProfilePhoto() throws Exception {
		Patient patient = testPatient.setup(true);
		PatientProfilePhoto patientProfilePhoto = new PatientProfilePhoto();

		File file = new File(getClass().getResource("patient.jpg").getFile());
		byte[] bytes = Files.readAllBytes(file.toPath());
		patientProfilePhoto.setPhoto(bytes);
		assertThat(patientProfilePhoto.getPhotoAsImage()).isNotNull();

		patientProfilePhoto.setPhoto(null);
		assertThat(patientProfilePhoto.getPhoto()).isNull();

		patientProfilePhoto.setPatient(patient);
		assertThat(patientProfilePhoto.getPatient()).isEqualTo(patient);
	}

	private void resetHashMaps() throws Exception {
		Field diuresisDescriptionHashMap = patientBrowserManager.getClass().getDeclaredField("maritalHashMap");
		diuresisDescriptionHashMap.setAccessible(true);
		diuresisDescriptionHashMap.set(patientBrowserManager, null);

		Field bowelDescriptionHashMap = patientBrowserManager.getClass().getDeclaredField("professionHashMap");
		bowelDescriptionHashMap.setAccessible(true);
		bowelDescriptionHashMap.set(patientBrowserManager, null);
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

	private void checkPatientIntoDb(Integer code) throws OHServiceException {
		Patient foundPatient = patientIoOperation.getPatient(code);
		testPatient.check(foundPatient);
	}
}