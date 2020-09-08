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
package org.isf.pricesothers.test;

import static org.junit.Assert.assertEquals;

import org.isf.pricesothers.model.PricesOthers;
import org.isf.utils.exception.OHException;

public class TestPricesOthers 
{	
    private String Code = "TestCode";
    private String Description = "TestDescription";
	private boolean opdInclude = true;
	private boolean ipdInclude = false;
	private boolean daily = true;
	private boolean discharge = false;
	private boolean undefined = true;
    
			
	public PricesOthers setup(
			boolean usingSet) throws OHException 
	{
		PricesOthers pricesOthers;
	
				
		if (usingSet)
		{
			pricesOthers = new PricesOthers();
			_setParameters(pricesOthers);
		}
		else
		{
			// Create PricesOthers with all parameters 
			pricesOthers = new PricesOthers(0, Code, Description, opdInclude, ipdInclude, daily, discharge, undefined);
		}
				    	
		return pricesOthers;
	}
	
	public void _setParameters(
			PricesOthers pricesOthers) 
	{	
		pricesOthers.setCode(Code);
		pricesOthers.setDescription(Description);
		pricesOthers.setDaily(daily);
		pricesOthers.setDischarge(discharge);
		pricesOthers.setIpdInclude(ipdInclude);
		pricesOthers.setOpdInclude(opdInclude);
		pricesOthers.setUndefined(undefined);		
		
		return;
	}
	
	public void check(
			PricesOthers pricesOthers) 
	{		
    	assertEquals(Code, pricesOthers.getCode());
    	assertEquals(Description, pricesOthers.getDescription());
    	assertEquals(daily, pricesOthers.isDaily());
    	assertEquals(discharge, pricesOthers.isDischarge());
    	assertEquals(ipdInclude, pricesOthers.isIpdInclude());
    	assertEquals(opdInclude, pricesOthers.isOpdInclude());
    	assertEquals(undefined, pricesOthers.isUndefined());	
		
		return;
	}
}
