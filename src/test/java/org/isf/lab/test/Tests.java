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

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exa.test.TestExam;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.exatype.test.TestExamType;
import org.isf.lab.manager.LabManager;
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
	LabRowIoOperationRepository labRowIoOperationRepository;
	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	private LabManager labManager;
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
	public void testManagerNewLaboratoryTransaction() throws Exception {
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

		labManager.setIoOperations(labIoOperation);
		boolean result = labManager.newLaboratory(laboratories, labRowList);

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