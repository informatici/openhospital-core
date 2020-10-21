/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

/**
 * 		   @author mwithi
 */
public class MovementForPrint implements Comparable<MovementForPrint>{

	private String ward;
	private LocalDateTime date;
	private String medical;
	private double quantity;
	private String lot;
	
	public MovementForPrint(Movement mov) {
		
		super();
		this.ward = mov.getWard().getDescription();
		this.date = removeTime(mov.getDate());
		this.medical = mov.getMedical().getDescription();
		this.quantity = mov.getQuantity();
		this.lot = mov.getLot().getCode();
	}
	
	public String getWard() {
		return ward;
	}
	public String getLot() {
		return lot;
	}
	public LocalDateTime getDate() {
		return date;
	}

	public String getMedical() {
		return medical;
	}

	public double getQuantity() {
		return quantity;
	}

	public String toString(){
		return medical;
	}

	@Override
	public int compareTo(MovementForPrint o) {
		return this.date.compareTo(o.getDate());
	}
	
	private LocalDateTime removeTime(LocalDateTime date) {
		return date.withHour(0).withMinute(0).withSecond(0);
	}
}
