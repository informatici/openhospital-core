/**
 * 
 */
package org.isf.anamnesis.model;

import org.isf.patient.model.Patient;


/**
 * @author Mwithi
 *
 */
public class PatientPatientHistory { // extends PatientHistory

	private PatientHistory patientHistory;
	
	private Patient patient;

	/**
	 * @param patientHistory
	 * @param patient
	 */
	public PatientPatientHistory(PatientHistory patientHistory, Patient patient) {
		super();
		this.patientHistory = patientHistory;
		this.patient = patient;
	}

	/**
	 * @return the patientHistory
	 */
	public PatientHistory getPatientHistory() {
		return patientHistory;
	}

	/**
	 * @param patientHistory the patientHistory to set
	 */
	public void setPatientHistory(PatientHistory patientHistory) {
		this.patientHistory = patientHistory;
	}

	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
