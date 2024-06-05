package org.isf.fhir;

import java.util.Optional;

import org.isf.fhir.model.FhirMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FhirService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FhirService.class);

	private final ObjectMapper objectMapper;

	public FhirService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public Optional<FhirMessage> convertStringToFhirMessage(String data) {
		try {
			return Optional.ofNullable(objectMapper.readValue(data, FhirMessage.class));
		} catch (JsonProcessingException e) {
			LOGGER.warn("Could not convert string to FHIR message", e);
			return Optional.empty();
		}
	}

	public Optional<String> convertFhirMessageToJson(FhirMessage message) {
		try {
			return Optional.ofNullable(objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			LOGGER.warn("Could not convert fhir message to String", e);
			return Optional.empty();
		}
	}

}
