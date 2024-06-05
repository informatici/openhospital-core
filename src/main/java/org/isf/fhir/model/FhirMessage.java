package org.isf.fhir.model;

import java.util.List;

import org.isf.fhir.model.resource.FhirResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FhirMessage(FhirResourceType resourceType,
                          String id,
                          FhirMessageType type,
                          String timestamp,
                          @JsonProperty("entry") List<FhirEntry> entries) {

	private record FhirEntry(String fullUrl, FhirResource resource) {

	}

	public enum FhirMessageType {
		message
	}
}
