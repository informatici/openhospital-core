package org.isf.opd.service;

import org.isf.opd.model.Opd;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OpdPatientMergedEventListener {
	@Autowired
	OpdIoOperations opdIoOperations;

	@EventListener
	@Transactional
	public void handle(PatientMergedEvent patientMergedEvent) throws OHServiceException {
		List<Opd> opds = opdIoOperations.getOpdList(patientMergedEvent.getObsoletePatient().getCode());
		for(Opd opd : opds) {
			opd.setPatient(patientMergedEvent.getMergedPatient());
		}
	}

}
