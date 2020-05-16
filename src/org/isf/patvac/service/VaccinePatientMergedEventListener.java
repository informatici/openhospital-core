package org.isf.patvac.service;

import org.isf.patient.model.PatientMergedEvent;
import org.isf.patvac.model.PatientVaccine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class VaccinePatientMergedEventListener {
	@Autowired
	PatVacIoOperations patVacIoOperations;

	@EventListener
	@Transactional
	public void handle(PatientMergedEvent patientMergedEvent) {
		List<PatientVaccine> vaccines = patVacIoOperations.findForPatient(patientMergedEvent.getObsoletePatient().getCode());
		for (PatientVaccine vaccine : vaccines) {
			vaccine.setPatient(patientMergedEvent.getMergedPatient());
		}
	}

}
