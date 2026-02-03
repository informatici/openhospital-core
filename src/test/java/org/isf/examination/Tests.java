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
package org.isf.examination;

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
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.examination.model.GenderPatientExamination;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.examination.service.ExaminationOperations;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class Tests extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestPatientExamination testPatientExamination;

	@Autowired
	ExaminationOperations examinationOperations;
	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
	@Autowired
	ExaminationBrowserManager examinationBrowserManager;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

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
	void testPatientExaminationGets() throws Exception {
		int id = setupTestPatientExamination(false);
		checkPatientExaminationIntoDb(id);
	}

	@Test
	void testPatientExaminationSets() throws Exception {
		int id = setupTestPatientExamination(true);
		checkPatientExaminationIntoDb(id);
	}

	@Test
	void testIoGetFromLastPatientExamination() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		PatientExamination patientExamination = examinationOperations.getFromLastPatientExamination(lastPatientExamination);
		testPatientExamination.check(patientExamination);
		testPatient.check(patientExamination.getPatient());
	}

	@Test
	void testIoSaveOrUpdate() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(patientExamination).isNotNull();
		Integer pex_hr = patientExamination.getPex_hr();
		patientExamination.setPex_hr(pex_hr + 1);
		examinationOperations.saveOrUpdate(patientExamination);
		assertThat(patientExamination.getPex_hr()).isEqualTo((Integer) (pex_hr + 1));
	}

	@Test
	void testIoGetByID() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationOperations.getByID(id);
		testPatientExamination.check(patientExamination);
		testPatient.check(patientExamination.getPatient());
	}

	@Test
	void testIoGetLastByPatID() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		PatientExamination foundExamination = examinationOperations.getLastByPatID(patient.getCode());
		checkPatientExaminationIntoDb(foundExamination.getPex_ID());
	}

	@Test
	void testIoGetLastNByPatID() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		List<PatientExamination> foundExamination = examinationOperations.getLastNByPatID(patient.getCode(), 1);
		checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}
	
	@Test
	void testIoGetLastNByPatIDPaginated() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		PagedResponse<PatientExamination> foundExamination = examinationOperations.getLastNByPatIDPageable(patient.getCode(), 1);
		checkPatientExaminationIntoDb(foundExamination.getData().get(0).getPex_ID());
	}

	@Test
	void testIoGetByPatID() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		List<PatientExamination> foundExamination = examinationOperations.getByPatID(patient.getCode());
		checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}

	@Test
	void testIoRemove() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationBrowserManager.getByID(id);
		List<PatientExamination> patexList = new ArrayList<>(1);
		patexList.add(patientExamination);
		examinationOperations.remove(patexList);
		assertThat(examinationIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void testIoListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = setupTestPatientExamination(false);
		PatientExamination found = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(found).isNotNull();
		Patient mergedPatient = setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		PatientExamination result = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(result).isNotNull();
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	@Test
	void testMgrGetFromLastPatientExamination() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		PatientExamination patientExamination = examinationBrowserManager.getFromLastPatientExamination(lastPatientExamination);
		testPatientExamination.check(patientExamination);
		testPatient.check(patientExamination.getPatient());
	}

	@Test
	void testMgrSaveOrUpdate() throws Exception {
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
	void testMgrGetLastByPatID() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		PatientExamination foundExamination = examinationBrowserManager.getLastByPatID(patient.getCode());
		checkPatientExaminationIntoDb(foundExamination.getPex_ID());
	}

	@Test
	void testMgrGetLastNByPatID() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		List<PatientExamination> foundExamination = examinationBrowserManager.getLastNByPatID(patient.getCode(), 1);
		checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}

	@Test
	void testMgrGetByPatID() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		List<PatientExamination> foundExamination = examinationBrowserManager.getByPatID(patient.getCode());
		checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}

	@Test
	void testMgrRemove() throws Exception {
		int id = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(id).orElse(null);
		assertThat(patientExamination).isNotNull();
		List<PatientExamination> patexList = new ArrayList<>(1);
		patexList.add(patientExamination);
		examinationBrowserManager.remove(patexList);
		assertThat(examinationIoOperationRepository.findById(id)).isEmpty();
	}

	@Test
	void testMgrListsTranslated() throws Exception {
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
	void testMgrGetAuscultationKey() throws Exception {
		resetHashMaps();
		assertThat(examinationBrowserManager.getAuscultationKey("shouldNotBeThere")).isEmpty();
		setKnownKeyValueInHashMaps();
		assertThat(examinationBrowserManager.getAuscultationKey("knownValue")).isEqualTo("knownKey");
	}

	@Test
	void testMgrGetBowelDescriptionKey() throws Exception {
		resetHashMaps();
		assertThat(examinationBrowserManager.getBowelDescriptionKey("shouldNotBeThere")).isEmpty();
		setKnownKeyValueInHashMaps();
		assertThat(examinationBrowserManager.getBowelDescriptionKey("knownValue")).isEqualTo("knownKey");
	}

	@Test
	void testMgrGetDiuresisDescriptionKey() throws Exception {
		resetHashMaps();
		assertThat(examinationBrowserManager.getDiuresisDescriptionKey("shouldNotBeThere")).isEmpty();
		setKnownKeyValueInHashMaps();
		assertThat(examinationBrowserManager.getDiuresisDescriptionKey("knownValue")).isEqualTo("knownKey");
	}

	@Test
	void testGetBMIdescription() throws Exception {
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
	void testGetDefaultPatientExamination() throws Exception {
		Patient patient = testPatient.setup(false);
		assertThat(examinationBrowserManager.getDefaultPatientExamination(patient)).isNotNull();
	}

	@Test
	void testMgrExaminationValidation() throws Exception {
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

	@Test
	void testPatientExaminationEqualHashCompareTo() throws Exception {
		int code = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(code).orElse(null);
		assertThat(patientExamination).isNotNull();
		Patient patient = testPatient.setup(false);
		PatientExamination patientExamination2 = testPatientExamination.setup(patient, false);
		patientExamination2.setPex_ID(-1);
		assertThat(patientExamination).isEqualTo(patientExamination);
		assertThat(patientExamination)
				.isNotEqualTo(patientExamination2)
				.isNotEqualTo("xyzzy");
		patientExamination2.setPex_ID(patientExamination.getPex_ID());
		assertThat(patientExamination).isEqualTo(patientExamination2);
		assertThat(patientExamination.compareTo(patientExamination2)).isZero();

		assertThat(patientExamination.hashCode()).isPositive();
	}

	@Test
	void testPatientExaminationGetBMI() throws Exception {
		int code = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(code).orElse(null);
		assertThat(patientExamination).isNotNull();
		assertThat(patientExamination.getBMI()).isPositive();
		patientExamination.setPex_height(null);
		assertThat(patientExamination.getBMI()).isZero();
	}

	@Test
	void testPatientExaminationGettersSetters() throws Exception {
		int code = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(code).orElse(null);
		assertThat(patientExamination).isNotNull();

		int hgt = patientExamination.getPex_hgt();
		patientExamination.setPex_hgt(-1);
		assertThat(patientExamination.getPex_hgt()).isEqualTo(-1);
		patientExamination.setPex_hgt(hgt);

		int diuresis = patientExamination.getPex_diuresis();
		patientExamination.setPex_diuresis(-1);
		assertThat(patientExamination.getPex_diuresis()).isEqualTo(-1);
		patientExamination.setPex_diuresis(diuresis);

		String desc = patientExamination.getPex_diuresis_desc();
		patientExamination.setPex_diuresis_desc("new description");
		assertThat(patientExamination.getPex_diuresis_desc()).isEqualTo("new description");
		patientExamination.setPex_diuresis_desc(desc);

		desc = patientExamination.getPex_bowel_desc();
		patientExamination.setPex_bowel_desc("new description too");
		assertThat(patientExamination.getPex_bowel_desc()).isEqualTo("new description too");
		patientExamination.setPex_bowel_desc(desc);
	}

	@Test
	void testGenderPatientExamination() throws Exception {
		int code = setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findById(code).orElse(null);
		assertThat(patientExamination).isNotNull();

		GenderPatientExamination genderPatientExamination = new GenderPatientExamination(patientExamination, false);
		assertThat(genderPatientExamination.isMale()).isFalse();
		genderPatientExamination.setMale(true);
		assertThat(genderPatientExamination.isMale()).isTrue();

		assertThat(genderPatientExamination.getPatex()).isEqualTo(patientExamination);
		Patient patient = testPatient.setup(false);
		PatientExamination patientExamination2 = testPatientExamination.setup(patient, false);
		genderPatientExamination.setPatex(patientExamination2);
		assertThat(genderPatientExamination.getPatex()).isEqualTo(patientExamination2);
	}

	private Patient setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
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