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
package org.isf.medicalstockward.service;

import org.isf.medicalstockward.model.MovementWard;
import org.isf.ward.model.Ward;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Transactional
public class MedicalStockWardIoOperationRepositoryImpl implements MedicalStockWardIoOperationRepositoryCustom {

	private static final String WARD = "ward";
	private static final String DATE = "date";
	private static final String CODE = "code";

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")	
	@Override
	public List<Integer> findAllWardMovement(
			String wardId, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo) {
		return _getWardMovementQuery(wardId, dateFrom, dateTo);
	}	
		

	public List<Integer> _getWardMovementQuery(
			String wardId, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo)
	{
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<MovementWard> root = query.from(MovementWard.class);
		query.select(root.<Integer>get(CODE));
		List<Predicate> predicates = new ArrayList<Predicate>();

		if (wardId != null && !wardId.equals("")) {
			predicates.add(builder.equal(root.<Ward>get(WARD).<String>get(CODE), wardId));
		}
		if ((dateFrom != null) && (dateTo != null)) {
			predicates.add(builder.between(root.<GregorianCalendar>get(DATE), dateFrom, dateTo));
		}

		List<Order> orderList = new ArrayList<Order>();
		orderList.add(builder.asc(root.get(DATE)));

		query.where(predicates.toArray(new Predicate[]{})).orderBy(orderList);
		return entityManager.createQuery(query).getResultList();
	}
}