package org.isf.fhir.model.resource;

import org.isf.fhir.model.FhirResourceType;

public record FhirResourceMessageHeader(FhirResourceType type, String id, FhirText text, FhirEventCoding eventCoding,
                                        FhirSource source, FhirFocus focus, String reference) {

	/*
	"resourceType" : "MessageHeader",
      "id" : "267b18ce-3d37-4581-9baa-6fada338038b",
      "text" : {
        "status" : "generated",
        "div" : "<div xmlns=\"http://www.w3.org/1999/xhtml\">\n\t\t\t\t\t\t<p>This message is a request to link Patient records 654321 (Patient Donald DUCK @ Acme Healthcare, Inc) and 123456 (Patient Donald D DUCK @ Acme Healthcare, Inc)</p>\n\t\t\t\t\t</div>"
      },
      "eventCoding" : {
        "system" : "http://example.org/fhir/message-events",
        "code" : "patient-link"
      },
      "source" : {
        "endpointUrl" : "http://example.org/clients/ehr-lite"
      },
      "focus" : [{
        "reference" : "http://acme.com/ehr/fhir/Patient/pat1"
      },
      {
        "reference" : "http://acme.com/ehr/fhir/Patient/pat12"
      }]
    }
	 */

	public record FhirText(String status, String div) {

	}

	public record FhirEventCoding(String system, String code) {

	}

	public record FhirSource(String endpointUrl) {

	}
	public record FhirFocus(String reference) {

	}

}
