/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.accounting.model.Bill;
import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;
import org.isf.priceslist.model.PriceList;
import org.isf.utils.exception.OHException;

public class TestBill {

	private static LocalDateTime date = LocalDateTime.of(10, 9, 8, 0, 0, 0);
	private static LocalDateTime update = LocalDateTime.of(7, 6, 5, 0, 0, 0);
	private static boolean isList = false;
	private static String listName = "TestListName";
	private static boolean isPatient = true;
	private static String patName = "TestPatName";
	private static String status = "O";
	private static Double amount = 10.10;
	private static Double balance = 20.20;
	private static String user = "TestUser";

	public Bill setup(PriceList priceList, Patient patient, Admission admission, boolean usingSet) throws OHException {
		Bill bill;

		if (usingSet) {
			bill = new Bill();
			setParameters(bill, priceList, patient, admission);
		} else {
			// Create Bill with all parameters 
			bill = new Bill(0, date, update, isList, priceList, listName, isPatient, patient, patName,
					status, amount, balance, user, admission);
		}
		return bill;
	}

	public void setParameters(Bill bill, PriceList priceList, Patient patient, Admission admission) {
		bill.setDate(date);
		bill.setUpdate(update);
		bill.setIsList(isList);
		bill.setPriceList(priceList);
		bill.setListName(listName);
		bill.setIsPatient(isPatient);
		bill.setBillPatient(patient);
		bill.setPatName(patName);
		bill.setStatus(status);
		bill.setAmount(amount);
		bill.setBalance(balance);
		bill.setUser(user);
		bill.setAdmission(admission);
	}

	public void check(Bill bill) {
		assertThat(bill.getDate()).isCloseTo(date, within(1, ChronoUnit.SECONDS));
		assertThat(bill.getUpdate()).isCloseTo(update, within(1, ChronoUnit.SECONDS));
		assertThat(bill.isList()).isEqualTo(isList);
		assertThat(bill.getListName()).isEqualTo(listName);
		assertThat(bill.isPatient()).isEqualTo(isPatient);
		assertThat(bill.getPatName()).isEqualTo(patName);
		assertThat(bill.getStatus()).isEqualTo(status);
		assertThat(bill.getAmount()).isEqualTo(amount);
		assertThat(bill.getBalance()).isEqualTo(balance);
		assertThat(bill.getUser()).isEqualTo(user);
	}
}
