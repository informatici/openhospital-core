package org.isf.fhir;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.isf.fhir.model.FhirMessage;
import org.isf.fhir.model.FhirResourceType;
import org.isf.fhir.model.FhirUseType;
import org.isf.fhir.model.resource.FhirResourcePatient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FhirServiceTest {

	@Autowired
	private FhirService fhirService;

	@Test
	void testConvertStringToFhirMessage() {
		Optional<FhirMessage> fhirMessage = fhirService.convertStringToFhirMessage(getFhirMessage());
		assertThat(fhirMessage).isPresent();
	}

	@Test
	void testConvertFhirMessageToJson() {
		FhirMessage fhirMessage = new FhirMessage(FhirResourceType.Bundle, UUID.randomUUID().toString(), FhirMessage.FhirMessageType.message,
			Instant.now().toString(), List.of());
		Optional<String> jsonRaw = fhirService.convertFhirMessageToJson(fhirMessage);
		assertThat(jsonRaw).isPresent();
	}

	@Test
	void testConvertFhirMessageToJsonWithPatients() {
		FhirMessage fhirMessage = new FhirMessage(FhirResourceType.Bundle, UUID.randomUUID().toString(), FhirMessage.FhirMessageType.message,
			Instant.now().toString(), List.of(new FhirMessage.FhirEntry("http://url.com", createPatient())));
		Optional<String> jsonRaw = fhirService.convertFhirMessageToJson(fhirMessage);
		assertThat(jsonRaw).isPresent();
		assertThat(jsonRaw.get()).contains("Patient");
		assertThat(jsonRaw.get()).contains("http://url.com");
		assertThat(jsonRaw.get()).contains("Donald");
		assertThat(jsonRaw.get()).contains("Duck");
	}

	private FhirResourcePatient createPatient() {
		return FhirResourcePatient.builder()
			.withActive(true)
			.withIdentifier(FhirUseType.usual, List.of(new FhirResourcePatient.FhirCoding("system", "code")), "system", "value")
			.withContact(List.of(new FhirResourcePatient.FhirCoding("test", "M")), new FhirResourcePatient.FhirOrganization("reference", "display"))
			.withName(FhirUseType.official, "Donald", List.of("Duck"))
			.build();

	}

	private String getFhirMessage() {
		return "{\n"
			+ "  \"resourceType\" : \"Bundle\",\n"
			+ "  \"id\" : \"10bb101f-a121-4264-a920-67be9cb82c74\",\n"
			+ "  \"identifier\" : {\n"
			+ "    \"system\" : \"urn:example-org:sender.identifiers\",\n"
			+ "    \"value\" : \"efdd254b-0e09-4164-883e-35cf3871715f\"\n"
			+ "  },\n"
			+ "  \"type\" : \"message\",\n"
			+ "  \"timestamp\" : \"2015-07-14T11:15:33+10:00\",\n"
			+ "  \"entry\" : [{\n"
			+ "    \"fullUrl\" : \"urn:uuid:267b18ce-3d37-4581-9baa-6fada338038b\",\n"
			+ "    \"resource\" : {\n"
			+ "      \"resourceType\" : \"MessageHeader\",\n"
			+ "      \"id\" : \"267b18ce-3d37-4581-9baa-6fada338038b\",\n"
			+ "      \"text\" : {\n"
			+ "        \"status\" : \"generated\",\n"
			+ "        \"div\" : \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n\\t\\t\\t\\t\\t\\t<p>This message is a request to link Patient records 654321 (Patient Donald DUCK @ Acme Healthcare, Inc) and 123456 (Patient Donald D DUCK @ Acme Healthcare, Inc)</p>\\n\\t\\t\\t\\t\\t</div>\"\n"
			+ "      },\n"
			+ "      \"eventCoding\" : {\n"
			+ "        \"system\" : \"http://example.org/fhir/message-events\",\n"
			+ "        \"code\" : \"patient-link\"\n"
			+ "      },\n"
			+ "      \"source\" : {\n"
			+ "        \"endpointUrl\" : \"http://example.org/clients/ehr-lite\"\n"
			+ "      },\n"
			+ "      \"focus\" : [{\n"
			+ "        \"reference\" : \"http://acme.com/ehr/fhir/Patient/pat1\"\n"
			+ "      },\n"
			+ "      {\n"
			+ "        \"reference\" : \"http://acme.com/ehr/fhir/Patient/pat12\"\n"
			+ "      }]\n"
			+ "    }\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"fullUrl\" : \"http://acme.com/ehr/fhir/Patient/pat1\",\n"
			+ "    \"resource\" : {\n"
			+ "      \"resourceType\" : \"Patient\",\n"
			+ "      \"id\" : \"pat1\",\n"
			+ "      \"text\" : {\n"
			+ "        \"status\" : \"generated\",\n"
			+ "        \"div\" : \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n\\t\\t\\t\\t\\t\\t<p>Patient Donald DUCK @ Acme Healthcare, Inc. MR = 654321</p>\\n\\t\\t\\t\\t\\t</div>\"\n"
			+ "      },\n"
			+ "      \"identifier\" : [{\n"
			+ "        \"use\" : \"usual\",\n"
			+ "        \"type\" : {\n"
			+ "          \"coding\" : [{\n"
			+ "            \"system\" : \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n"
			+ "            \"code\" : \"MR\"\n"
			+ "          }]\n"
			+ "        },\n"
			+ "        \"system\" : \"urn:oid:0.1.2.3.4.5.6.7\",\n"
			+ "        \"value\" : \"654321\"\n"
			+ "      }],\n"
			+ "      \"active\" : true,\n"
			+ "      \"name\" : [{\n"
			+ "        \"use\" : \"official\",\n"
			+ "        \"family\" : \"Donald\",\n"
			+ "        \"given\" : [\"Duck\"]\n"
			+ "      }],\n"
			+ "      \"gender\" : \"male\",\n"
			+ "      \"contact\" : [{\n"
			+ "        \"relationship\" : [{\n"
			+ "          \"coding\" : [{\n"
			+ "            \"system\" : \"http://example.org/fhir/CodeSystem/patient-contact-relationship\",\n"
			+ "            \"code\" : \"E\"\n"
			+ "          }]\n"
			+ "        }],\n"
			+ "        \"organization\" : {\n"
			+ "          \"reference\" : \"Organization/1\",\n"
			+ "          \"display\" : \"Walt Disney Corporation\"\n"
			+ "        }\n"
			+ "      }],\n"
			+ "      \"managingOrganization\" : {\n"
			+ "        \"reference\" : \"Organization/1\",\n"
			+ "        \"display\" : \"ACME Healthcare, Inc\"\n"
			+ "      }\n"
			+ "    }\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"fullUrl\" : \"http://acme.com/ehr/fhir/Patient/pat12\",\n"
			+ "    \"resource\" : {\n"
			+ "      \"resourceType\" : \"Patient\",\n"
			+ "      \"id\" : \"pat2\",\n"
			+ "      \"text\" : {\n"
			+ "        \"status\" : \"generated\",\n"
			+ "        \"div\" : \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n\\t\\t\\t\\t\\t\\t<p>Patient Donald D DUCK @ Acme Healthcare, Inc. MR = 123456</p>\\n\\t\\t\\t\\t\\t</div>\"\n"
			+ "      },\n"
			+ "      \"identifier\" : [{\n"
			+ "        \"use\" : \"usual\",\n"
			+ "        \"type\" : {\n"
			+ "          \"coding\" : [{\n"
			+ "            \"system\" : \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n"
			+ "            \"code\" : \"MR\"\n"
			+ "          }]\n"
			+ "        },\n"
			+ "        \"system\" : \"urn:oid:0.1.2.3.4.5.6.7\",\n"
			+ "        \"value\" : \"123456\"\n"
			+ "      }],\n"
			+ "      \"active\" : true,\n"
			+ "      \"name\" : [{\n"
			+ "        \"use\" : \"official\",\n"
			+ "        \"family\" : \"Donald\",\n"
			+ "        \"given\" : [\"Duck\",\n"
			+ "        \"D\"]\n"
			+ "      }],\n"
			+ "      \"gender\" : \"other\",\n"
			+ "      \"_gender\" : {\n"
			+ "        \"extension\" : [{\n"
			+ "          \"url\" : \"http://example.org/Profile/administrative-status\",\n"
			+ "          \"valueCodeableConcept\" : {\n"
			+ "            \"coding\" : [{\n"
			+ "              \"system\" : \"http://terminology.hl7.org/CodeSystem/v2-0001\",\n"
			+ "              \"code\" : \"A\",\n"
			+ "              \"display\" : \"Ambiguous\"\n"
			+ "            }]\n"
			+ "          }\n"
			+ "        }]\n"
			+ "      },\n"
			+ "      \"managingOrganization\" : {\n"
			+ "        \"reference\" : \"Organization/1\",\n"
			+ "        \"display\" : \"ACME Healthcare, Inc\"\n"
			+ "      }\n"
			+ "    }\n"
			+ "  }]\n"
			+ "}";
	}

}