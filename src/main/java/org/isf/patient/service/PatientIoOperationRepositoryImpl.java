package org.isf.patient.service;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.isf.patient.model.Patient;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class PatientIoOperationRepositoryImpl implements PatientIoOperationRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	
	@SuppressWarnings("unchecked")	
	@Override
	public List<Patient> findByFieldsContainingWordsFromLiteral(String literal) {
		return this.entityManager.
				createQuery(buildSearchQuery(literal)).
					getResultList();
	}	

	
	private CriteriaQuery<Patient> buildSearchQuery(String regex) {
		String[] words = getWordsToSearchForInPatientsRepository(regex);
		return createQuerySearchingForPatientContainingGivenWordsInHisProperties(words);
	}
	
	private String[] getWordsToSearchForInPatientsRepository(String regex)	{
		String[] words = new String[0];

		if ((regex != null) && (!regex.equals(""))) {
			String string = regex.trim().toLowerCase();
			words = string.split(" ");
		}

		return words;
	}
		
	private CriteriaQuery<Patient> createQuerySearchingForPatientContainingGivenWordsInHisProperties(String[] words)	{
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
		Root<Patient> patientRoot = query.from(Patient.class);
		query.select(patientRoot);
		List<Predicate> where = new ArrayList<Predicate>();

		for (String word : words) {
			where.add(wordExistsInOneOfPatientFields(word, cb, patientRoot));
		}

		where.add(cb.or(
			cb.equal(patientRoot.get("deleted"), "N"),
			cb.isNull(patientRoot.get("deleted"))
		));

		query.where(cb.and(where.toArray(new Predicate[where.size()])));
		query.orderBy(cb.desc(patientRoot.get("code")));

		return query;
	}

	private Predicate wordExistsInOneOfPatientFields(String word, CriteriaBuilder cb, Root<Patient> root) {
		return cb.or(
			cb.like(cb.lower(root.get("code").as(String.class)), like(word)),
			cb.like(cb.lower(root.get("secondName").as(String.class)), like(word)),
			cb.like(cb.lower(root.get("firstName").as(String.class)), like(word)),
			cb.like(cb.lower(root.get("note").as(String.class)), like(word)),
			cb.like(cb.lower(root.get("taxCode").as(String.class)), like(word))
		);
	}

	private String like(String word) {
		return "%" + word + "";
	}
}