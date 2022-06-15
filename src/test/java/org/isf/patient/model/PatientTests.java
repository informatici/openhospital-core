/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patient.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.List;

import org.isf.OHCoreTestCase5;
import org.isf.opd.model.Opd;
import org.isf.opd.test.TestOpd;
import org.isf.patient.data.PatientHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientTests extends OHCoreTestCase5 {

	private static PatientHelper createPatient;

	@BeforeAll
	public void beforeAll() {
		createPatient = new PatientHelper();
	}

	@BeforeEach
	public void beforeEach() {
		cleanH2InMemoryDb();
	}

	@Test
	void gets() {
		List<Integer> codes = createPatient.createPatient(20);
		for (Integer code : codes) {
			createPatient.checkPatientInDb(code);
		}
	}

	@Test
	void sets() {
		String firstName = "TestFirstName";
		String secondName = "TestSecondName";
		LocalDate birthDate = LocalDate.of(1984, Calendar.AUGUST, 14);
		Period period = birthDate.until(LocalDate.now());
		int age = period.getYears();
		String agetype = "Date";
		char sex = 'F';
		String address = "TestAddress";
		String city = "TestCity";
		String nextKin = "TestNextKin";
		String telephone = "TestTelephone";
		String motherName = "TestMotherName";
		char mother = 'A';
		String fatherName = "TestFatherName";
		char father = 'D';
		String bloodType = "O+";
		char hasInsurance = 'Y';
		char parentTogether = 'U';
		String taxCode = "TestTaxCode";
		String maritalStatus = "divorced";
		String profession = "business";

		Patient patient = new Patient();
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
		patient.setMotherName(motherName);
		patient.setMother(mother);
		patient.setFatherName(fatherName);
		patient.setFather(father);
		patient.setBloodType(bloodType);
		patient.setHasInsurance(hasInsurance);
		patient.setParentTogether(parentTogether);
		patient.setTaxCode(taxCode);
		patient.setMaritalStatus(maritalStatus);
		patient.setProfession(profession);
		patient.setPatientProfilePhoto(null);

		assertThat(patient.getFirstName()).isEqualTo(firstName);
		assertThat(patient.getSecondName()).isEqualTo(secondName);
		assertThat(patient.getBirthDate()).isEqualTo(birthDate);
		assertThat(patient.getAge()).isEqualTo(age);
		assertThat(patient.getAgetype()).isEqualTo(agetype);
		assertThat(patient.getSex()).isEqualTo(sex);
		assertThat(patient.getAddress()).isEqualTo(address);
		assertThat(patient.getCity()).isEqualTo(city);
		assertThat(patient.getNextKin()).isEqualTo(nextKin);
		assertThat(patient.getTelephone()).isEqualTo(telephone);
		assertThat(patient.getMotherName()).isEqualTo(motherName);
		assertThat(patient.getMother()).isEqualTo(mother);
		assertThat(patient.getFatherName()).isEqualTo(fatherName);
		assertThat(patient.getFather()).isEqualTo(father);
		assertThat(patient.getBloodType()).isEqualTo(bloodType);
		assertThat(patient.getHasInsurance()).isEqualTo(hasInsurance);
		assertThat(patient.getParentTogether()).isEqualTo(parentTogether);
		assertThat(patient.getTaxCode()).isEqualTo(taxCode);
		assertThat(patient.getMaritalStatus()).isEqualTo(maritalStatus);
		assertThat(patient.getProfession()).isEqualTo(profession);
		assertThat(patient.getPatientProfilePhoto()).isNull();
	}

	@Test
	void opdConstructor() throws Exception {
		Opd opd = new TestOpd().setup(null, null, false);
		Patient patient = new Patient(opd);

		assertThat(patient.getSex()).isEqualTo('F');
		assertThat(patient.getCode()).isNull();
		assertThat(patient.getBirthDate()).isNull();

		assertThat(patient.getDeleted()).isEqualTo("N");
		patient.setDeleted("Y");
		assertThat(patient.getDeleted()).isEqualTo("Y");
	}

	@Test
	void patientConstructor() {
		Patient patient = new Patient(99, "firstName", "secondName", "name", null, 99, " ", 'F', "address",
				"city", "nextOfKin", "noPhone", "note", "motherName", ' ', "fatherName", ' ',
				"bloodType", ' ', ' ', "personalCode", "maritalStatus", "profession");

		assertThat(patient.getCode()).isEqualTo(99);
		assertThat(patient.getSex()).isEqualTo('F');
		assertThat(patient.getBirthDate()).isNull();

		assertThat(patient.getLock()).isZero();
		patient.setLock(99);
		assertThat(patient.getLock()).isEqualTo(99);
	}

	@Test
	void patientGetSearchString() {
		Patient patient = new Patient(99, "testFirstname", "testSecondname", "testFirstname testSecondname", null, 99, " ", 'F', "testAddress", "testCity",
				"nextOfKin", "testTelephone", null, "motherName", ' ', "fatherName", ' ', "bloodType", ' ', ' ', "testTaxcode", "maritalStatus",
				"profession");

		assertThat(patient.getSearchString()).isEqualTo("99 testfirstname testsecondname testcity testaddress testTelephone testtaxcode ");
	}

	@Test
	void patientGetInformations() {
		Patient patient = new Patient("testFirstname", "testSecondname", LocalDate.now(), 99, "", 'F', "testAddress", "testCity",
				"nextOfKin", "testTelephone", "motherName", 'U', "fatherName", 'U', "bloodType", ' ', ' ', "testTaxCode", "maritalStatus",
				"profession");

		patient.setNote("someNote");
		assertThat(patient.getInformations()).isEqualTo("testCity - testAddress - testTelephone - someNote - testTaxCode");
	}

	@Test
	void patientEquals() {
		Patient patient = new Patient("testFirstname", "testSecondname", LocalDate.now(), 99, "", 'F', "testAddress", "testCity",
				"nextOfKin", "testTelephone", "motherName", 'U', "fatherName", 'U', "bloodType", ' ', ' ', "testTaxcode", "maritalStatus",
				"profession");
		assertThat(patient.equals(patient)).isTrue();
		assertThat(patient)
				.isNotNull()
				.isNotEqualTo("someString");

		Patient patient2 = new Patient("testFirstname2", "testSecondname2", LocalDate.now(), 99, "", 'M', "testAddress2", "testCity",
				"nextOfKin", "testTelephone", "motherName", 'U', "fatherName", 'U', "bloodType", ' ', ' ', "testTaxcode", "maritalStatus",
				"profession");
		patient.setCode(1);
		patient2.setCode(2);
		assertThat(patient).isNotEqualTo(patient2);
		patient2.setCode(patient.getCode());
		assertThat(patient).isEqualTo(patient2);
	}

	@Test
	void patientHashCode() {
		Patient patient = new Patient("testFirstname", "testSecondname", LocalDate.now(), 99, "", 'F', "testAddress", "testCity",
				"nextOfKin", "testTelephone", "motherName", 'U', "fatherName", 'U', "bloodType", ' ', ' ', "testTaxcode", "maritalStatus",
				"profession");
		patient.setCode(1);
		// compute
		int hashCode = patient.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1);
		// check stored value
		assertThat(patient.hashCode()).isEqualTo(hashCode);

		Patient patient2 = new Patient("testFirstname2", "testSecondname2", LocalDate.now(), 99, "", 'M', "testAddress2", "testCity",
				"nextOfKin", "testTelephone", "motherName", 'U', "fatherName", 'U', "bloodType", ' ', ' ', "testTaxcode", "maritalStatus",
				"profession");
		patient2.setCode(null);
		assertThat(patient2.hashCode()).isEqualTo(23 * 133);
	}

	@Test
	void profilePhoto() throws Exception {
		Patient patient = new Patient("testFirstname", "testSecondname", LocalDate.now(), 99, "", 'F', "testAddress", "testCity",
				"nextOfKin", "testTelephone", "motherName", 'U', "fatherName", 'U', "bloodType", ' ', ' ', "testTaxcode", "maritalStatus",
				"profession");
		PatientProfilePhoto patientProfilePhoto = new PatientProfilePhoto();

		File file = new File(getClass().getResource("patient.jpg").getFile());
		byte[] bytes = Files.readAllBytes(file.toPath());
		patientProfilePhoto.setPhoto(bytes);
		assertThat(patientProfilePhoto.getPhotoAsImage()).isNotNull();

		patientProfilePhoto.setPhoto(null);
		assertThat(patientProfilePhoto.getPhoto()).isNull();

		patientProfilePhoto.setPatient(patient);
		assertThat(patientProfilePhoto.getPatient()).isEqualTo(patient);
	}

	@Test
	void profilePhotoNull() throws Exception {
		Patient patient = new Patient("testFirstname", "testSecondname", LocalDate.now(), 99, "", 'F', "testAddress", "testCity",
				"nextOfKin", "testTelephone", "motherName", 'U', "fatherName", 'U', "bloodType", ' ', ' ', "testTaxcode", "maritalStatus",
				"profession");
		PatientProfilePhoto patientProfilePhoto = new PatientProfilePhoto();

		File file = new File(getClass().getResource("patient.jpg").getFile());
		byte[] bytes = Files.readAllBytes(file.toPath());
		patientProfilePhoto.setPhoto(bytes);
		assertThat(patientProfilePhoto.getPhotoAsImage()).isNotNull();

		patient.setPatientProfilePhoto(patientProfilePhoto);
		assertThat(patient.getPatientProfilePhoto()).isNotNull();

		patient.setPatientProfilePhoto(null);
		assertThat(patient.getPatientProfilePhoto()).isNull();
	}

	@Test
	void patientToString() {
		Patient patient = new Patient("testFirstname", "testSecondname", LocalDate.now(), 99, "", 'F', "testAddress", "testCity",
				"nextOfKin", "testTelephone", "motherName", 'U', "fatherName", 'U', "bloodType", ' ', ' ', "testTaxCode", "maritalStatus",
				"profession");

		patient.setNote("someNote");
		assertThat(patient.toString()).isEqualTo("testFirstname testSecondname");
	}

}
