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
package org.isf.patient.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.isf.patient.data.PatientHelper.PATIENT_DATA_TABLE;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase5;
import org.isf.patient.data.PatientHelper;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientManagerTests extends OHCoreTestCase5 {

	private static PatientHelper createPatient;

	@Autowired
	private PatientIoOperations patientIoOperation;
	@Autowired
	private PatientBrowserManager patientBrowserManager;

	@BeforeAll
	public void beforeAll() {
		createPatient = new PatientHelper();
	}

	@BeforeEach
	public void beforeEach() {
		cleanH2InMemoryDb();
	}

	private Pageable createPageRequest() {
		return PageRequest.of(0, 10);   // Page size 10
	}

	@Test
	void getPatients() throws Exception {
		createPatient.createPatient();
		List<Patient> patients = patientBrowserManager.getPatient();
		createPatient.checkPatientInDb(patients.get(patients.size() - 1).getCode());
	}

	@Test
	void getPatientsByParams() throws Exception {
		createPatient.createPatient();
		Map<String, Object> params = new HashMap<>();
		params.put("firstName", PATIENT_DATA_TABLE[2].getFirstName());
		params.put("birthDate", PATIENT_DATA_TABLE[2].getBirthDate().atStartOfDay());
		params.put("address", PATIENT_DATA_TABLE[2].getAddress());
		List<Patient> patients = patientBrowserManager.getPatients(params);
		assertThat(patients.size()).isPositive();
	}

	@Test
	void getPatientsPageable() throws Exception {
		createPatient.createPatient(15);

		// First page of 10
		List<Patient> patients = patientBrowserManager.getPatient(0, 10);
		assertThat(patients).hasSize(10);
		createPatient.checkPatientInDb(patients.get(patients.size() - 1).getCode());

		// Go get the next page or 10
		patients = patientBrowserManager.getPatient(1, 10);
		assertThat(patients).hasSize(5);
	}

	@Test
	void getPatientsByOneOfFieldsLike() throws Exception {
		createPatient.createPatient();
		// Pay attention that query return with PAT_ID descendant
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(null);
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeFirstName() throws Exception {
		List<Integer> codes = createPatient.createPatient(7);
		Patient foundPatient = patientIoOperation.getPatient(codes.get(3));
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getFirstName());
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeMiddleOfFirstName() throws Exception {
		List<Integer> codes = createPatient.createPatient(5);
		Patient foundPatient = patientIoOperation.getPatient(codes.get(2));

		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(
				foundPatient.getFirstName().substring(1, foundPatient.getFirstName().length() - 2));
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeSecondName() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(4));
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeNote() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(8));
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeTaxCode() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(2));
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike(foundPatient.getTaxCode());
		createPatient.checkPatientInDb(patients.get(0).getCode());
	}

	@Test
	void getPatientsByOneOfFieldsLikeNotExistingStringShouldNotFindAnything() throws Exception {
		createPatient.createPatient();
		List<Patient> patients = patientBrowserManager.getPatientsByOneOfFieldsLike("dupaxyzzy");
		assertThat(patients).isEmpty();
	}

	@Test
	void getPatientByName() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(2));
		Patient patient = patientBrowserManager.getPatientByName(foundPatient.getName());
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void getPatientByNameDoesNotExist() throws Exception {
		assertThat(patientBrowserManager.getPatientByName("someUnusualNameThatWillNotBeFound")).isNull();
	}

	@Test
	void getPatientById() throws Exception {
		List<Integer> codes = createPatient.createPatient(6);
		Patient foundPatient = patientIoOperation.getPatient(codes.get(4));
		Patient patient = patientBrowserManager.getPatientById(codes.get(4));
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void getPatienByIdDoesNotExist() throws Exception {
		assertThat(patientBrowserManager.getPatientById(-987654321)).isNull();
	}

	@Test
	void getPatientAll() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(0));
		Patient patient = patientBrowserManager.getPatientAll(codes.get(0));
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	void saveNewPatient() throws Exception {
		assertThat(patientBrowserManager.savePatient(PATIENT_DATA_TABLE[0])).isNotNull();
	}

	@Test
	void updatePatient() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient patient = patientIoOperation.getPatient(codes.get(6));
		patient.setFirstName("someNewFirstName");
		Patient updatedPatient = patientBrowserManager.savePatient(patient);
		assertThat(updatedPatient).isNotNull();
		assertThat(updatedPatient.getFirstName()).isEqualTo(patient.getFirstName());
	}

	@Test
	void deletePatient() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient patient = patientIoOperation.getPatient(codes.get(1));
		assertThat(patientBrowserManager.deletePatient(patient)).isTrue();
	}

	@Test
	void isNamePresent() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		Patient foundPatient = patientIoOperation.getPatient(codes.get(8));
		assertThat(patientBrowserManager.isNamePresent(foundPatient.getName())).isTrue();
	}

	@Test
	void isNamePresentNotFound() throws Exception {
		assertThat(patientBrowserManager.isNamePresent("someNameWeAreSureDoesNotExist")).isFalse();
	}

	@Test
	void getNextPatientCode() throws Exception {
		List<Integer> codes = createPatient.createPatient();
		int max = patientBrowserManager.getNextPatientCode();
		assertThat((codes.size() + 1)).isEqualTo(max);
	}

	@Test
	void getMaritalList() {
		resetHashMaps();
		String[] maritalDescriptionList = patientBrowserManager.getMaritalList();
		assertThat(maritalDescriptionList).isNotEmpty();
	}

	@Test
	void getMaritalTranslated() {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getMaritalTranslated(null)).isEqualTo("angal.patient.maritalstatusunknown.txt");
		assertThat(patientBrowserManager.getMaritalTranslated("someKeyNotInTheList")).isEqualTo("angal.patient.maritalstatusunknown.txt");
		assertThat(patientBrowserManager.getMaritalTranslated("married")).isEqualTo("angal.patient.maritalstatusmarried.txt");
	}

	@Test
	void getMaritalKey() {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getMaritalKey(null)).isEqualTo("undefined");
		assertThat(patientBrowserManager.getMaritalKey("someKeyNotInTheList")).isEqualTo("undefined");
		assertThat(patientBrowserManager.getMaritalKey("angal.patient.maritalstatusmarried.txt")).isEqualTo("married");
	}

	@Test
	void getProfessionList() {
		resetHashMaps();
		String[] maritalDescriptionList = patientBrowserManager.getProfessionList();
		assertThat(maritalDescriptionList).isNotEmpty();
	}

	@Test
	void getProfessionTranslated() {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getProfessionTranslated(null)).isEqualTo("angal.patient.profession.unknown.txt");
		assertThat(patientBrowserManager.getProfessionTranslated("someKeyNotInTheList")).isEqualTo("angal.patient.profession.unknown.txt");
		assertThat(patientBrowserManager.getProfessionTranslated("mining")).isEqualTo("angal.patient.profession.mining.txt");
	}

	@Test
	void getProfessionKey() {
		resetHashMaps();
		// TODO: if resource bundles are made avaiable in core then the values being compared will need to change
		assertThat(patientBrowserManager.getProfessionKey(null)).isEqualTo("undefined");
		assertThat(patientBrowserManager.getProfessionKey("someKeyNotInTheList")).isEqualTo("undefined");
		assertThat(patientBrowserManager.getProfessionKey("angal.patient.profession.mining.txt")).isEqualTo("mining");
	}

	@Test
	void patientValidationNoFirstName() {
		assertThatThrownBy(() -> {
			List<Integer> codes = createPatient.createPatient(7);
			Patient patient = patientIoOperation.getPatient(codes.get(codes.size() - 1));

			patient.setFirstName("");

			patientBrowserManager.savePatient(patient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void patientValidationNullFirstName() {
		assertThatThrownBy(() -> {
			List<Integer> codes = createPatient.createPatient(8);
			Patient patient = patientIoOperation.getPatient(codes.get(codes.size() - 1));

			patient.setFirstName(null);

			patientBrowserManager.savePatient(patient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void patientValidationNoSecondName() {
		assertThatThrownBy(() -> {
			List<Integer> codes = createPatient.createPatient(9);
			Patient patient = patientIoOperation.getPatient(codes.get(codes.size() - 1));

			patient.setSecondName("");

			patientBrowserManager.savePatient(patient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void patientValidationNullSecondName() {
		assertThatThrownBy(() -> {
			List<Integer> codes = createPatient.createPatient(10);
			Patient patient = patientIoOperation.getPatient(codes.get(codes.size() - 1));

			patient.setSecondName(null);

			patientBrowserManager.savePatient(patient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void patientValidationBirthDateNull() {
		assertThatThrownBy(() -> {
			List<Integer> codes = createPatient.createPatient(11);
			Patient patient = patientIoOperation.getPatient(codes.get(codes.size() - 1));

			patient.setBirthDate(null);

			patientBrowserManager.savePatient(patient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void patientValidationBirthDateTooFarInFuture() {
		assertThatThrownBy(() -> {
			List<Integer> codes = createPatient.createPatient(12);
			Patient patient = patientIoOperation.getPatient(codes.get(codes.size() - 1));

			patient.setBirthDate(LocalDate.of(999, 1, 1));

			patientBrowserManager.savePatient(patient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void patientValidationAgeLessThanZero() {
		assertThatThrownBy(() -> {
			List<Integer> codes = createPatient.createPatient(13);
			Patient patient = patientIoOperation.getPatient(codes.get(codes.size() - 1));

			patient.setBirthDate(null);
			patient.setAge(-1);

			patientBrowserManager.savePatient(patient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void patientValidationAgeToHigh() {
		assertThatThrownBy(() -> {
			List<Integer> codes = createPatient.createPatient(14);
			Patient patient = patientIoOperation.getPatient(codes.get(codes.size() - 1));

			patient.setBirthDate(null);
			patient.setAge(201);

			patientBrowserManager.savePatient(patient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void patientValidationSexEmpty() {
		assertThatThrownBy(() -> {
			List<Integer> codes = createPatient.createPatient(15);
			Patient patient = patientIoOperation.getPatient(codes.get(codes.size() - 1));

			patient.setSex(' ');

			patientBrowserManager.savePatient(patient);
		})
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	private void resetHashMaps() {
		patientBrowserManager.maritalHashMap = null;
		patientBrowserManager.professionHashMap = null;
	}

}
