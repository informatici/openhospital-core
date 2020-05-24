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

import static org.isf.utils.time.TimeTools.getBeginningOfDay;
import static org.isf.utils.time.TimeTools.getBeginningOfNextDay;


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
		where.add(admissionNotDeletedPredicate(cb, admissionRoot));
		Join<Admission, Patient> patient = admissionRoot.join("patient");
		if (terms != null) {
			where.addAll(termsPredicates(terms, cb, patient));
		}
		where.add(patientNotDeletedPredicate(cb, patient));
		query.where(cb.and(where.toArray(new Predicate[where.size()])));
		query.orderBy(cb.desc(patient.get("code")));

    	return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Admission> findAllBySearchAndDateRanges(String searchTerms, GregorianCalendar[] admissionRange,
														GregorianCalendar[] dischargeRange) {
		return this.entityManager.
				createQuery(createQuerySearchingForPatientContainingGivenWordsInHisProperties(getTermsToSearch(searchTerms), admissionRange, dischargeRange)).
					getResultList();
	}

	private CriteriaQuery<Admission> createQuerySearchingForPatientContainingGivenWordsInHisProperties(String[] terms,
																									   GregorianCalendar[] admissionRange,
																									   GregorianCalendar[] dischargeRange) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Admission> query = cb.createQuery(Admission.class);
		Root<Admission> admissionRoot = query.from(Admission.class);
		List<Predicate> where = new ArrayList<Predicate>();

		query.select(admissionRoot);
		where.add(admissionNotDeletedPredicate(cb, admissionRoot));
		Join<Admission, Patient> patient = admissionRoot.join("patient");
		if (terms != null) {
			where.addAll(termsPredicates(terms, cb, patient));
		}
		where.add(patientNotDeletedPredicate(cb, patient));
		where.addAll(dateRangePredicates(cb, admissionRoot, admissionRange, dischargeRange));
		query.where(cb.and(where.toArray(new Predicate[where.size()])));
		query.orderBy(cb.desc(patient.get("code")));

		return query;
	}

	private List<Predicate> dateRangePredicates(CriteriaBuilder cb,
										 Root<Admission> admissionRoot,
										 GregorianCalendar[] admissionRange,
										 GregorianCalendar[] dischargeRange) {
		List<Predicate> predicates = new ArrayList<Predicate>();

		if(admissionRange != null) {
			if (admissionRange.length == 2 && admissionRange[0] != null && admissionRange[1] != null) {
				predicates.add(
					cb.and(
						cb.greaterThanOrEqualTo(admissionRoot.<Comparable>get("admDate"), getBeginningOfDay(admissionRange[0])),
						cb.lessThan(admissionRoot.<Comparable>get("admDate"),  getBeginningOfNextDay(admissionRange[1])))
					);
			}
		}

		if (dischargeRange != null) {
			if (dischargeRange.length == 2 && dischargeRange[0] != null && dischargeRange[1] != null) {
				predicates.add(
					cb.and(
						cb.greaterThanOrEqualTo(admissionRoot.<Comparable>get("disDate"), getBeginningOfDay(dischargeRange[0])),
						cb.lessThan(admissionRoot.<Comparable>get("disDate"), getBeginningOfNextDay(dischargeRange[1]))
					));
			}
		}
		return predicates;
	}

	private Predicate admissionNotDeletedPredicate(CriteriaBuilder cb, Root<Admission> admissionRoot) {
		return cb.or(
			cb.equal(admissionRoot.get("deleted"), "N"),
			cb.isNull(admissionRoot.get("deleted"))
		);
	}

	private Predicate patientNotDeletedPredicate(CriteriaBuilder cb, Join<Admission, Patient> patient) {
		return cb.or(
			cb.equal(patient.get("deleted"), "N"),
			cb.isNull(patient.get("deleted"))
		);
	}

	private List<Predicate> termsPredicates(String[] terms, CriteriaBuilder cb, Join<Admission, Patient> patient) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		for(String term : terms) {
			predicates.add(wordExistsInOneOfPatientFields(term, cb, patient));
		}
		return predicates;
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
}