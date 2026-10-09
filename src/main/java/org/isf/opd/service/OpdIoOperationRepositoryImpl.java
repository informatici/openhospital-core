/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.opd.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.isf.opd.model.Opd;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OpdIoOperationRepositoryImpl implements OpdIoOperationRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")	
	@Override
	public List<Opd> findAllOpdWhereParams(
			Ward ward,
			String diseaseTypeCode,
			String diseaseCode,
			LocalDate dateFrom,
			LocalDate dateTo,
			int ageFrom,
			int ageTo,
			char sex,
			char newPatient,
			String user) {
		return getOpdQuery(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user).getResultList();
	}	

	private TypedQuery<Opd> getOpdQuery(
			Ward ward, 
			String diseaseTypeCode,
			String diseaseCode,
			LocalDate dateFrom,
			LocalDate dateTo,
			int ageFrom, 
			int ageTo,
			char sex,
			char newPatient,
			String user) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Opd> query = cb.createQuery(Opd.class);
		Root<Opd> opd = query.from(Opd.class);
		query.select(opd);
		List<Predicate> predicates = buildOpdPredicates(cb, opd, ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user);
		query.where(cb.and(predicates.toArray(new Predicate[0])));

		return entityManager.createQuery(query);
	}

	@Override
	public Page<Opd> findAllOpdWhereParamsPageable(
			Ward ward,
			String diseaseTypeCode,
			String diseaseCode,
			LocalDate dateFrom,
			LocalDate dateTo,
			int ageFrom,
			int ageTo,
			char sex,
			char newPatient,
			String user,
			Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Opd> query = cb.createQuery(Opd.class);
		Root<Opd> opd = query.from(Opd.class);
		query.select(opd);
		List<Predicate> predicates = buildOpdPredicates(cb, opd, ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user);
		query.where(cb.and(predicates.toArray(new Predicate[0])));
		// a total order is required so DB-level LIMIT/OFFSET paging does not drop or duplicate rows across pages
		query.orderBy(cb.asc(opd.get("prog_year")), cb.asc(opd.get("code")));
		TypedQuery<Opd> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());
		List<Opd> content = typedQuery.getResultList();
		long total = countOpdWhereParams(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user);
		return new PageImpl<>(content, pageable, total);
	}

	private long countOpdWhereParams(
			Ward ward,
			String diseaseTypeCode,
			String diseaseCode,
			LocalDate dateFrom,
			LocalDate dateTo,
			int ageFrom,
			int ageTo,
			char sex,
			char newPatient,
			String user) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<Opd> opd = query.from(Opd.class);
		query.select(cb.count(opd));
		List<Predicate> predicates = buildOpdPredicates(cb, opd, ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user);
		query.where(cb.and(predicates.toArray(new Predicate[0])));
		return entityManager.createQuery(query).getSingleResult();
	}

	private List<Predicate> buildOpdPredicates(
			CriteriaBuilder cb,
			Root<Opd> opd,
			Ward ward,
			String diseaseTypeCode,
			String diseaseCode,
			LocalDate dateFrom,
			LocalDate dateTo,
			int ageFrom,
			int ageTo,
			char sex,
			char newPatient,
			String user) {
		List<Predicate> predicates = new ArrayList<>();
		if (ward != null) {
			predicates.add(
					cb.equal(opd.join("ward").get("code"), ward.getCode())
			);
		}
		if (diseaseTypeCode != null && !diseaseTypeCode.equals("")) {
			predicates.add(
					cb.equal(opd.join("disease").join("diseaseType").get("code"), diseaseTypeCode)
			);
		}
		if (diseaseCode != null && !diseaseCode.equals("")) {
			predicates.add(
					cb.equal(opd.join("disease").get("code"), diseaseCode)
			);
		}
		if (ageFrom != 0 || ageTo != 0) {
			predicates.add(
					cb.between(opd.<Integer>get("age"), ageFrom, ageTo)
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
		if (user != null) {
			predicates.add(
					cb.equal(opd.get("userID"), user)
			);
		}
		predicates.add(
				cb.between(opd.<LocalDateTime>get("date"), dateFrom.atStartOfDay(), dateTo.plusDays(1).atStartOfDay())
		);
		return predicates;
	}

}
