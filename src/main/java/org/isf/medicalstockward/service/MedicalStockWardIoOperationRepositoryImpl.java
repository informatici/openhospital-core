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