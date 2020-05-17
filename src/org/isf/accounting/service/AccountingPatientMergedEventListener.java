package org.isf.accounting.service;

import org.isf.accounting.model.Bill;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AccountingPatientMergedEventListener {
	@Autowired
	AccountingIoOperations accountingIoOperations;

	@EventListener
	@Transactional
	public void handle(PatientMergedEvent patientMergedEvent) throws OHServiceException {
		List<Bill> bills = accountingIoOperations.getAllPatientsBills(patientMergedEvent.getObsoletePatient().getCode());
		for (Bill bill : bills) {
			bill.setPatient(patientMergedEvent.getMergedPatient());
		}
	}
}