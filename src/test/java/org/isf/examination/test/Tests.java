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
package org.isf.examination.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.examination.service.ExaminationOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class Tests extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestPatientExamination testPatientExamination;

	@Autowired
	ExaminationOperations examinationOperations;
	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@BeforeClass
	public static void setUpClass() {
		testPatient = new TestPatient();
		testPatientExamination = new TestPatientExamination();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testPatientExaminationGets() throws Exception {
		int id = _setupTestPatientExamination(false);
		_checkPatientExaminationIntoDb(id);
	}

	@Test
	public void testPatientExaminationSets() throws Exception {
		int id = _setupTestPatientExamination(true);
		_checkPatientExaminationIntoDb(id);
	}

	@Test
	public void testGetFromLastPatientExamination() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		PatientExamination patientExamination = examinationOperations.getFromLastPatientExamination(lastPatientExamination);
		testPatientExamination.check(patientExamination);
		testPatient.check(patientExamination.getPatient());
	}

	@Test
	public void testSaveOrUpdate() throws Exception {
		int id = _setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationIoOperationRepository.findOne(id);
		Integer pex_hr = patientExamination.getPex_hr();
		patientExamination.setPex_hr(pex_hr + 1);
		examinationOperations.saveOrUpdate(patientExamination);
		assertThat(patientExamination.getPex_hr()).isEqualTo((Integer) (pex_hr + 1));
	}

	@Test
	public void testGetByID() throws Exception {
		int id = _setupTestPatientExamination(false);
		PatientExamination patientExamination = examinationOperations.getByID(id);
		testPatientExamination.check(patientExamination);
		testPatient.check(patientExamination.getPatient());
	}

	@Test
	public void testGetLastByPatID() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		PatientExamination foundExamination = examinationOperations.getLastByPatID(patient.getCode());
		_checkPatientExaminationIntoDb(foundExamination.getPex_ID());
	}

	@Test
	public void testGetLastNByPatID() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		ArrayList<PatientExamination> foundExamination = examinationOperations.getLastNByPatID(patient.getCode(), 1);
		_checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}

	@Test
	public void testGetByPatID() throws Exception {
		Patient patient = testPatient.setup(false);
		PatientExamination lastPatientExamination = testPatientExamination.setup(patient, false);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(lastPatientExamination);
		ArrayList<PatientExamination> foundExamination = examinationOperations.getByPatID(patient.getCode());
		_checkPatientExaminationIntoDb(foundExamination.get(0).getPex_ID());
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = _setupTestPatientExamination(false);
		PatientExamination found = examinationIoOperationRepository.findOne(id);
		Patient mergedPatient = _setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		PatientExamination result = examinationIoOperationRepository.findOne(id);
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private Patient _setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private int _setupTestPatientExamination(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		PatientExamination patientExamination = testPatientExamination.setup(patient, usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		examinationIoOperationRepository.saveAndFlush(patientExamination);
		return patientExamination.getPex_ID();
	}

	private void _checkPatientExaminationIntoDb(int id) throws OHException {
		PatientExamination foundPatientExamination = examinationIoOperationRepository.findOne(id);
		testPatientExamination.check(foundPatientExamination);
		testPatient.check(foundPatientExamination.getPatient());
	}
}