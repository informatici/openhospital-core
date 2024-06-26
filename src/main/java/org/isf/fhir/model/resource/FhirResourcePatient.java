package org.isf.fhir.model.resource;

import java.util.ArrayList;
import java.util.List;

import org.isf.fhir.model.FhirResourceType;
import org.isf.fhir.model.FhirUseType;

public class FhirResourcePatient extends FhirResource {

	private FhirText text;
	private List<FhirIdentifier> identifier;
	private boolean active;
	private List<FhirName> name;
	private String gender;
	private List<FhirContact> contact;
	private FhirOrganization managingOrganization;

	public FhirResourcePatient() {
	}

	public FhirResourcePatient(FhirResourceType resourceType, String id, FhirText text, List<FhirIdentifier> identifier, boolean active,
		List<FhirName> name, String gender, List<FhirContact> contact, FhirOrganization managingOrganization) {
		super(resourceType, id);
		this.text = text;
		this.identifier = identifier;
		this.active = active;
		this.name = name;
		this.gender = gender;
		this.contact = contact;
		this.managingOrganization = managingOrganization;
	}




	public FhirText getText() {
		return text;
	}

	public List<FhirIdentifier> getIdentifier() {
		return identifier;
	}

	public boolean isActive() {
		return active;
	}

	public List<FhirName> getName() {
		return name;
	}

	public String getGender() {
		return gender;
	}

	public List<FhirContact> getContact() {
		return contact;
	}

	public FhirOrganization getManagingOrganization() {
		return managingOrganization;
	}

	public record FhirText(String status, String div) {

	}

	public record FhirIdentifier(FhirUseType use, FhirPatientType type, String system, String value) {

	}

	public record FhirPatientType(List<FhirCoding> coding) {

	}

	public record FhirCoding(String system, String code) {

	}

	public record FhirName(FhirUseType use, String family, List<String> given) {

	}

	public record FhirContact(List<FhirContactRelationship> relationship, FhirOrganization organization) {

	}

	public record FhirContactRelationship(List<FhirCoding> coding) {

	}

	public record FhirOrganization(String reference, String display) {

	}


	public static PatientBuilder builder() {
		return new PatientBuilder();
	}



	public static class PatientBuilder {
		private String id;
		private FhirText text;
		private List<FhirIdentifier> identifiers = new ArrayList<>();
		private boolean active;
		private List<FhirName> names = new ArrayList<>();
		private String gender;
		private List<FhirContact> contacts = new ArrayList<>();
		private FhirOrganization managingOrganization;


		public PatientBuilder withId(String id) {
			this.id = id;
			return this;
		}

		public PatientBuilder withText(String status, String div) {
			this.text = new FhirText(status, div);
			return this;
		}

		public PatientBuilder withIdentifier(FhirUseType useType, List<FhirCoding> fireCodings, String system, String value) {
			identifiers.add(new FhirIdentifier(useType, new FhirPatientType(fireCodings), system, value ));
			return this;
		}

		public PatientBuilder withActive(boolean active) {
			this.active = active;
			return this;
		}

		public PatientBuilder withName(FhirUseType use, String name, List<String> given) {
			this.names.add(new FhirName(use, name, given));
			return this;
		}

		public PatientBuilder withGender(String gender) {
			this.gender = gender;
			return this;
		}

		public PatientBuilder withContact(List<FhirCoding> fhirCodings, FhirOrganization organization) {
			this.contacts.add(new FhirContact(List.of(new FhirContactRelationship(fhirCodings)), organization));
			return this;
		}
		public PatientBuilder withManagingOrganization(FhirOrganization managingOrganization) {
			this.managingOrganization = managingOrganization;
			return this;
		}
		public FhirResourcePatient build() {
			return new FhirResourcePatient(FhirResourceType.Patient, id, text, identifiers, active, names, gender, contacts, managingOrganization);
		}
	}


	/*
	 "resourceType" : "Patient",
      "id" : "pat1",
      "text" : {
        "status" : "generated",
        "div" : "<div xmlns=\"http://www.w3.org/1999/xhtml\">\n\t\t\t\t\t\t<p>Patient Donald DUCK @ Acme Healthcare, Inc. MR = 654321</p>\n\t\t\t\t\t</div>"
      },
      "identifier" : [{
        "use" : "usual",
        "type" : {
          "coding" : [{
            "system" : "http://terminology.hl7.org/CodeSystem/v2-0203",
            "code" : "MR"
          }]
        },
        "system" : "urn:oid:0.1.2.3.4.5.6.7",
        "value" : "654321"
      }],
      "active" : true,
      "name" : [{
        "use" : "official",
        "family" : "Donald",
        "given" : ["Duck"]
      }],
      "gender" : "male",
      "contact" : [{
        "relationship" : [{
          "coding" : [{
            "system" : "http://example.org/fhir/CodeSystem/patient-contact-relationship",
            "code" : "E"
          }]
        }],
        "organization" : {
          "reference" : "Organization/1",
          "display" : "Walt Disney Corporation"
        }
      }],
      "managingOrganization" : {
        "reference" : "Organization/1",
        "display" : "ACME Healthcare, Inc"
      }
    }
	 */

}
