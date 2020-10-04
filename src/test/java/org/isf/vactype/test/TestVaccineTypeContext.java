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
package org.isf.vactype.test;

import java.util.List;

import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.vactype.model.VaccineType;

public class TestVaccineTypeContext 
{		
	private static List<VaccineType> savedVaccineType;
		
		
	@SuppressWarnings("unchecked")
	public void saveAll(
			DbJpaUtil jpa) throws OHException 
    {	
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM VACCINETYPE", VaccineType.class, false);
		savedVaccineType = (List<VaccineType>)jpa.getList();
		jpa.commitTransaction();
        		
        return;
    }
	
	public List<VaccineType> getAllSaved() throws OHException 
    {	        		
        return savedVaccineType;
    }
	    
    @SuppressWarnings("unchecked")
    public void deleteNews(
    		DbJpaUtil jpa) throws OHException 
    {
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM VACCINETYPE", VaccineType.class, false);
		List<VaccineType> VaccineTypes = (List<VaccineType>)jpa.getList();
		for (VaccineType vaccineType: VaccineTypes) 
		{    		
			int index = savedVaccineType.indexOf(vaccineType);
			
			
			if (index == -1)
			{				
				jpa.remove(vaccineType);
			}
	    }        
		jpa.commitTransaction();
		        
        return;
    } 
}
