/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.opd.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.data.jpa.repository.Query;
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
			LocalDate dateFrom,
			LocalDate dateTo,
			int ageFrom,
			int ageTo,
			char sex,
			char newPatient) {
		return getOpdQuery(diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient).getResultList();
	}	

	private TypedQuery<Opd> getOpdQuery(
			String diseaseTypeCode,
			String diseaseCode,
			LocalDate dateFrom,
			LocalDate dateTo,
			int ageFrom, 
			int ageTo,
			char sex,
			char newPatient) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Opd> query = cb.createQuery(Opd.class);
		Root<Opd> opd = query.from(Opd.class);
		List<Predicate> predicates = new ArrayList<>();

		query.select(opd);
		if (!(diseaseTypeCode.equals(MessageBundle.getMessage("angal.common.alltypes.txt")))) {
			predicates.add(
					cb.equal(opd.join("disease").join("diseaseType").get("code"), diseaseTypeCode)
			);
		}
		if (!diseaseCode.equals(MessageBundle.getMessage("angal.opd.alldiseases.txt"))) {
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
		predicates.add(
				cb.between(opd.<LocalDateTime>get("date"), dateFrom.atStartOfDay(), dateTo.plusDays(1).atStartOfDay())
		);
		query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

		return entityManager.createQuery(query);
	}

}
