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
package org.isf.malnutrition.test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.isf.admission.model.Admission;
import org.isf.malnutrition.model.Malnutrition;
import org.isf.utils.exception.OHException;

public class TestMalnutrition 
{	 
	private int code = 0;
	private GregorianCalendar now = new GregorianCalendar();
	private GregorianCalendar dateSupp = new GregorianCalendar(now.get(Calendar.YEAR), 1, 1);
    private GregorianCalendar dateConf = new GregorianCalendar(now.get(Calendar.YEAR), 10, 11);
    private float height = (float)185.47;
	private float weight = (float)70.70;
	
	public Malnutrition setup(Admission admission,
			boolean usingSet) throws OHException 
	{
		Malnutrition malnutrition;
				
		if (usingSet)
		{
			malnutrition = new Malnutrition();
			_setParameters(admission, malnutrition);
		}
		else
		{
			// Create Malnutrition with all parameters 
			malnutrition = new Malnutrition(code, dateSupp, dateConf, admission, height, weight);
		}
				    	
		return malnutrition;
	}
	
	public void _setParameters(Admission admission,
			Malnutrition malnutrition) 
	{	
		malnutrition.setAdmission(admission);
		malnutrition.setDateConf(dateConf);
		malnutrition.setDateSupp(dateSupp);
		malnutrition.setHeight(height);
		malnutrition.setWeight(weight);
		
		return;
	}
	
	public void check(
			Malnutrition malnutrition) 
	{		
    	assertEquals(dateConf, malnutrition.getDateConf());
    	assertEquals(dateSupp, malnutrition.getDateSupp());
    	assertEquals(height, malnutrition.getHeight(), 0.1);
    	assertEquals(weight, malnutrition.getWeight(), 0.1);
		
		return;
	}
}
