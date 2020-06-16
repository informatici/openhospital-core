package org.isf.pricesothers.test;


import java.util.List;

import org.isf.pricesothers.model.PricesOthers;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;

public class TestPricesOthersContext 
{		
	private static List<PricesOthers> savedPricesOthers;
		
		
	@SuppressWarnings("unchecked")
	public void saveAll(
			DbJpaUtil jpa) throws OHException 
    {	
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM PRICESOTHERS", PricesOthers.class, false);
		savedPricesOthers = (List<PricesOthers>)jpa.getList();
		jpa.commitTransaction();
        		
        return;
    }
	
	public List<PricesOthers> getAllSaved() throws OHException 
    {	        		
        return savedPricesOthers;
    }
	    
    @SuppressWarnings("unchecked")
    public void deleteNews(
    		DbJpaUtil jpa) throws OHException 
    {
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM PRICESOTHERS", PricesOthers.class, false);
		List<PricesOthers> pricesOthers = (List<PricesOthers>)jpa.getList();
		for (PricesOthers price: pricesOthers)
		{    		
			int index = savedPricesOthers.indexOf(price);
			
			
			if (index == -1)
			{				
				jpa.remove(price);
			}
	  }        
		jpa.commitTransaction();
		        
        return;
    } 
}
