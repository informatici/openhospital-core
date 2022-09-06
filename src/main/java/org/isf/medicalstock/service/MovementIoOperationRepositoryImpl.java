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
package org.isf.medicalstock.service;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.MedicalStockIoOperations.MovementOrder;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medtype.model.MedicalType;
import org.isf.ward.model.Ward;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MovementIoOperationRepositoryImpl implements MovementIoOperationRepositoryCustom {

	private static final String WARD = "ward";
	private static final String DATE = "date";
	private static final String CODE = "code";
	private static final String REF_NO = "refNo";
	private static final String MEDICAL = "medical";
	private static final String LOT = "lot";
	private static final String TYPE ="type";
	private static final String DESCRIPTION = "description";

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")	
	@Override
	public List<Integer> findMovementWhereDatesAndId(
			String wardId, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo) {
		return _getMovementWhereDatesAndId(wardId, dateFrom, dateTo);
	}

	@SuppressWarnings("unchecked")	
	@Override
	public List<Integer> findMovementWhereData(
			Integer medicalCode,
			String medicalType, 
			String wardId, 
			String movType,
			GregorianCalendar movFrom, 
			GregorianCalendar movTo,
			GregorianCalendar lotPrepFrom, 
			GregorianCalendar lotPrepTo,
			GregorianCalendar lotDueFrom, 
			GregorianCalendar lotDueTo) {
		return _getMovementWhereData(medicalCode, medicalType, wardId, movType, movFrom, movTo,
				lotPrepFrom, lotPrepTo, lotDueFrom, lotDueTo);
	}		

	@SuppressWarnings("unchecked")	
	@Override
	public List<Integer> findMovementForPrint(
			String medicalDescription,
			String medicalTypeCode, 
			String wardId, 
			String movType,
			GregorianCalendar movFrom, 
			GregorianCalendar movTo, 
			String lotCode,
			MovementOrder order) {
		return _getMovementForPrint(medicalDescription, medicalTypeCode, wardId, movType, movFrom, movTo,
				lotCode, order);
	}	

		
	private List<Integer> _getMovementWhereDatesAndId(
			String wardId, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo)
	{
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<Movement> root = query.from(Movement.class);
		query.select(root.<Integer>get(CODE));
		List<Predicate> predicates = new ArrayList<>();

		if ((dateFrom != null) && (dateTo != null))
		{
			predicates.add(builder.between(root.<GregorianCalendar>get(DATE), dateFrom, dateTo));
		}
		if (wardId != null && !wardId.equals("")) 
		{
			predicates.add(builder.equal(root.<Ward>get(WARD).<String>get(CODE), wardId));
		}

		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.desc(root.get(DATE)));
		orderList.add(builder.desc(root.get(REF_NO)));
		query.where(predicates.toArray(new Predicate[]{})).orderBy(orderList);
		return entityManager.createQuery(query).getResultList();
	}
	
	private List<Integer> _getMovementWhereData(
			Integer medicalCode,
			String medicalType, 
			String wardId, 
			String movType,
			GregorianCalendar movFrom, 
			GregorianCalendar movTo,
			GregorianCalendar lotPrepFrom, 
			GregorianCalendar lotPrepTo,
			GregorianCalendar lotDueFrom, 
			GregorianCalendar lotDueTo) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<Movement> root = query.from(Movement.class);
		query.select(root.<Integer>get(CODE));
		List<Predicate> predicates = new ArrayList<>();

		if (medicalCode != null) {
			predicates.add(builder.equal(root.<Medical>get(MEDICAL).<String>get(CODE), medicalCode));
		}
		if (medicalType != null) {
			predicates.add(builder.equal(root.<Medical>get(MEDICAL).<MedicalType>get(TYPE).<String>get(CODE), medicalType));
		}
		if ((movFrom != null) && (movTo != null)) {
			predicates.add(builder.between(root.<GregorianCalendar>get(DATE), movFrom, movTo));
		}
		if ((lotPrepFrom != null) && (lotPrepTo != null)) {
			predicates.add(builder.between(root.<Lot>get(LOT).<GregorianCalendar>get("preparationDate"), lotPrepFrom, lotPrepTo));
		}
		if ((lotDueFrom != null) && (lotDueTo != null)) {
			predicates.add(builder.between(root.<Lot>get(LOT).<GregorianCalendar>get("dueDate"), lotDueFrom, lotDueTo));
		}
		if (movType != null) {
			predicates.add(builder.equal(root.<MedicalType>get(TYPE).<String>get(CODE), movType));
		}
		if (wardId != null) {
			predicates.add(builder.equal(root.<Ward>get(WARD).<String>get(CODE), wardId));
		}

		List<Order> orderList = new ArrayList<>();
		orderList.add(builder.desc(root.get(DATE)));
		orderList.add(builder.desc(root.get(REF_NO)));
		query.where(predicates.toArray(new Predicate[]{})).orderBy(orderList);
		return entityManager.createQuery(query).getResultList();
	}	
	
	private List<Integer> _getMovementForPrint(
			String medicalDescription,
			String medicalTypeCode, 
			String wardId, 
			String movType,
			GregorianCalendar movFrom, 
			GregorianCalendar movTo, 
			String lotCode,
			MovementOrder order) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<Movement> root = query.from(Movement.class);
		query.select(root.<Integer>get(CODE));
		List<Predicate> predicates = new ArrayList<>();

		if (medicalDescription != null) {
			predicates.add(builder.equal(root.<Medical>get(MEDICAL).<String>get(DESCRIPTION), medicalDescription));
		}
		if (medicalTypeCode != null) {
			predicates.add(builder.equal(root.<Medical>get(MEDICAL).<MedicalType>get(TYPE).<String>get(CODE), medicalTypeCode));
		}
		if (lotCode != null) {
			predicates.add(builder.equal(root.<Ward>get(LOT).<String>get(CODE), lotCode));
		}
		if ((movFrom != null) && (movTo != null)) {
			predicates.add(builder.between(root.<GregorianCalendar>get(DATE), movFrom, movTo));
		}
		if (movType != null) {
			predicates.add(builder.equal(root.<MedicalType>get(TYPE).<String>get(CODE), movType));
		}
		if (wardId != null) {
			predicates.add(builder.equal(root.<Ward>get(WARD).<String>get(CODE), wardId));
		}
		List<Order> orderList = new ArrayList<>();
		switch (order) {
			case DATE:
				orderList.add(builder.desc(root.get(DATE)));
				orderList.add(builder.desc(root.get(REF_NO)));
				break;
			case WARD:
				orderList.add(builder.desc(root.get(REF_NO)));
				orderList.add(builder.desc(root.<Ward>get(WARD).get(DESCRIPTION)));
				break;
			case PHARMACEUTICAL_TYPE:
				orderList.add(builder.desc(root.get(REF_NO)));
				orderList.add(builder.asc(root.<Medical>get(MEDICAL).<MedicalType>get(TYPE)));
				orderList.add(builder.asc(root.<Medical>get(MEDICAL).<MedicalType>get(TYPE).get(DESCRIPTION)));
				break;
			case TYPE:
				orderList.add(builder.desc(root.get(REF_NO)));
				orderList.add(builder.asc(root.<MovementType>get(TYPE).<MedicalType>get(DESCRIPTION)));
				break;
		}
		query.where(predicates.toArray(new Predicate[]{})).orderBy(orderList);
		return entityManager.createQuery(query).getResultList();
	}
}
