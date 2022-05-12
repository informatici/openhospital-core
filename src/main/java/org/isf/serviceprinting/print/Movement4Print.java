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
package org.isf.serviceprinting.print;

import java.time.LocalDateTime;

import org.isf.medicalstock.model.Movement;

public class Movement4Print {
	private LocalDateTime date;
	private String pharmaceuticalName;
	private String pharmaceuticalType;
	private String movementType;
	private String ward;
	private int quantity;
	private String lot;

	public Movement4Print(Movement movement) {
		date = movement.getDate();
		pharmaceuticalName = movement.getMedical().getDescription();
		pharmaceuticalType = movement.getMedical().getType().getDescription();
		movementType = movement.getType().getType();
		if (movement.getWard() != null) {
			ward = movement.getWard().getDescription();
		}
		quantity = movement.getQuantity();
		lot = movement.getLot().getCode();
	}

	public String getDate() {
		return getConvertedString(date);
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getLot() {
		if (lot != null) {
			return lot;
		}
		return "No Lot";
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public String getMovementType() {
		return movementType;
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public String getPharmaceuticalName() {
		return pharmaceuticalName;
	}

	public void setPharmaceuticalName(String pharmaceuticalName) {
		this.pharmaceuticalName = pharmaceuticalName;
	}

	public String getPharmaceuticalType() {
		return pharmaceuticalType;
	}

	public void setPharmaceuticalType(String pharmaceuticalType) {
		this.pharmaceuticalType = pharmaceuticalType;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getWard() {
		if (ward == null) {
			return "No Ward";
		}
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	private String getConvertedString(LocalDateTime time) {
		if (time == null) {
			return "No Date";
		}
		String string = time.getDayOfMonth() + "/" + time.getMonthValue();
		String year = String.valueOf(time.getYear());
		year = year.substring(2);
		string += '/' + year;
		return string;
	}

}
