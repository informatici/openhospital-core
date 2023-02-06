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
package org.isf.patient.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Calendar;

import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.utils.exception.OHException;

public class TestPatient {

	private static String firstName = "TestFirstName";
	private static String secondName = "TestSecondName";
	private static LocalDate birthDate = LocalDate.of(1984, Calendar.AUGUST, 14);
	private static int age = 31; //IT WILL CHANGE WITH TIME
	private static String agetype = "d1";
	private static char sex = 'F';
	private static String address = "TestAddress";
	private static String city = "TestCity";
	private static String nextKin = "TestNextKin";
	private static String telephone = "TestTelephone";
	private static String mother_name = "TestMotherName";
	private static char mother = 'A'; // D=dead, A=alive
	private static String father_name = "TestFatherName"; // father's name
	private static char father = 'A'; // D=dead, A=alive
	private static String bloodType = "0-/+"; // (0-/+, A-/+ , B-/+, AB-/+)
	private static char hasInsurance = 'Y'; // Y=Yes, N=no
	private static char parentTogether = 'Y'; // parents together: Y or N
	private static String taxCode = "TestTaxCode";
	private static String maritalStatus = "divorced";
	private static String profession = "business";
	private String anamnesis = "anamnesis";
	private String allergies = "allergies";
	//private static Blob photo;	
	//private static Image photoImage;

	public Patient setup(boolean usingSet) throws OHException {
		Patient patient;

		if (usingSet) {
			patient = new Patient();
			patient.setPatientProfilePhoto(new PatientProfilePhoto());
			setParameters(patient);
		} else {
			// Create Patient with all parameters 
			patient = new Patient(firstName, secondName, birthDate, age, agetype, sex,
					address, city, nextKin, telephone, mother_name, mother, father_name, father,
					bloodType, hasInsurance, parentTogether, taxCode, maritalStatus, profession);
			patient.setAge(patient.getAge()); //IT WILL CHANGE WITH TIME
			patient.setPatientProfilePhoto(new PatientProfilePhoto());
			patient.setAllergies(allergies);
			patient.setAnamnesis(anamnesis);
			
		}

		return patient;
	}

	public void setParameters(Patient patient) {
		patient.setFirstName(firstName);
		patient.setSecondName(secondName);
		patient.setBirthDate(birthDate);
		patient.setAge(age);
		patient.setAgetype(agetype);
		patient.setSex(sex);
		patient.setAddress(address);
		patient.setCity(city);
		patient.setNextKin(nextKin);
		patient.setTelephone(telephone);
		patient.setMotherName(mother_name);
		patient.setMother(mother);
		patient.setFatherName(father_name);
		patient.setFather(father);
		patient.setBloodType(bloodType);
		patient.setHasInsurance(hasInsurance);
		patient.setParentTogether(parentTogether);
		patient.setTaxCode(taxCode);
		patient.setMaritalStatus(maritalStatus);
		patient.setProfession(profession);
		patient.setAllergies(allergies);
		patient.setAnamnesis(anamnesis);
	}

	public void check(Patient patient) {
		assertThat(patient.getFirstName()).isEqualTo(firstName);
		assertThat(patient.getSecondName()).isEqualTo(secondName);
		assertThat(patient.getBirthDate()).isEqualTo(birthDate);
		assertThat(patient.getAgetype()).isEqualTo(agetype);
		assertThat(patient.getSex()).isEqualTo(sex);
		assertThat(patient.getAddress()).isEqualTo(address);
		assertThat(patient.getCity()).isEqualTo(city);
		assertThat(patient.getNextKin()).isEqualTo(nextKin);
		assertThat(patient.getTelephone()).isEqualTo(telephone);
		assertThat(patient.getMotherName()).isEqualTo(mother_name);
		assertThat(patient.getMother()).isEqualTo(mother);
		assertThat(patient.getFatherName()).isEqualTo(father_name);
		assertThat(patient.getFather()).isEqualTo(father);
		assertThat(patient.getBloodType()).isEqualTo(bloodType);
		assertThat(patient.getHasInsurance()).isEqualTo(hasInsurance);
		assertThat(patient.getParentTogether()).isEqualTo(parentTogether);
		assertThat(patient.getTaxCode()).isEqualTo(taxCode);
		assertThat(patient.getMaritalStatus()).isEqualTo(maritalStatus);
		assertThat(patient.getProfession()).isEqualTo(profession);
		assertThat(patient.getAllergies()).isEqualTo(allergies);
		assertThat(patient.getAnamnesis()).isEqualTo(anamnesis);
	}
}
