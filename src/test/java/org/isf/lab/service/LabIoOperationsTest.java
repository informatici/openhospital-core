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

import java.util.ArrayList;
import java.util.List;
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
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.pagination.PagedResponse;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class LabIoOperationsTest extends OHCoreTestCase {

	private static TestLaboratory testLaboratory;
	private static TestLaboratoryRow testLaboratoryRow;
	private static TestExam testExam;
	private static TestExamType testExamType;
	private static TestPatient testPatient;

	@Autowired
	LabIoOperations labIoOperation;
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
	void getLabRow_shouldReturnRowsOfLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<LaboratoryRow> laboratoryRows = labIoOperation.getLabRow(foundLaboratoryRow.getLabId().getCode());
		assertThat(laboratoryRows).contains(foundLaboratoryRow);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratory_shouldReturnLaboratoryOfLastWeek_whenNoFilterIsGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labIoOperation.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryPageable_shouldReturnPageWithLaboratory_whenOneWeekIsFalse(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratory(false);
		boolean oneWeek = false;
		int pageNo = 0;
		int pageSize = 10;
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		PagedResponse<Laboratory> laboratories = labIoOperation.getLaboratoryPageable(oneWeek, pageNo, pageSize);
		assertThat(laboratories.getData().get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}
	
	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryPageable_shouldReturnPageWithLaboratory_whenOneWeekIsTrue(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratory(false);
		boolean oneWeek = true;
		int pageNo = 0;
		int pageSize = 10;
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		PagedResponse<Laboratory> laboratories = labIoOperation.getLaboratoryPageable(oneWeek, pageNo, pageSize);
		assertThat(laboratories.getData().get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratory_shouldReturnLaboratory_whenExamAndDatesAreGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labIoOperation
				.getLaboratory(foundLaboratory.getExam().getDescription(), foundLaboratory.getLabDate(), foundLaboratory.getLabDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratory_shouldReturnLaboratory_whenExamIsNullAndDatesAreGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();

		// when:
		List<Laboratory> laboratories = labIoOperation.getLaboratory(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratory_shouldReturnPatientLaboratories_whenOnlyPatientIsGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<Laboratory> laboratories = labIoOperation.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryForPrint_shouldReturnLaboratory_whenExamIsNullAndDatesAreLastWeek(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratory(true);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		var now = TimeTools.getNow();
		List<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint(null, now.minusWeeks(1), now, null);
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryForPrint_shouldReturnLaboratory_whenExamAndDatesAreGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<LaboratoryForPrint> laboratories = labIoOperation
				.getLaboratoryForPrint(foundLaboratory.getExam().getDescription(), foundLaboratory.getLabDate(), foundLaboratory.getLabDate(), null);
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryForPrint_shouldReturnLaboratory_whenPartOfExamDescriptionIsGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labIoOperation
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getLabDate(), foundLaboratory.getLabDate(), null);

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryForPrint_shouldReturnLaboratory_whenExamIsNullAndDatesAreGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();

		// when:
		List<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate(), null);

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void newLabFirstProcedure_shouldSaveLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		Laboratory newLaboratory = labIoOperation.newLabFirstProcedure(laboratory);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void newLabSecondProcedure_shouldSaveLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		List<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labRow.add("TestLabRow");
		Laboratory newLaboratory = labIoOperation.newLabSecondProcedure(laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void newLabSecondProcedure_shouldSaveLaboratory_whenRowDescriptionHasSixtyCharacters(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		List<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labRow.add("TestLabRow");
		labRow.add("TestLabRowTestLabRowTestLabRowTestLabRowTestLabRowTestLabRow"); // Causing rollback
		Laboratory newLaboratory = labIoOperation.newLabSecondProcedure(laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void newLabSecondProcedure2_shouldReturnSavedLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		List<LaboratoryRow> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		LaboratoryRow laboratoryRow = testLaboratoryRow.setup(laboratory, false);
		labRowIoOperationRepository.saveAndFlush(laboratoryRow);
		labRow.add(laboratoryRow);
		Laboratory newLaboratory = labIoOperation.newLabSecondProcedure2(laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void updateLabFirstProcedure_shouldSaveNewNote(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		foundLaboratory.setNote("Update");
		Laboratory updatedLaboratory = labIoOperation.updateLabFirstProcedure(foundLaboratory);
		assertThat(updatedLaboratory.getNote()).isEqualTo("Update");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void updateLabSecondProcedure_shouldSaveNewRow(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		List<String> labRow = new ArrayList<>();
		Integer code = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		labRow.add("Update");
		labIoOperation.updateLabSecondProcedure(foundLaboratoryRow.getLabId(), labRow);
		LaboratoryRow updatedLaboratoryRow = labRowIoOperationRepository.findById(code + 1).orElse(null);
		assertThat(updatedLaboratoryRow).isNotNull();
		assertThat(updatedLaboratoryRow.getDescription()).isEqualTo("Update");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void deleteLaboratory_shouldRemoveLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		labIoOperation.deleteLaboratory(foundLaboratory);
		assertThat(labIoOperation.isCodePresent(code)).isFalse();
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void deleteLaboratory_shouldRemoveLaboratory_whenExamProcedureIsTwo(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		labIoOperation.deleteLaboratory(laboratory);
		assertThat(labIoOperation.isCodePresent(laboratory.getCode())).isFalse();
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

	private void checkLaboratoryIntoDb(Integer code) {
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		testLaboratory.check(foundLaboratory);
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

}
