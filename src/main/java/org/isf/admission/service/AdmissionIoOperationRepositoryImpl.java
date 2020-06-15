package org.isf.admission.service;


import org.isf.utils.time.TimeTools;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


@Transactional
public class AdmissionIoOperationRepositoryImpl implements AdmissionIoOperationRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	
	@SuppressWarnings("unchecked")	
	@Override
	public List<Object[]> findAllBySearch(String searchTerms) {
		return this.entityManager.
				createNativeQuery(_getAdmissionsBySearch(searchTerms)).
					getResultList();
	}

	@Override
	public List<PatientAdmission> findPatientAndAdmissionId(final String searchTerms) {
		String[] terms = _calculateAdmittedPatientsTerms(searchTerms);
		String query = "SELECT PAT.PAT_ID, ADM.ADM_ID " +
				"FROM PATIENT PAT LEFT JOIN " +
				"(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM " +
				"ON ADM.ADM_PAT_ID = PAT.PAT_ID " +
				"WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ";
		if (terms != null) {
			for (String term: terms) {
				query += " AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) LIKE \"%" + term + "%\"";
			}
		}
		query += " ORDER BY PAT_ID DESC";

		return findPatientAdmissionQuery(this.entityManager.createNativeQuery(query).getResultList());
	}

	@Override
	public List<PatientAdmission> findPatientAdmissionsBySearchAndDateRanges(final String searchTerms,
																			 final GregorianCalendar[] admissionRange,
																			 final GregorianCalendar[] dischargeRange) {
		String[] terms = _calculateAdmittedPatientsTerms(searchTerms);

		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT PAT.PAT_ID, ADM.ADM_ID " +
				"FROM PATIENT PAT LEFT JOIN " +
				"(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) ORDER BY ADM_IN DESC) ADM " +
				"ON ADM.ADM_PAT_ID = PAT.PAT_ID " +
				"WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ");

		if(admissionRange != null) {
			if (admissionRange.length == 2 && admissionRange[0] != null //
					&& admissionRange[1] != null) {
				queryBuilder.append(" AND DATE(ADM.ADM_DATE_ADM) BETWEEN '")
						.append(TimeTools.formatDateTime(admissionRange[0], TimeTools.YYYY_MM_DD))
						.append("' AND '")
						.append(TimeTools.formatDateTime(admissionRange[1], TimeTools.YYYY_MM_DD))
						.append("'");
			}
		}

		if(dischargeRange != null) {
			if (dischargeRange.length == 2 && dischargeRange[0] != null //
					&& dischargeRange[1] != null) {
				queryBuilder.append(" AND DATE(ADM.ADM_DATE_DIS) BETWEEN '")
						.append(TimeTools.formatDateTime(dischargeRange[0], TimeTools.YYYY_MM_DD))
						.append("' AND '")
						.append(TimeTools.formatDateTime(dischargeRange[1], TimeTools.YYYY_MM_DD))
						.append("'");
			}
		}

		if (terms != null) {
			for (String term:terms) {
				queryBuilder.append(" AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) LIKE \"%")
						.append(term)
						.append("%\"");
			}
		}
		queryBuilder.append(" ORDER BY PAT_ID DESC");

		return findPatientAdmissionQuery(this.entityManager.createNativeQuery(queryBuilder.toString()).getResultList());
	}

	private List<PatientAdmission> findPatientAdmissionQuery(List<Object[]> resultList) {
		final List<PatientAdmission> result = new ArrayList<PatientAdmission>(resultList.size());
		for (final Object[] arrays : resultList) {
			result.add(new PatientAdmission((Integer) arrays[0], (Integer) arrays[1]));
		}
		return result;
	}

	private String _getAdmissionsBySearch(
			String searchTerms) 
	{
		String[] terms = _calculateAdmittedPatientsTerms(searchTerms);
		String query = _calculateAdmittedPatientsQuery(terms);
		
		return query;
	}
	
    private String[] _calculateAdmittedPatientsTerms(
			String searchTerms) 
	{
    	String[] terms = null;
    	
    	
    	if (searchTerms != null && !searchTerms.isEmpty()) 
		{
			searchTerms = searchTerms.trim().toLowerCase();
			terms = searchTerms.split(" ");
		}
    	
    	return terms;
	}

	private String _calculateAdmittedPatientsQuery(
    		String[] terms)
	{
    	String query = null;	

    	
    	query = "SELECT PAT.*, ADM.* " +
    			"FROM PATIENT PAT LEFT JOIN " +
    			"(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM " +
    			"ON ADM.ADM_PAT_ID = PAT.PAT_ID " +
    			"WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ";
    	if (terms != null) 
		{
			for (String term:terms) 
			{
				query += " AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) LIKE \"%" + term + "%\"";
			}
		}
		query += " ORDER BY PAT_ID DESC";
    	
    	return query;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findAllBySearchAndDateRanges(String searchTerms, GregorianCalendar[] admissionRange,
			GregorianCalendar[] dischargeRange) {
		return this.entityManager.
				createNativeQuery(_getAdmissionsBySearchAndDateRanges(searchTerms, admissionRange, dischargeRange)).
					getResultList();
	}
	
	private String _getAdmissionsBySearchAndDateRanges(
			String searchTerms, GregorianCalendar[] admissionRange, GregorianCalendar[] dischargeRange) 
	{
		String[] terms = _calculateAdmittedPatientsTerms(searchTerms);
		String query = _calculateAdmittedPatientsQuery(terms, admissionRange, dischargeRange);

		
		return query;
	}
	
	private String _calculateAdmittedPatientsQuery(
    		String[] terms, GregorianCalendar[] admissionRange, GregorianCalendar[] dischargeRange)
	{
    	String query = null;
    	
    	query = "SELECT PAT.*, ADM.* " +
    			"FROM PATIENT PAT LEFT JOIN " +
    			"(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) ORDER BY ADM_IN DESC) ADM " +
    			"ON ADM.ADM_PAT_ID = PAT.PAT_ID " +
    			"WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ";
    	
    	if(admissionRange != null) {
			if (admissionRange.length == 2 && admissionRange[0] != null //
					&& admissionRange[1] != null) {
				query += " AND DATE(ADM.ADM_DATE_ADM) BETWEEN " + 
					TimeTools.formatDateTime(admissionRange[0], "'yyyy-MM-dd'") + 
					" AND " +  
					TimeTools.formatDateTime(admissionRange[1], "'yyyy-MM-dd'");
			}
		}
    	
    	if(dischargeRange != null) {
			if (dischargeRange.length == 2 && dischargeRange[0] != null //
					&& dischargeRange[1] != null) {
				query += " AND DATE(ADM.ADM_DATE_DIS) BETWEEN '" +
						TimeTools.formatDateTime(dischargeRange[0], TimeTools.YYYY_MM_DD) +
						"' AND '" +  
						TimeTools.formatDateTime(dischargeRange[1], TimeTools.YYYY_MM_DD) +
						"'";
			}
		}
    	
    	if (terms != null) 
		{
			for (String term:terms) 
			{
				query += " AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) LIKE \"%" + term + "%\"";
			}
		}
		query += " ORDER BY PAT_ID DESC";
    	
    	return query;
	}
}