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
package org.isf.medicalstock;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.MedicalStock;
import org.isf.medicalstock.model.Movement;
import org.isf.utils.exception.OHException;

public class TestMedicalStock {

	private LocalDate balanceDate = LocalDate.of(2000, 1, 1);
	private int balance = 100;
	private LocalDate nextMovDate;
	private Integer days;

	public MedicalStock setup(Medical medical, boolean usingSet) throws OHException {
		MedicalStock medicalStock;

		if (usingSet) {
			medicalStock = new MedicalStock();
			setParameters(medical, medicalStock);
		} else {
			// Create medicalStock with all parameters
			medicalStock = new MedicalStock();
		}

		return medicalStock;
	}

	public MedicalStock setup(Movement movement) {
		MedicalStock medicalStock = new MedicalStock();
		medicalStock.setMedical(movement.getMedical());
		medicalStock.setBalanceDate(movement.getDate().toLocalDate());
		medicalStock.setBalance(movement.getQuantity());
		medicalStock.setNextMovDate(null);
		medicalStock.setDays(null);
		return medicalStock;
	}

	public void setParameters(Medical medical, MedicalStock medicalStock) {
		medicalStock.setMedical(medical);
		medicalStock.setBalanceDate(balanceDate);
		medicalStock.setBalance(balance);
		medicalStock.setNextMovDate(nextMovDate);
		medicalStock.setDays(days);
	}

	public void check(MedicalStock medicalStock) {
		assertThat(medicalStock.getBalanceDate()).isEqualTo(balanceDate);
		assertThat(medicalStock.getBalance()).isEqualTo(balance);
		assertThat(medicalStock.getNextMovDate()).isEqualTo(nextMovDate);
		assertThat(medicalStock.getDays()).isEqualTo(days);
	}

}
