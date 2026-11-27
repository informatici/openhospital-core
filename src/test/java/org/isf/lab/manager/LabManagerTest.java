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
package org.isf.lab.manager;

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
import org.isf.lab.TestLaboratory;
import org.isf.lab.TestLaboratoryRow;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.lab.model.LaboratoryStatus;
import org.isf.lab.service.LabIoOperationRepository;
import org.isf.lab.service.LabIoOperations;
import org.isf.lab.service.LabRowIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.pagination.PagedResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class LabManagerTest extends OHCoreTestCase {

	private static final String DRAFT = LaboratoryStatus.draft.toString();
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
	void getLaboratoryRowList_shouldReturnSavedRow(boolean labExtended) throws Exception {
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
	void getLaboratoryPageable_shouldReturnPageWithLaboratory_whenOneWeekIsFalse(boolean labExtended) throws Exception {
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
	void getLaboratory_shouldReturnLaboratoryOfLastWeek_whenNoFilterIsGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<Laboratory> laboratories = labManager.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryPageable_shouldReturnPageWithLaboratory_whenExamAndPatientAreGiven(boolean labExtended) throws Exception {
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
	void getLaboratoryForPrint_shouldReturnLaboratory_whenExamIsGivenAndPatientIsNull(boolean labExtended) throws Exception {
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
	void getLaboratoryForPrint_shouldReturnLaboratory_whenExamIsNullAndPatientIsGiven(boolean labExtended) throws Exception {
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
	void getLaboratoryForPrint_shouldReturnLaboratory_whenExamAndPatientAreNull(boolean labExtended) throws Exception {
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
	void getLaboratoryForPrint_shouldReturnLaboratory_whenExamAndPatientAreGiven(boolean labExtended) throws Exception {
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
	void getLaboratoryPageable_shouldReturnPageWithLaboratory_whenExamIsGivenAndPatientIsNull(boolean labExtended) throws Exception {
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
	void getLaboratoryPageable_shouldReturnPageWithLaboratory_whenExamIsNullAndPatientIsGiven(boolean labExtended) throws Exception {
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
	void getLaboratoryPageable_shouldReturnPageWithLaboratory_whenExamAndPatientAreNull(boolean labExtended) throws Exception {
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
	void getLaboratory_shouldReturnLaboratory_whenExamAndPatientAreGiven(boolean labExtended) throws Exception {
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
	void getLaboratory_shouldReturnLaboratory_whenExamIsGivenAndPatientIsNull(boolean labExtended) throws Exception {
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
	void getLaboratory_shouldReturnLaboratory_whenExamIsEmptyAndPatientIsGiven(boolean labExtended) throws Exception {
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
	void getLaboratory_shouldReturnLaboratory_whenExamIsEmptyAndPatientIsNull(boolean labExtended) throws Exception {
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
	void getLaboratory_shouldReturnPatientLaboratories_whenOnlyPatientIsGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		int id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratoryRow).isNotNull();
		List<Laboratory> laboratories = labManager.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratory_shouldReturnLaboratory_whenExamAndDatesAreGiven(boolean labExtended) throws Exception {
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
	void getLaboratory_shouldReturnLaboratory_whenExamIsNullAndDatesAreGiven(boolean labExtended) throws Exception {
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
	void getLaboratoryForPrint_shouldReturnLaboratory_whenExamAndDatesAreGiven(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		List<LaboratoryForPrint> laboratories = labManager
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
		List<LaboratoryForPrint> laboratories = labManager
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
		List<LaboratoryForPrint> laboratories = labManager.getLaboratoryForPrint(null, foundLaboratory.getLabDate(), foundLaboratory.getLabDate(), null);

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryForPrint_shouldReturnAllNegativeResult_whenResultIsMultipleAndLaboratoryHasNoRows(boolean labExtended) throws Exception {
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
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getLabDate(), foundLaboratory.getLabDate(), null);

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
		// TODO: if resource bundles are made available this value needs to change
		assertThat(laboratories.get(0).getResult()).isEqualTo("angal.lab.allnegative.txt");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryForPrint_shouldAppendRowDescriptionsToResult_whenResultIsMultiple(boolean labExtended) throws Exception {
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
				.getLaboratoryForPrint(firstCharsOfDescription, laboratory.getLabDate(), laboratory.getLabDate(), null);

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
		// TODO: if resource bundles are made available this value needs to change
		assertThat(laboratories.get(0).getResult()).isEqualTo("angal.lab.multipleresults.txt,TestDescription");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratoryForPrint_shouldAppendRowDescriptionsToResult_whenResultIsMultipleAndPatientIsGiven(boolean labExtended) throws Exception {
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

		// when: the patient-aware path also expands multiple results
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(laboratory.getExam().getDescription(), laboratory.getLabDate(), laboratory.getLabDate(), patient);

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
		// TODO: if resource bundles are made available this value needs to change
		assertThat(laboratories.get(0).getResult()).isEqualTo("angal.lab.multipleresults.txt,TestDescription");
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
		// method is protected not public thus use of reflection
		Method method = labManager.getClass().getDeclaredMethod("newLabFirstProcedure", Laboratory.class);
		method.setAccessible(true);
		Laboratory newLaboratory = (Laboratory) method.invoke(labManager, laboratory);
		checkLaboratoryIntoDb(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void newLabSecondProcedure_shouldSaveLaboratory(boolean labExtended) throws Exception {
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
	void newLaboratory_shouldThrow_whenExamProcedureIsTwoAndLabRowsAreEmpty(boolean labExtended) {
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
	void updateExamRequest_shouldThrow_whenLaboratoryNotFound(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThatThrownBy(() -> {
			labManager.updateExamRequest(-99, "status");
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void updateExamRequest_shouldSaveNewStatus(boolean labExtended) throws Exception {
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

		Laboratory updatedLaboratory = labManager.updateExamRequest(newLaboratory.getCode(), DRAFT);
		assertThat(updatedLaboratory.getStatus()).isEqualTo(DRAFT);
		assertThat(updatedLaboratory.getCode()).isEqualTo(newLaboratory.getCode());
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getLaboratory_shouldReturnLaboratory_whenCodeExists(boolean labExtended) throws Exception {
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
	void newLaboratory_shouldThrow_whenExamProcedureIsTwoAndLabRowsAreNull(boolean labExtended) {
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
	void newLaboratory_shouldThrow_whenExamProcedureIsUnknown(boolean labExtended) {
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
	void newLabSecondProcedure_shouldSaveLaboratory_whenRowDescriptionHasSixtyCharacters(boolean labExtended) throws Exception {
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
	void newLaboratory2_shouldReturnSavedLaboratory_whenExamProcedureIsOne(boolean labExtended) throws Exception {
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
	void newLaboratory2_shouldSetResultToMultipleResults_whenExamProcedureIsTwo(boolean labExtended) throws Exception {
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
	void newLaboratory2_shouldReturnSavedLaboratory_whenExamProcedureIsThree(boolean labExtended) throws Exception {
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
	void newLaboratory_shouldThrowDataValidation_whenExamProcedureIsTwoAndLabRowsAreNull(boolean labExtended) {
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
	void newLaboratory2_shouldThrow_whenExamProcedureIsUnknown(boolean labExtended) {
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
	void newLaboratory_shouldSaveLaboratory_whenExamProcedureIsOne(boolean labExtended) throws Exception {
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
	void newLaboratory_shouldThrow_whenLabDateIsNull(boolean labExtended) {
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
	void newLaboratory_shouldThrow_whenPatientIsNullAndLabExtendedIsTrue(boolean labExtended) {
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
	void newLaboratory_shouldCopyPatientNameAgeAndSex_whenLabExtendedIsTrue(boolean labExtended) throws Exception {
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
	void newLaboratory_shouldThrow_whenPatientIsNullAndSexIsInvalid(boolean labExtended) {
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
	void newLaboratory_shouldThrow_whenPatientIsNullAndAgeIsNegative(boolean labExtended) {
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
	void newLaboratory_shouldThrow_whenExamIsNull(boolean labExtended) {
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
	void newLaboratory_shouldThrow_whenResultIsEmpty(boolean labExtended) {
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
	void newLaboratory_shouldThrow_whenMaterialIsEmpty(boolean labExtended) {
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
	void newLaboratory_shouldThrow_whenInOutPatientIsEmpty(boolean labExtended) {
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
	void updateLaboratory_shouldSaveNewNote_whenExamProcedureIsOne(boolean labExtended) throws Exception {
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
	void updateLaboratory_shouldSaveNewNote_whenExamProcedureIsTwo(boolean labExtended) throws Exception {
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
	void updateLaboratory_shouldSaveNewNote_whenExamProcedureIsThree(boolean labExtended) throws Exception {
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
	void updateLaboratory_shouldThrow_whenExamProcedureIsTwoAndLabRowsAreEmpty(boolean labExtended) {
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
	void updateLaboratory_shouldThrow_whenExamProcedureIsTwoAndLabRowsAreNull(boolean labExtended) {
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
	void updateLaboratory_shouldThrow_whenExamProcedureIsUnknown(boolean labExtended) {
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
	void newLaboratory_shouldReturnFirstSavedLaboratory_whenLaboratoryListIsGiven(boolean labExtended) throws Exception {
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
	void newLaboratory_shouldThrow_whenLaboratoryListIsEmpty(boolean labExtended) {
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
	void newLaboratory_shouldThrow_whenLaboratoryListAndRowListSizesDiffer(boolean labExtended) {
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
	void newLaboratory2_shouldReturnFirstSavedLaboratory_whenLaboratoryListIsGiven(boolean labExtended) throws Exception {
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
	void newLaboratory2_shouldThrow_whenLaboratoryListIsEmpty(boolean labExtended) {
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
	void newLaboratory2_shouldThrow_whenLaboratoryListAndRowListSizesDiffer(boolean labExtended) {
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
	void deleteLaboratory_shouldRemoveLaboratory(boolean labExtended) throws Exception {
		GeneralData.LABEXTENDED = labExtended;
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).orElse(null);
		assertThat(foundLaboratory).isNotNull();
		labManager.deleteLaboratory(foundLaboratory);
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
		labManager.deleteLaboratory(laboratory);
		assertThat(labIoOperation.isCodePresent(laboratory.getCode())).isFalse();
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getMaterialKey_shouldReturnUndefined_whenDescriptionIsUnknown(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThat(labManager.getMaterialKey("notThere")).isEqualTo("undefined");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getMaterialKey_shouldReturnKey_whenDescriptionIsKnown(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialKey("angal.lab.film.txt")).isEqualTo("film");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getMaterialTranslated_shouldReturnUndefinedDescription_whenKeyIsUnknown(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("notThere")).isEqualTo("angal.lab.undefined.txt");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getMaterialTranslated_shouldReturnUndefinedDescription_whenKeyIsNull(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated(null)).isEqualTo("angal.lab.undefined.txt");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getMaterialTranslated_shouldReturnDescription_whenKeyIsKnown(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("film")).isEqualTo("angal.lab.film.txt");
	}

	@ParameterizedTest(name = "Test with LABEXTENDED={0}")
	@MethodSource("labExtended")
	void getMaterialList_shouldReturnNineDescriptionsSortedWithUndefinedFirst(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		assertThat(labManager.getMaterialList()).hasSize(9);
		List<String> materailList = labManager.getMaterialList();
		// TODO: if resource bundles are made available this needs to change
		assertThat(materailList.get(0)).isEqualTo("angal.lab.undefined.txt");
		materailList.remove(0);   // Remove the default value that is placed first in the list even if out of order
		assertThat(materailList).isSorted();
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