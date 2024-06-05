package org.isf.fhir.model.resource;


import org.isf.fhir.model.FhirResourceType;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	visible = true,
	property = "resourceType")
@JsonSubTypes({
	@JsonSubTypes.Type(value = FhirResourceMessageHeader.class, name = "MessageHeader"),
	@JsonSubTypes.Type(value = FhirResourcePatient.class, name = "Patient")
})
public class FhirResource {

	private FhirResourceType resourceType;
	private String id;

	public FhirResource(FhirResourceType resourceType, String id) {
		this.resourceType = resourceType;
		this.id = id;
	}

	public FhirResource() {
	}

	public FhirResourceType getResourceType() {
		return resourceType;
	}

	public String getId() {
		return id;
	}

}
