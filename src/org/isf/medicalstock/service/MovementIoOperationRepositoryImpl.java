package org.isf.medicalstock.service;


import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.isf.medicalstock.service.MedicalStockIoOperations.MovementOrder;
import org.isf.utils.db.DbJpaUtil;
import org.springframework.transaction.annotation.Transactional;

import static org.isf.medicalstock.service.QueryParameterContainer.*;


@Transactional
public class MovementIoOperationRepositoryImpl implements MovementIoOperationRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")	
	@Override
	public List<Integer> findMovementWhereDatesAndId(
			String wardId, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo) {
		String qlString = _getMovementWhereDatesAndId(wardId, dateFrom, dateTo);
		Query query = this.entityManager.createQuery(qlString);
		QueryParameterContainer parameterContainer = QueryParameterContainer.builder()
				.withMovementFromTo(dateFrom, dateTo)
				.withWardId(wardId)
				.build();
		DbJpaUtil.addJpqlParametersToQueryByParameterContainer(qlString, query, parameterContainer);
		return query.getResultList();
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
		String qlString = _getMovementWhereData(medicalCode, medicalType, wardId, movType, movFrom, movTo,
				lotPrepFrom, lotPrepTo, lotDueFrom, lotDueTo);
		Query query = this.entityManager.createQuery(qlString);
		QueryParameterContainer parameterContainer = QueryParameterContainer.builder()
				.withMedicalCode(medicalCode)
				.withMedicalType(medicalType)
				.withWardId(wardId)
				.withMovementType(movType)
				.withMovementFromTo(movFrom, movTo)
				.withLotPrepFromTo(lotPrepFrom, lotPrepTo)
				.withLotDueFromTo(lotDueFrom, lotDueTo)
				.build();
		DbJpaUtil.addJpqlParametersToQueryByParameterContainer(qlString, query, parameterContainer);
		return query.getResultList();
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
		String qlString = _getMovementForPrint(medicalDescription, medicalTypeCode, wardId, movType, movFrom, movTo,
				lotCode, order);
		Query query = this.entityManager.createQuery(qlString);
		QueryParameterContainer parameterContainer = QueryParameterContainer.builder()
				.withMedicalDescription(medicalDescription)
				.withMedicalType(medicalTypeCode)
				.withWardId(wardId)
				.withMovementType(movType)
				.withMovementFromTo(movFrom, movTo)
				.withLotCode(lotCode)
				.build();
		DbJpaUtil.addJpqlParametersToQueryByParameterContainer(qlString, query, parameterContainer);
		return query.getResultList();
	}	

		
	private String _getMovementWhereDatesAndId(
			String wardId, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo)
	{	
		String query = "select mov.code from Movement mov " +
				"join mov.type movtype " +
				"join mov.medical med " +
				"join med.type medtype " +
				"left join mov.lot lot " +
				"left join mov.ward ward " +
				"left join mov.supplier sup ";
		boolean dateQuery = false;
		
		
		if ((dateFrom != null) && (dateTo != null)) 
		{
			query += "where mov.date between :movFrom and :movTo ";
			dateQuery = true;
		}
		if (wardId != null && !wardId.equals("")) 
		{
			if (dateQuery) 
			{
				query += "and ";
			}
			else 
			{
				query += "where ";
			}
			query += "ward.code=:wardId ";
		}
		query += "order by mov.date desc, mov.refNo desc";

		return query;
	}
	
	private String _getMovementWhereData(
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
		String query = "select mov.code from Movement mov " +
				"join mov.type movtype " +
				"join mov.medical med " +
				"join med.type medtype " +
				"left join mov.ward ward " +
				"left join mov.lot lot " +
				"left join mov.supplier sup " +
				"where ";
		boolean paramQuery = false;
				

		if ((medicalCode != null) || (medicalType != null)) 
		{
			if (medicalCode == null) 
			{
				query += "(medtype.code=:medicalType) ";
				paramQuery = true;
			} else if (medicalType == null)
			{
				query += "(med.code=:medicalCode) ";
				paramQuery = true;
			}
		}
		if ((movFrom != null) && (movTo != null)) 
		{
			if (paramQuery) 
			{
				query += "and ";
			}
			query += "(mov.date between :movFrom and :movTo) ";
			paramQuery = true;
		}
		if ((lotPrepFrom != null) && (lotPrepTo != null)) 
		{
			if (paramQuery) 
			{
				query += "and ";
			}
			query += "(lot.preparationDate between :lotPrepFrom and :lotPrepTo) ";
			paramQuery = true;
		}
		if ((lotDueFrom != null) && (lotDueTo != null)) 
		{
			if (paramQuery) 
			{
				query += "and ";
			}
			query += "(lot.dueDate between :lotDueFrom and :lotDueTo) ";
			paramQuery = true;
		}
		if (movType != null) {
			if (paramQuery) 
			{
				query += "and ";
			}
			query += "(movtype.code=:movType) ";
			paramQuery = true;
		}
		if (wardId != null) 
		{
			if (paramQuery) 
			{
				query += "and ";
			}
			query += "(ward.code=:wardId) ";
		}
		query += " order by mov.date desc, mov.refNo desc";
		
		return query;
	}	
	
	private String _getMovementForPrint(
			String medicalDescription,
			String medicalTypeCode, 
			String wardId, 
			String movType,
			GregorianCalendar movFrom, 
			GregorianCalendar movTo, 
			String lotCode,
			MovementOrder order) {
		String query = "";
		boolean paramQuery = false;
		
		
		query = "select mov.code from Movement mov " +
				"join mov.type movtype " +
				"join mov.medical med " +
				"left join mov.ward ward " +
				"left join mov.lot lot " +
				"left join mov.supplier sup " +
				"where ";

		if ((medicalDescription != null) || (medicalTypeCode != null)) 
		{
			if (medicalDescription == null) 
			{
				query += "(medtype.code=:medicalType) ";
				paramQuery = true;
			} 
			else if (medicalTypeCode == null) 
			{
				query += "(med.description like :medicalDescription) ";
				paramQuery = true;
			}
		}
		if (lotCode != null) 
		{
			if (paramQuery) 
			{
				query += "and ";
			}
			query += "(lot.code like :lotCode) ";
			paramQuery = true;
		}
		if ((movFrom != null) && (movTo != null)) 
		{
			if (paramQuery) 
			{
				query += "and ";
			}
			query += "(mov.date between :movFrom and :movTo) ";
			paramQuery = true;
		}
		if (movType != null) 
		{
			if (paramQuery) 
			{
				query += "and ";
			}
			query += "(movtype.code=:movType) ";
			paramQuery = true;
		}
		if (wardId != null) 
		{
			if (paramQuery) 
			{
				query += "and ";
			}
			query += "(ward.code=:wardId) ";
		}
		switch (order) {
			case DATE:
				query += "order by mov.date desc, mov.refNo desc";
				break;
			case WARD:
				query += "order by mov.refNo desc, ward.description desc";
				break;
			case PHARMACEUTICAL_TYPE:
				query += "order by mov.refNo desc, med.type, med.description";
				break;
			case TYPE:
				query += "order by mov.refNo desc, movtype.description";
				break;
		}
		
		return query;
	}
}