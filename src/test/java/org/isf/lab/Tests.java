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
package org.isf.lab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.lang.reflect.Method;
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
import org.isf.lab.manager.LabManager;
import org.isf.lab.manager.LabRowManager;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.lab.model.LaboratoryStatus;
import org.isf.lab.service.LabIoOperationRepository;
import org.isf.lab.service.LabIoOperations;
import org.isf.lab.service.LabRowIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.pagination.PagedResponse;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class Tests extends OHCoreTestCase {

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
	LabManager labManager;
	@Autowired
	LabRowIoOperationRepository labRowIoOperationRepository;
	@Autowired
	LabRowManager labRowManager;
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
	void testLaboratoryGets(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int code = setupTestLaboratory(false);
		checkLaboratoryIntoDb(code);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testLaboratorySets(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer code = setupTestLaboratory(true);
		checkLaboratoryIntoDb(code);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testLaboratoryRowGets(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int code = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(code);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testLaboratoryRowSets(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int code = setupTestLaboratoryRow(true);
		checkLaboratoryRowIntoDb(code);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testIoGetLabRowByLabId(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<LaboratoryRow> laboratoryRows = labIoOperation.getLabRow(foundLaboratoryRow.getLabId().getCode());
		assertThat(laboratoryRows).contains(foundLaboratoryRow);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testIoGetLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labIoOperation.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryRowList(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();

		List<LaboratoryRow> laboratoryRowList = labManager.getLaboratoryRowList(foundLaboratoryRow.getCode());
		assertThat(laboratoryRowList.get(0).getCode()).isEqualTo(foundLaboratoryRow.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testIoGetLaboratoryPageable(boolean labExtended) throws Exception {
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
	void testIoGetLaboratoryOnWeekPageable(boolean labExtended) throws Exception {
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
	void testIoGetLaboratoryWithDates(boolean labExtended) throws Exception {
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
	void testIoGetLaboratoryWithoutDescription(boolean labExtended) throws Exception {
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
	void testIoGetLaboratoryFromPatient(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<Laboratory> laboratories = labIoOperation.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testIoGetLaboratoryForPrint(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratory(true);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testIoGetLaboratoryForPrintWithDates(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<LaboratoryForPrint> laboratories = labIoOperation
				.getLaboratoryForPrint(foundLaboratory.getExam().getDescription(), foundLaboratory.getLabDate(), foundLaboratory.getLabDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testIoGetLaboratoryForPrintWithExamDescriptionLikePersistedOne(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labIoOperation
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testIoGetLaboratoryForPrintWithNullExamDescription(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();

		// when:
		List<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testIoNewLabFirstProcedure(boolean labExtended) throws Exception {
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
	void testIoNewLabSecondProcedure(boolean labExtended) throws Exception {
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
	void testIoNewLabSecondProcedureTransaction(boolean labExtended) throws Exception {
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
	void testIoNewLabSecondProcedure2(boolean labExtended) throws Exception {
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
	void testIoUpdateLaboratory(boolean labExtended) throws Exception {
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
	void testIoEditLabSecondProcedure(boolean labExtended) throws Exception {
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
	void testIoDeleteLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		labIoOperation.deleteLaboratory(foundLaboratory);
		assertThat(labIoOperation.isCodePresent(code)).isFalse();
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testIoDeleteLaboratoryProcedureEquals2(boolean labExtended) throws Exception {
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

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryPageable(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratory(false);
		boolean oneWeek = false;
		int pageNo = 0;
		int pageSize = 10;
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		PagedResponse<Laboratory> laboratories = labManager.getLaboratoryPageable(oneWeek, pageNo, pageSize);
		assertThat(laboratories.getData().get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labManager.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithPatientPageable(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		int pageNo = 0;
		int pageSize = 10;
		PagedResponse<Laboratory> laboratories = labManager.getLaboratoryPageable(laboratory.getExam().getDescription(), laboratory.getLabDate(),
						laboratory.getLabDate(), foundLaboratoryRow.getLabId().getPatient(), pageNo, pageSize);
		assertThat(laboratories.getData().get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithNullPatientForPrint(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		List<LaboratoryForPrint> laboratories = labManager.getLaboratoryForPrint(laboratory.getExam().getDescription(), laboratory.getLabDate(),
						laboratory.getLabDate(), null);
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithPatientExamNullForPrint(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		List<LaboratoryForPrint> laboratories = labManager.getLaboratoryForPrint(null, laboratory.getLabDate(), laboratory.getLabDate(),
						foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithPatientNullExamNullForPrint(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		List<LaboratoryForPrint> laboratories = labManager.getLaboratoryForPrint(null, laboratory.getLabDate(), laboratory.getLabDate(), null);
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithPatientForPrint(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		List<LaboratoryForPrint> laboratories = labManager.getLaboratoryForPrint(laboratory.getExam().getDescription(), laboratory.getLabDate(),
			laboratory.getLabDate(), foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithNullPatientPageable(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		int pageNo = 0;
		int pageSize = 10;
		PagedResponse<Laboratory> laboratories = labManager.getLaboratoryPageable(laboratory.getExam().getDescription(), laboratory.getLabDate(),
			laboratory.getLabDate(), null, pageNo, pageSize);
		assertThat(laboratories.getData().get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithPatientExamNull(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		int pageNo = 0;
		int pageSize = 10;
		PagedResponse<Laboratory> laboratories = labManager.getLaboratoryPageable(null, laboratory.getLabDate(), laboratory.getLabDate(),
			foundLaboratoryRow.getLabId().getPatient(), pageNo, pageSize);
		assertThat(laboratories.getData().get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithPatientNullExamNull(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		int pageNo = 0;
		int pageSize = 10;
		PagedResponse<Laboratory> laboratories = labManager.getLaboratoryPageable(null, laboratory.getLabDate(), laboratory.getLabDate(), null, pageNo, pageSize);
		assertThat(laboratories.getData().get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithPatient(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		List<Laboratory> laboratories = labManager.getLaboratory(laboratory.getExam().getDescription(), laboratory.getLabDate(),
						laboratory.getLabDate(), foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithNullPatient(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		List<Laboratory> laboratories = labManager.getLaboratory(laboratory.getExam().getDescription(), laboratory.getLabDate(),
						laboratory.getLabDate(), null);
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithPatientEmptyExam(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		List<Laboratory> laboratories = labManager.getLaboratory("", laboratory.getLabDate(), laboratory.getLabDate(),
						foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithPatientNullEmptyExam(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(id);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		Laboratory laboratory = foundLaboratoryRow.getLabId();
		List<Laboratory> laboratories = labManager.getLaboratory("", laboratory.getLabDate(), laboratory.getLabDate(), null);
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testRowMgrGetLabRowByLabId(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<LaboratoryRow> laboratoryRows = labRowManager.getLabRowByLabId(foundLaboratoryRow.getLabId().getCode());
		assertThat(laboratoryRows).contains(foundLaboratoryRow);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryRelatedToPatient(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<Laboratory> laboratories = labManager.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithDatesAndExam(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labManager.getLaboratory(foundLaboratory.getExam().getDescription(), foundLaboratory.getLabDate(),
						foundLaboratory.getLabDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryWithoutExamName(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();

		// when:
		List<Laboratory> laboratories = labManager.getLaboratory(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryForPrintWithDates(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(foundLaboratory.getExam().getDescription(), foundLaboratory.getLabDate(), foundLaboratory.getLabDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryForPrintWithExamDescriptionLikePersistedOne(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryForPrintWithNullExamDescription(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();

		// when:
		List<LaboratoryForPrint> laboratories = labManager.getLaboratoryForPrint(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryForPrintMultipeResultsNoRows(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		// TODO: if resource bundles are made available this setResults() needs to change
		foundLaboratory.setResult("angal.lab.multipleresults.txt");
		List<String> labRow = new ArrayList<>();
		labManager.updateLaboratory(foundLaboratory, labRow);
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
		// TODO: if resource bundles are made available this value needs to change
		assertThat(laboratories.get(0).getResult()).isEqualTo("angal.lab.allnegative.txt");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetLaboratoryForPrintMultipeResultsRows(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		// TODO: if resource bundles are made available this setResults() needs to change
		laboratory.setResult("angal.lab.multipleresults.txt");
		labIoOperationRepository.saveAndFlush(laboratory);
		LaboratoryRow laboratoryRow = testLaboratoryRow.setup(laboratory, false);
		labRowIoOperationRepository.saveAndFlush(laboratoryRow);
		String description = laboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(firstCharsOfDescription, laboratory.getLabDate(), laboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
		// TODO: if resource bundles are made available this value needs to change
		assertThat(laboratories.get(0).getResult()).isEqualTo("angal.lab.multipleresults.txt,TestDescription");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratoryProcedureEquals1(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		// method is protected not public thus use of reflection
		Method method = labManager.getClass().getDeclaredMethod("newLabFirstProcedure", Laboratory.class);
		method.setAccessible(true);
		Laboratory newLaboratory = (Laboratory) method.invoke(labManager, laboratory);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratoryProcedureEquals2(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ArrayList<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labRow.add("TestLabRow");
		// method is protected not public thus use of reflection
		Method method = labManager.getClass().getDeclaredMethod("newLabSecondProcedure", Laboratory.class, List.class);
		method.setAccessible(true);
		Laboratory newLaboratory = (Laboratory) method.invoke(labManager, laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratoryProcedureEquals2EmptyLabRows(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			List<String> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 2, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.newLaboratory(laboratory, labRow);
		})
			.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrUpdateExamRequestUnknownExam(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() -> {
			labManager.updateExamRequest(-99, "status");
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrUpdateExamRequest(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		String DRAFT = LaboratoryStatus.draft.toString();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);

		List<String> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);

		Laboratory newLaboratory = labManager.newLaboratory(laboratory, labRow);

		Laboratory updatedLaboratory = labManager.updateExamRequest(newLaboratory.getCode(), DRAFT);
		assertThat(updatedLaboratory.getStatus()).isEqualTo(DRAFT);
		assertThat(updatedLaboratory.getCode()).isEqualTo(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);

		List<String> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);

		Laboratory newLaboratory = labManager.newLaboratory(laboratory, labRow);

		Laboratory foundLaboratory = labManager.getLaboratory(newLaboratory.getCode()).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		assertThat(foundLaboratory.getCode()).isEqualTo(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratoryProcedureEquals2NullLabRows(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 2, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.newLaboratory(laboratory, null);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratoryExceptionsBadProcedureNumber(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			List<String> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 99, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratoryProcedureEquals2RollbackTransaction(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ArrayList<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labRow.add("TestLabRow");
		labRow.add("TestLabRowTestLabRowTestLabRowTestLabRowTestLabRowTestLabRow"); // Causing rollback
		// method is protected not public thus use of reflection
		Method method = labManager.getClass().getDeclaredMethod("newLabSecondProcedure", Laboratory.class, List.class);
		method.setAccessible(true);
		Laboratory newLaboratory = (Laboratory) method.invoke(labManager, laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratory2ProcedureEquals1(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		List<LaboratoryRow> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		LaboratoryRow laboratoryRow = testLaboratoryRow.setup(laboratory, false);
		labRowIoOperationRepository.saveAndFlush(laboratoryRow);
		labRow.add(laboratoryRow);
		Laboratory newLaboratory = labManager.newLaboratory2(laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratory2ProcedureEquals2(boolean labExtended) throws Exception {
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
		Laboratory newLaboratory = labManager.newLaboratory2(laboratory, labRow);
		// TODO: if resource bundles are made available this must change
		assertThat(newLaboratory.getResult()).isEqualTo("angal.lab.multipleresults.txt");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratory2ProcedureEquals3(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		List<LaboratoryRow> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 3, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		LaboratoryRow laboratoryRow = testLaboratoryRow.setup(laboratory, false);
		labRowIoOperationRepository.saveAndFlush(laboratoryRow);
		labRow.add(laboratoryRow);
		Laboratory newLaboratory = labManager.newLaboratory2(laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratory2ProcedureEquals2NullLabRows(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 2, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.newLaboratory(laboratory, null);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratory2BadProcedureNumber(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			List<LaboratoryRow> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 99, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.newLaboratory2(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationMissingExamDate(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);

		List<String> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);

		Laboratory newLaboratory = labManager.newLaboratory(laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationMissingLabDate(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			List<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setLabDate(null);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationLABEXTENDEDNoPatient(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// Only generates an exception if LABEXTENDED is true
		assumeThat(GeneralData.LABEXTENDED).isTrue();
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			List<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratory.setPatient(null);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationLABEXTENDEDPatient(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		// Only run if LABEXTENDED is true
		assumeThat(GeneralData.LABEXTENDED).isTrue();

		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);

		// laboratory 1, Procedure One
		List<String> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labManager.newLaboratory(laboratory, labRow);

		Laboratory foundLaboratory = labIoOperationRepository.findById(laboratory.getCode()).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		assertThat(laboratory.getPatName()).isEqualTo(laboratory.getPatient().getName());
		assertThat(laboratory.getAge()).isEqualTo(laboratory.getPatient().getAge());
		assertThat(laboratory.getSex()).isEqualTo(String.valueOf(laboratory.getPatient().getSex()));
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationLNoPatientBadSex(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			List<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setPatient(null);
			laboratory.setSex("?");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationLNoPatientBadAge(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			List<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setPatient(null);
			laboratory.setAge(-99);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationLNoExam(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			List<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setExam(null);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationLNoResult(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			List<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setResult("");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationLNoMaterial(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			List<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setMaterial("");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrValidationLNoInOutPatient(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			List<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setInOutPatient("");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrUpdateLaboratoryProcedure1(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		foundLaboratory.setNote("Update");
		Laboratory updatedLaboratory = labManager.updateLaboratory(foundLaboratory, null);
		assertThat(updatedLaboratory.getNote()).isEqualTo("Update");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrUpdateLaboratoryProcedure2(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		List<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		Laboratory foundLaboratory = labIoOperationRepository.findById(laboratory.getCode()).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		foundLaboratory.setNote("Update");
		labRow.add("laboratoryRow");
		Laboratory updatedLaboratory = labManager.updateLaboratory(foundLaboratory, labRow);
		assertThat(updatedLaboratory.getNote()).isEqualTo("Update");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrUpdateLaboratoryProcedure3(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 3, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		Laboratory foundLaboratory = labIoOperationRepository.findById(laboratory.getCode()).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		foundLaboratory.setNote("Update");
		Laboratory updatedLaboratory = labManager.updateLaboratory(foundLaboratory, null);
		assertThat(updatedLaboratory.getNote()).isEqualTo("Update");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrUpdateLaboratoryExceptionEmptyLabRows(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			List<String> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 2, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.updateLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrUpdateLaboratoryExceptionNullLablRows(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 2, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.updateLaboratory(laboratory, null);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrUpdateLaboratoryExceptionBadProcedureNumber(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			List<String> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 99, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.updateLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratoryTransaction(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		List<Laboratory> laboratories = new ArrayList<>();
		List<List<String>> labRowList = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Exam exam2 = testExam.setup(examType, 2, false);
		exam2.setCode("ZZZ");
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examIoOperationRepository.saveAndFlush(exam2);
		patientIoOperationRepository.saveAndFlush(patient);

		// laboratory 1, Procedure One
		List<String> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		laboratories.add(laboratory);
		labRowList.add(labRow);

		// laboratory 2, Procedure Two
		Laboratory laboratory2 = testLaboratory.setup(exam2, patient, false);
		laboratories.add(laboratory2);
		labRow.add("TestLabRow");
		labRow.add("TestLabRowTestLabRowTestLabRowTestLabRowTestLabRowTestLabRow"); // Causing rollback
		labRowList.add(labRow);

		Laboratory newLaboratory  = labManager.newLaboratory(laboratories, labRowList);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratoryTransactionNoLabList(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			List<Laboratory> laboratories = new ArrayList<>();
			List<List<String>> labRowList = new ArrayList<>();

			labManager.newLaboratory(laboratories, labRowList);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratoryTransactionLabListNotEqualLabRowList(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			List<Laboratory> laboratories = new ArrayList<>();
			List<List<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			List<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratories.add(laboratory);
			labRowList.add(labRow);

			// laboratory 2, Procedure Two
			labRow.add("TestLabRow");
			labRowList.add(labRow);

			labManager.newLaboratory(laboratories, labRowList);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratory2Transaction(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		List<Laboratory> labList = new ArrayList<>();
		List<List<LaboratoryRow>> labRowList = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Exam exam2 = testExam.setup(examType, 2, false);
		exam2.setCode("ZZZ");
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examIoOperationRepository.saveAndFlush(exam2);
		patientIoOperationRepository.saveAndFlush(patient);

		// laboratory 1, Procedure One
		List<LaboratoryRow> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labList.add(laboratory);
		labRowList.add(labRow);
		labIoOperationRepository.saveAndFlush(laboratory);

		// laboratory 2, Procedure Two
		Laboratory laboratory2 = testLaboratory.setup(exam2, patient, false);
		labList.add(laboratory2);
		LaboratoryRow laboratoryRow = testLaboratoryRow.setup(laboratory2, false);
		labIoOperationRepository.saveAndFlush(laboratory2);
		labRowIoOperationRepository.saveAndFlush(laboratoryRow);

		labRow.add(laboratoryRow);
		labRow.add(laboratoryRow); // Causing rollback
		labRowList.add(labRow);

		Laboratory newLaboratory = labManager.newLaboratory2(labList, labRowList);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratory2TransactionNoLabList(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			List<Laboratory> labList = new ArrayList<>();
			List<List<LaboratoryRow>> labRowList = new ArrayList<>();

			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labIoOperationRepository.saveAndFlush(laboratory);

			labManager.newLaboratory2(labList, labRowList);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrNewLaboratory2TransactionLabListNotEqualLabRowList(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() ->
		{
			List<Laboratory> labList = new ArrayList<>();
			List<List<LaboratoryRow>> labRowList = new ArrayList<>();

			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			List<LaboratoryRow> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labIoOperationRepository.saveAndFlush(laboratory);
			labList.add(laboratory);
			labRowList.add(labRow);

			// laboratory 2, Procedure Two
			LaboratoryRow laboratoryRow = testLaboratoryRow.setup(laboratory, false);
			labRowIoOperationRepository.saveAndFlush(laboratoryRow);
			labRow.add(laboratoryRow);
			labRowList.add(labRow);

			labManager.newLaboratory2(labList, labRowList);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrDeleteLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		labManager.deleteLaboratory(foundLaboratory);
		assertThat(labIoOperation.isCodePresent(code)).isFalse();
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrDeleteLaboratoryProcedure2(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		labManager.deleteLaboratory(laboratory);
		assertThat(labIoOperation.isCodePresent(laboratory.getCode())).isFalse();
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetMaterialKeyUndefined(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThat(labManager.getMaterialKey("notThere")).isEqualTo("undefined");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetMaterialKeyFound(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialKey("angal.lab.film.txt")).isEqualTo("film");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetMaterialTranslatedNotThere(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("notThere")).isEqualTo("angal.lab.undefined.txt");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetMaterialTranslatedNull(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated(null)).isEqualTo("angal.lab.undefined.txt");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetMaterialTranslatedFound(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("film")).isEqualTo("angal.lab.film.txt");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testMgrGetMaterialLIst(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThat(labManager.getMaterialList()).hasSize(9);
		List<String> materailList = labManager.getMaterialList();
		// TODO: if resource bundles are made available this needs to change
		assertThat(materailList.get(0)).isEqualTo("angal.lab.undefined.txt");
		materailList.remove(0);   // Remove the default value that is placed first in the list even if out of order
		assertThat(materailList).isSorted();
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive(boolean labExtended) throws Exception {
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

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testNewLaboratoryGetterSetters(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer code = 0;
		String result = "TestResult";
		String note = "TestNote";
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = new Laboratory(code, exam, TimeTools.getNow(), result, note, patient, patient.getName());
		assertThat(laboratory).isNotNull();
		assertThat(laboratory.getCode()).isEqualTo(code);
		assertThat(laboratory.getExam()).isEqualTo(exam);
		assertThat(laboratory.getResult()).isEqualTo(result);
		assertThat(laboratory.getNote()).isEqualTo(note);
		assertThat(laboratory.getPatName()).isEqualTo(patient.getName());

		laboratory.setCode(-1);
		assertThat(laboratory.getCode()).isEqualTo(-1);
		laboratory.setLock(-2);
		assertThat(laboratory.getLock()).isEqualTo(-2);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testLaboratoryEqualsHashToString(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int code = setupTestLaboratory(false);
		Laboratory laboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(laboratory).isNotNull();
		Laboratory laboratory2 = new Laboratory(code + 1, null, TimeTools.getNow(), "result", "note", null, "name");
		assertThat(laboratory)
				.isEqualTo(laboratory)
				.isNotEqualTo(laboratory2)
				.isNotEqualTo("xyzzy");
		laboratory2.setCode(code);
		assertThat(laboratory).isEqualTo(laboratory2);

		assertThat(laboratory.hashCode()).isPositive();

		assertThat(laboratory.toString()).isNotEmpty();
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testNewLaboratoryRowGetterSetters(boolean labExtended) throws Exception {
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
	void testLaboratoryRowEqualsHash(boolean labExtended) throws Exception {
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

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void testLaboratoryForPrintGetterSetter(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		setupTestLaboratory(false);
		List<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint();

		LaboratoryForPrint laboratoryForPrint = laboratories.get(0);

		laboratoryForPrint.setCode(-1);
		assertThat(laboratoryForPrint.getCode()).isEqualTo(-1);
		laboratoryForPrint.setDate(laboratoryForPrint.getDate());
		assertThat(laboratoryForPrint.getDate()).isEqualTo(laboratoryForPrint.getDate());
		laboratoryForPrint.setExam("examString");
		assertThat(laboratoryForPrint.getExam()).isEqualTo("examString");
		laboratoryForPrint.setResult("resultString");
		assertThat(laboratoryForPrint.getResult()).isEqualTo("resultString");
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

	private void checkLaboratoryRowIntoDb(Integer code) {
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		testLaboratoryRow.check(foundLaboratoryRow);
	}
}