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
package org.isf.medicalstock.test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.data.Offset.offset;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.medicalstock.model.Lot;
import org.isf.utils.exception.OHException;

public class TestLot 
{	 
	private String code = "123456";
	private LocalDateTime now = LocalDateTime.now();
	private LocalDateTime preparationDate = now.withMonth(1).withDayOfMonth(2);
	private LocalDateTime dueDate = now.withMonth(1).withDayOfMonth(2);
	private BigDecimal cost = new BigDecimal(10.10);
	    
			
	public Lot setup(
			boolean usingSet) throws OHException 
	{
		Lot lot;
	
				
		if (usingSet)
		{
			lot = new Lot();
			_setParameters(lot);
		}
		else
		{
			// Create Lot with all parameters 
			lot = new Lot(code, preparationDate, dueDate, cost);
		}
				    	
		return lot;
	}
	
	public void _setParameters(
			Lot lot) 
	{	
		lot.setCode(code);
		lot.setCost(cost);
		lot.setDueDate(dueDate);
		lot.setPreparationDate(preparationDate);
	}
	
	public void check(
			Lot lot) 
	{		
    	assertThat(lot.getCost().doubleValue()).isCloseTo(cost.doubleValue(), offset(0.0));
    	assertThat(lot.getDueDate()).isCloseTo(dueDate, within(1, ChronoUnit.SECONDS));
    	assertThat(lot.getPreparationDate()).isCloseTo(preparationDate, within(1, ChronoUnit.SECONDS));
	}
}
