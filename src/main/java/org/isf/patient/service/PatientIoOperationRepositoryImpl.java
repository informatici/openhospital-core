package org.isf.patient.service;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;


@Transactional
public class PatientIoOperationRepositoryImpl implements PatientIoOperationRepositoryCustom {
	
	@PersistenceContext
	private EntityManager entityManager;

	
	@SuppressWarnings("unchecked")	
	@Override
	public List<Integer> findAllByHeightAndWeight(String regex) {
		return this.entityManager.
				createNativeQuery(_getPatientsWithHeightAndWeightQueryByRegex(regex)).
					getResultList();
	}	

	
	private String _getPatientsWithHeightAndWeightQueryByRegex(
			String regex) 
	{
		String[] words = _getPatientsWithHeightAndWeightRegex(regex);
		String query = _getPatientsWithHeightAndWeightQuery(words);
		
		
		return query;
	}
	
	private String[] _getPatientsWithHeightAndWeightRegex(
			String regex) 
	{
		String string = null;
		String[] words = new String[0];
		
		
		if ((regex != null) 
			&& (!regex.equals(""))) 
		{
			string = regex.trim().toLowerCase();
			words = string.split(" ");
		}

		return words;
	}
		
	private String _getPatientsWithHeightAndWeightQuery(
			String[] words)
	{
		// FIXME: not sure why need to LEFT JOIN with PATIENTEXAMINATION here. Seems unnecessary.
		StringBuilder queryBld = new StringBuilder(
				"SELECT PAT_ID FROM PATIENT LEFT JOIN (SELECT PEX_PAT_ID FROM PATIENTEXAMINATION) "
				+ "AS HW ON PAT_ID = HW.PEX_PAT_ID WHERE (PAT_DELETED='N' or PAT_DELETED is null) ");

        for (final String word : words) {
            queryBld.append("AND CONCAT_WS(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
            queryBld.append("LIKE '%").append(word).append("%'");
        }
		queryBld.append(" ORDER BY PAT_ID DESC");

		return queryBld.toString();
	}
}