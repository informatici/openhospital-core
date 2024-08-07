package org.isf.fhir.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;

import org.hl7.fhir.r5.model.Bundle;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

class FhirPatientServiceTest {

	private final FhirPatientService fhirPatientService;

	FhirPatientServiceTest() {
		fhirPatientService = new FhirPatientService();
	}

	@Test
	void transformFHIRBundleToOHPatient() throws IOException, OHServiceException {
		Bundle bundle = FhirContext.forR5().newJsonParser().parseResource(
			Bundle.class,
			this.getClass().getClassLoader().getResourceAsStream("org/isf/fhir/bundle-test.json")
		);
		fhirPatientService.transformFHIRBundleToOHPatient(bundle);
	}
}