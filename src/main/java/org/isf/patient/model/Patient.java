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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.patient.model;

import java.time.LocalDate;
import java.time.Period;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.isf.anamnesis.model.PatientHistory;
import org.isf.opd.model.Opd;
import org.isf.patconsensus.model.PatientConsensus;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_PATIENT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PAT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PAT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PAT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PAT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PAT_LAST_MODIFIED_DATE"))

public class Patient extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PAT_ID")
	private Integer code;

	@NotNull
	@Column(name="PAT_FNAME")
	private String firstName;

	@NotNull
	@Column(name="PAT_SNAME")
	private String secondName;

	@NotNull
	@Column(name="PAT_NAME")
	private String name;

	@NotNull
	@Column(name="PAT_BDATE")	// SQL type: date
	private LocalDate birthDate;

	@NotNull
	@Column(name="PAT_AGE")
	private int age;

	@Column(name="PAT_AGETYPE")
	private String agetype;

	@NotNull
	@Column(name="PAT_SEX")
	private char sex;

	@Column(name="PAT_ADDR")
	private String address;

	@NotNull
	@Column(name="PAT_CITY")
	private String city;

	@Column(name="PAT_NEXT_KIN")
	private String nextKin;

	@Column(name="PAT_TELE")
	private String telephone;

	@Column(name="PAT_NOTE")
	private String note;

	@NotNull
	@Column(name="PAT_MOTH_NAME")
	private String motherName; // mother's name

	@Column(name="PAT_MOTH")
	private char mother = ' '; // D=dead, A=alive

	@NotNull
	@Column(name="PAT_FATH_NAME")
	private String fatherName; // father's name

	@Column(name="PAT_FATH")
	private char father = ' '; // D=dead, A=alive

	@NotNull
	@Column(name="PAT_BTYPE")
	private String bloodType; // (0-/+, A-/+ , B-/+, AB-/+)

	@Column(name="PAT_ESTA")
	private char hasInsurance = ' '; // Y=Yes, N=no

	@Column(name="PAT_PTOGE")
	private char parentTogether = ' '; // parents together: Y or N

	@Column(name="PAT_TAXCODE")
	private String taxCode;

	@Column(name="PAT_MAR_STAT")
	private String maritalStatus;

	@Column(name="PAT_PROFESSION")
	private String profession;

	@NotNull
	@Column(name="PAT_DELETED", columnDefinition = "char(1) default 'N'")
	private char deleted = 'N';

	/**
	 * field for "ui"
	 * NOTE: to be replaced with {@link PatientHistory}
	 */
	@Column(name="PAT_ANAMNESIS")
	private String anamnesis;

	/**
	 * field for "ui"
	 * NOTE: to be replaced with {@link PatientHistory}
	 */
	@Column(name="PAT_ALLERGIES")
	private String allergies;

	@Version
	@Column(name="PAT_LOCK")
	private int lock;

	@OneToOne(
			fetch = FetchType.LAZY,
			cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
			orphanRemoval = true
	)
	@JoinColumn(name = "PAT_PROFILE_PHOTO_ID", referencedColumnName = "PAT_PROFILE_PHOTO_ID", nullable = true)
	private PatientProfilePhoto patientProfilePhoto; // nullable because user can choose to save on file system

	@Transient
	private volatile int hashCode;

	@OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
	private PatientConsensus patientConsensus;

	public Patient() {
		this.firstName = "";
		this.secondName = "";
		this.name = this.firstName + ' ' + this.secondName;
		this.birthDate = null;    // TODO the field is marked as "@NotNull"
		this.age = 0;
		this.agetype = "";
		this.sex = ' ';
		this.address = "";
		this.city = "";
		this.nextKin = "";
		this.telephone = "";
		this.motherName = "";
		this.mother = ' ';
		this.fatherName = "";
		this.father = ' ';
		this.bloodType = "";
		this.hasInsurance = ' ';
		this.parentTogether = ' ';
		this.taxCode = "";
		this.maritalStatus = "";
		this.profession = "";
	}

	public Patient(Opd opd) {
		this.firstName = opd.getfirstName();
		this.secondName = opd.getsecondName();
		this.name = this.firstName + ' ' + this.secondName;
		this.birthDate = null;    // TODO the field is marked as "@NotNull"
		this.age = opd.getAge();
		this.agetype = "";
		this.sex = opd.getSex();
		this.address = opd.getaddress();
		this.city = opd.getcity();
		this.nextKin = opd.getnextKin();
		this.telephone = "";
		this.motherName = "";
		this.mother = ' ';
		this.fatherName = "";
		this.father = ' ';
		this.bloodType = "";
		this.hasInsurance = ' ';
		this.parentTogether = ' ';
		this.maritalStatus = "";
		this.profession = "";
	}

	public Patient(String firstName, String secondName, LocalDate birthDate, int age, String agetype, char sex,
			String address, String city, String nextKin, String telephone,
			String motherName, char mother, String fatherName, char father,
			String bloodType, char economicStatut, char parentTogether, String personalCode,
			String maritalStatus, String profession) { //Changed EduLev with bloodType
		this.firstName = firstName;
		this.secondName = secondName;
		this.name = this.firstName + ' ' + this.secondName;
		this.birthDate = birthDate;
		this.age = age;
		this.agetype = agetype;
		this.sex = sex;
		this.address = address;
		this.city = city;
		this.nextKin = nextKin;
		this.telephone = telephone;
		this.motherName = motherName;
		this.mother = mother;
		this.fatherName = fatherName;
		this.father = father;
		this.hasInsurance = economicStatut;
		this.bloodType = bloodType;
		this.parentTogether = parentTogether;
		this.taxCode = personalCode;
		this.maritalStatus = maritalStatus;
		this.profession = profession;
	}

	public Patient(int code, String firstName, String secondName, String name, LocalDate birthDate, int age, String agetype, char sex,
			String address, String city, String nextKin, String telephone, String note,
			String motherName, char mother, String fatherName, char father,
			String bloodType, char economicStatut, char parentTogether, String taxCode,
			String maritalStatus, String profession) { //Changed EduLev with bloodType
		this.code = code;
		this.firstName = firstName;
		this.secondName = secondName;
		this.name = name;
		this.birthDate = birthDate;
		this.age = age;
		this.agetype = agetype;
		this.sex = sex;
		this.address = address;
		this.city = city;
		this.nextKin = nextKin;
		this.telephone = telephone;
		this.note = note;
		this.motherName = motherName;
		this.mother = mother;
		this.fatherName = fatherName;
		this.father = father;
		this.hasInsurance = economicStatut;
		this.bloodType = bloodType;
		this.parentTogether = parentTogether;
		this.taxCode = taxCode;
		this.maritalStatus = maritalStatus;
		this.profession = profession;
	}

	public PatientConsensus getPatientConsensus() {
		return patientConsensus;
	}


	public void setPatientConsensus(PatientConsensus patientConsensus) {
		this.patientConsensus = patientConsensus;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public int getAge() {
		if (this.birthDate != null) {
			Period periodAge = Period.between(birthDate, LocalDate.now());
			age = periodAge.getYears();
		}
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setAgetype(String agetype) {
		this.agetype = agetype;
	}

	public String getAgetype() {
		return agetype;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		this.name = this.firstName + ' ' + this.secondName;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getNextKin() {
		return nextKin;
	}

	public void setNextKin(String nextKin) {
		this.nextKin = nextKin;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
		this.name = this.firstName + ' ' + this.secondName;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getBloodType() {
	    return bloodType;
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public String getName() {
		return this.name;
	}

	public char getHasInsurance() {
		return hasInsurance;
	}

	public void setHasInsurance(char hasInsurance) {
		this.hasInsurance = hasInsurance;
	}

	public char getFather() {
		return father;
	}

	public void setFather(char father) {
		this.father = father;
	}

	public char getMother() {
		return mother;
	}

	public void setMother(char mother) {
		this.mother = mother;
	}

	public char getParentTogether() {
		return parentTogether;
	}

	public void setParentTogether(char parentTogether) {
		this.parentTogether = parentTogether;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

    public char getDeleted() {
        return deleted;
    }

    public void setDeleted(char deleted) {
        this.deleted = deleted;
    }

	public PatientProfilePhoto getPatientProfilePhoto() {
		return patientProfilePhoto;
	}

	/**
	 * field for "ui"
	 * NOTE: to be replaced with {@link PatientHistory}
	 */
	public String getAnamnesis() {
		return anamnesis;
	}

	/**
	 * field for "ui"
	 * NOTE: to be replaced with {@link PatientHistory}
	 */
	public void setAnamnesis(String anamnesis) {
		this.anamnesis = anamnesis;
	}

	/**
	 * field for "ui"
	 * NOTE: to be replaced with {@link PatientHistory}
	 */
	public String getAllergies() {
		return allergies;
	}

	/**
	 * field for "ui"
	 * NOTE: to be replaced with {@link PatientHistory}
	 */
	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

	/**
	 * Method kept as POJO standard, but it ignores {@code name} param
	 * and uses {@link #firstName} and {@link #secondName} to set
	 * the field (as {@link #setFirstName} and {@link #setSecondName}
	 * methods do as well).
	 * 
	 * @param name (ignored, uses {@code firstName} and {@code secondName} instead
	 */
	public void setName(String name) {
		this.name = this.firstName + ' ' + this.secondName;
	}

	public void setPatientProfilePhoto(PatientProfilePhoto patientProfilePhoto) {
		if (patientProfilePhoto == null) {
			if (this.patientProfilePhoto != null) {
				this.patientProfilePhoto.setPatient(null);
			}
		} else {
			patientProfilePhoto.setPatient(this);
		}
		this.patientProfilePhoto = patientProfilePhoto;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Patient)) {
			return false;
		}

		Patient patient = (Patient)obj;
		return (this.getCode().equals(patient.getCode()));
	}

	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;

	        c = m * c + ((code == null) ? 0 : code);

	        this.hashCode = c;
	    }

	    return this.hashCode;
	}

	public String getSearchString() {
		StringBuilder sbName = new StringBuilder();
		sbName.append(getCode());
		sbName.append(' ');
		sbName.append(getFirstName().toLowerCase());
		sbName.append(' ');
		sbName.append(getSecondName().toLowerCase());
		sbName.append(' ');
		sbName.append(getCity().toLowerCase());
		sbName.append(' ');
		if (getAddress() != null) {
			sbName.append(getAddress().toLowerCase()).append(' ');
		}
		if (getTelephone() != null) {
			sbName.append(getTelephone()).append(' ');
		}
		if (getNote() != null) {
			sbName.append(getNote().toLowerCase()).append(' ');
		}
		if (getTaxCode() != null) {
			sbName.append(getTaxCode().toLowerCase()).append(' ');
		}
		return sbName.toString();
	}

	public String getInformations() {
		int i = 0;
		StringBuilder infoBfr = new StringBuilder();
		if (StringUtils.isNotEmpty(city)) {
			infoBfr.append(city);
			i++;
		}
		if (StringUtils.isNotEmpty(address)) {
			infoBfr.append(i > 0 ? " - " : "");
			infoBfr.append(address);
			i++;
		}
		if (StringUtils.isNotEmpty(telephone)) {
			infoBfr.append(i > 0 ? " - " : "");
			infoBfr.append(telephone);
			i++;
		}
		if (StringUtils.isNotEmpty(note)) {
			infoBfr.append(i > 0 ? " - " : "");
			infoBfr.append(note);
			i++;
		}
		if (StringUtils.isNotEmpty(taxCode)) {
			infoBfr.append(i > 0 ? " - " : "");
			infoBfr.append(taxCode);
		}
		return infoBfr.toString();
	}
}