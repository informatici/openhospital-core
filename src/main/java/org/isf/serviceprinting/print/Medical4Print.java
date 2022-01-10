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

import org.isf.medicals.model.Medical;

public class Medical4Print {

	private String medicalDescription;
	private String medicalType;
	private double minQty;
	private double curQty;
	private String expiring;

	public Medical4Print(Medical medical) {
		medicalDescription = medical.getDescription();
		medicalType = medical.getType().getDescription();
		minQty = medical.getMinqty();
		curQty = medical.getInitialqty() + medical.getInqty() - medical.getOutqty();
		if (curQty < minQty) {
			expiring = "Yes";
		} else {
			expiring = "No";
		}
	}

	public double getCurQty() {
		return curQty;
	}

	public void setCurQty(double curQty) {
		this.curQty = curQty;
	}
	public String getExpiring() {
		return expiring;
	}

	public void setExpiring(String expiring) {
		this.expiring = expiring;
	}

	public String getMedicalDescription() {
		return medicalDescription;
	}

	public void setMedicalDescription(String medicalDescription) {
		this.medicalDescription = medicalDescription;
	}

	public String getMedicalType() {
		return medicalType;
	}

	public void setMedicalType(String medicalType) {
		this.medicalType = medicalType;
	}

	public double getMinQty() {
		return minQty;
	}

	public void setMinQty(double minQty) {
		this.minQty = minQty;
	}
	
}
