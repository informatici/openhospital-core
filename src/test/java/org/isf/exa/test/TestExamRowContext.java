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
package org.isf.exa.test;

import java.util.List;

import org.isf.exa.model.ExamRow;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;

public class TestExamRowContext 
{		
	private static List<ExamRow> savedExamRow;
		
		
	@SuppressWarnings("unchecked")
	public void saveAll(
			DbJpaUtil jpa) throws OHException 
    {	
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM EXAMROW", ExamRow.class, false);
		savedExamRow = (List<ExamRow>)jpa.getList();
		jpa.commitTransaction();
        		
        return;
    }
	
	public List<ExamRow> getAllSaved() throws OHException 
    {	        		
        return savedExamRow;
    }
	    
    @SuppressWarnings("unchecked")
    public void deleteNews(
    		DbJpaUtil jpa) throws OHException 
    {
		jpa.beginTransaction();			
		jpa.createQuery("SELECT * FROM EXAMROW", ExamRow.class, false);
		List<ExamRow> ExamRows = (List<ExamRow>)jpa.getList();
		for (ExamRow examRow: ExamRows) 
		{    		
			int index = savedExamRow.indexOf(examRow);
			
			
			if (index == -1)
			{				
				jpa.remove(examRow);
			}
	    }        
		jpa.commitTransaction();
		        
        return;
    } 
}
