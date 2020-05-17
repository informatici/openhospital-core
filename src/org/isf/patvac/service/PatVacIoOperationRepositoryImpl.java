package org.isf.patvac.service;


import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.isf.patvac.model.PatientVaccine;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class PatVacIoOperationRepositoryImpl implements PatVacIoOperationRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	
	@SuppressWarnings("unchecked")	
	@Override
	public List<PatientVaccine> findAllByCodesAndDatesAndSexAndAges(
			String vaccineTypeCode, 
			String vaccineCode, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo, 
			char sex, 
			int ageFrom, 
			int ageTo) {
		return this.entityManager.
				createQuery(_getPatientVaccineQuery(
						vaccineTypeCode, vaccineCode, dateFrom, dateTo,
						sex, ageFrom, ageTo)).
					getResultList();
	}	

	private CriteriaQuery<PatientVaccine> _getPatientVaccineQuery(
			String vaccineTypeCode, 
			String vaccineCode, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo, 
			char sex, 
			int ageFrom, 
			int ageTo) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PatientVaccine> query = cb.createQuery(PatientVaccine.class);
		Root<PatientVaccine> pvRoot = query.from(PatientVaccine.class);

		query.select(pvRoot);
		if (dateFrom != null) {
			query.where(
				cb.greaterThanOrEqualTo(pvRoot.<Comparable>get("vaccineDate"), dateFrom)
			);
		}
		if (dateTo != null) {
			query.where(
				cb.lessThanOrEqualTo(pvRoot.<Comparable>get("vaccineDate"), dateTo)
			);
		}
		if (vaccineTypeCode != null) {
			query.where(
				cb.equal(pvRoot.join("vaccine").get("code"), vaccineTypeCode)
			);
		}
		if (vaccineCode != null) {
			query.where(
				cb.equal(pvRoot.join("vaccine").get("code"), vaccineCode)
			);
		}
		if (sex != 'A') {
			query.where(
				cb.equal(pvRoot.join("patient").get("sex"), sex)
			);
		}
		if (ageFrom != 0 || ageTo != 0) {
			query.where(
				cb.between(pvRoot.join("patient").<Comparable>get("age"), ageFrom, ageTo)
			);
		}
		query.orderBy(cb.desc(pvRoot.get("vaccineDate")), cb.asc(pvRoot.get("code")));

		return query;
	}
}