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
package org.isf.serviceprinting.print;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import org.isf.medicalstockward.model.MovementWard;

/**
 * @author mwithi
 */
public class MovementWardForPrint implements Comparable<MovementWardForPrint>{

	private int code;
	private String ward;
	private Date date;
	private String medical;
	private Double quantity;
	private String units;
	private String lot;
	private boolean patient;
	
	public MovementWardForPrint(MovementWard mov) {
		super();
		this.ward = mov.getWard().getDescription();
		this.date = removeTime(mov.getDate());
		this.medical = null;
		this.medical = mov.getMedical().getDescription();
		this.quantity = mov.getQuantity();
		this.units = mov.getUnits();
		this.patient = mov.isPatient() || mov.getWardTo() == null || mov.getWardFrom() == null;
		this.lot = mov.getLot().getCode();
	}

	public int getCode() {
		return code;
	}

	public String getMedical() {
		return medical;
	}

	public Date getDate() {
		return date;
	}

	public Double getQuantity() {
		return quantity;
	}

	public String getWard() {
		return ward;
	}

	public String getLot() {
		return lot;
	}

	public String getUnits() {
		return units;
	}

	public boolean getPatient() {
		return patient;
	}

	@Override
	public int compareTo(MovementWardForPrint o) {
		return this.date.compareTo(o.getDate());
	}

	private Date removeTime(LocalDateTime date) {
		return Timestamp.valueOf(date);
	}

}
