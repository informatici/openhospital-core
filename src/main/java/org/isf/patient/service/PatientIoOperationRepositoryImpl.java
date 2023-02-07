/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.patient.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
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

	private String[] getWordsToSearchForInPatientsRepository(String regex) {
		String[] words = new String[0];

		if ((regex != null) && (!regex.equals(""))) {
			String string = regex.trim().toLowerCase();
			words = string.split(" ");
		}

		return words;
	}

	private CriteriaQuery<Patient> createQuerySearchingForPatientContainingGivenWordsInHisProperties(String[] words) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
		Root<Patient> patientRoot = query.from(Patient.class);
		query.select(patientRoot);
		List<Predicate> where = new ArrayList<>();

		for (String word : words) {
			where.add(wordExistsInOneOfPatientFields(word, cb, patientRoot));
		}

		where.add(cb.or(
				cb.equal(patientRoot.get("deleted"), 'N'),
				cb.isNull(patientRoot.get("deleted"))
		));

		query.where(cb.and(where.toArray(new Predicate[where.size()])));
		query.orderBy(cb.desc(patientRoot.get("code")));

		return query;
	}

	private Predicate wordExistsInOneOfPatientFields(String word, CriteriaBuilder cb, Root<Patient> root) {
		return cb.or(
				cb.like(cb.lower(root.get("code").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("firstName").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("secondName").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("city").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("address").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("telephone").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("note").as(String.class)), like(word)),
				cb.like(cb.lower(root.get("taxCode").as(String.class)), like(word))
		);
	}

	private String like(String word) {
		return "%" + word + "%";
	}

	public List<Patient> getPatientsByParams(Map<String, Object> params) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
		Root<Patient> patient = query.from(Patient.class);

		// Only not deleted patient
		Predicate deletedN = cb.equal(patient.get("deleted"), 'N');
		Predicate deletedNull = cb.isNull(patient.get("deleted"));
		Predicate notDeleted = cb.or(deletedN, deletedNull);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(notDeleted);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			Path<String> keyPath = patient.get(entry.getKey());

			if (entry.getKey().equals("birthDate")) {
				LocalDateTime birthDateFrom = (LocalDateTime) entry.getValue();
				LocalDateTime birthDateTo = birthDateFrom.plusDays(1);
				predicates.add(cb.between(keyPath.as(LocalDateTime.class), birthDateFrom, birthDateTo));
			} else {
				if (entry.getValue() instanceof String) {
					predicates.add(cb.like(cb.lower(keyPath), like(((String) entry.getValue()).toLowerCase())));
				}
			}
		}
		query.select(patient).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

		return entityManager.createQuery(query).getResultList();
	}

}
