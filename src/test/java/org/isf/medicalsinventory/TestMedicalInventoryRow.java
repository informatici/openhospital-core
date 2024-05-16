/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.medicalsinventory;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.utils.exception.OHException;

public class TestMedicalInventoryRow {

	private int id = 1;
	private double theoreticQty = 50.0;
	private double realQty = 50.0;
	
	public MedicalInventoryRow setup(MedicalInventory inventory, Medical medical, Lot lot, boolean usingSet) throws OHException {
		MedicalInventoryRow medInventoryRow;
		if (usingSet) {
			medInventoryRow = new MedicalInventoryRow();
			setParameters(medInventoryRow);
			medInventoryRow.setInventory(inventory);
			medInventoryRow.setMedical(medical);
			medInventoryRow.setLot(lot);
		} else {
			// Create MedicalInventoryRow with all parameters 
			medInventoryRow = new MedicalInventoryRow(id, theoreticQty, realQty, inventory, medical, lot);
		}
		return medInventoryRow;
	}
	
	public void setParameters(MedicalInventoryRow medInventoryRow) {
		medInventoryRow.setId(id);
		medInventoryRow.setTheoreticQty(theoreticQty);
		medInventoryRow.setRealqty(realQty);
	}

	public void check(MedicalInventoryRow medicalInventoryRow, int id) {
		assertThat(medicalInventoryRow.getId()).isEqualTo(id);
		assertThat(medicalInventoryRow.getTheoreticQty()).isEqualTo(theoreticQty);
		assertThat(medicalInventoryRow.getRealQty()).isEqualTo(realQty);
	}
}
