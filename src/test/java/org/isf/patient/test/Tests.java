/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patient.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestPatient testPatient;

	@Autowired
	PatientIoOperations patientIoOperation;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testPatient = new TestPatient();
	}

	@Before
	public void setUp() throws OHException {
		cleanH2InMemoryDb();
	}

	@Test
	public void testPatientGets() throws Exception {
		Integer code = _setupTestPatient(false);
		_checkPatientIntoDb(code);
	}

	@Test
	public void testPatientSets() throws Exception {
		Integer code = _setupTestPatient(true);
		_checkPatientIntoDb(code);
	}

	@Test
	public void testIoGetPatients() throws Exception {
		_setupTestPatient(false);
		ArrayList<Patient> patients = patientIoOperation.getPatients();
		testPatient.check(patients.get(patients.size() - 1));
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLike() throws Exception {
		_setupTestPatient(false);
		// Pay attention that query return with PAT_ID descendant
		ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(null);
		testPatient.check(patients.get(0));
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeFirstName() throws Exception {
		// given:
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getFirstName());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeMiddleOfFirstName() throws Exception {
		// given:
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		ArrayList<Patient> patients = patientIoOperation
				.getPatientsByOneOfFieldsLike(foundPatient.getFirstName().substring(1, foundPatient.getFirstName().length() - 2));

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeSecondName() throws Exception {
		// given:
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeNote() throws Exception {
		// given:
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getSecondName());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeTaxCode() throws Exception {
		// given:
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);

		// when:
		ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike(foundPatient.getTaxCode());

		// then:
		testPatient.check(patients.get(0));
	}

	@Test
	public void testIoGetPatientsByOneOfFieldsLikeNotExistingStringShouldNotFindAnything() throws Exception {
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		ArrayList<Patient> patients = patientIoOperation.getPatientsByOneOfFieldsLike("dupa");
		assertThat(patients).isEmpty();
	}

	@Test
	public void testIoGetPatientFromName() throws Exception {
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientIoOperation.getPatient(foundPatient.getName());
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	public void testIoGetPatientFromCode() throws Exception {
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientIoOperation.getPatient(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	public void testIoGetPatientAll() throws Exception {
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		Patient patient = patientIoOperation.getPatientAll(code);
		assertThat(patient.getName()).isEqualTo(foundPatient.getName());
	}

	@Test
	public void testNewPatient() throws Exception {
		Patient patient = testPatient.setup(true);
		assertThat(patientIoOperation.savePatient(patient)).isNotNull();
	}

	@Test
	public void testUpdatePatientTrue() throws Exception {
		Integer code = _setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		Patient result = patientIoOperation.savePatient(patient);
		assertThat(result).isNotNull();
	}

	@Test
	public void testDeletePatient() throws Exception {
		Integer code = _setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		boolean result = patientIoOperation.deletePatient(patient);
		assertThat(result).isTrue();
	}

	@Test
	public void testIsPatientPresent() throws Exception {
		Integer code = _setupTestPatient(false);
		Patient foundPatient = patientIoOperation.getPatient(code);
		boolean result = patientIoOperation.isPatientPresentByName(foundPatient.getName());
		assertThat(result).isTrue();
	}

	@Test
	public void testGetNextPatientCode() throws Exception {
		Integer code = _setupTestPatient(false);
		Integer max = patientIoOperation.getNextPatientCode();
		assertThat((code + 1)).isEqualTo(max);
	}

	@Test
	public void testMergePatientHistory() throws Exception {
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

	private void assertThatObsoletePatientWasDeletedAndMergedIsTheActiveOne(Patient mergedPatient, Patient obsoletePatient) throws OHException {
		Patient mergedPatientResult = patientIoOperationRepository.findOne(mergedPatient.getCode());
		Patient obsoletePatientResult = patientIoOperationRepository.findOne(obsoletePatient.getCode());
		assertThat(obsoletePatientResult.getDeleted()).isEqualTo("Y");
		assertThat(mergedPatientResult.getDeleted()).isEqualTo("N");
	}

	private Integer _setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient.getCode();
	}

	private void _checkPatientIntoDb(Integer code) throws OHServiceException {
		Patient foundPatient = patientIoOperation.getPatient(code);
		testPatient.check(foundPatient);
	}
}

