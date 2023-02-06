/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import static org.assertj.core.data.Offset.offset;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.utils.exception.OHException;

public class TestBillItems {

	private static boolean isPrice = false;
	private static String priceID = "TestPId";
	private static String itemDescription = "TestItemDescription";
	private static double itemAmount = 10.10;
	private static int itemQuantity = 20;

	public BillItems setup(Bill bill, boolean usingSet) throws OHException {
		BillItems billItem;

		if (usingSet) {
			billItem = new BillItems();
			setParameters(billItem, bill);
		} else {
			// Create BillItem with all parameters 
			billItem = new BillItems(0, bill, isPrice, priceID, itemDescription, itemAmount, itemQuantity);
		}

		return billItem;
	}

	public void setParameters(BillItems billItem, Bill bill) {
		billItem.setBill(bill);
		billItem.setItemAmount(itemAmount);
		billItem.setItemDescription(itemDescription);
		billItem.setItemQuantity(itemQuantity);
		billItem.setPrice(isPrice);
		billItem.setPriceID(priceID);
	}

	public void check(BillItems billItem) {
		assertThat(billItem.getItemAmount()).isCloseTo(itemAmount, offset(0.1));
		assertThat(billItem.getItemDescription()).isEqualTo(itemDescription);
		assertThat(billItem.getItemQuantity()).isEqualTo(itemQuantity);
		assertThat(billItem.isPrice()).isEqualTo(isPrice);
		assertThat(billItem.getPriceID()).isEqualTo(priceID);
	}
}
