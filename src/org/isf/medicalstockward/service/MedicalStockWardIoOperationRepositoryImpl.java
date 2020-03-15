package org.isf.medicalstockward.service;


import org.isf.medicalstock.service.QueryParameterContainer;
import org.isf.utils.db.DbJpaUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.GregorianCalendar;
import java.util.List;


@Transactional
public class MedicalStockWardIoOperationRepositoryImpl implements MedicalStockWardIoOperationRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	
	@SuppressWarnings("unchecked")	
	@Override
	public List<Integer> findAllWardMovement(
			String wardId, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo) {
		String qlString = _getWardMovementQuery(wardId, dateFrom, dateTo);
		QueryParameterContainer parameterContainer = QueryParameterContainer.builder()
				.withWardId(wardId)
				.withDateFromTo(dateFrom, dateTo)
				.build();
		Query query = entityManager.createQuery(qlString);
		DbJpaUtil.addJpqlParametersToQueryByParameterContainer(qlString, query, parameterContainer);
		return query.getResultList();
	}	
		

	public String _getWardMovementQuery(
			String wardId, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo)
	{	
		StringBuilder query = new StringBuilder();
		boolean firstParam = true;
		
		
		query.append("select movWard.code from MovementWard movWard ");
		if (wardId!=null || dateFrom!=null || dateTo!=null) 
		{
			query.append("where ");
		}
		if (wardId != null && !wardId.equals("")) 
		{
			firstParam = false;
			query.append("movWard.ward.code=:wardId ");
		}
		if ((dateFrom != null) && (dateTo != null)) 
		{
			if (!firstParam)
			{
				query.append("and ");
			}
			query.append("movWard.date between :dateFrom and :dateTo ");
		}
		query.append(" order by movWard.date asc");
		
		String result = query.toString();

		return result;
	}
}