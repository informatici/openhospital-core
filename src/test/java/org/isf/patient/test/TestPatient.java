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
package org.isf.patient.test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.utils.exception.OHException;

public class TestPatient 
{			
	private static String firstName = "TestFirstName";
	private static String secondName = "TestSecondName";
	private static Date birthDate = new GregorianCalendar(1984, Calendar.AUGUST, 14).getTime();
	private static int age = 31; //IT WILL CHANGE WITH TIME
	private static String agetype = "Date";
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
	private static float height = 184;
	private static float weight = 60;
	private static String maritalStatus = "divorced";
	private static String profession = "business";
	//private static Blob photo;	
	//private static Image photoImage;
				
		
	public Patient setup(
			boolean usingSet) throws OHException 
	{
		Patient patient;
	
				
		if (usingSet)
		{
			patient = new Patient();
			patient.setPatientProfilePhoto(new PatientProfilePhoto());
			_setParameters(patient);
		}
		else
		{
			// Create Patient with all parameters 
			patient = new Patient(firstName, secondName, birthDate, age, agetype, sex,
					address, city, nextKin, telephone, mother_name, mother, father_name, father,
					bloodType, hasInsurance, parentTogether, taxCode, maritalStatus, profession);
			patient.setAge(patient.getAge()); //IT WILL CHANGE WITH TIME
			patient.setPatientProfilePhoto(new PatientProfilePhoto());
		}
				    	
		return patient;
	}
	
	public void _setParameters(
			Patient patient) 
	{		
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
		patient.setMother_name(mother_name);
		patient.setMother(mother);
		patient.setFather_name(father_name);
		patient.setFather(father);
		patient.setBloodType(bloodType);
		patient.setHasInsurance(hasInsurance);		
		patient.setParentTogether(parentTogether);
		patient.setTaxCode(taxCode);
		patient.setHeight(height);
		patient.setWeight(weight);
		patient.setMaritalStatus(maritalStatus);
		patient.setProfession(profession);
		
		return;
	}
	
	public void check(
			Patient patient) 
	{		
		assertEquals(firstName, patient.getFirstName());
		assertEquals(secondName, patient.getSecondName());
		assertEquals(birthDate, patient.getBirthDate());
		//assertEquals(age, patient.getAge());
		assertEquals(agetype, patient.getAgetype());
		assertEquals(sex, patient.getSex());
		assertEquals(address, patient.getAddress());
		assertEquals(city, patient.getCity());
		assertEquals(nextKin, patient.getNextKin());
		assertEquals(telephone, patient.getTelephone());
		assertEquals(mother_name, patient.getMother_name());
		assertEquals(mother, patient.getMother());
		assertEquals(father_name, patient.getFather_name());
		assertEquals(father, patient.getFather());
		assertEquals(bloodType, patient.getBloodType());
		assertEquals(hasInsurance, patient.getHasInsurance());		
		assertEquals(parentTogether, patient.getParentTogether());
		assertEquals(taxCode, patient.getTaxCode());
		assertEquals(maritalStatus, patient.getMaritalStatus());
		assertEquals(profession, patient.getProfession());
		//assertEquals(height, patient.getHeight());
		//assertEquals(weight, patient.getWeight());
		
		return;
	}
}
