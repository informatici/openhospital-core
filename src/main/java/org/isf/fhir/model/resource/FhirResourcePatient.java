package org.isf.fhir.model.resource;

import java.util.List;

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

	public FhirResourcePatient(FhirText text, List<FhirIdentifier> identifier, boolean active,
		List<FhirName> name, String gender, List<FhirContact> contact, FhirOrganization managingOrganization) {
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

	private record FhirPatientType(List<FhirCoding> coding, String system, String value) {

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
