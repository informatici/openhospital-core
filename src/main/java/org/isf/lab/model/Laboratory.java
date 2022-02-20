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
package org.isf.lab.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.isf.exa.model.Exam;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * Laboratory - laboratory execution model
 * -----------------------------------------
 * modification history
 * 02/03/2006 - theo - first beta version
 * 10/11/2006 - ross - new fields data esame, sex, age, material, inout flag added
 * 06/01/2016 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="LABORATORY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="LAB_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="LAB_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="LAB_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="LAB_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="LAB_LAST_MODIFIED_DATE"))
})
public class Laboratory extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="LAB_ID")
	private Integer code;

	@Column(name="LAB_MATERIAL")
	private String material;

	@NotNull
	@ManyToOne
	@JoinColumn(name="LAB_EXA_ID_A")
	private Exam exam;

	@NotNull
	@Column(name="LAB_DATE")
	private GregorianCalendar labDate;

	@Column(name="LAB_EXAM_DATE")
	@Temporal(TemporalType.DATE)
	@Deprecated
	private Calendar examDate;

	@NotNull
	@Column(name="LAB_RES")
	private String result;

	@Version
	@Column(name="LAB_LOCK")
	private int lock;

	@Column(name="LAB_NOTE")
	private String note;

	@ManyToOne
	@JoinColumn(name="LAB_PAT_ID")
	private Patient patient;

	@Column(name="LAB_PAT_NAME")
	private String patName;

	@Column(name="LAB_PAT_INOUT")
	private String InOutPatient;

	@Column(name="LAB_AGE")
	private Integer age;

	@Column(name="LAB_SEX")
	private String sex;

	@Transient
	private volatile int hashCode = 0;

	public Laboratory() {
	}

	public Laboratory(Exam aExam, GregorianCalendar aDate, String aResult, String aNote, Patient aPatId, String aPatName) {
		exam = aExam;
		labDate = aDate;
		result = aResult;
		note = aNote;
		patient = aPatId;
		patName = aPatName;
	}

	public Laboratory(Integer aCode, Exam aExam, GregorianCalendar aDate, String aResult, String aNote, Patient aPatId, String aPatName) {
		code = aCode;
		exam = aExam;
		labDate = aDate;
		result = aResult;
		note = aNote;
		patient = aPatId;
		patName = aPatName;
	}

	public Exam getExam() {
		return exam;
	}
	public GregorianCalendar getDate() {
		return labDate;
	}
	public String getResult() {
		return result;
	}
	public Integer getCode() {
		return code;
	}
	public int getLock() {
		return lock;
	}
	public void setCode(Integer aCode) {
		code = aCode;
	}
	public void setExam(Exam aExam) {
		exam = aExam;
	}
	public void setLock(int aLock) {
		lock = aLock;
	}
	/**
	 * use <code>getCreatedDate()</code> instead
	 */
	@Deprecated
	public GregorianCalendar getExamDate() {
		return (GregorianCalendar) examDate;
	}
	/**
	 * the field has been replaced by <code>createdDate()</code> and it's not meant to be managed by the user (Spring managed)
	 */
	@Deprecated
	public void setExamDate(GregorianCalendar exDate) {
		this.examDate = exDate;
	}
	public void setDate(GregorianCalendar aDate) {
		labDate = aDate;
	}
	public void setResult(String aResult) {
		result = aResult;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getInOutPatient() {
		return InOutPatient;
	}
	public void setInOutPatient(String InOut) {
		if (InOut == null)
			InOut = "";
		this.InOutPatient = InOut;
	}
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Laboratory)) {
			return false;
		}

		Laboratory laboratory = (Laboratory) obj;
		return (this.getCode().equals(laboratory.getCode()));

	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;

			c = m * c + (code == null ? 0 : code);

			this.hashCode = c;
		}

		return this.hashCode;
	}

	@Override
	public String toString() {
		return "-------------------------------------------\nLaboratory{" + "code=" + code + ", material=" + material
				+ ", exam=" + exam + ", registrationDate=" + labDate + ", examDate=" + examDate + ", result="
				+ result + ", lock=" + lock + ", note=" + note + ", patient=" + patient + ", patName=" + patName
				+ ", InOutPatient=" + InOutPatient + ", age=" + age + ", sex=" + sex + ", hashCode=" + hashCode
				+ "}\n---------------------------------------------";
	}

}
