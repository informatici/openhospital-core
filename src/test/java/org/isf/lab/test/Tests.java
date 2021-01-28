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
package org.isf.lab.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.GregorianCalendar;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class Tests extends OHCoreTestCase {

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

	@Test
	public void testLaboratoryGets() throws Exception {
		int code = _setupTestLaboratory(false);
		_checkLaboratoryIntoDb(code);
	}

	@Test
	public void testLaboratorySets() throws Exception {
		int code = _setupTestLaboratory(true);
		_checkLaboratoryIntoDb(code);
	}

	@Test
	public void testLaboratoryRowGets() throws Exception {
		int code = _setupTestLaboratoryRow(false);
		_checkLaboratoryRowIntoDb(code);
	}

	@Test
	public void testLaboratoryRowSets() throws Exception {
		int code = _setupTestLaboratoryRow(true);
		_checkLaboratoryRowIntoDb(code);
	}

	@Test
	public void testIoGetLabRowByLabId() throws Exception {
		Integer id = _setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findOne(id);
		ArrayList<LaboratoryRow> laboratoryRows = labIoOperation.getLabRow(foundLaboratoryRow.getLabId().getCode());
		assertThat(laboratoryRows).contains(foundLaboratoryRow);
	}

	@Test
	public void testIoGetLaboratory() throws Exception {
		int id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		ArrayList<Laboratory> laboratories = labIoOperation.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryWithDates() throws Exception {
		int id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		ArrayList<Laboratory> laboratories = labIoOperation
				.getLaboratory(foundLaboratory.getExam().getDescription(), foundLaboratory.getExamDate(), foundLaboratory.getExamDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryWithoutDescription() throws Exception {
		// given:
		int id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);

		// when:
		ArrayList<Laboratory> laboratories = labIoOperation.getLaboratory(null, foundLaboratory.getExamDate(), foundLaboratory.getExamDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryFromPatient() throws Exception {
		int id = _setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findOne(id);
		ArrayList<Laboratory> laboratories = labIoOperation.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@Test
	public void testIoGetLaboratoryForPrint() throws Exception {
		Integer id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		ArrayList<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryForPrintWithDates() throws Exception {
		Integer id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		ArrayList<LaboratoryForPrint> laboratories = labIoOperation
				.getLaboratoryForPrint(foundLaboratory.getExam().getDescription(), foundLaboratory.getExamDate(), foundLaboratory.getExamDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryForPrintWithExamDescriptionLikePersistedOne() throws Exception {
		// given:
		Integer id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		ArrayList<LaboratoryForPrint> laboratories = labIoOperation
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getExamDate(), foundLaboratory.getExamDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testIoGetLaboratoryForPrintWithNullExamDescription() throws Exception {
		// given:
		Integer id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);

		// when:
		ArrayList<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint(null, foundLaboratory.getExamDate(), foundLaboratory.getExamDate());

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
		_checkLaboratoryIntoDb(laboratory.getCode());
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
		_checkLaboratoryIntoDb(laboratory.getCode());
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
		_checkLaboratoryIntoDb(laboratory.getCode());
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
		_checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testIoUpdateLaboratory() throws Exception {
		Integer code = _setupTestLaboratory(false);
		Laboratory foundlaboratory = labIoOperationRepository.findOne(code);
		foundlaboratory.setNote("Update");
		boolean result = labIoOperation.updateLabFirstProcedure(foundlaboratory);
		assertThat(result).isTrue();
		Laboratory updateLaboratory = labIoOperationRepository.findOne(code);
		assertThat(updateLaboratory.getNote()).isEqualTo("Update");
	}

	@Test
	public void testIoEditLabSecondProcedure() throws Exception {
		ArrayList<String> labRow = new ArrayList<>();
		Integer code = _setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findOne(code);
		labRow.add("Update");
		boolean result = labIoOperation.updateLabSecondProcedure(foundLaboratoryRow.getLabId(), labRow);
		assertThat(result).isTrue();
		LaboratoryRow updateLaboratoryRow = labRowIoOperationRepository.findOne(code + 1);
		assertThat(updateLaboratoryRow.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoDeleteLaboratory() throws Exception {
		Integer code = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(code);
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
		int id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		ArrayList<Laboratory> laboratories = labManager.getLaboratory();
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testRowMgrGetLabRowByLabId() throws Exception {
		Integer id = _setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findOne(id);
		ArrayList<LaboratoryRow> laboratoryRows = labRowManager.getLabRowByLabId(foundLaboratoryRow.getLabId().getCode());
		assertThat(laboratoryRows).contains(foundLaboratoryRow);
	}

	@Test
	public void testMgrGetLaboratoryRelatedToPatient() throws Exception {
		int id = _setupTestLaboratoryRow(false);
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findOne(id);
		ArrayList<Laboratory> laboratories = labManager.getLaboratory(foundLaboratoryRow.getLabId().getPatient());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratoryRow.getLabId().getCode());
	}

	@Test
	public void testMgrGetLaboratoryWithDatesAndExam() throws Exception {
		int id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		ArrayList<Laboratory> laboratories = labManager
				.getLaboratory(foundLaboratory.getExam().getDescription(), foundLaboratory.getExamDate(), foundLaboratory.getExamDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryWithoutExamName() throws Exception {
		// given:
		int id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);

		// when:
		ArrayList<Laboratory> laboratories = labManager.getLaboratory(null, foundLaboratory.getExamDate(), foundLaboratory.getExamDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryForPrintWithDates() throws Exception {
		Integer id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		ArrayList<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(foundLaboratory.getExam().getDescription(), foundLaboratory.getExamDate(), foundLaboratory.getExamDate());
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryForPrintWithExamDescriptionLikePersistedOne() throws Exception {
		// given:
		Integer id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		ArrayList<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getExamDate(), foundLaboratory.getExamDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryForPrintWithNullExamDescription() throws Exception {
		// given:
		Integer id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);

		// when:
		ArrayList<LaboratoryForPrint> laboratories = labManager.getLaboratoryForPrint(null, foundLaboratory.getExamDate(), foundLaboratory.getExamDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
	}

	@Test
	public void testMgrGetLaboratoryForPrintMultipeResultsNoRows() throws Exception {
		// given:
		Integer id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		// TODO: if resource bundles are made available this setResults() needs to change
		foundLaboratory.setResult("angal.lab.multipleresults");
		ArrayList<String> labRow = new ArrayList<>();
		labManager.updateLaboratory(foundLaboratory, labRow);
		String description = foundLaboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(firstCharsOfDescription, foundLaboratory.getExamDate(), foundLaboratory.getExamDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(foundLaboratory.getCode());
		// TODO: if resource bundles are made available this value needs to change
		assertThat(laboratories.get(0).getResult()).isEqualTo("angal.lab.allnegative");
	}

	@Test
	public void testMgrGetLaboratoryForPrintMultipeResultsRows() throws Exception {
		ArrayList<String> labRow = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		// TODO: if resource bundles are made available this setResults() needs to change
		laboratory.setResult("angal.lab.multipleresults");
		labIoOperationRepository.saveAndFlush(laboratory);
		LaboratoryRow laboratoryRow = testLaboratoryRow.setup(laboratory, false);
		labRow.add("TestDescription");
		labRowIoOperationRepository.saveAndFlush(laboratoryRow);
		String description = laboratory.getExam().getDescription();
		String firstCharsOfDescription = description.substring(0, description.length() - 1);

		// when:
		List<LaboratoryForPrint> laboratories = labManager
				.getLaboratoryForPrint(firstCharsOfDescription, laboratory.getExamDate(), laboratory.getExamDate());

		// then:
		assertThat(laboratories.get(0).getCode()).isEqualTo(laboratory.getCode());
		// TODO: if resource bundles are made available this value needs to change
		assertThat(laboratories.get(0).getResult()).isEqualTo("angal.lab.multipleresults,TestDescription");
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
		_checkLaboratoryIntoDb(laboratory.getCode());
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
		Method method = labManager.getClass().getDeclaredMethod("newLabSecondProcedure", Laboratory.class, ArrayList.class);
		method.setAccessible(true);
		assertThat((Boolean) method.invoke(labManager, laboratory, labRow)).isTrue();
		_checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratoryProcedureEquals2EmptyLabRows() throws Exception {
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
	public void testMgrNewLaboratoryProcedureEquals2NullLabRows() throws Exception {
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
	public void testMgrNewLaboratoryExceptionsBadProcedureNumber() throws Exception {
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
		Method method = labManager.getClass().getDeclaredMethod("newLabSecondProcedure", Laboratory.class, ArrayList.class);
		method.setAccessible(true);
		assertThat((Boolean) method.invoke(labManager, laboratory, labRow)).isTrue();
		_checkLaboratoryIntoDb(laboratory.getCode());
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
		_checkLaboratoryIntoDb(laboratory.getCode());
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
		Laboratory foundLaboratory = labIoOperationRepository.findOne(laboratory.getCode());
		assertThat(laboratory.getResult()).isEqualTo("angal.lab.multipleresults");
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
		_checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratory2ProcedureEquals2EmptyLabRows() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<LaboratoryRow> labRow = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 2, false);
			Patient patient = testPatient.setup(false);
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labManager.newLaboratory2(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewLaboratory2ProcedureEquals2NullLabRows() throws Exception {
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
	public void testMgrNewLaboratory2BadProcedureNumber() throws Exception {
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

	// validation

	@Test
	public void testMgrValidationMissingDate() throws Exception {
		ArrayList<Laboratory> laboratories = new ArrayList<>();
		ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);

		ArrayList<String> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		laboratories.add(laboratory);
		labRowList.add(labRow);

		laboratory.setDate(null);

		boolean result = labManager.newLaboratory(laboratory, labRow);
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrValidationMissingExamDate() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratories.add(laboratory);
			labRowList.add(labRow);

			laboratory.setExamDate(null);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLABEXTENDEDNoPatient() throws Exception {
		boolean origLABEXTENDED = GeneralData.LABEXTENDED;
		GeneralData.LABEXTENDED = true;
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratories.add(laboratory);
			labRowList.add(labRow);

			laboratory.setPatient(null);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
		GeneralData.LABEXTENDED = origLABEXTENDED;
	}

	@Test
	public void testMgrValidationLABEXTENDEDPatient() throws Exception {
		boolean origLABEXTENDED = GeneralData.LABEXTENDED;
		GeneralData.LABEXTENDED = true;

		ArrayList<Laboratory> laboratories = new ArrayList<>();
		ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Patient patient = testPatient.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		patientIoOperationRepository.saveAndFlush(patient);

		// laboratory 1, Procedure One
		ArrayList<String> labRow = new ArrayList<>();
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		laboratories.add(laboratory);
		labRowList.add(labRow);

		labManager.newLaboratory(laboratory, labRow);

		GeneralData.LABEXTENDED = origLABEXTENDED;

		Laboratory foundLaboratory = labIoOperationRepository.findOne(laboratory.getCode());
		assertThat(laboratory.getPatName()).isEqualTo(laboratory.getPatient().getName());
		assertThat(laboratory.getAge()).isEqualTo(laboratory.getPatient().getAge());
		assertThat(laboratory.getSex()).isEqualTo(String.valueOf(laboratory.getPatient().getSex()));
	}

	@Test
	public void testMgrValidationLNoPatientBadSex() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratories.add(laboratory);
			labRowList.add(labRow);

			laboratory.setPatient(null);
			laboratory.setSex("?");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoPatientBadAge() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratories.add(laboratory);
			labRowList.add(labRow);

			laboratory.setPatient(null);
			laboratory.setAge(-99);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoExam() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratories.add(laboratory);
			labRowList.add(labRow);

			laboratory.setExam(null);

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoResult() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratories.add(laboratory);
			labRowList.add(labRow);

			laboratory.setResult("");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoMaterial() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratories.add(laboratory);
			labRowList.add(labRow);

			laboratory.setMaterial("");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationLNoInOutPatient() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			laboratories.add(laboratory);
			labRowList.add(labRow);

			laboratory.setInOutPatient("");

			labManager.newLaboratory(laboratory, labRow);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrUpdateLaboratoryProcedure1() throws Exception {
		Integer code = _setupTestLaboratory(false);
		Laboratory foundlaboratory = labIoOperationRepository.findOne(code);
		foundlaboratory.setNote("Update");
		boolean result = labManager.updateLaboratory(foundlaboratory, null);
		assertThat(result).isTrue();
		Laboratory updateLaboratory = labIoOperationRepository.findOne(code);
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
		Laboratory foundlaboratory = labIoOperationRepository.findOne(laboratory.getCode());
		foundlaboratory.setNote("Update");
		labRow.add("laboratoryRow");
		boolean result = labManager.updateLaboratory(foundlaboratory, labRow);
		assertThat(result).isTrue();
		Laboratory updateLaboratory = labIoOperationRepository.findOne(foundlaboratory.getCode());
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
		Laboratory foundlaboratory = labIoOperationRepository.findOne(laboratory.getCode());
		foundlaboratory.setNote("Update");
		boolean result = labManager.updateLaboratory(foundlaboratory, null);
		assertThat(result).isTrue();
		Laboratory updateLaboratory = labIoOperationRepository.findOne(foundlaboratory.getCode());
		assertThat(updateLaboratory.getNote()).isEqualTo("Update");
	}

	@Test
	public void testMgrUpdateLaboratoryExceptionEmptyLabRows() throws Exception {
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
	public void testMgrUpdateLaboratoryExceptionNullLablRows() throws Exception {
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
	public void testMgrUpdateLaboratoryExceptionBadProcedureNumber() throws Exception {
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
		ArrayList<Laboratory> laboratories = new ArrayList<>();
		ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
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
		ArrayList<String> labRow = new ArrayList<>();
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
		_checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratoryTransactionNoLabList() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();

			labManager.newLaboratory(laboratories, labRowList);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewLaboratoryTransactionLabListNotEqualLabRowList() throws Exception {
		assertThatThrownBy(() ->
		{
			ArrayList<Laboratory> laboratories = new ArrayList<>();
			ArrayList<ArrayList<String>> labRowList = new ArrayList<>();
			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<String> labRow = new ArrayList<>();
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
		ArrayList<ArrayList<LaboratoryRow>> labRowList = new ArrayList<>();
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
		ArrayList<LaboratoryRow> labRow = new ArrayList<>();
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
		_checkLaboratoryIntoDb(laboratory.getCode());
	}

	@Test
	public void testMgrNewLaboratory2TransactionNoLabList() throws Exception {
		assertThatThrownBy(() ->
		{
			List<Laboratory> labList = new ArrayList<>();
			ArrayList<ArrayList<LaboratoryRow>> labRowList = new ArrayList<>();

			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			ArrayList<LaboratoryRow> labRow = new ArrayList<>();
			Laboratory laboratory = testLaboratory.setup(exam, patient, false);
			labIoOperationRepository.saveAndFlush(laboratory);

			labManager.newLaboratory2(labList, labRowList);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewLaboratory2TransactionLabListNotEqualLabRowList() throws Exception {
		assertThatThrownBy(() ->
		{
			List<Laboratory> labList = new ArrayList<>();
			ArrayList<ArrayList<LaboratoryRow>> labRowList = new ArrayList<>();

			ExamType examType = testExamType.setup(false);
			Exam exam = testExam.setup(examType, 1, false);
			Patient patient = testPatient.setup(false);
			examTypeIoOperationRepository.saveAndFlush(examType);
			examIoOperationRepository.saveAndFlush(exam);
			patientIoOperationRepository.saveAndFlush(patient);

			// laboratory 1, Procedure One
			ArrayList<LaboratoryRow> labRow = new ArrayList<>();
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
		Integer code = _setupTestLaboratory(false);
		Laboratory foundlaboratory = labIoOperationRepository.findOne(code);
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
		Integer code = _setupTestLaboratory(false);
		Laboratory laboratory = labIoOperationRepository.findOne(code);
		labRow.add("Update");
		// method is protected not public
		Method method = labManager.getClass().getDeclaredMethod("editLabSecondProcedure", Laboratory.class, ArrayList.class);
		method.setAccessible(true);
		assertThat((Boolean) method.invoke(labManager, laboratory, labRow)).isTrue();
		List<LaboratoryRow> updateLaboratoryRow = labRowIoOperationRepository.findAll();
		assertThat(updateLaboratoryRow).hasSize(1);
		assertThat(updateLaboratoryRow.get(0).getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrDeleteLaboratory() throws Exception {
		Integer code = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(code);
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
	public void testMgrGetMaterialKeyUndefined() throws Exception {
		assertThat(labManager.getMaterialKey("notThere")).isEqualTo("undefined");
	}

	@Test
	public void testMgrGetMaterialKeyFound() throws Exception {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialKey("angal.lab.film")).isEqualTo("film");
	}

	@Test
	public void testMgrGetMaterialTranslatedNotThere() throws Exception {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("notThere")).isEqualTo("angal.lab.undefined");
	}

	@Test
	public void testMgrGetMaterialTranslatedNull() throws Exception {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated(null)).isEqualTo("angal.lab.undefined");
	}

	@Test
	public void testMgrGetMaterialTranslatedFound() throws Exception {
		// TODO: if resource bundles are made available this needs to change
		assertThat(labManager.getMaterialTranslated("film")).isEqualTo("angal.lab.film");
	}

	@Test
	public void testMgrGetMaterialLIst() throws Exception {
		assertThat(labManager.getMaterialList()).hasSize(9);
		ArrayList materailList = labManager.getMaterialList();
		// TODO: if resource bundles are made available this needs to change
		assertThat(materailList.get(0)).isEqualTo("angal.lab.undefined");
		materailList.remove(0);   // Remove the default value that is placed first in the list even if out of order
		assertThat(materailList).isSorted();
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = _setupTestLaboratory(false);
		Laboratory found = labIoOperationRepository.findOne(id);
		Patient mergedPatient = _setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		Laboratory result = labIoOperationRepository.findOne(id);
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
		Laboratory laboratory = new Laboratory(code, exam, new GregorianCalendar(), result, note, patient, patient.getName());
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
		int code = _setupTestLaboratory(false);
		Laboratory laboratory = labIoOperationRepository.findOne(code);
		Laboratory laboratory2 = new Laboratory(code + 1, null, new GregorianCalendar(), "result", "note", null, "name");
		assertThat(laboratory.equals(laboratory)).isTrue();
		assertThat(laboratory.equals(laboratory2)).isFalse();
		assertThat(laboratory.equals("xyzzy")).isFalse();
		laboratory2.setCode(code);
		assertThat(laboratory.equals(laboratory2)).isTrue();

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
		Laboratory laboratory = new Laboratory(code, exam, new GregorianCalendar(), result, note, patient, patient.getName());
		LaboratoryRow laboratoryRow = new LaboratoryRow(code, laboratory, "description");
		assertThat(laboratoryRow).isNotNull();

		laboratoryRow.setCode(-1);
		assertThat(laboratoryRow.getCode()).isEqualTo(-1);
	}

	@Test
	public void testLaboratoryRowEqualsHash() throws Exception {
		int code = _setupTestLaboratoryRow(false);
		LaboratoryRow laboratoryRow = labRowIoOperationRepository.findOne(code);
		LaboratoryRow laboratoryRow2 = new LaboratoryRow(code + 1, null, "description");
		assertThat(laboratoryRow.equals(laboratoryRow)).isTrue();
		assertThat(laboratoryRow.equals(laboratoryRow2)).isFalse();
		assertThat(laboratoryRow.equals("xyzzy")).isFalse();
		laboratoryRow2.setCode(code);
		assertThat(laboratoryRow.equals(laboratoryRow2)).isTrue();

		laboratoryRow.setCode(null);
		laboratoryRow2.setCode(null);
		assertThat(laboratoryRow.equals(laboratoryRow2)).isFalse();
		laboratoryRow.setDescription("description");
		assertThat(laboratoryRow.equals(laboratoryRow)).isTrue();

		assertThat(laboratoryRow.hashCode()).isPositive();
	}

	@Test
	public void testLaboratoryForPrintGetterSetter() throws Exception {
		Integer id = _setupTestLaboratory(false);
		Laboratory foundLaboratory = labIoOperationRepository.findOne(id);
		ArrayList<LaboratoryForPrint> laboratories = labIoOperation.getLaboratoryForPrint();

		LaboratoryForPrint laboratoryForPrint = laboratories.get(0);

		laboratoryForPrint.setCode(-1);
		assertThat(laboratoryForPrint.getCode()).isEqualTo(-1);
		laboratoryForPrint.setDate("dateString");
		assertThat(laboratoryForPrint.getDate()).isEqualTo("dateString");
		laboratoryForPrint.setExam("examString");
		assertThat(laboratoryForPrint.getExam()).isEqualTo("examString");
		laboratoryForPrint.setResult("resultString");
		assertThat(laboratoryForPrint.getResult()).isEqualTo("resultString");
	}

	private Patient _setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private Integer _setupTestLaboratory(boolean usingSet) throws OHException {
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

	private void _checkLaboratoryIntoDb(Integer code) {
		Laboratory foundLaboratory = labIoOperationRepository.findOne(code);
		testLaboratory.check(foundLaboratory);
	}

	private Integer _setupTestLaboratoryRow(boolean usingSet) throws OHException {
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

	private void _checkLaboratoryRowIntoDb(Integer code) {
		LaboratoryRow foundLaboratoryRow = labRowIoOperationRepository.findOne(code);
		testLaboratoryRow.check(foundLaboratoryRow);
	}
}