package org.isf.medstockmovtype.test;


import java.util.List;

import org.isf.medstockmovtype.model.MovementType;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;

public class TestMovementTypeContext 
{		
	private static List<MovementType> savedMovementType;
		
		
	@SuppressWarnings("unchecked")
	public void saveAll(
			DbJpaUtil jpa) throws OHException 
    {	
		jpa.beginTransaction();			
		jpa.createQuery("select movType from MovementType movType", MovementType.class, true);
		savedMovementType = (List<MovementType>)jpa.getList();
		jpa.commitTransaction();
        		
        return;
    }
	
	public List<MovementType> getAllSaved() throws OHException 
    {	        		
        return savedMovementType;
    }
	    
    @SuppressWarnings("unchecked")
    public void deleteNews(
    		DbJpaUtil jpa) throws OHException 
    {
		jpa.beginTransaction();			
		jpa.createQuery("select movType from MovementType movType", MovementType.class, true);
		List<MovementType> MovementTypes = (List<MovementType>)jpa.getList();
		for (MovementType movementType: MovementTypes) 
		{    		
			int index = savedMovementType.indexOf(movementType);
			
			
			if (index == -1)
			{				
				jpa.remove(movementType);
			}
	    }        
		jpa.commitTransaction();
		        
        return;
    } 
}
