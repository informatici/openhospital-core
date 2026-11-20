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
package org.isf.lab.model;

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
import org.isf.lab.TestLaboratoryRow;
import org.isf.lab.service.LabIoOperationRepository;
import org.isf.lab.service.LabRowIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class LaboratoryRowTest extends OHCoreTestCase {

	private static TestLaboratory testLaboratory;
	private static TestLaboratoryRow testLaboratoryRow;
	private static TestExam testExam;
	private static TestExamType testExamType;
	private static TestPatient testPatient;

	@Autowired
	LabIoOperationRepository labIoOperationRepository;
	@Autowired
	LabRowIoOperationRepository labRowIoOperationRepository;
	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testLaboratory = new TestLaboratory();
		testLaboratoryRow = new TestLaboratoryRow();
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
	void constructor_shouldSetFields(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int code = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(code);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void setters_shouldSetFields(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int code = setupTestLaboratoryRow(true);
		checkLaboratoryRowIntoDb(code);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void setCode_shouldUpdateCode(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer code = 0;
		String result = "TestResult";
		String note = "TestNote";
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = new Laboratory(code, exam, TimeTools.getNow(), result, note, patient, patient.getName());
		LaboratoryRow laboratoryRow = new LaboratoryRow(code, laboratory, "description");
		assertThat(laboratoryRow).isNotNull();

		laboratoryRow.setCode(-1);
		assertThat(laboratoryRow.getCode()).isEqualTo(-1);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void equalsHashCode_shouldUseLaboratoryRowFields(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int code = setupTestLaboratoryRow(false);
		LaboratoryRow laboratoryRow = labRowIoOperationRepository.findById(code).orElse(null);
		assertThat(laboratoryRow).isNotNull();
		LaboratoryRow laboratoryRow2 = new LaboratoryRow(code + 1, null, "description");
		assertThat(laboratoryRow)
				.isEqualTo(laboratoryRow)
				.isNotEqualTo(laboratoryRow2)
				.isNotEqualTo("xyzzy");
		laboratoryRow2.setCode(code);
		assertThat(laboratoryRow).isEqualTo(laboratoryRow2);

		laboratoryRow.setCode(null);
		laboratoryRow2.setCode(null);
		assertThat(laboratoryRow).isNotEqualTo(laboratoryRow2);
		laboratoryRow.setDescription("description");
		assertThat(laboratoryRow).isEqualTo(laboratoryRow);

		assertThat(laboratoryRow.hashCode()).isPositive();
	}

	private Integer setupTestLaboratoryRow(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		LaboratoryRow laboratoryRow = testLaboratoryRow.setup(laboratory, usingSet);
		labRowIoOperationRepository.saveAndFlush(laboratoryRow);
		return laboratoryRow.getCode();
	}

	private void checkLaboratoryRowIntoDb(Integer code) {
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		testLaboratoryRow.check(foundLaboratoryRow);
	}

}
