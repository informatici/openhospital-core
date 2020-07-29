package org.isf.therapy.service;

import org.isf.patient.model.PatientMergedEvent;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TherapyPatientMergedEventListener {
	@Autowired
	TherapyIoOperations therapyIoOperations;

	@EventListener
	@Transactional
	public void handle(PatientMergedEvent patientMergedEvent) throws OHServiceException {
		List<TherapyRow> therapyRows = therapyIoOperations.getTherapyRows(patientMergedEvent.getObsoletePatient().getCode());
		for (TherapyRow therapyRow : therapyRows) {
			therapyRow.setPatID(patientMergedEvent.getMergedPatient());
		}
	}

}
