package org.isf.examination.service;

import org.isf.examination.model.PatientExamination;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ExaminationPatientMergedEventListener {
	@Autowired
	private ExaminationOperations examinationOperations;

	@EventListener
	@Transactional
	public void handle(PatientMergedEvent patientMergedEvent) throws OHServiceException {
		List<PatientExamination> patientExaminations = examinationOperations.getByPatID(patientMergedEvent.getObsoletePatient().getCode());
		for (PatientExamination examination : patientExaminations) {
			examination.setPatient(patientMergedEvent.getMergedPatient());
		}
	}

}
