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
import static org.assertj.core.data.Offset.offset;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillPayments;
import org.isf.utils.exception.OHException;

public class TestBillPayments {

	public LocalDateTime paymentDate = LocalDateTime.of(4, 3, 2, 0, 0, 0);
	private static double paymentAmount = 10.10;
	private static String paymentUser = "TestUser";

	public BillPayments setup(Bill bill, boolean usingSet) throws OHException {
		BillPayments billPayment;

		if (usingSet) {
			billPayment = new BillPayments();
			setParameters(billPayment, bill);
		} else {
			// Create bill payment with all parameters 
			billPayment = new BillPayments(0, bill, paymentDate, paymentAmount, paymentUser);
		}

		return billPayment;
	}

	public void setParameters(BillPayments billPayment, Bill bill) {
		billPayment.setBill(bill);
		billPayment.setDate(paymentDate);
		billPayment.setAmount(paymentAmount);
		billPayment.setUser(paymentUser);
	}

	public void check(BillPayments billPayment) {
		assertThat(billPayment.getAmount()).isCloseTo(paymentAmount, offset(0.1));
		assertThat(billPayment.getDate()).isCloseTo(paymentDate, within(1, ChronoUnit.SECONDS));
		assertThat(billPayment.getUser()).isEqualTo(paymentUser);
	}
}
