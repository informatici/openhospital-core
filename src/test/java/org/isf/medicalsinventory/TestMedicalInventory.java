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
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

public class TestMedicalInventory {
	
	private int id = 1;
	private String status = "STATUS";
	private LocalDateTime inventoryDate = LocalDateTime.now();
	private String user = "USER";
	private String inventoryReference = "REFERENCE";
	private String inventoryType = "TYPE";
	private String ward = "Z";

	public MedicalInventory setup(Ward ward, boolean usingSet) throws OHException {
		MedicalInventory medInventory;

		if (usingSet) {
			medInventory = new MedicalInventory();
			setParameters(medInventory);
		} else {
			// Create MedicalInventory with all parameters 
			medInventory = new MedicalInventory(id, status, inventoryDate, user, inventoryReference, inventoryType, ward.getCode());
		}
		return medInventory;
	}
	
	public void setParameters(MedicalInventory medInventory) {
		medInventory.setId(id);
		medInventory.setStatus(status);
		medInventory.setInventoryDate(inventoryDate);
		medInventory.setUser(user);
		medInventory.setInventoryReference(inventoryReference);
		medInventory.setInventoryType(inventoryType);
		medInventory.setWard(ward);
	}
	
	public void check(MedicalInventory medInventory, int id) {
		assertThat(medInventory.getId()).isEqualTo(id);
		assertThat(medInventory.getStatus()).isEqualTo(status);
		assertThat(medInventory.getInventoryDate()).isCloseTo(inventoryDate, within(1, ChronoUnit.SECONDS));
		assertThat(medInventory.getUser()).isEqualTo(user);
		assertThat(medInventory.getInventoryReference()).isEqualTo(inventoryReference);
		assertThat(medInventory.getInventoryType()).isEqualTo(inventoryType);
		assertThat(medInventory.getWard()).isEqualTo(ward);
	}
}
