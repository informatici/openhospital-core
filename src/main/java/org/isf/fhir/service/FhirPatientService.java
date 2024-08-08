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
package org.isf.fhir.service;

import static java.util.Optional.ofNullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r5.model.Address;
import org.hl7.fhir.r5.model.Attachment;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.ContactPoint;
import org.hl7.fhir.r5.model.HumanName;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.RelatedPerson;
import org.hl7.fhir.r5.model.ResourceType;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FhirPatientService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FhirPatientService.class);

	public Optional<Patient> transformOHPatientToFHIRPatient(org.isf.patient.model.Patient ohPatient) {
		try {

			return Optional.of(new Patient()
				.addAddress(new Address()
					.setCity(ohPatient.getCity())
					.setUse(Address.AddressUse.HOME)
					.setType(Address.AddressType.PHYSICAL)
					.setText(ohPatient.getAddress()))
				.addName(new HumanName().setUse(HumanName.NameUse.USUAL).setText(ohPatient.getName()))
				.addContact(new Patient.ContactComponent()
					.setName(new HumanName().setText(ohPatient.getFatherName()))
					.setRelationship(List.of(
						new CodeableConcept()
							.setCoding(List.of(new Coding("http://terminology.hl7.org/CodeSystem/v2-0063", "FTH", "Father"))))))
				.addContact(new Patient.ContactComponent()
					.setName(new HumanName().setText(ohPatient.getMotherName()))
					.setRelationship(List.of(
						new CodeableConcept()
							.setCoding(List.of(new Coding("http://terminology.hl7.org/CodeSystem/v2-0063", "MTH", "Mother"))))))
				.addAddress(new Address()
					.setText(ohPatient.getAddress())
					.setType(Address.AddressType.PHYSICAL)
					.setUse(Address.AddressUse.HOME)
					.setCity(ohPatient.getCity()))
				.addPhoto(new Attachment().setData(ohPatient.getPatientProfilePhoto().getPhoto()))
				.addTelecom(new ContactPoint().setValue(ohPatient.getTelephone()))
			);
		} catch (Exception e) {
			LOGGER.warn("Could not create FHIR Patient", e);
			return Optional.empty();
		}
	}

	/*
	String firstName, String secondName, LocalDate birthDate, int age, String agetype, char sex,
			String address, String city, String nextKin, String telephone,
			String motherName, char mother, String fatherName, char father,
			String bloodType, char economicStatut, char parentTogether, String personalCode,
			String maritalStatus, String profession
	 */
	public org.isf.patient.model.Patient transformFHIRBundleToOHPatient(Bundle bundle) throws OHServiceException {
		org.isf.patient.model.Patient ohPatient = new org.isf.patient.model.Patient();
		bundle.getEntry().forEach(p -> {
			if (p.getResource().getResourceType().equals(ResourceType.Patient)) {
				Patient fhirPatient = (Patient) p.getResource();
				ofNullable(fhirPatient.getBirthDate()).ifPresent(birthdate -> {
					var birthday = birthdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					ohPatient.setBirthDate(birthday);
					ohPatient.setAge((int) ChronoUnit.YEARS.between(birthday, LocalDate.now()));
				});

				String phone = fhirPatient.getTelecom().stream()
					.filter(t -> t.getSystem().toCode().equals("phone"))
					.map(ContactPoint::getValue).findFirst()
					.orElse("");

				ohPatient.setFirstName(fhirPatient.getNameFirstRep().getGivenAsSingleString());
				ohPatient.setSecondName(fhirPatient.getNameFirstRep().getFamily());

				ohPatient.setSex(getGender(fhirPatient.getGender().toCode()));
				ofNullable(fhirPatient.getAddressFirstRep().getLine())
					.flatMap(t -> t.stream().findFirst())
					.ifPresent(r -> ohPatient.setAddress(r.asStringValue()));
				ohPatient.setCity(fhirPatient.getAddressFirstRep().getCity());
				ohPatient.setTelephone(phone);
			}
			if (p.getResource().getResourceType().equals(ResourceType.RelatedPerson)) {
				RelatedPerson relatedPerson = (RelatedPerson) p.getResource();
				if (relatedPerson.getRelationshipFirstRep().getText().equals("father")) {
					ohPatient.setFatherName(relatedPerson.getNameFirstRep().getNameAsSingleString());
				}
				if (relatedPerson.getRelationshipFirstRep().getText().equals("mother")) {
					ohPatient.setMotherName(relatedPerson.getNameFirstRep().getNameAsSingleString());
				}
			}
		});
		return ohPatient;
	}

	private char getGender(String code) {
		return switch (code) {
			case "male" -> 'M';
			case "female" -> 'F';
			default -> 'A';
		};
	}
}
