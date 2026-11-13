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
package org.isf.examination.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.examination.TestPatientExamination;
import org.isf.examination.model.PatientExamination;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class ExaminationPatientMergedEventListenerTest extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestPatientExamination testPatientExamination;

	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
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
	void handle_shouldUpdatePatientToMergedPatient_whenPatientMergedEventArrives() throws Exception {
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

}
