package org.isf.opd.service;


import org.isf.disease.model.Disease;
import org.isf.distype.model.DiseaseType;
import org.isf.generaldata.MessageBundle;
import org.isf.opd.model.Opd;
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
	public List<Integer> findAllOpdWhereParams(
			String diseaseTypeCode,
			String diseaseCode, 
			GregorianCalendar dateFrom,
			GregorianCalendar dateTo,
			int ageFrom, 
			int ageTo,
			char sex,
			char newPatient) {
		return this.entityManager.
				createNativeQuery(_getOpdQuery(
						diseaseTypeCode, diseaseCode, dateFrom, dateTo,
						ageFrom, ageTo, sex, newPatient)).
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

	public String _getOpdQuery(
			String diseaseTypeCode,
			String diseaseCode, 
			GregorianCalendar dateFrom,
			GregorianCalendar dateTo,
			int ageFrom, 
			int ageTo,
			char sex,
			char newPatient)
	{	
		String query = "SELECT OPD_ID FROM OPD LEFT JOIN PATIENT ON OPD_PAT_ID = PAT_ID LEFT JOIN DISEASE ON OPD_DIS_ID_A = DIS_ID_A LEFT JOIN DISEASETYPE ON DIS_DCL_ID_A = DCL_ID_A WHERE 1";
		if (!(diseaseTypeCode.equals(MessageBundle.getMessage("angal.opd.alltype")))) {
			query += " AND DIS_DCL_ID_A = \"" + diseaseTypeCode + "\"";
		}
		if(!diseaseCode.equals(MessageBundle.getMessage("angal.opd.alldisease"))) {
			query += " AND DIS_ID_A = \"" + diseaseCode + "\"";
		}
		if (ageFrom != 0 || ageTo != 0) {
			query += " AND OPD_AGE BETWEEN \"" + ageFrom + "\" AND \"" + ageTo + "\"";
		}
		if (sex != 'A') {
			query += " AND OPD_SEX =  \"" + sex + "\"";
		}
		if (newPatient != 'A') {
			query += " AND OPD_NEW_PAT =  \"" + newPatient + "\"";
		}
		query += " AND OPD_DATE_VIS BETWEEN  \"" + _convertToSQLDateLimited(dateFrom) + "\" AND \"" + _convertToSQLDateLimited(dateTo) + "\"";

		return query;
	}
		
	/**
	 * return a String representing the date in format <code>yyyy-MM-dd</code>
	 * 
	 * @param date
	 * @return the date in format <code>yyyy-MM-dd</code>
	 */
	private String _convertToSQLDateLimited(GregorianCalendar date) 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
		return sdf.format(date.getTime());
	}
}