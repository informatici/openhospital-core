package org.isf.patient.service;


import org.isf.patient.model.Patient;

import java.util.List;


public interface PatientIoOperationRepositoryCustom {

	List<Patient> findByFieldsContainingWordsFromLiteral(String regex);
	
}
