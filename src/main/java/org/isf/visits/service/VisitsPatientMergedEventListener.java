package org.isf.visits.service;

import org.isf.patient.model.PatientMergedEvent;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VisitsPatientMergedEventListener {
	@Autowired
	VisitsIoOperations visitsIoOperations;

	@EventListener
	public void handle(PatientMergedEvent patientMergedEvent) throws OHServiceException {
		List<Visit> visits = visitsIoOperations.getVisits(patientMergedEvent.getObsoletePatient().getCode());
		for(Visit visit : visits) {
			visit.setPatient(patientMergedEvent.getMergedPatient());
		}
	}

}
