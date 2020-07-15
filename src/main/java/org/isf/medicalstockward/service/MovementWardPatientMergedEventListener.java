package org.isf.medicalstockward.service;

import org.isf.medicalstockward.model.MovementWard;
import org.isf.patient.model.PatientMergedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MovementWardPatientMergedEventListener {
	@Autowired
	MedicalStockWardIoOperations medicalStockWardIoOperations;

	@EventListener
	@Transactional
	public void handle(PatientMergedEvent patientMergedEvent) {
		List<MovementWard> movementWards = medicalStockWardIoOperations.findAllForPatient(patientMergedEvent.getObsoletePatient());
		for (MovementWard movementWard : movementWards) {
			movementWard.setPatient(patientMergedEvent.getMergedPatient());
		}
	}

}
