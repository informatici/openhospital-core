package org.isf.patient.test;

import org.isf.patient.model.PatientMergedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TestPatientMergedEventListener {
	private PatientMergedEvent patientMergedEvent;

	private boolean shouldFail = false;

	@EventListener
	public void handle(PatientMergedEvent patientMergedEvent) {
		if(shouldFail) {
			throw new RuntimeException("failure testing");
		}
		this.patientMergedEvent = patientMergedEvent;
	}

	public PatientMergedEvent getPatientMergedEvent() {
		return patientMergedEvent;
	}

	public void setShouldFail(boolean shouldFail) {
		this.shouldFail = shouldFail;
	}

}
