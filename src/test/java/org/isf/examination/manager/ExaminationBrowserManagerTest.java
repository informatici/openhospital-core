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
package org.isf.examination.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.examination.TestPatientExamination;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExaminationBrowserManagerTest extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestPatientExamination testPatientExamination;

	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
	@Autowired
	ExaminationBrowserManager examinationBrowserManager;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testPatient = new TestPatient();
		testPatientExamination = new TestPatientExamination();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getFromLastPatientExamination_shouldCopyLastExaminationValues() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		PatientExamination patientExamination = examinationBrowserManager.getFromLastPatientExamination(lastPatientExamination);
		testPatientExamination.check(patientExamination);
		testPatient.check(patientExamination.getPatient());
	}

	@Test
	void saveOrUpdate_shouldKeepUpdatedHeartRate() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(patientExamination).isNotNull();
		Integer pex_hr = patientExamination.getPex_hr();
		patientExamination.setPex_hr(pex_hr + 1);
		resetHashMaps();
		assertThat(examinationBrowserManager.getDiuresisDescriptionList()).isNotEmpty();
		assertThat(examinationBrowserManager.getBowelDescriptionList()).isNotEmpty();
		assertThat(examinationBrowserManager.getAuscultationList()).isNotEmpty();
		examinationBrowserManager.saveOrUpdate(patientExamination);
		assertThat(patientExamination.getPex_hr()).isEqualTo((Integer) (pex_hr + 1));
	}

	@Test
	void getLastByPatID_shouldReturnLastExaminationOfPatient() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		PatientExamination foundExamination = examinationBrowserManager.getLastByPatID(patient.getCode());
		checkPatientExaminationIntoDb(foundExamination.getPex_ID());
	}

	@Test
	void getLastNByPatID_shouldReturnLastExaminationsOfPatient() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		List<PatientExamination> foundExamination = examinationBrowserManager.getLastNByPatID(patient.getCode(), 1);
		checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}

	@Test
	void getByPatID_shouldReturnExaminationsOfPatient() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		List<PatientExamination> foundExamination = examinationBrowserManager.getByPatID(patient.getCode());
		checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}

	@Test
	void remove_shouldDeleteExaminations() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(patientExamination).isNotNull();
		List<PatientExamination> patexList = new ArrayList<>(1);
		patexList.add(patientExamination);
		examinationBrowserManager.remove(patexList);
		assertThat(examinationIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void getTranslatedVariants_shouldTranslateKnownKeysAndReturnNullForUnknownKeys() throws Exception {
		resetHashMaps();

		assertThat(examinationBrowserManager.getAuscultationTranslated("normal"))
				.isNotEmpty()
				.startsWith("angal");
		assertThat(examinationBrowserManager.getAuscultationTranslated("not there")).isNull();

		assertThat(examinationBrowserManager.getBowelDescriptionTranslated("regular"))
				.isNotEmpty()
				.startsWith("angal");
		assertThat(examinationBrowserManager.getBowelDescriptionTranslated("not there")).isNull();

		assertThat(examinationBrowserManager.getDiuresisDescriptionTranslated("frequent"))
				.isNotEmpty()
				.startsWith("angal");
		assertThat(examinationBrowserManager.getDiuresisDescriptionTranslated("not there")).isNull();
	}

	@Test
	void getAuscultationKey_shouldReturnMatchingKeyOrEmptyString() throws Exception {
		resetHashMaps();
		assertThat(examinationBrowserManager.getAuscultationKey("shouldNotBeThere")).isEmpty();
		setKnownKeyValueInHashMaps();
		assertThat(examinationBrowserManager.getAuscultationKey("knownValue")).isEqualTo("knownKey");
	}

	@Test
	void getBowelDescriptionKey_shouldReturnMatchingKeyOrEmptyString() throws Exception {
		resetHashMaps();
		assertThat(examinationBrowserManager.getBowelDescriptionKey("shouldNotBeThere")).isEmpty();
		setKnownKeyValueInHashMaps();
		assertThat(examinationBrowserManager.getBowelDescriptionKey("knownValue")).isEqualTo("knownKey");
	}

	@Test
	void getDiuresisDescriptionKey_shouldReturnMatchingKeyOrEmptyString() throws Exception {
		resetHashMaps();
		assertThat(examinationBrowserManager.getDiuresisDescriptionKey("shouldNotBeThere")).isEmpty();
		setKnownKeyValueInHashMaps();
		assertThat(examinationBrowserManager.getDiuresisDescriptionKey("knownValue")).isEqualTo("knownKey");
	}

	@Test
	void getBMIdescription_shouldReturnCategoryForEachBmiRange() throws Exception {
		// TODO: if message resources are added to the project this code needs to be changed
		assertThat(examinationBrowserManager.getBMIdescription(0.0d)).isEqualTo("angal.examination.bmi.severeunderweight.txt");
		assertThat(examinationBrowserManager.getBMIdescription(17.0d)).isEqualTo("angal.examination.bmi.underweight.txt");
		assertThat(examinationBrowserManager.getBMIdescription(20.0d)).isEqualTo("angal.examination.bmi.normalweight.txt");
		assertThat(examinationBrowserManager.getBMIdescription(27.0d)).isEqualTo("angal.examination.bmi.overweight.txt");
		assertThat(examinationBrowserManager.getBMIdescription(33.0d)).isEqualTo("angal.examination.bmi.obesityclassilight.txt");
		assertThat(examinationBrowserManager.getBMIdescription(37.0d)).isEqualTo("angal.examination.bmi.obesityclassiimedium.txt");
		assertThat(examinationBrowserManager.getBMIdescription(100.0d)).isEqualTo("angal.examination.bmi.obesityclassiiisevere.txt");
	}

	@Test
	void getDefaultPatientExamination_shouldReturnNonNullExamination() throws Exception {
		Patient patient = testPatient.setup(false);
		assertThat(examinationBrowserManager.getDefaultPatientExamination(patient)).isNotNull();
	}

	@Test
	void saveOrUpdate_shouldThrow_whenDiuresisBowelOrAuscultationIsUnknown() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(patientExamination).isNotNull();

		patientExamination.setPex_diuresis_desc("somethingNotThere");
		patientExamination.setPex_bowel_desc(null);
		patientExamination.setPex_auscultation(null);

		assertThatThrownBy(() -> examinationBrowserManager.saveOrUpdate(patientExamination))
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);

		patientExamination.setPex_diuresis_desc(null);
		patientExamination.setPex_bowel_desc("somethingNotThere");
		patientExamination.setPex_auscultation(null);

		assertThatThrownBy(() -> examinationBrowserManager.saveOrUpdate(patientExamination))
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);

		patientExamination.setPex_diuresis_desc(null);
		patientExamination.setPex_bowel_desc(null);
		patientExamination.setPex_auscultation("somethingNotThere");

		assertThatThrownBy(() -> examinationBrowserManager.saveOrUpdate(patientExamination))
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	private int setupTestPatientExamination(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PatientExamination patientExamination = testPatientExamination.setup(patient, usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(patientExamination);
		return patientExamination.getPex_ID();
	}

	private void checkPatientExaminationIntoDb(int id) throws OHException {
		PatientExamination foundPatientExamination = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPatientExamination).isNotNull();
		testPatientExamination.check(foundPatientExamination);
		testPatient.check(foundPatientExamination.getPatient());
	}

	private void resetHashMaps() throws Exception {
		Field diuresisDescriptionHashMap = examinationBrowserManager.getClass().getDeclaredField("diuresisDescriptionHashMap");
		diuresisDescriptionHashMap.setAccessible(true);
		diuresisDescriptionHashMap.set(examinationBrowserManager, null);

		Field bowelDescriptionHashMap = examinationBrowserManager.getClass().getDeclaredField("bowelDescriptionHashMap");
		bowelDescriptionHashMap.setAccessible(true);
		bowelDescriptionHashMap.set(examinationBrowserManager, null);

		Field auscultationHashMap = examinationBrowserManager.getClass().getDeclaredField("auscultationHashMap");
		auscultationHashMap.setAccessible(true);
		auscultationHashMap.set(examinationBrowserManager, null);
	}

	private void setKnownKeyValueInHashMaps() throws Exception {
		Map<String, String> knownValues = new HashMap<>(3);
		knownValues.put("key1", "value1");
		knownValues.put("knownKey", "knownValue");
		knownValues.put("key3", "value3");

		Field diuresisDescriptionHashMap = examinationBrowserManager.getClass().getDeclaredField("diuresisDescriptionHashMap");
		diuresisDescriptionHashMap.setAccessible(true);
		diuresisDescriptionHashMap.set(examinationBrowserManager, knownValues);

		Field bowelDescriptionHashMap = examinationBrowserManager.getClass().getDeclaredField("bowelDescriptionHashMap");
		bowelDescriptionHashMap.setAccessible(true);
		bowelDescriptionHashMap.set(examinationBrowserManager, knownValues);

		Map<String, String> linkedKnownValues = new LinkedHashMap<>(3);
		linkedKnownValues.put("key1", "value1");
		linkedKnownValues.put("knownKey", "knownValue");
		linkedKnownValues.put("key3", "value3");

		Field auscultationHashMap = examinationBrowserManager.getClass().getDeclaredField("auscultationHashMap");
		auscultationHashMap.setAccessible(true);
		auscultationHashMap.set(examinationBrowserManager, linkedKnownValues);
	}

}