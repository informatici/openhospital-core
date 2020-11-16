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
package org.isf.patvac.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.patvac.model.PatientVaccine;
import org.isf.patvac.service.PatVacIoOperationRepository;
import org.isf.patvac.service.PatVacIoOperations;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vaccine.test.TestVaccine;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.isf.vactype.test.TestVaccineType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class Tests extends OHCoreTestCase {

	private static TestPatientVaccine testPatientVaccine;
	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;
	private static TestPatient testPatient;

	@Autowired
	PatVacIoOperations patvacIoOperation;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	VaccineIoOperationRepository vaccineIoOperationRepository;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;
	@Autowired
	PatVacIoOperationRepository patVacIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@BeforeClass
	public static void setUpClass() {
		testPatientVaccine = new TestPatientVaccine();
		testPatient = new TestPatient();
		testVaccine = new TestVaccine();
		testVaccineType = new TestVaccineType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testPatientVaccineGets() throws Exception {
		int code = _setupTestPatientVaccine(false);
		_checkPatientVaccineIntoDb(code);
	}

	@Test
	public void testPatientVaccineSets() throws Exception {
		int code = _setupTestPatientVaccine(true);
		_checkPatientVaccineIntoDb(code);
	}

	@Test
	public void testIoGetPatientVaccine() throws Exception {
		int code = _setupTestPatientVaccine(false);
		PatientVaccine foundPatientVaccine =  patVacIoOperationRepository.findOne(code);
		ArrayList<PatientVaccine> patientVaccines = patvacIoOperation.getPatientVaccine(
				foundPatientVaccine.getVaccine().getVaccineType().getCode(),
				foundPatientVaccine.getVaccine().getCode(),
				foundPatientVaccine.getVaccineDate(),
				foundPatientVaccine.getVaccineDate(),
				foundPatientVaccine.getPatient().getSex(),
				foundPatientVaccine.getPatient().getAge(),
				foundPatientVaccine.getPatient().getAge());
		assertThat(patientVaccines.get(patientVaccines.size() - 1).getPatName()).isEqualTo(foundPatientVaccine.getPatName());
	}

	@Test
	public void testIoUpdatePatientVaccine() throws Exception {
		int code = _setupTestPatientVaccine(false);
		PatientVaccine foundPatientVaccine = patVacIoOperationRepository.findOne(code);
		foundPatientVaccine.setPatName("Update");
		boolean result = patvacIoOperation.updatePatientVaccine(foundPatientVaccine);
		assertThat(result).isTrue();
		PatientVaccine updatePatientVaccine = patVacIoOperationRepository.findOne(code);
		assertThat(updatePatientVaccine.getPatName()).isEqualTo("Update");
	}

	@Test
	public void testIoNewPatientVaccine() throws Exception {
		Patient patient = testPatient.setup(false);
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		patientIoOperationRepository.saveAndFlush(patient);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, true);
		boolean result = patvacIoOperation.newPatientVaccine(patientVaccine);
		assertThat(result).isTrue();
		_checkPatientVaccineIntoDb(patientVaccine.getCode());
	}

	@Test
	public void testIoDeletePatientVaccine() throws Exception {
		int code = _setupTestPatientVaccine(false);
		PatientVaccine foundPatientVaccine = patVacIoOperationRepository.findOne(code);
		boolean result = patvacIoOperation.deletePatientVaccine(foundPatientVaccine);
		assertThat(result).isTrue();
		result = patvacIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoGetProgYear() throws Exception {
		int prog_year = 0;
		int found_prog_year = 0;
		_setupTestPatientVaccine(false);
		List<PatientVaccine> patientVaccineList = patvacIoOperation.getPatientVaccine(null, null, null, null, 'A', 0, 0);
		for (PatientVaccine patVac : patientVaccineList) {
			if (patVac.getProgr() > found_prog_year)
				found_prog_year = patVac.getProgr();
		}
		prog_year = patvacIoOperation.getProgYear(0);
		assertThat(prog_year).isEqualTo(found_prog_year);

		prog_year = patvacIoOperation.getProgYear(1984); //TestPatientVaccine's year
		assertThat(prog_year).isEqualTo(10);
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = _setupTestPatientVaccine(false);
		PatientVaccine found = patVacIoOperationRepository.findOne(id);
		Patient mergedPatient = _setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		PatientVaccine result = patVacIoOperationRepository.findOne(id);
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private Patient _setupTestPatient(boolean usingSet) throws Exception {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private int _setupTestPatientVaccine(boolean usingSet) throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		Patient patient = testPatient.setup(false);
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, usingSet);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		patientIoOperationRepository.saveAndFlush(patient);
		patVacIoOperationRepository.saveAndFlush(patientVaccine);
		return patientVaccine.getCode();
	}

	private void _checkPatientVaccineIntoDb(int code) throws Exception {
		PatientVaccine foundPatientVaccine = patVacIoOperationRepository.findOne(code);
		testPatientVaccine.check(foundPatientVaccine);
	}
}