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
package org.isf.ward.test;

import static org.junit.Assert.assertEquals;

import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

public class TestWard 
{	
    private String code = "Z";
    private String description = "TestDescription";
    private String telephone = "TestTelephone";
    private String fax = "TestFac";
    private String email = "TestEmail";
    private Integer beds = 100;
    private Integer nurs = 101;
    private Integer docs = 102;   
    private boolean isPharmacy = true;    
    private boolean isFemale = false;  
    private boolean isMale = true;
    
			
	public Ward setup(
			boolean usingSet) throws OHException 
	{
		Ward ward;
	
				
		if (usingSet)
		{
			ward = new Ward();
			_setParameters(ward);
		}
		else
		{
			// Create Ward with all parameters 
			ward = new Ward(code, description, telephone, fax, email, beds, nurs, docs,
					isPharmacy, isMale, isFemale);
		}
				    	
		return ward;
	}
	
	public void _setParameters(
			Ward ward) 
	{	
		ward.setCode(code);
		ward.setBeds(beds);
		ward.setDescription(description);
		ward.setDocs(docs);
		ward.setEmail(email);
		ward.setFax(fax);
		ward.setFemale(isFemale);
		ward.setMale(isMale);
		ward.setNurs(nurs);
		ward.setPharmacy(isPharmacy);
		ward.setTelephone(telephone);
		
		return;
	}
	
	public void check(
			Ward ward) 
	{		
    	assertEquals(code, ward.getCode());
    	assertEquals(beds, ward.getBeds());
    	assertEquals(description, ward.getDescription());
    	assertEquals(docs, ward.getDocs());
    	assertEquals(email, ward.getEmail());
    	assertEquals(fax, ward.getFax());
    	assertEquals(isFemale, ward.isFemale());
    	assertEquals(isMale, ward.isMale());
    	assertEquals(nurs, ward.getNurs());
    	assertEquals(isPharmacy, ward.isPharmacy());
    	assertEquals(telephone, ward.getTelephone());
		
		return;
	}
}
