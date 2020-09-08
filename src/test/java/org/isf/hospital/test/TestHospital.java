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
package org.isf.hospital.test;

import static org.junit.Assert.assertEquals;

import org.isf.hospital.model.Hospital;
import org.isf.utils.exception.OHException;

public class TestHospital 
{	
    private String code = "ZZ";
    private String description = "TestDescription";    
    private String address = "TestAddress";
    private String city = "TestCity";
    private String telephone = "Testtelephone";
    private String fax = "TestFax";
    private String email = "TestEmail";
    private String currencyCod = "Cod";
    
			
	public Hospital setup(
			boolean usingSet) throws OHException 
	{
		Hospital hospital;
	
				
		if (usingSet)
		{
			hospital = new Hospital();
			_setParameters(hospital);
		}
		else
		{
			// Create Hospital with all parameters 
			hospital = new Hospital(code, description, address, city, telephone, fax, email, currencyCod);
		}
				    	
		return hospital;
	}
	
	public void _setParameters(
			Hospital hospital) 
	{	
		hospital.setCode(code);
		hospital.setDescription(description);
		hospital.setAddress(address);
		hospital.setCity(city);
		hospital.setTelephone(telephone);
		hospital.setEmail(email);
		hospital.setFax(fax);
		hospital.setCurrencyCod(currencyCod);
		
		return;
	}
	
	public void check(
			Hospital hospital) 
	{		
    	assertEquals(code, hospital.getCode());
    	assertEquals(description, hospital.getDescription());
    	assertEquals(address, hospital.getAddress());
    	assertEquals(city, hospital.getCity());
    	assertEquals(telephone, hospital.getTelephone());
    	assertEquals(email, hospital.getEmail());
    	assertEquals(fax, hospital.getFax());
    	assertEquals(currencyCod, hospital.getCurrencyCod());
		
		return;
	}
}
