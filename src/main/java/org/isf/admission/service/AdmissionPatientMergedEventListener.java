package org.isf.admission.service;

import org.isf.admission.model.Admission;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdmissionPatientMergedEventListener {
	@Autowired
	AdmissionIoOperations admissionIoOperations;

	@EventListener
	public void handle(PatientMergedEvent patientMergedEvent) throws OHServiceException {
		List<Admission> admissions = admissionIoOperations.getAdmissions(patientMergedEvent.getObsoletePatient());
		for (Admission admission : admissions) {
			admission.setPatient(patientMergedEvent.getMergedPatient());
		}
	}

}
