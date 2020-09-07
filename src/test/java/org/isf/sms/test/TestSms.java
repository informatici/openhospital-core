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
package org.isf.sms.test;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.GregorianCalendar;

import org.isf.sms.model.Sms;
import org.isf.utils.exception.OHException;

public class TestSms 
{	 
	private int smsId = 0;	
	private Date smsDate = new GregorianCalendar(2010, 9, 8).getTime();
	private Date smsDateSched = new GregorianCalendar(2011, 9, 8).getTime();	
	private String smsNumber = "TestNumber";	
	private String smsText = "TestText";
	private Date smsDateSent = null;
	private String smsUser = "TestUser";
	private String module = "TestModule";
	private String moduleID = "TestModId";
	 
			
	public Sms setup(
			boolean usingSet) throws OHException 
	{
		Sms sms;
	
				
		if (usingSet)
		{
			sms = new Sms();
			_setParameters(sms);
		}
		else
		{
			// Create Sms with all parameters 
			sms = new Sms(smsId, smsDate, smsDateSched, smsNumber, smsText, 
							smsDateSent, smsUser, module, moduleID);
		}
				    	
		return sms;
	}
	
	public void _setParameters(
			Sms sms) 
	{	
		sms.setModule(module);
		sms.setModuleID(moduleID);
		sms.setSmsDate(smsDate);
		sms.setSmsDateSched(smsDateSched);
		sms.setSmsDateSent(smsDateSent);
		sms.setSmsNumber(smsNumber);
		sms.setSmsText(smsText);
		sms.setSmsUser(smsUser);
		
		return;
	}
	
	public void check(
			Sms sms) 
	{		
    	assertEquals(module, sms.getModule());
    	assertEquals(moduleID, sms.getModuleID());
    	assertEquals(smsDate, sms.getSmsDate());
    	assertEquals(smsDateSched, sms.getSmsDateSched());
    	assertEquals(smsDateSent, sms.getSmsDateSent());
    	assertEquals(smsNumber, sms.getSmsNumber());
    	assertEquals(smsText, sms.getSmsText());
    	assertEquals(smsUser, sms.getSmsUser());
    			
		return;
	}
}
