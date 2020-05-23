package org.isf.admission.service;


import org.isf.admission.model.Admission;

import java.util.GregorianCalendar;
import java.util.List;


public interface AdmissionIoOperationRepositoryCustom {

	List<Admission> findAllBySearch(String searchTerms);
	
	List<Object[]> findAllBySearchAndDateRanges(String searchTerms, GregorianCalendar[] admissionRange,
			GregorianCalendar[] dischargeRange);
	
}
