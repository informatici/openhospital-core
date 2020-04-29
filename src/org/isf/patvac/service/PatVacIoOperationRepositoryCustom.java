package org.isf.patvac.service;


import org.isf.patvac.model.PatientVaccine;

import java.util.GregorianCalendar;
import java.util.List;


public interface PatVacIoOperationRepositoryCustom {
	
	List<PatientVaccine> findAllByCodesAndDatesAndSexAndAges(String vaccineTypeCode, String vaccineCode,
															 GregorianCalendar dateFrom, GregorianCalendar dateTo, char sex, int ageFrom, int ageTo);
}
