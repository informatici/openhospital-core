/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.lab.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exa.test.TestExam;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.exatype.test.TestExamType;
import org.isf.generaldata.GeneralData;
import org.isf.lab.manager.LabManager;
import org.isf.lab.manager.LabRowManager;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.lab.service.LabIoOperationRepository;
import org.isf.lab.service.LabIoOperations;
import org.isf.lab.service.LabRowIoOperationRepository;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@RunWith(Parameterized.class)
public class Tests extends OHCoreTestCase {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

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

	public Tests(boolean labExtended) {
		GeneralData.LABEXTENDED = labExtended;
		// Note: GeneralData.LABMULTIPLEINSERT is only used in the GUI code
	}

	@BeforeClass
	public static void setUpClass() {
		testLaboratory = new TestLaboratory();
		testLaboratoryRow = new TestLaboratoryRow();
		testExam = new TestExam();
		testExamType = new TestExamType();
		testPatient = new TestPatient();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Parameterized.Parameters(name = "Test with LABEXTENDED={0}")
	public static Collection<Object[]> generalDataLab() {
		return Arrays.asList(new Object[][] {
				{ false },
				{ true }
		});
	}

	@Test
	public void testLaboratoryGets() throws Exception {
		int code = setupTestLaboratory(false);
		checkLaboratoryIntoDb(code);
	}

	@Test
	public void testLaboratorySets() throws Exception {
		Integer code = setupTestLaboratory(true);
		checkLaboratoryIntoDb(code);
	}

	@Test
	public void testLaboratoryRowGets() throws Exception {
		int code = setupTestLaboratoryRow(false);
		checkLaboratoryRowIntoDb(code);
	}

	@Test
	public void testLaboratoryRowSets() throws Exception {
		int code = setupTestLaboratoryRow(true);
		checkLaboratoryRowIntoDb(code);
	}

	@Test
	public void testIoGetLabRowByLabId() throws Exception {
		Integer id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).get();
		List<LaboratoryRow> laboratoryRows = labIoOperation.getLabRow(foundLaboratoryRow.getLabId().getCode());
		assertThat(laboratoryRows).contains(foundLaboratoryRow);
	}

	@Test
	public void testIoGetLaboratory() throws Exception {
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		List<Laboratory> laboratories = labIoOperation.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryWithDates() throws Exception {
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		List<Laboratory> laboratories = labIoOperation
				.getLaboratory(foundLaboratory.getExam().getDescription(), foundLaboratory.getDate(), foundLaboratory.getDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryWithoutDescription() throws Exception {
		// given:
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();

		// when:
		List<Laboratory> laboratories = labIoOperation.getLaboratory(null, foundLaboratory.getDate(), foundLaboratory.getDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryFromPatient() throws Exception {
		int id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).get();
		List<Laboratory> laboratories = labIoOperation.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@Test
	public void testIoGetLaboratoryForPrint() throws Exception {
		Integer id = setupTestLaboratory(true);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		List<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryForPrintWithDates() throws Exception {
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		List<LaboratoryForPrint> laboratories = labIoOperation
				.getLaboratoryForPrint(foundLaboratory.getExam().getDescription(), foundLaboratory.getDate(), foundLaboratory.getDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryForPrintWithExamDescriptionLikePersistedOne() throws Exception {
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labIoOperation
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getDate(), foundLaboratory.getDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryForPrintWithNullExamDescription() throws Exception {
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();

		// when:
		List<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint(null, foundLaboratory.getDate(), foundLaboratory.getDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoNewLabFirstProcedure() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		boolean result = labIoOperation.newLabFirstProcedure(laboratory);
		assertThat(result).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testIoNewLabSecondProcedure() throws Exception {
		ArrayList<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labRow.add("TestLabRow");
		boolean result = labIoOperation.newLabSecondProcedure(laboratory, labRow);
		assertThat(result).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testIoNewLabSecondProcedureTransaction() throws Exception {
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
		boolean result = labIoOperation.newLabSecondProcedure(laboratory, labRow);
		assertThat(result).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testIoNewLabSecondProcedure2() throws Exception {
		ArrayList<LaboratoryRow> labRow = new ArrayList<>();
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
		boolean result = labIoOperation.newLabSecondProcedure2(laboratory, labRow);
		assertThat(result).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testIoUpdateLaboratory() throws Exception {
		Integer code = setupTestLaboratory(false);
		Laboratory foundlaboratory = labIoOperationRepository.findById(code).get();
		foundlaboratory.setNote("Update");
		boolean result = labIoOperation.updateLabFirstProcedure(foundlaboratory);
		assertThat(result).isTrue();
		Laboratory updateLaboratory = labIoOperationRepository.findById(code).get();
		assertThat(updateLaboratory.getNote()).isEqualTo("Update");
	}

	@Test
	public void testIoEditLabSecondProcedure() throws Exception {
		ArrayList<String> labRow = new ArrayList<>();
		Integer code = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(code).get();
		labRow.add("Update");
		boolean result = labIoOperation.updateLabSecondProcedure(foundLaboratoryRow.getLabId(), labRow);
		assertThat(result).isTrue();
		LaboratoryRow updateLaboratoryRow = labRowIoOperationRepository.findById(code + 1).get();
		assertThat(updateLaboratoryRow.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoDeleteLaboratory() throws Exception {
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).get();
		boolean result = labIoOperation.deleteLaboratory(foundLaboratory);
		assertThat(result).isTrue();
		result = labIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoDeleteLaboratoryProcedureEquals2() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		boolean result = labIoOperation.deleteLaboratory(laboratory);
		assertThat(result).isTrue();
		result = labIoOperation.isCodePresent(laboratory.getCode());
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetLaboratory() throws Exception {
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		List<Laboratory> laboratories = labManager.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testRowMgrGetLabRowByLabId() throws Exception {
		Integer id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).get();
		List<LaboratoryRow> laboratoryRows = labRowManager.getLabRowByLabId(foundLaboratoryRow.getLabId().getCode());
		assertThat(laboratoryRows).contains(foundLaboratoryRow);
	}

	@Test
	public void testMgrGetLaboratoryRelatedToPatient() throws Exception {
		int id = setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(id).get();
		List<Laboratory> laboratories = labManager.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@Test
	public void testMgrGetLaboratoryWithDatesAndExam() throws Exception {
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		List<Laboratory> laboratories = labManager
				.getLaboratory(foundLaboratory.getExam().getDescription(), foundLaboratory.getDate(), foundLaboratory.getDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryWithoutExamName() throws Exception {
		// given:
		int id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();

		// when:
		List<Laboratory> laboratories = labManager.getLaboratory(null, foundLaboratory.getDate(), foundLaboratory.getDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryForPrintWithDates() throws Exception {
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(foundLaboratory.getExam().getDescription(), foundLaboratory.getDate(), foundLaboratory.getDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryForPrintWithExamDescriptionLikePersistedOne() throws Exception {
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getDate(), foundLaboratory.getDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryForPrintWithNullExamDescription() throws Exception {
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();

		// when:
		List<LaboratoryForPrint> laboratories = labManager.getLaboratoryForPrint(null, foundLaboratory.getDate(), foundLaboratory.getDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryForPrintMultipeResultsNoRows() throws Exception {
		// given:
		Integer id = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(id).get();
		// TODO: if resource bundles are made available this setResults() needs to change
		foundLaboratory.setResult("angal.lab.multipleresults.txt");
		ArrayList<String> labRow = new ArrayList<>();
		labManager.updateLaboratory(foundLaboratory, labRow);
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getDate(), foundLaboratory.getDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
		// TODO: if resource bundles are made available this value needs to change
		assertThat(laboratories.get(0).getResult()).isEqualTo("angal.lab.allnegative.txt");
	}

	@Test
	public void testMgrGetLaboratoryForPrintMultipeResultsRows() throws Exception {
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
				.getLaboratoryForPrint(firstCharsOfDescription, laboratory.getDate(), laboratory.getDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
		// TODO: if resource bundles are made available this value needs to change
		assertThat(laboratories.get(0).getResult()).isEqualTo("angal.lab.multipleresults.txt,TestDescription");
	}

	@Test
	public void testMgrnewLaboratoryProcedureEquals1() throws Exception {
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
		assertThat((Boolean) method.invoke(labManager, laboratory)).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrnewLaboratoryProcedureEquals2() throws Exception {
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
		assertThat((Boolean) method.invoke(labManager, laboratory, labRow)).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratoryProcedureEquals2EmptyLabRows() {
		assertThatThrownBy(() ->
		{
			ArrayList<String> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 2, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewLaboratoryProcedureEquals2NullLabRows() {
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
	public void testMgrNewLaboratoryExceptionsBadProcedureNumber() {
		assertThatThrownBy(() ->
		{
			ArrayList<String> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 99, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewLaboratoryProcedureEquals2RollbackTransaction() throws Exception {
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
		assertThat((Boolean) method.invoke(labManager, laboratory, labRow)).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratory2ProcedureEquals1() throws Exception {
		ArrayList<LaboratoryRow> labRow = new ArrayList<>();
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
		boolean result = labManager.newLaboratory2(laboratory, labRow);
		assertThat(result).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratory2ProcedureEquals2() throws Exception {
		ArrayList<LaboratoryRow> labRow = new ArrayList<>();
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
		boolean result = labManager.newLaboratory2(laboratory, labRow);
		assertThat(result).isTrue();
		// TODO: if resource bundles are made available this must change
		Laboratory foundLaboratory = labIoOperationRepository.findById(laboratory.getCode()).get();
		assertThat(laboratory.getResult()).isEqualTo("angal.lab.multipleresults.txt");
	}

	@Test
	public void testMgrNewLaboratory2ProcedureEquals3() throws Exception {
		ArrayList<LaboratoryRow> labRow = new ArrayList<>();
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
		boolean result = labManager.newLaboratory2(laboratory, labRow);
		assertThat(result).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratory2ProcedureEquals2NullLabRows() {
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
	public void testMgrNewLaboratory2BadProcedureNumber() {
		assertThatThrownBy(() ->
		{
			ArrayList<LaboratoryRow> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 99, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.newLaboratory2(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationMissingExamDate() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);

		ArrayList<String> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);

		assertThat(labManager.newLaboratory(laboratory, labRow)).isTrue();
	}

	@Test
	public void testMgrValidationMissingLabDate() {
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setDate(null);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLABEXTENDEDNoPatient() {
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
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratory.setPatient(null);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLABEXTENDEDPatient() throws Exception {
		// Only run if LABEXTENDED is true
		assumeThat(GeneralData.LABEXTENDED).isTrue();

		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);

		// laboratory 1, Procedure One
		ArrayList<String> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		labManager.newLaboratory(laboratory, labRow);

		Laboratory foundLaboratory = labIoOperationRepository.findById(laboratory.getCode()).get();
		assertThat(laboratory.getPatName()).isEqualTo(laboratory.getPatient().getName());
		assertThat(laboratory.getAge()).isEqualTo(laboratory.getPatient().getAge());
		assertThat(laboratory.getSex()).isEqualTo(String.valueOf(laboratory.getPatient().getSex()));
	}

	@Test
	public void testMgrValidationLNoPatientBadSex() {
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setPatient(null);
			laboratory.setSex("?");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoPatientBadAge() {
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setPatient(null);
			laboratory.setAge(-99);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoExam() {
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setExam(null);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoResult() {
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setResult("");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoMaterial() {
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setMaterial("");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoInOutPatient() {
		assertThatThrownBy(() ->
		{
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);

			laboratory.setInOutPatient("");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrUpdateLaboratoryProcedure1() throws Exception {
		Integer code = setupTestLaboratory(false);
		Laboratory foundlaboratory = labIoOperationRepository.findById(code).get();
		foundlaboratory.setNote("Update");
		boolean result = labManager.updateLaboratory(foundlaboratory, null);
		assertThat(result).isTrue();
		Laboratory updateLaboratory = labIoOperationRepository.findById(code).get();
		assertThat(updateLaboratory.getNote()).isEqualTo("Update");
	}

	@Test
	public void testMgrUpdateLaboratoryProcedure2() throws Exception {
		ArrayList<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		Laboratory foundlaboratory = labIoOperationRepository.findById(laboratory.getCode()).get();
		foundlaboratory.setNote("Update");
		labRow.add("laboratoryRow");
		boolean result = labManager.updateLaboratory(foundlaboratory, labRow);
		assertThat(result).isTrue();
		Laboratory updateLaboratory = labIoOperationRepository.findById(foundlaboratory.getCode()).get();
		assertThat(updateLaboratory.getNote()).isEqualTo("Update");
	}

	@Test
	public void testMgrUpdateLaboratoryProcedure3() throws Exception {
		ArrayList<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 3, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		Laboratory foundlaboratory = labIoOperationRepository.findById(laboratory.getCode()).get();
		foundlaboratory.setNote("Update");
		boolean result = labManager.updateLaboratory(foundlaboratory, null);
		assertThat(result).isTrue();
		Laboratory updateLaboratory = labIoOperationRepository.findById(foundlaboratory.getCode()).get();
		assertThat(updateLaboratory.getNote()).isEqualTo("Update");
	}

	@Test
	public void testMgrUpdateLaboratoryExceptionEmptyLabRows() {
		assertThatThrownBy(() ->
		{
			ArrayList<String> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 2, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.updateLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrUpdateLaboratoryExceptionNullLablRows() {
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
	public void testMgrUpdateLaboratoryExceptionBadProcedureNumber() {
		assertThatThrownBy(() ->
		{
			ArrayList<String> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 99, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.updateLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewLaboratoryTransaction() throws Exception {
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

		boolean result = labManager.newLaboratory(laboratories, labRowList);

		assertThat(result).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratoryTransactionNoLabList() {
		assertThatThrownBy(() ->
		{
			List<Laboratory> laboratories = new ArrayList<>();
			List<List<String>> labRowList = new ArrayList<>();

			labManager.newLaboratory(laboratories, labRowList);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewLaboratoryTransactionLabListNotEqualLabRowList() {
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
	public void testMgrNewLaboratory2Transaction() throws Exception {
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

		boolean result = labManager.newLaboratory2(labList, labRowList);

		assertThat(result).isTrue();
		checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratory2TransactionNoLabList() {
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

			List<LaboratoryRow> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labIoOperationRepository.saveAndFlush(laboratory);

			labManager.newLaboratory2(labList, labRowList);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewLaboratory2TransactionLabListNotEqualLabRowList() {
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
	public void testMgrEditLabFirstProcedure() throws Exception {
		Integer code = setupTestLaboratory(false);
		Laboratory foundlaboratory = labIoOperationRepository.findById(code).get();
		foundlaboratory.setNote("Update");
		// method is protected not public
		Method method = labManager.getClass().getDeclaredMethod("editLabFirstProcedure", Laboratory.class);
		method.setAccessible(true);
		assertThat((boolean) method.invoke(labManager, foundlaboratory)).isTrue();
		List<Laboratory> updateLaboratory = labIoOperationRepository.findAll();
		assertThat(updateLaboratory).hasSize(1);
		assertThat(updateLaboratory.get(0).getNote()).isEqualTo("Update");
	}

	@Test
	public void testMgrEditLabSecondProcedure() throws Exception {
		ArrayList<String> labRow = new ArrayList<>();
		Integer code = setupTestLaboratory(false);
		Laboratory laboratory = labIoOperationRepository.findById(code).get();
		labRow.add("Update");
		// method is protected not public
		Method method = labManager.getClass().getDeclaredMethod("editLabSecondProcedure", Laboratory.class, List.class);
		method.setAccessible(true);
		assertThat((Boolean) method.invoke(labManager, laboratory, labRow)).isTrue();
		List<LaboratoryRow> updateLaboratoryRow = labRowIoOperationRepository.findAll();
		assertThat(updateLaboratoryRow).hasSize(1);
		assertThat(updateLaboratoryRow.get(0).getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrDeleteLaboratory() throws Exception {
		Integer code = setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).get();
		boolean result = labManager.deleteLaboratory(foundLaboratory);
		assertThat(result).isTrue();
		result = labIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrDeleteLaboratoryProcedure2() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		labIoOperationRepository.saveAndFlush(laboratory);
		boolean result = labManager.deleteLaboratory(laboratory);
		assertThat(result).isTrue();
		result = labIoOperation.isCodePresent(laboratory.getCode());
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetMaterialKeyUndefined() {
		assertThat(labManager.getMaterialKey("notThere")).isEqualTo("undefined");
	}

	@Test
	public void testMgrGetMaterialKeyFound() {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialKey("angal.lab.film.txt")).isEqualTo("film");
	}

	@Test
	public void testMgrGetMaterialTranslatedNotThere() {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("notThere")).isEqualTo("angal.lab.undefined.txt");
	}

	@Test
	public void testMgrGetMaterialTranslatedNull() {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated(null)).isEqualTo("angal.lab.undefined.txt");
	}

	@Test
	public void testMgrGetMaterialTranslatedFound() {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("film")).isEqualTo("angal.lab.film.txt");
	}

	@Test
	public void testMgrGetMaterialLIst() {
		assertThat(labManager.getMaterialList()).hasSize(9);
		List materailList = labManager.getMaterialList();
		// TODO: if resource bundles are made available this needs to change
		assertThat(materailList.get(0)).isEqualTo("angal.lab.undefined.txt");
		materailList.remove(0);   // Remove the default value that is placed first in the list even if out of order
		assertThat(materailList).isSorted();
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = setupTestLaboratory(false);
		Laboratory found = labIoOperationRepository.findById(id).get();
		Patient mergedPatient = testPatient.setup(true);
		patientIoOperationRepository.saveAndFlush(mergedPatient);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		Laboratory result = labIoOperationRepository.findById(id).get();
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
		assertThat(result.getPatName()).isEqualTo(mergedPatient.getName());
		assertThat(Long.valueOf(result.getAge())).isEqualTo(Long.valueOf(mergedPatient.getAge()));
		assertThat(result.getSex()).isEqualTo(String.valueOf(mergedPatient.getSex()));
	}

	@Test
	public void testNewLaboratoryGetterSetters() throws Exception {
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
	public void testLaboratoryEqualsHashToString() throws Exception {
		int code = setupTestLaboratory(false);
		Laboratory laboratory = labIoOperationRepository.findById(code).get();
		Laboratory laboratory2 = new Laboratory(code + 1, null, TimeTools.getNow(), "result", "note", null, "name");
		assertThat(laboratory.equals(laboratory)).isTrue();
		assertThat(laboratory)
				.isNotEqualTo(laboratory2)
				.isNotEqualTo("xyzzy");
		laboratory2.setCode(code);
		assertThat(laboratory).isEqualTo(laboratory2);

		assertThat(laboratory.hashCode()).isPositive();

		assertThat(laboratory.toString()).isNotEmpty();
	}

	@Test
	public void testNewLaboratoryRowGetterSetters() throws Exception {
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
	public void testLaboratoryRowEqualsHash() throws Exception {
		int code = setupTestLaboratoryRow(false);
		LaboratoryRow laboratoryRow = labRowIoOperationRepository.findById(code).get();
		LaboratoryRow laboratoryRow2 = new LaboratoryRow(code + 1, null, "description");
		assertThat(laboratoryRow.equals(laboratoryRow)).isTrue();
		assertThat(laboratoryRow)
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
	public void testLaboratoryForPrintGetterSetter() throws Exception {
		Integer id = setupTestLaboratory(false);
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
		Laboratory foundLaboratory = labIoOperationRepository.findById(code).get();
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
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findById(code).get();
		testLaboratoryRow.check(foundLaboratoryRow);
	}
}