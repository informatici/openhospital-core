package org.isf.opd.service;


import org.isf.disease.model.Disease;
import org.isf.distype.model.DiseaseType;
import org.isf.generaldata.MessageBundle;
import org.isf.opd.model.Opd;
import java.util.Date;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


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

    public List<Opd> findAllOpdWhereParamsWithPagination(
            String diseaseTypeCode,
            String diseaseCode,
            GregorianCalendar dateFrom,
            GregorianCalendar dateTo,
            int ageFrom,
            int ageTo,
            char sex,
            char newPatient, int pageNumber, int pageSize) {
        TypedQuery<Opd> opdTypedQuery = getOpdIdQuery(diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient);
        opdTypedQuery.setFirstResult(pageNumber * pageSize);
        opdTypedQuery.setMaxResults(pageSize);
        return opdTypedQuery.getResultList();
    }

    public TypedQuery<Opd> getOpdIdQuery(
            String diseaseTypeCode,
            String diseaseCode,
            GregorianCalendar dateFrom,
            GregorianCalendar dateTo,
            int ageFrom,
            int ageTo,
            char sex,
            char newPatient) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Opd> criteriaQuery = criteriaBuilder.createQuery(Opd.class);
        Metamodel m = entityManager.getMetamodel();
        EntityType<Opd> opdEntityType = m.entity(Opd.class);
        EntityType<Disease> diseaseEntityType = m.entity(Disease.class);

        Root<Opd> opd = criteriaQuery.from(Opd.class);
        criteriaQuery.select(opd);
        Join<Opd, Disease> diseaseJoin = opd.join(opdEntityType.getSingularAttribute("disease", Disease.class));
        Join<Disease, DiseaseType> diseaseTypeJoin = diseaseJoin.join(diseaseEntityType.getSingularAttribute("diseaseType", DiseaseType.class));
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (!(diseaseTypeCode.equals(MessageBundle.getMessage("angal.opd.alltype")))) {
            predicates.add(criteriaBuilder.equal(diseaseTypeJoin.get("code"), diseaseTypeCode));
        }
        if (!diseaseCode.equals(MessageBundle.getMessage("angal.opd.alldisease"))) {
            predicates.add(criteriaBuilder.equal(diseaseJoin.get("code"), diseaseCode));
        }
        if (ageFrom != 0 || ageTo != 0) {
			predicates.add(criteriaBuilder.between(opd.get("age"), ageFrom, ageTo));
		}
        if (sex != 'A') {
            predicates.add(criteriaBuilder.equal(opd.get("sex"), sex));
        }
        if (newPatient != 'A') {
            predicates.add(criteriaBuilder.equal(opd.get("newPatient"), newPatient));
        }
		predicates.add(criteriaBuilder.between(opd.get("visitDate"), dateFrom, dateTo));

		criteriaQuery.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(criteriaQuery);
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
			predicates.add(cb.equal(opd.join("disease").join("diseaseType").get("code"), diseaseTypeCode));
		}
		if (!diseaseCode.equals(MessageBundle.getMessage("angal.opd.alldisease"))) {
			predicates.add(cb.equal(opd.join("disease").get("code"), diseaseCode));
		}
		if (ageFrom != 0 || ageTo != 0) {
			predicates.add(cb.between(opd.<Integer> get("age"), ageFrom, ageTo));
		}
		if (sex != 'A') {
			predicates.add(cb.equal(opd.get("sex"), sex));
		}
		if (newPatient != 'A') {
			predicates.add(cb.equal(opd.get("newPatient"), newPatient));
		}
		predicates.add(cb.between(opd.<Date> get("visitDate"), dateFrom.getTime(), dateTo.getTime()));
		query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

		return entityManager.createQuery(query);
	}
}