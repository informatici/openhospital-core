/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.isf.exa.model.Exam;
import org.isf.generaldata.GeneralData;
import org.isf.lab.model.Laboratory;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class TestLaboratory {

	private String material = "TestMaterial";
	private GregorianCalendar now = new GregorianCalendar();
	private GregorianCalendar labDate = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
	private GregorianCalendar examDate = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
	private String result = "TestResult";
	private String note = "TestNote";
	private String patName = "TestPatientName";
	private String InOutPatient = "O";
	private Integer age = 37;
	private String sex = "F";

	public Laboratory setup(Exam exam, Patient patient, boolean usingSet) throws OHException {
		Laboratory laboratory;

		if (usingSet) {
			laboratory = new Laboratory();
			_setParameters(laboratory, exam, patient);
		} else {
			// Create Laboratory with all parameters 
			laboratory = new Laboratory(exam, labDate, result, note, patient, patName);
			laboratory.setAge(age);
			laboratory.setDate(labDate);
			laboratory.setExamDate(examDate);
			laboratory.setInOutPatient(InOutPatient);
			laboratory.setMaterial(material);
			laboratory.setResult(result);
			laboratory.setSex(sex);
		}

		return laboratory;
	}

	public void _setParameters(Laboratory laboratory, Exam exam, Patient patient) {
		laboratory.setAge(age);
		laboratory.setDate(labDate);
		laboratory.setExam(exam);
		laboratory.setExamDate(examDate);
		laboratory.setInOutPatient(InOutPatient);
		laboratory.setMaterial(material);
		laboratory.setNote(note);
		laboratory.setPatient(patient);
		laboratory.setPatName(patName);
		laboratory.setResult(result);
		laboratory.setSex(sex);
	}

	public void check(Laboratory laboratory) {
		// If GeneralData.LABEXTENDED is true then the age found in the patient record is
		// copied into the Laboratory record and as the age in the patient record changes
		// based on when (what day and year) the test is run then a comparison to a fixed
		// value almost always fails (except for late in 2021 and early in 2022).
		// Likewise the patient name is copied from the Patient record which does not
		// match the default in the Laboratory record.
		if (!GeneralData.LABEXTENDED) {
			assertThat(laboratory.getAge()).isEqualTo(age);
			assertThat(laboratory.getPatName()).isEqualTo(patName);
		}
		assertThat(laboratory.getDate()).isEqualTo(labDate);
		assertThat(laboratory.getExamDate()).isEqualTo(examDate);
		assertThat(laboratory.getInOutPatient()).isEqualTo(InOutPatient);
		assertThat(laboratory.getMaterial()).isEqualTo(material);
		assertThat(laboratory.getNote()).isEqualTo(note);
		assertThat(laboratory.getResult()).isEqualTo(result);
		assertThat(laboratory.getSex()).isEqualTo(sex);
	}
}
