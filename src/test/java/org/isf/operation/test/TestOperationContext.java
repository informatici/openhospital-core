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
package org.isf.operation.test;

import java.util.List;

import org.isf.operation.model.Operation;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;

public class TestOperationContext 
{		
	private static List<Operation> savedOperation;
		
		
	@SuppressWarnings("unchecked")
	public void saveAll(
			DbJpaUtil jpa) throws OHException 
    {	
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM OPERATION", Operation.class, false);
		savedOperation = (List<Operation>)jpa.getList();
		jpa.commitTransaction();
        		
        return;
    }
	
	public List<Operation> getAllSaved() throws OHException 
    {	        		
        return savedOperation;
    }
	    
    @SuppressWarnings("unchecked")
    public void deleteNews(
    		DbJpaUtil jpa) throws OHException 
    {
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM OPERATION", Operation.class, false);
		List<Operation> Operations = (List<Operation>)jpa.getList();
		for (Operation operation: Operations) 
		{    		
			int index = savedOperation.indexOf(operation);
			
			
			if (index == -1)
			{				
				jpa.remove(operation);
			}
	    }        
		jpa.commitTransaction();
		        
        return;
    } 
}
