package org.isf.fhir.model.resource;

import java.util.ArrayList;
import java.util.List;

import org.isf.fhir.model.FhirResourceType;

public class FhirResourceMessageHeader extends FhirResource {

	private FhirText text;
	private FhirEventCoding eventCoding;
	private FhirSource source;
	private List<FhirFocus> focus;

	public FhirResourceMessageHeader() {
	}

	public FhirResourceMessageHeader(FhirResourceType resourceType, String id, FhirText text, FhirEventCoding eventCoding, FhirSource source,
		List<FhirFocus> focus) {
		super(resourceType, id);
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

	public static MessageHeaderBuilder builder() {
		return new MessageHeaderBuilder();
	}

	public static class MessageHeaderBuilder {
		private String id;
		private FhirText text;
		private FhirEventCoding eventCoding;
		private FhirSource source;
		private List<FhirFocus> focus = new ArrayList<>();


		public MessageHeaderBuilder withId(String id) {
			this.id = id;
			return this;
		}

		public MessageHeaderBuilder withText(String status, String div) {
			this.text = new FhirText(status, div);
			return this;
		}

		public MessageHeaderBuilder withEventCoding(String system, String code) {
			eventCoding = new FhirEventCoding(system, code);
			return this;
		}

		public MessageHeaderBuilder withSource(String endpointUrl) {
			this.source = new FhirSource(endpointUrl);
			return this;
		}

		public MessageHeaderBuilder addFocus(String reference) {
			this.focus.add(new FhirFocus(reference));
			return this;
		}

		public FhirResourceMessageHeader build() {
			return new FhirResourceMessageHeader(FhirResourceType.MessageHeader, id, text, eventCoding, source, focus);
		}
	}

}
