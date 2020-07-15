package org.isf.lab.service;

import org.isf.lab.model.Laboratory;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class LabPatientMergedEventListener {
	@Autowired
	LabIoOperations labIoOperations;

	@EventListener
	@Transactional
	public void handle(PatientMergedEvent patientMergedEvent) throws OHServiceException {
		List<Laboratory> laboratories = labIoOperations.getLaboratory(patientMergedEvent.getObsoletePatient());
		for (Laboratory laboratory : laboratories) {
			Patient mergedPatient = patientMergedEvent.getMergedPatient();
			laboratory.setPatient(mergedPatient);
			laboratory.setPatName(mergedPatient.getName());
			laboratory.setAge(mergedPatient.getAge());
			laboratory.setSex(String.valueOf(mergedPatient.getSex()));
		}
	}
}
