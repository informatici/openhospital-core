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
package org.isf.exa.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.exa.model.Exam;
import org.isf.exa.model.ExamRow;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.exa.service.ExamIoOperations;
import org.isf.exa.service.ExamRowIoOperationRepository;
import org.isf.exa.service.ExamRowIoOperations;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.exatype.test.TestExamType;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestExam testExam;
	private static TestExamRow testExamRow;
	private static TestExamType testExamType;

	@Autowired
	ExamIoOperations examIoOperation;
	@Autowired
	ExamRowIoOperations examRowIoOperation;
	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	ExamRowIoOperationRepository examRowIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testExam = new TestExam();
		testExamType = new TestExamType();
		testExamRow = new TestExamRow();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testExamGets() throws Exception {
		String code = _setupTestExam(false);
		_checkExamIntoDb(code);
	}

	@Test
	public void testExamSets() throws Exception {
		String code = _setupTestExam(true);
		_checkExamIntoDb(code);
	}

	@Test
	public void testExamRowGets() throws Exception {
		int code = _setupTestExamRow(false);
		_checkExamRowIntoDb(code);
	}

	@Test
	public void testExamRowSets() throws Exception {
		int code = _setupTestExamRow(true);
		_checkExamRowIntoDb(code);
	}

	@Test
	public void testIoGetExamRow() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		ArrayList<ExamRow> examRows = examRowIoOperation.getExamRow(0, null);
		assertThat(examRows.get(examRows.size() - 1).getDescription()).isEqualTo(foundExamRow.getDescription());
	}

	@Test
	public void testIoGetExams() throws Exception {
		String code = _setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findOne(code);
		List<Exam> exams = examIoOperation.getExams();
		assertThat(exams.get(exams.size() - 1).getDescription()).isEqualTo(foundExam.getDescription());
	}

	@Test
	public void testIoGetExamType() throws Exception {
		String code = _setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findOne(code);
		ArrayList<ExamType> examTypes = examIoOperation.getExamType();
		assertThat(examTypes).contains(foundExamType);
	}

	@Test
	public void testIoNewExamRow() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 2, false);
		ExamRow examRow = testExamRow.setup(exam, true);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examIoOperation.newExamRow(examRow);
		assertThat(result).isTrue();
		_checkExamRowIntoDb(examRow.getCode());
	}

	@Test
	public void testIoNewExam() throws Exception {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		Exam exam = testExam.setup(examType, 1, false);
		boolean result = examIoOperation.newExam(exam);
		assertThat(result).isTrue();
		_checkExamIntoDb(exam.getCode());
	}

	@Test
	public void testIoUpdateExam() throws Exception {
		String code = _setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findOne(code);
		foundExam.setDescription("Update");
		boolean result = examIoOperation.updateExam(foundExam);
		assertThat(result).isTrue();
		Exam updateExam = examIoOperationRepository.findOne(code);
		assertThat(updateExam.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoDeleteExam() throws Exception {
		String code = _setupTestExam(false);
		Exam foundExam = examIoOperationRepository.findOne(code);
		boolean result = examIoOperation.deleteExam(foundExam);
		assertThat(result).isTrue();
		result = examIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoDeleteExamRow() throws Exception {
		int code = _setupTestExamRow(false);
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		boolean result = examIoOperation.deleteExamRow(foundExamRow);
		assertThat(result).isTrue();
		result = examIoOperation.isRowPresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoIsKeyPresent() throws Exception {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		boolean result = examIoOperation.isKeyPresent(exam);
		assertThat(result).isTrue();
	}

	private String _setupTestExam(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		return exam.getCode();
	}

	private void _checkExamIntoDb(String code) throws OHException {
		Exam foundExam = examIoOperationRepository.findOne(code);
		testExam.check(foundExam);
	}

	private int _setupTestExamRow(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(usingSet);
		Exam exam = testExam.setup(examType, 2, usingSet);
		ExamRow examRow = testExamRow.setup(exam, usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		examRowIoOperationRepository.saveAndFlush(examRow);
		return examRow.getCode();
	}

	private void _checkExamRowIntoDb(int code) throws OHException {
		ExamRow foundExamRow = examRowIoOperationRepository.findOne(code);
		testExamRow.check(foundExamRow);
	}

	private String _setupTestExamType(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		return examType.getCode();
	}
}