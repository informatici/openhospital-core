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
package org.isf.lab.test;

import static org.junit.Assert.assertEquals;

import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryRow;
import org.isf.utils.exception.OHException;

public class TestLaboratoryRow 
{	
	private Integer code = 0;
    private String description = "TestDescription";
    
			
	public LaboratoryRow setup(
			Laboratory laboratory,
			boolean usingSet) throws OHException 
	{
		LaboratoryRow laboratoryRow;
	
				
		if (usingSet)
		{
			laboratoryRow = new LaboratoryRow();
			_setParameters(laboratoryRow, laboratory);
		}
		else
		{
			// Create LaboratoryRow with all parameters 
			laboratoryRow = new LaboratoryRow(laboratory, description);
		}
				    	
		return laboratoryRow;
	}
	
	public void _setParameters(
			LaboratoryRow laboratoryRow,
			Laboratory laboratory) 
	{	
		laboratoryRow.setDescription(description);
		laboratoryRow.setLabId(laboratory);
		
		return;
	}
	
	public void check(
			LaboratoryRow laboratoryRow) 
	{		
    	assertEquals(description, laboratoryRow.getDescription());
		
		return;
	}
}
