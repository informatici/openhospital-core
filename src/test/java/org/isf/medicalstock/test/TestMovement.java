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
package org.isf.medicalstock.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.GregorianCalendar;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

public class TestMovement {

	private GregorianCalendar date = new GregorianCalendar(2000, 2, 2);
	private int quantity = 10;
	private String refNo = "TestRef";

	public Movement setup(
			Medical medical,
			MovementType movementType,
			Ward ward,
			Lot lot,
			Supplier supplier,
			boolean usingSet) throws OHException {
		Movement movement;

		if (usingSet) {
			movement = new Movement();
			_setParameters(movement, medical, movementType, ward, lot, supplier);
		} else {
			// Create Movement with all parameters 
			movement = new Movement(medical, movementType, ward, lot, date, quantity, supplier, refNo);
		}

		return movement;
	}

	public void _setParameters(
			Movement movement,
			Medical medical,
			MovementType movementType,
			Ward ward,
			Lot lot,
			Supplier supplier) {
		movement.setDate(date);
		movement.setLot(lot);
		movement.setMedical(medical);
		movement.setQuantity(quantity);
		movement.setRefNo(refNo);
		movement.setSupplier(supplier);
		movement.setType(movementType);
		movement.setWard(ward);
	}

	public void check(Movement movement) {
		assertThat(movement.getDate()).isEqualTo(date);
		assertThat(movement.getQuantity()).isEqualTo(quantity);
		assertThat(movement.getRefNo()).isEqualTo(refNo);
	}
}
