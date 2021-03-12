/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.admission.service;

import static org.isf.utils.time.TimeTools.getBeginningOfDay;
import static org.isf.utils.time.TimeTools.getBeginningOfNextDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AdmissionIoOperationRepositoryImpl implements AdmissionIoOperationRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Optional<Admission> findOneByPatientAndDateRanges(Patient patient, GregorianCalendar[] admissionRange,
															 GregorianCalendar[] dischargeRange) {
		return this.entityManager.
			createQuery(createQueryToSearchByPatientAndDates(patient, admissionRange, dischargeRange)).
			getResultList().stream()
			.findFirst();
	}

	private CriteriaQuery<Admission> createQueryToSearchByPatientAndDates(Patient patient, GregorianCalendar[] admissionRange, GregorianCalendar[] dischargeRange) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Admission> query = cb.createQuery(Admission.class);
		Root<Admission> admissionRoot = query.from(Admission.class);
		List<Predicate> where = new ArrayList<>();

		query.select(admissionRoot);
		where.add(admissionNotDeletedPredicate(cb, admissionRoot));
		Join<Admission, Patient> patientJoin = admissionRoot.join("patient");
		where.add(patientEqualPredicate(cb, patientJoin, patient));
		where.add(patientNotDeletedPredicate(cb, patientJoin));
		where.addAll(dateRangePredicates(cb, admissionRoot, admissionRange, dischargeRange));
		query.where(cb.and(where.toArray(new Predicate[where.size()])));
		query.orderBy(cb.desc(patientJoin.get("code")));

		return query;
	}

	private List<Predicate> dateRangePredicates(CriteriaBuilder cb,
										 Root<Admission> admissionRoot,
										 GregorianCalendar[] admissionRange,
										 GregorianCalendar[] dischargeRange) {
		List<Predicate> predicates = new ArrayList<>();

		if(admissionRange != null) {
			if (admissionRange.length == 2 && admissionRange[0] != null && admissionRange[1] != null) {
				predicates.add(
					cb.and(
						cb.greaterThanOrEqualTo(admissionRoot.<Date>get("admDate"), getBeginningOfDay(admissionRange[0]).getTime()),
						cb.lessThan(admissionRoot.<Date>get("admDate"),  getBeginningOfNextDay(admissionRange[1]).getTime()))
					);
			}
		}

		if (dischargeRange != null) {
			if (dischargeRange.length == 2 && dischargeRange[0] != null && dischargeRange[1] != null) {
				predicates.add(
					cb.and(
						cb.greaterThanOrEqualTo(admissionRoot.<Date>get("disDate"), getBeginningOfDay(dischargeRange[0]).getTime()),
						cb.lessThan(admissionRoot.<Date>get("disDate"), getBeginningOfNextDay(dischargeRange[1]).getTime())
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

	private Predicate patientEqualPredicate(CriteriaBuilder cb, Join<Admission, Patient> patientJoin, Patient patient) {
		return cb.equal(patientJoin.get("code"), patient.getCode());
	}
}
