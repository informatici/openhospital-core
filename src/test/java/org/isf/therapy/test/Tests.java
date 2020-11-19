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
package org.isf.therapy.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicals.test.TestMedical;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.medtype.test.TestMedicalType;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.therapy.model.TherapyRow;
import org.isf.therapy.service.TherapyIoOperationRepository;
import org.isf.therapy.service.TherapyIoOperations;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class Tests extends OHCoreTestCase {

	private static TestTherapy testTherapyRow;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestPatient testPatient;

	@Autowired
	TherapyIoOperations therapyIoOperation;
	@Autowired
	TherapyIoOperationRepository therapyIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@BeforeClass
	public static void setUpClass() {
		testTherapyRow = new TestTherapy();
		testPatient = new TestPatient();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testTherapyRowGets() throws Exception {
		int id = _setupTestTherapyRow(false);
		_checkTherapyRowIntoDb(id);
	}

	@Test
	public void testTherapyRowSets() throws Exception {
		int id = _setupTestTherapyRow(true);
		_checkTherapyRowIntoDb(id);
	}

	@Test
	public void testIoGetTherapyRow() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);
		ArrayList<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(foundTherapyRow.getPatient().getCode());
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	@Test
	public void testIoGetTherapyRowWithZeroAsIdentifierProvided() throws Exception {
		// given:
		int id = _setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);

		// when:
		ArrayList<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(0);

		// then:
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	@Test
	public void testIoNewTherapyRow() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
		therapyIoOperation.newTherapy(therapyRow);
		_checkTherapyRowIntoDb(therapyRow.getTherapyID());
	}

	@Test
	public void testIoDeleteTherapyRow() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);
		boolean result = therapyIoOperation.deleteAllTherapies(foundTherapyRow.getPatient());
		assertThat(result).isTrue();
		result = therapyIoOperation.isCodePresent(id);
		assertThat(result).isFalse();
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = _setupTestTherapyRow(false);
		TherapyRow found = therapyIoOperationRepository.findOne(id);
		Patient obsoletePatient = found.getPatient();
		Patient mergedPatient = _setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(obsoletePatient, mergedPatient));

		// then:
		TherapyRow result = therapyIoOperationRepository.findOne(id);
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private Patient _setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private int _setupTestTherapyRow(boolean usingSet) throws OHException {
		TherapyRow therapyRow;
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		therapyRow = testTherapyRow.setup(patient, medical, usingSet);
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		return therapyRow.getTherapyID();
	}

	private void _checkTherapyRowIntoDb(int id) throws OHException {
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);
		testTherapyRow.check(foundTherapyRow);
	}
}