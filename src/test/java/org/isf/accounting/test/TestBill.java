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
package org.isf.accounting.test;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;

import org.isf.accounting.model.Bill;
import org.isf.patient.model.Patient;
import org.isf.priceslist.model.PriceList;
import org.isf.utils.exception.OHException;

public class TestBill 
{	
	private static GregorianCalendar date = new GregorianCalendar(10, 9, 8);
	private static GregorianCalendar update = new GregorianCalendar(7, 6, 5);
	private static boolean isList = false;
	private static String listName = "TestListName";
	private static boolean isPatient = true;
	private static String patName = "TestPatName";
	private static String status = "O";
	private static Double amount = 10.10;
	private static Double balance = 20.20;
	private static String user = "TestUser";
	
			
	public Bill setup(
			PriceList priceList,
			Patient patient,
			boolean usingSet) throws OHException 
	{
		Bill bill;
	
				
		if (usingSet)
		{
			bill = new Bill();
			_setParameters(bill, priceList, patient);
		}
		else
		{
			// Create Bill with all parameters 
			bill = new Bill(0, date, update, isList, priceList, listName, isPatient, patient, patName, 
					status, amount, balance, user);
		}
				    	
		return bill;
	}
	
	public void _setParameters(
			Bill bill,
			PriceList priceList,
			Patient patient) 
	{		
		bill.setDate(date);
		bill.setUpdate(update);
		bill.setList(isList);
		bill.setList(priceList);
		bill.setListName(listName);
		bill.setPatient(isPatient);
		bill.setPatient(patient);
		bill.setStatus(status);
		bill.setAmount(amount);
		bill.setBalance(balance);
		bill.setUser(user);
		
		return;
	}
	
	public void check(
			Bill bill) 
	{		
    	assertEquals(date, bill.getDate());
    	assertEquals(update, bill.getUpdate());
    	assertEquals(isList, bill.isList());
    	assertEquals(listName, bill.getListName());
    	assertEquals(isPatient,bill.isPatient());
    	assertEquals(status,bill.getStatus());
    	assertEquals(amount, bill.getAmount());
    	assertEquals(balance, bill.getBalance());
    	assertEquals(user, bill.getUser());
		
		return;
	}
}
