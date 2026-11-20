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
package org.isf.patvac.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patvac.TestPatientVaccine;
import org.isf.patvac.model.PatientVaccine;
import org.isf.vaccine.TestVaccine;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class VaccinePatientMergedEventListenerTest extends OHCoreTestCase {

	private static TestPatientVaccine testPatientVaccine;
	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;
	private static TestPatient testPatient;

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

	@BeforeAll
	static void setUpClass() {
		testPatientVaccine = new TestPatientVaccine();
		testPatient = new TestPatient();
		testVaccine = new TestVaccine();
		testVaccineType = new TestVaccineType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void handle_shouldUpdatePatientToMergedPatient_whenPatientMergedEventArrives() throws Exception {
		// given:
		int id = setupTestPatientVaccine(false);
		PatientVaccine found = patVacIoOperationRepository.findById(id).orElse(null);
		assertThat(found).isNotNull();
		Patient mergedPatient = setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		PatientVaccine result = patVacIoOperationRepository.findById(id).orElse(null);
		assertThat(result).isNotNull();
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private Patient setupTestPatient(boolean usingSet) throws Exception {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private int setupTestPatientVaccine(boolean usingSet) throws Exception {
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

}
