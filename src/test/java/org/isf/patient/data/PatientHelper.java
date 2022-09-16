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
package org.isf.patient.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHApplicationContextAware;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;

public class PatientHelper extends OHApplicationContextAware {

	public static Patient[] PATIENT_DATA_TABLE = null;

	private PatientIoOperationRepository patientIoOperationRepository;

	public PatientHelper() {
		super();
		patientIoOperationRepository = (PatientIoOperationRepository) applicationContext.getBean("patientIoOperationRepository");

		PATIENT_DATA_TABLE = new Patient[] {
				new Patient("Isaiah", "Alford", LocalDate.parse("2021-04-22"), 0, "", 'M', "134 Entebbe Rd.", "Ssabagabo", "", "33532330",
						"Emily", 'U', "Cooper", 'A', "AB-", 'Y', 'U', "", "unknown", "unknown"),
				new Patient("Bale", "Burch", LocalDate.parse("2021-06-24"), 0, "", 'M', "165 Galiraya Road", "Busolwe", "", "53936466",
						"Kaylee", 'D', "Eli", 'A', "O-", 'N', 'N', "", "unknown", "unknown"),
				new Patient("Andrea", "Lawson", LocalDate.parse("2021-07-20"), 0, "", 'F', "100 Dokolo Rd.", "Kanoni", "", "32445036296",
						"Natalie", 'D', "Andrew", 'A', "A-", 'N', 'N', "", "unknown", "unknown"),
				new Patient("Ashley", "Wright", LocalDate.parse("2021-05-11"), 0, "", 'F', "18 Mbarara Bypass Road", "Kiryandongo", "", "53320192",
						"Julia", 'D', "Adam", 'D', "AB+", 'Y', 'N', "", "unknown", "unknown"),
				new Patient("Kaikara", "Garner", LocalDate.parse("2018-08-24"), 3, "", 'M', "71 Acholibur St.", "Kayunga", "", "32397908482",
						"Layla", 'A', "Henry", 'U', "O+", 'Y', 'U', "", "unknown", "unknown"),
				new Patient("Nathan", "Park", LocalDate.parse("2019-10-11"), 2, "", 'M', "47 Galiraya Road", "Namutumba", "", "05469155",
						"Olivia", 'U', "Balondemu", 'D', "A+", 'N', 'U', "", "unknown", "unknown"),
				new Patient("Sophie", "Atkinson", LocalDate.parse("2019-12-05"), 2, "", 'F', "197 Mubende Rd.", "Isingiro", "", "31583796942",
						"Emma", 'U', "Bryson", 'D', "AB+", 'Y', 'U', "", "unknown", "unknown"),
				new Patient("Victoria", "Francis", LocalDate.parse("2018-10-24"), 3, "", 'F', "105 Kyenjojo Rd.", "Kyotera", "", "9080261246",
						"Samantha", 'D', "Gavin", 'U', "B-", 'Y', 'U', "", "unknown", "unknown"),
				new Patient("Logan", "Melendez", LocalDate.parse("2011-07-04"), 10, "", 'M', "65 Kaiso Rd.", "Luweero", "", "13850080",
						"Dembe", 'U', "Julian", 'A', "A+", 'N', 'U', "", "unknown", "unknown"),
				new Patient("Charles", "Watson", LocalDate.parse("2012-03-12"), 9, "", 'M', "153 Rukungiri Rd.", "Abim", "", "7694177734",
						"Jasmine", 'D', "Ryan", 'D', "O+", 'Y', 'N', "", "unknown", "unknown"),
				new Patient("Jacob", "Adams", LocalDate.parse("2016-01-25"), 6, "", 'M', "38 Fort Portal St.", "Pader", "", "0123738433",
						"Emma", 'U', "Cameron", 'U', "A-", 'N', 'U', "", "unknown", "unknown"),
				new Patient("Charlotte", "Nunez", LocalDate.parse("2015-09-13"), 6, "", 'F', "188 Kaiso Rd.", "Ibanda", "", "46062967",
						"Arianna", 'A', "Gavin", 'D', "Unknown", 'Y', 'N', "", "unknown", "unknown"),
				new Patient("Abbo", "Wynn", LocalDate.parse("2014-03-25"), 7, "", 'F', "15 Tonya Road", "Namayingo", "", "59007137",
						"Audrey", 'U', "Charles", 'U', "Unknown", 'N', 'U', "", "unknown", "unknown"),
				new Patient("Mia", "Perkins", LocalDate.parse("2011-09-02"), 10, "", 'F', "38 Katosi Road", "Bukedea", "", "92602464",
						"Amelia", 'U', "Bryson", 'U', "AB+", 'N', 'U', "", "unknown", "unknown"),
				new Patient("David", "Taylor", LocalDate.parse("2005-10-02"), 16, "", 'M', "60 Masaka Rd.", "Mbale", "", "8136095828",
						"Faith", 'U', "Parker", 'A', "AB+", 'Y', 'U', "29504582438", "unknown", "unknown"),
				new Patient("Eli", "Donaldson", LocalDate.parse("2001-06-18"), 20, "", 'M', "55 Rwekunye Rd.", "Kalongo", "", "96384905445",
						"Sarah", 'U', "Xavier", 'D', "A+", 'Y', 'U', "20133901514", "Married", "Mechanic"),
				new Patient("Hunter", "Solomon", LocalDate.parse("2002-11-14"), 19, "", 'M', "82 Nimule Road", "Fort Portal", "", "4466498864",
						"Julia", 'D', "Grayson", 'A', "AB-", 'N', 'N', "48512357054", "unknown", "unknown"),
				new Patient("Austin", "Sanders", LocalDate.parse("2001-12-08"), 20, "", 'M', "45 Mbale Rd.", "Apac", "", "90066587",
						"Natukunda", 'U', "Liam", 'D', "O-", 'Y', 'U', "11900098927", "Married", "Farming"),
				new Patient("Maya", "Whitfield", LocalDate.parse("2007-06-15"), 14, "", 'F', "40 Entebbe Rd.", "Mayuge", "", "70669061213",
						"Brooklyn", 'D', "Landon", 'A', "O+", 'Y', 'N', "", "unknown", "unknown"),
				new Patient("Molly", "Brock", LocalDate.parse("2001-07-29"), 20, "", 'F', "145 Ntungamo Rd.", "Mitooma", "", "59191252975",
						"Sofia", 'A', "Cooper", 'D', "AB-", 'N', 'N', "67934855298", "Widowed", "Homemaker"),
				new Patient("Samantha", "Pollard", LocalDate.parse("1997-06-17"), 24, "", 'F', "26 Dokolo Rd.", "Alebtong", "", "06869564034",
						"Chloe", 'D', "Carson", 'U', "A-", 'Y', 'U', "00015661712", "Widowed", "Unknown"),
				new Patient("Claire", "Jensen", LocalDate.parse("1999-08-10"), 22, "", 'F', "13 Tonya Road", "Buliisa", "", "35533963",
						"Bacia", 'U', "Connor", 'D', "AB-", 'N', 'U', "95621441883", "Married", "Other"),
				new Patient("Matthew", "Crawford", LocalDate.parse("1977-07-01"), 44, "", 'M', "60 Kisoro Road", "Masaka", "", "86260997",
						"Grace", 'D', "Luis", 'A', "AB-", 'Y', 'N', "77855263050", "Widowed", "Farming"),
				new Patient("Noah", "Armstrong", LocalDate.parse("1976-01-12"), 46, "", 'M', "122 Kikagati Road", "Adjumani", "", "2019363843",
						"Taylor", 'D', "Dominic", 'A', "O+", 'Y', 'N', "64926450736", "Widowed", "Farming"),
				new Patient("Angel", "Hodges", LocalDate.parse("1980-08-25"), 41, "", 'M', "73 Bundibugyo Road", "Ntungamo", "", "30694691080",
						"Gabriella", 'D', "Nathan", 'A', "B-", 'N', 'N', "08009506460", "Single", "Medicine"),
				new Patient("Jack", "Wyatt", LocalDate.parse("1986-08-15"), 35, "", 'M', "13 Kabale Rd.", "Butaleja", "", "80036397546",
						"Zoey", 'U', "Jose", 'A', "O-", 'N', 'U', "39600123632", "Unknown", "Farming"),
				new Patient("Jackson", "Padilla", LocalDate.parse("1966-04-08"), 55, "", 'M', "123 Vurra Rd.", "Bugembe", "", "27048635875",
						"Lillian", 'U', "Aiden", 'U', "B-", 'N', 'U', "00007313969", "Unknown", "Janitorial Services"),
				new Patient("Zachary", "Grant", LocalDate.parse("1994-12-03"), 27, "", 'M', "183 Lwakhakha St.", "Alebtong", "", "2095709929",
						"Madelyn", 'D', "Kayden", 'D', "Unknown", 'N', 'N', "22677615518", "Unknown", "Medicine"),
				new Patient("Kaylee", "Wade", LocalDate.parse("1966-06-04"), 55, "", 'F', "126 Lokitanyala Road", "Nakasongola", "", "2235711609",
						"Alexis", 'A', "Grayson", 'A', "AB-", 'Y', 'Y', "55157320712", "Divorced", "Unknown"),
				new Patient("Molly", "Massey", LocalDate.parse("1987-11-18"), 34, "", 'F', "33 Ntungamo Rd.", "Sironko", "", "40550531684",
						"Abbo", 'D', "Nathaniel", 'D', "B+", 'N', 'N', "58591629576", "Single", "Food/Hospitality"),
				new Patient("Addison", "Goff", LocalDate.parse("1968-10-30"), 53, "", 'F', "187 Matugga Rd.", "Rakai", "", "14338164502",
						"Morgan", 'U', "Ethan", 'D', "AB+", 'Y', 'U', "46518903166", "Single", "Unknown"),
				new Patient("Arianna", "Chan", LocalDate.parse("1963-12-24"), 58, "", 'F', "87 Mbale Rd.", "Mitooma", "", "97022069057",
						"Madeline", 'U', "Aaron", 'U', "O+", 'Y', 'U', "71735788441", "Divorced", "Homemaker"),
				new Patient("Aria", "Watson", LocalDate.parse("1974-12-08"), 47, "", 'F', "23 Nimule Road", "Masaka", "", "75317264475",
						"Sofia", 'A', "Blake", 'D', "AB-", 'Y', 'N', "67848140477", "Divorced", "Other"),
				new Patient("Melanie", "Gay", LocalDate.parse("1967-02-25"), 54, "", 'F', "141 Acholibur St.", "Oyam", "", "75502833076",
						"Ariana", 'A', "Gabriel", 'D', "O-", 'Y', 'N', "60957304684", "Widowed", "Medicine"),
				new Patient("Ryder", "Wong", LocalDate.parse("1956-08-21"), 65, "", 'M', "189 Katuna Road", "Soroti", "", "79167110677",
						"Brianna", 'A', "Michael", 'D', "A-", 'Y', 'N', "41619319887", "Married", "Other"),
				new Patient("Acanit", "Moore", LocalDate.parse("1942-07-04"), 79, "", 'F', "50 Lwakhakha St.", "Budaka", "", "71924245419",
						"Kimberly", 'A', "Jackson", 'A', "B-", 'N', 'N', "00582998832", "Single", "Food/Hospitality")
		};
	}

	public List<Integer> createPatient() {
		return createPatient(PATIENT_DATA_TABLE.length);
	}

	public List<Integer> createPatient(int numberOfInstances) {
		assertThat(numberOfInstances)
				.isPositive()
				.isLessThanOrEqualTo(PATIENT_DATA_TABLE.length);
		List<Integer> codes = new ArrayList<>(numberOfInstances);
		LocalDate now = LocalDate.now();
		for (int idx = 0; idx < numberOfInstances; idx++) {
			// ensure the age is correct with the corrent year
			LocalDate birthdate = PATIENT_DATA_TABLE[idx].getBirthDate();
			Period period = birthdate.until(now);
			int age = period.getYears();
			PATIENT_DATA_TABLE[idx].setAge(age);
			Patient patient = PATIENT_DATA_TABLE[idx];
			Patient savedPatient = patientIoOperationRepository.saveAndFlush(patient);
			codes.add(savedPatient.getCode());
		}
		return codes;
	}

	public void checkPatientInDb(Integer code) {
		Patient patient = patientIoOperationRepository.findById(code).orElse(null);
		assertThat(patient).isNotNull();
		int row = code - 1;
		assertThat(patient.getFirstName()).isEqualTo(PATIENT_DATA_TABLE[row].getFirstName());
		assertThat(patient.getSecondName()).isEqualTo(PATIENT_DATA_TABLE[row].getSecondName());
		assertThat(patient.getBirthDate()).isEqualTo(PATIENT_DATA_TABLE[row].getBirthDate());
		assertThat(patient.getAge()).isEqualTo(PATIENT_DATA_TABLE[row].getAge());
		assertThat(patient.getAgetype()).isEqualTo(PATIENT_DATA_TABLE[row].getAgetype());
		assertThat(patient.getSex()).isEqualTo(PATIENT_DATA_TABLE[row].getSex());
		assertThat(patient.getAddress()).isEqualTo(PATIENT_DATA_TABLE[row].getAddress());
		assertThat(patient.getCity()).isEqualTo(PATIENT_DATA_TABLE[row].getCity());
		assertThat(patient.getNextKin()).isEqualTo(PATIENT_DATA_TABLE[row].getNextKin());
		assertThat(patient.getTelephone()).isEqualTo(PATIENT_DATA_TABLE[row].getTelephone());
		assertThat(patient.getMotherName()).isEqualTo(PATIENT_DATA_TABLE[row].getMotherName());
		assertThat(patient.getMother()).isEqualTo(PATIENT_DATA_TABLE[row].getMother());
		assertThat(patient.getFatherName()).isEqualTo(PATIENT_DATA_TABLE[row].getFatherName());
		assertThat(patient.getFather()).isEqualTo(PATIENT_DATA_TABLE[row].getFather());
		assertThat(patient.getBloodType()).isEqualTo(PATIENT_DATA_TABLE[row].getBloodType());
		assertThat(patient.getHasInsurance()).isEqualTo(PATIENT_DATA_TABLE[row].getHasInsurance());
		assertThat(patient.getParentTogether()).isEqualTo(PATIENT_DATA_TABLE[row].getParentTogether());
		assertThat(patient.getTaxCode()).isEqualTo(PATIENT_DATA_TABLE[row].getTaxCode());
		assertThat(patient.getMaritalStatus()).isEqualTo(PATIENT_DATA_TABLE[row].getMaritalStatus());
		assertThat(patient.getProfession()).isEqualTo(PATIENT_DATA_TABLE[row].getProfession());
	}

}
