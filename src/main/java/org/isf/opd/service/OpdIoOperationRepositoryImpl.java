package org.isf.opd.service;


import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.isf.generaldata.MessageBundle;
import org.isf.opd.model.Opd;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class OpdIoOperationRepositoryImpl implements OpdIoOperationRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")	
	@Override
	public List<Opd> findAllOpdWhereParams(
			String diseaseTypeCode,
			String diseaseCode, 
			GregorianCalendar dateFrom,
			GregorianCalendar dateTo,
			int ageFrom, 
			int ageTo,
			char sex,
			char newPatient) {
		return _getOpdQuery(
						diseaseTypeCode, diseaseCode, dateFrom, dateTo,
						ageFrom, ageTo, sex, newPatient).
					getResultList();
	}	

	private TypedQuery<Opd> _getOpdQuery(
			String diseaseTypeCode,
			String diseaseCode, 
			GregorianCalendar dateFrom,
			GregorianCalendar dateTo,
			int ageFrom, 
			int ageTo,
			char sex,
			char newPatient) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Opd> query = cb.createQuery(Opd.class);
		Root<Opd> opd = query.from(Opd.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		query.select(opd);
		if (!(diseaseTypeCode.equals(MessageBundle.getMessage("angal.opd.alltype")))) {
			predicates.add(
				cb.equal(opd.join("disease").join("diseaseType").get("code"), diseaseTypeCode)
			);
		}
		if(!diseaseCode.equals(MessageBundle.getMessage("angal.opd.alldisease"))) {
			predicates.add(
				cb.equal(opd.join("disease").get("code"), diseaseCode)
			);
		}
		if (ageFrom != 0 || ageTo != 0) {
			predicates.add(
				cb.between(opd.<Comparable>get("age"), ageFrom, ageTo)
			);
		}
		if (sex != 'A') {
			predicates.add(
				cb.equal(opd.get("sex"), sex)
			);
		}
		if (newPatient != 'A') {
			predicates.add(
				cb.equal(opd.get("newPatient"), newPatient)
			);
		}
		predicates.add(
			cb.between(opd.<Comparable>get("visitDate"), dateFrom, dateTo)
		);
		query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

		return entityManager.createQuery(query);
	}
}