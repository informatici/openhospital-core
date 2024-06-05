package org.isf.fhir.model.resource;

import java.util.List;

public class FhirResourceMessageHeader extends FhirResource {

	private FhirText text;
	private FhirEventCoding eventCoding;
	private FhirSource source;
	private List<FhirFocus> focus;

	public FhirResourceMessageHeader() {
	}

	public FhirResourceMessageHeader(FhirText text, FhirEventCoding eventCoding, FhirSource source,
		List<FhirFocus> focus) {
		this.text = text;
		this.eventCoding = eventCoding;
		this.source = source;
		this.focus = focus;
	}

	public FhirText getText() {
		return text;
	}

	public FhirEventCoding getEventCoding() {
		return eventCoding;
	}

	public FhirSource getSource() {
		return source;
	}

	public List<FhirFocus> getFocus() {
		return focus;
	}

	public record FhirText(String status, String div) {

	}

	public record FhirEventCoding(String system, String code) {

	}

	public record FhirSource(String endpointUrl) {

	}

	public record FhirFocus(String reference) {

	}

}
