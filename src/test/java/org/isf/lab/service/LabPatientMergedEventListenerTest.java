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
package org.isf.lab.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.isf.OHCoreTestCase;
import org.isf.exa.TestExam;
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.generaldata.GeneralData;
import org.isf.lab.TestLaboratory;
import org.isf.lab.model.Laboratory;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class LabPatientMergedEventListenerTest extends OHCoreTestCase {

	private static TestLaboratory testLaboratory;
	private static TestExam testExam;
	private static TestExamType testExamType;
	private static TestPatient testPatient;

	@Autowired
	LabIoOperationRepository labIoOperationRepository;
	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@BeforeAll
	static void setUpClass() {
		testLaboratory = new TestLaboratory();
		testExam = new TestExam();
		testExamType = new TestExamType();
		testPatient = new TestPatient();
	}

	static Stream<Arguments> labExtended() {
		return Stream.of(Arguments.of(false), Arguments.of(true));
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void handle_shouldUpdatePatientToMergedPatient_whenPatientMergedEventArrives(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		int id = setupTestLaboratory(false);
		Laboratory found = labIoOperationRepository.findById(id).orElse(null);
		assertThat(found).isNotNull();
		Patient mergedPatient = testPatient.setup(true);
		patientIoOperationRepository.saveAndFlush(mergedPatient);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		Laboratory laboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(laboratory).isNotNull();
		assertThat(laboratory.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
		assertThat(laboratory.getPatName()).isEqualTo(mergedPatient.getName());
		assertThat(laboratory.getAge()).isEqualTo(mergedPatient.getAge());
		assertThat(laboratory.getSex()).isEqualTo(String.valueOf(mergedPatient.getSex()));
	}

	private Integer setupTestLaboratory(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		return laboratory.getCode();
	}

}
