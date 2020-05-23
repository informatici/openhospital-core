package org.isf.admission.service;


import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;
import org.isf.utils.time.TimeTools;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class AdmissionIoOperationRepositoryImpl implements AdmissionIoOperationRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	
	@SuppressWarnings("unchecked")	
	@Override
	public List<Admission> findAllBySearch(String literal) {
		return this.entityManager.
				createQuery(buildSearchQuery(literal)).
					getResultList();
	}	

	
	private CriteriaQuery<Admission> buildSearchQuery(String searchTerms) {
		String[] terms = getTermsToSearch(searchTerms);
		return createQuerySearchingForPatientContainingGivenWordsInHisProperties(terms);
	}
	
    private String[] getTermsToSearch(String searchTerms) {
    	String[] terms = null;

    	if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			terms = searchTerms.split(" ");
		}

    	return terms;
	}
    
    private CriteriaQuery<Admission> createQuerySearchingForPatientContainingGivenWordsInHisProperties(String[] terms) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Admission> query = cb.createQuery(Admission.class);
		Root<Admission> admissionRoot = query.from(Admission.class);
		List<Predicate> where = new ArrayList<Predicate>();
		query.select(admissionRoot);
		where.add(cb.or(
			cb.equal(admissionRoot.get("deleted"), "N"),
			cb.isNull(admissionRoot.get("deleted"))
		));
		Join<Admission, Patient> patient = admissionRoot.join("patient");
		if(terms != null) {
			for(String term : terms) {
				where.add(wordExistsInOneOfPatientFields(term, cb, patient));
			}
		}

		where.add(cb.or(
			cb.equal(patient.get("deleted"), "N"),
			cb.isNull(patient.get("deleted"))
		));
		query.where(cb.and(where.toArray(new Predicate[where.size()])));

		query.orderBy(cb.desc(patient.get("code")));

    	return query;
	}

	private Predicate wordExistsInOneOfPatientFields(String word, CriteriaBuilder cb, Join<Admission, Patient> patientRoot) {
		return cb.or(
			cb.like(cb.lower(patientRoot.get("code").as(String.class)), like(word)),
			cb.like(cb.lower(patientRoot.get("secondName").as(String.class)), like(word)),
			cb.like(cb.lower(patientRoot.get("firstName").as(String.class)), like(word)),
			cb.like(cb.lower(patientRoot.get("note").as(String.class)), like(word)),
			cb.like(cb.lower(patientRoot.get("taxCode").as(String.class)), like(word))
		);
	}

	private String like(String word) {
		return "%" + word + "%";
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
		String[] terms = getTermsToSearch(searchTerms);
		String query = createQuerySearchingForPatientContainingGivenWordsInHisProperties(terms, admissionRange, dischargeRange);
		
		
		return query;
	}
	
	private String createQuerySearchingForPatientContainingGivenWordsInHisProperties(
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
						TimeTools.formatDateTime(dischargeRange[0], "yyyy-MM-dd") + 
						"' AND '" +  
						TimeTools.formatDateTime(dischargeRange[1], "yyyy-MM-dd") +
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