package org.isf.opd.service;


import org.isf.opd.model.Opd;

import java.util.GregorianCalendar;
import java.util.List;


public interface OpdIoOperationRepositoryCustom {

	List<Integer> findAllOpdWhereParams(String diseaseTypeCode, String diseaseCode, GregorianCalendar dateFrom,
										GregorianCalendar dateTo, int ageFrom, int ageTo, char sex, char newPatient);

	List<Opd> findAllOpdWhereParamsWithPagination(
			String diseaseTypeCode,
			String diseaseCode,
			GregorianCalendar dateFrom,
			GregorianCalendar dateTo,
			int ageFrom,
			int ageTo,
			char sex,
			char newPatient, int pageNumber, int pageSize);
	
}
