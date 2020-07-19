package org.isf.patient.model;

public class PatientMergedEvent {
	private final Patient obsoletePatient;
	private final Patient mergedPatient;

	public PatientMergedEvent(Patient obsoletePatient, Patient mergedPatient) {
		this.obsoletePatient = obsoletePatient;
		this.mergedPatient = mergedPatient;
	}

	public Patient getObsoletePatient() {
		return obsoletePatient;
	}

	public Patient getMergedPatient() {
		return mergedPatient;
	}
}
