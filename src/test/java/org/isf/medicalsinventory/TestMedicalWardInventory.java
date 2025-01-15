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

import org.isf.medicalinventory.model.InventoryStatus;
import org.isf.medicalinventory.model.InventoryType;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;

public class TestMedicalWardInventory {
	private int id = 1;
	private String status = InventoryStatus.draft.toString();
	private LocalDateTime inventoryDate = TimeTools.getNow();
	private String user = "admin";
	private String inventoryReference = "REFERENCE";
	private String inventoryType = InventoryType.ward.toString();
	private String ward = "Z";

	public MedicalInventory setup(Ward ward, boolean usingSet) throws OHException {
		MedicalInventory medicalInventory;

		if (usingSet) {
			medicalInventory = new MedicalInventory();
			setParameters(medicalInventory);
		} else {
			// create MedicalInventory with all parameters
			medicalInventory = new MedicalInventory(id, status, inventoryDate, user, inventoryReference, inventoryType, ward.getCode());
		}
		return medicalInventory;
	}

	public void setParameters(MedicalInventory medicalInventory) {
		medicalInventory.setId(id);
		medicalInventory.setStatus(status);
		medicalInventory.setInventoryDate(inventoryDate);
		medicalInventory.setUser(user);
		medicalInventory.setInventoryReference(inventoryReference);
		medicalInventory.setInventoryType(inventoryType);
		medicalInventory.setWard(ward);
	}

	public void check(MedicalInventory medicalInventory, int id) {
		assertThat(medicalInventory.getId()).isEqualTo(id);
		assertThat(medicalInventory.getStatus()).isEqualTo(status);
		assertThat(medicalInventory.getInventoryDate()).isCloseTo(inventoryDate, within(1, ChronoUnit.SECONDS));
		assertThat(medicalInventory.getUser()).isEqualTo(user);
		assertThat(medicalInventory.getInventoryReference()).isEqualTo(inventoryReference);
		assertThat(medicalInventory.getInventoryType()).isEqualTo(inventoryType);
		assertThat(medicalInventory.getWard()).isEqualTo(ward);
	}
}
