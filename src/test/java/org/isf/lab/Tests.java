/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 *
 * You should have received a copy of the GNU General License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.lab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.isf.generaldata.GeneralData.LABEXTENDED;

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
import org.isf.lab.manager.LabManager;
import org.isf.lab.manager.LabRowManager;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
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
import org.junit.jupiter.api.Test;
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

	static Stream<Arguments> generalDataLab() {
		return Stream.of(Arguments.of(false), Arguments.of(true));
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testLaboratoryGets() throws Exception {
		int code = setupTestLaboratory(false);
		checkLaboratoryIntoDb(code);
	}

	@Test
	void testLaboratorySets() throws Exception {
		Integer code = setupTestLaboratory(true);
		checkLaboratoryIntoDb(code);
	}

	@Test
	void testLaboratoryRowGets() throws Exception {
		int code = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(code);
	}

	@Test
	void testLaboratoryRowSets() throws Exception {
		int code = setupTestLaboratoryRow(true);
		checkLaboratoryRowIntoDb(code);
	}

	@Test
	void testIoGetLabRowByLabId() throws Exception {
		Integer id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<LaboratoryRow> laboratoryRows = labIoOperation.getLabRow(foundLaboratoryRow.getLabId().getCode());
		assertThat(laboratoryRows).contains(foundLaboratoryRow);
	}

	@Test
	void testIoGetLaboratory() throws Exception {
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labIoOperation.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testIoGetLaboratoryPageable() throws Exception {
		int id = setupTestLaboratory(false);
		boolean oneWeek = false;
		int pageNo = 0;
		int pageSize = 10;
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		PagedResponse<Laboratory> laboratories = labIoOperation.getLaboratoryPageable(oneWeek, pageNo, pageSize);
		assertThat(laboratories.getData().get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testIoGetLaboratoryOnWeekPageable() throws Exception {
		int id = setupTestLaboratory(false);
		boolean oneWeek = true;
		int pageNo = 0;
		int pageSize = 10;
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		PagedResponse<Laboratory> laboratories = labIoOperation.getLaboratoryPageable(oneWeek, pageNo, pageSize);
		assertThat(laboratories.getData().get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testIoGetLaboratoryWithDates() throws Exception {
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labIoOperation
			.getLaboratory(foundLaboratory.getExam().getDescription(), foundLaboratory.getLabDate(), foundLaboratory.getLabDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testIoGetLaboratoryWithoutDescription() throws Exception {
		// given:
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();

		// when:
		List<Laboratory> laboratories = labIoOperation.getLaboratory(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testIoGetLaboratoryFromPatient() throws Exception {
		int id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<Laboratory> laboratories = labIoOperation.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@Test
	void testIoGetLaboratoryForPrint() throws Exception {
		Integer id = setupTestLaboratory(true);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testIoGetLaboratoryForPrintWithDates() throws Exception {
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<LaboratoryForPrint> laboratories = labIoOperation
			.getLaboratoryForPrint(foundLaboratory.getExam().getDescription(), foundLaboratory.getLabDate(), foundLaboratory.getLabDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testIoGetLaboratoryForPrintWithExamDescriptionLikePersistedOne() throws Exception {
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

	@Test
	void testIoGetLaboratoryForPrintWithNullExamDescription() throws Exception {
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();

		// when:
		List<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testIoNewLabFirstProcedure() throws Exception {
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

	@Test
	void testIoNewLabSecondProcedure() throws Exception {
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

	@Test
	void testIoNewLabSecondProcedureTransaction() throws Exception {
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

	@Test
	void testIoNewLabSecondProcedure2() throws Exception {
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

	@Test
	void testIoUpdateLaboratory() throws Exception {
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		foundLaboratory.setNote("Update");
		Laboratory updatedLaboratory = labIoOperation.updateLabFirstProcedure(foundLaboratory);
		assertThat(updatedLaboratory.getNote()).isEqualTo("Update");
	}

	@Test
	void testIoEditLabSecondProcedure() throws Exception {
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

	@Test
	void testIoDeleteLaboratory() throws Exception {
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		labIoOperation.deleteLaboratory(foundLaboratory);
		assertThat(labIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testIoDeleteLaboratoryProcedureEquals2() throws Exception {
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

	@Test
	void testMgrGetLaboratory() throws Exception {
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labManager.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testRowMgrGetLabRowByLabId() throws Exception {
		Integer id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<LaboratoryRow> laboratoryRows = labRowManager.getLabRowByLabId(foundLaboratoryRow.getLabId().getCode());
		assertThat(laboratoryRows).contains(foundLaboratoryRow);
	}

	@Test
	void testMgrGetLaboratoryRelatedToPatient() throws Exception {
		int id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<Laboratory> laboratories = labManager.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@Test
	void testMgrGetLaboratoryWithDatesAndExam() throws Exception {
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labManager
			.getLaboratory(foundLaboratory.getExam().getDescription(), foundLaboratory.getLabDate(), foundLaboratory.getLabDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testMgrGetLaboratoryWithoutExamName() throws Exception {
		// given:
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();

		// when:
		List<Laboratory> laboratories = labManager.getLaboratory(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testMgrGetLaboratoryForPrintWithDates() throws Exception {
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<LaboratoryForPrint> laboratories = labManager
			.getLaboratoryForPrint(foundLaboratory.getExam().getDescription(), foundLaboratory.getLabDate(), foundLaboratory.getLabDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testMgrGetLaboratoryForPrintWithExamDescriptionLikePersistedOne() throws Exception {
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

	@Test
	void testMgrGetLaboratoryForPrintWithNullExamDescription() throws Exception {
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();

		// when:
		List<LaboratoryForPrint> laboratories = labManager.getLaboratoryForPrint(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	void testMgrGetLaboratoryForPrintMultipeResultsNoRows() throws Exception {
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

	@Test
	void testMgrGetLaboratoryForPrintMultipeResultsRows() throws Exception {
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

	@Test
	void testMgrnewLaboratoryProcedureEquals1() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		// method is protected not thus use of reflection
		Method method = labManager.getClass().getDeclaredMethod("newLabFirstProcedure", Laboratory.class);
		method.setAccessible(true);
		Laboratory newLaboratory = (Laboratory) method.invoke(labManager, laboratory);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@Test
	void testMgrnewLaboratoryProcedureEquals2() throws Exception {
		ArrayList<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labRow.add("TestLabRow");
		// method is protected not thus use of reflection
		Method method = labManager.getClass().getDeclaredMethod("newLabSecondProcedure", Laboratory.class, List.class);
		method.setAccessible(true);
		Laboratory newLaboratory = (Laboratory) method.invoke(labManager, laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@Test
	void testMgrNewLaboratoryProcedureEquals2EmptyLabRows() {
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

	@Test
	void testMgrNewLaboratoryProcedureEquals2NullLabRows() {
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

	@Test
	void testMgrNewLaboratoryExceptionsBadProcedureNumber() {
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

	@Test
	void testMgrNewLaboratoryProcedureEquals2RollbackTransaction() throws Exception {
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
		// method is protected not thus use of reflection
		Method method = labManager.getClass().getDeclaredMethod("newLabSecondProcedure", Laboratory.class, List.class);
		method.setAccessible(true);
		Laboratory newLaboratory = (Laboratory) method.invoke(labManager, laboratory, labRow);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@Test
	void testMgrNewLaboratory2ProcedureEquals1() throws Exception {
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

	@Test
	void testMgrNewLaboratory2ProcedureEquals2() throws Exception {
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

	@Test
	void testMgrNewLaboratory2ProcedureEquals3() throws Exception {
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

	@Test
	void testMgrNewLaboratory2ProcedureEquals2NullLabRows() {
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

	@Test
	void testMgrNewLaboratory2BadProcedureNumber() {
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

	@Test
	void testMgrValidationMissingExamDate() throws Exception {
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

	@Test
	void testMgrValidationMissingLabDate() {
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
	@MethodSource("generalDataLab")
	void testMgrValidationLABEXTENDEDNoPatient(boolean labextended) {
		// Only generates an exception if LABEXTENDED is true
		LABEXTENDED = labextended;
		assumeThat(labextended).isTrue();

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
	@MethodSource("generalDataLab")
	void testMgrValidationLABEXTENDEDPatient(boolean labextended) throws Exception {
		// Only run if LABEXTENDED is true
		assumeThat(labextended).isTrue();
		LABEXTENDED = labextended;

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

	@Test
	void testMgrValidationLNoPatientBadSex() {
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

	@Test
	void testMgrValidationLNoPatientBadAge() {
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

	@Test
	void testMgrValidationLNoExam() {
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

	@Test
	void testMgrValidationLNoResult() {
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

	@Test
	void testMgrValidationLNoMaterial() {
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

	@Test
	void testMgrValidationLNoInOutPatient() {
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

	@Test
	void testMgrUpdateLaboratoryProcedure1() throws Exception {
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		foundLaboratory.setNote("Update");
		Laboratory updatedLaboratory = labManager.updateLaboratory(foundLaboratory, null);
		assertThat(updatedLaboratory.getNote()).isEqualTo("Update");
	}

	@Test
	void testMgrUpdateLaboratoryProcedure2() throws Exception {
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

	@Test
	void testMgrUpdateLaboratoryProcedure3() throws Exception {
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

	@Test
	void testMgrUpdateLaboratoryExceptionEmptyLabRows() {
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

	@Test
	void testMgrUpdateLaboratoryExceptionNullLablRows() {
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

	@Test
	void testMgrUpdateLaboratoryExceptionBadProcedureNumber() {
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

	@Test
	void testMgrNewLaboratoryTransaction() throws Exception {
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

		Laboratory newLaboratory = labManager.newLaboratory(laboratories, labRowList);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@Test
	void testMgrNewLaboratoryTransactionNoLabList() {
		assertThatThrownBy(() ->
		{
			List<Laboratory> laboratories = new ArrayList<>();
			List<List<String>> labRowList = new ArrayList<>();

			labManager.newLaboratory(laboratories, labRowList);
		})
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrNewLaboratoryTransactionLabListNotEqualLabRowList() {
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

	@Test
	void testMgrNewLaboratory2Transaction() throws Exception {
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

	@Test
	void testMgrNewLaboratory2TransactionNoLabList() {
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

	@Test
	void testMgrNewLaboratory2TransactionLabListNotEqualLabRowList() {
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

	@Test
	void testMgrDeleteLaboratory() throws Exception {
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		labManager.deleteLaboratory(foundLaboratory);
		assertThat(labIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrDeleteLaboratoryProcedure2() throws Exception {
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

	@Test
	void testMgrGetMaterialKeyUndefined() {
		assertThat(labManager.getMaterialKey("notThere")).isEqualTo("undefined");
	}

	@Test
	void testMgrGetMaterialKeyFound() {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialKey("angal.lab.film.txt")).isEqualTo("film");
	}

	@Test
	void testMgrGetMaterialTranslatedNotThere() {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("notThere")).isEqualTo("angal.lab.undefined.txt");
	}

	@Test
	void testMgrGetMaterialTranslatedNull() {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated(null)).isEqualTo("angal.lab.undefined.txt");
	}

	@Test
	void testMgrGetMaterialTranslatedFound() {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("film")).isEqualTo("angal.lab.film.txt");
	}

	@Test
	void testMgrGetMaterialLIst() {
		assertThat(labManager.getMaterialList()).hasSize(9);
		List<String> materailList = labManager.getMaterialList();
		// TODO: if resource bundles are made available this needs to change
		assertThat(materailList.get(0)).isEqualTo("angal.lab.undefined.txt");
		materailList.remove(0);   // Remove the default value that is placed first in the list even if out of order
		assertThat(materailList).isSorted();
	}

	@Test
	void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
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

	@Test
	void testNewLaboratoryGetterSetters() throws Exception {
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

	@Test
	void testLaboratoryEqualsHashToString() throws Exception {
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

	@Test
	void testNewLaboratoryRowGetterSetters() throws Exception {
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

	@Test
	void testLaboratoryRowEqualsHash() throws Exception {
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

	@Test
	void testLaboratoryForPrintGetterSetter() throws Exception {
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