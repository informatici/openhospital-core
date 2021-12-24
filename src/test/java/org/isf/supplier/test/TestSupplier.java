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
package org.isf.supplier.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHException;

public class TestSupplier {

	private Integer supId = null;
	private String supName = "TestName";
	private String supAddress = "TestAddress";
	private String supTaxcode = "TestTax";
	private String supPhone = "TestPhone";
	private String supFax = "TestFax";
	private String supEmail = "TestEmail";
	private String supNote = "TestNote";
	private Character supDeleted = 'N';

	public Supplier setup(boolean usingSet) throws OHException {
		Supplier supplier;

		if (usingSet) {
			supplier = new Supplier();
			setParameters(supplier);
		} else {
			// Create Supplier with all parameters 
			supplier = new Supplier(supId, supName, supAddress, supTaxcode, supPhone, supFax, supEmail, supNote, supDeleted);
		}

		return supplier;
	}

	public void setParameters(Supplier supplier) {
		supplier.setSupAddress(supAddress);
		supplier.setSupDeleted(supDeleted);
		supplier.setSupEmail(supEmail);
		supplier.setSupFax(supFax);
		supplier.setSupName(supName);
		supplier.setSupNote(supNote);
		supplier.setSupPhone(supPhone);
		supplier.setSupTaxcode(supTaxcode);
	}

	public void check(Supplier supplier) {
		assertThat(supplier.getSupAddress()).isEqualTo(supAddress);
		assertThat(supplier.getSupDeleted()).isEqualTo(supDeleted);
		assertThat(supplier.getSupEmail()).isEqualTo(supEmail);
		assertThat(supplier.getSupFax()).isEqualTo(supFax);
		assertThat(supplier.getSupName()).isEqualTo(supName);
		assertThat(supplier.getSupNote()).isEqualTo(supNote);
		assertThat(supplier.getSupPhone()).isEqualTo(supPhone);
		assertThat(supplier.getSupTaxcode()).isEqualTo(supTaxcode);
	}
}
