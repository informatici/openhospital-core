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
package org.isf.therapy.model;

import java.time.LocalDateTime;

import org.isf.medicals.model.Medical;

/**
 * Bean to host Therapies informations extract from TherapyRow beans.
 *
 * @author Mwithi
 */
public class Therapy {
	
	private int therapyID;
	private int patID;
	private LocalDateTime[] dates;
	private Medical medical;
	private Double qty;
	private String units;
	private int freqInDay;
	private String note;
	private boolean notify;
	private boolean sms;

	public Therapy() {
		super();
	}

	/**
	 * @param therapyID
	 * @param patID
	 * @param dates
	 * @param medical
	 * @param qty
	 * @param units
	 * @param freqInDay
	 * @param note
	 * @param notify
	 * @param sms
	 */
	public Therapy(int therapyID, int patID, LocalDateTime[] dates,
			Medical medical, Double qty, String units, int freqInDay,
			String note, boolean notify, boolean sms) {
		super();
		this.therapyID = therapyID;
		this.patID = patID;
		this.dates = dates;
		this.medical = medical;
		this.qty = qty;
		this.units = units;
		this.freqInDay = freqInDay;
		this.note = note;
		this.notify = notify;
		this.sms = sms;
	}
	
	public int getTherapyID() {
		return therapyID;
	}

	public void setTherapyID(int therapyID) {
		this.therapyID = therapyID;
	}

	public LocalDateTime[] getDates() {
		return dates;
	}

	public void setDates(LocalDateTime[] dates) {
		this.dates = dates;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public int getFreqInDay() {
		return freqInDay;
	}

	public void setFreqInDay(int freq) {
		this.freqInDay = freq;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical med) {
		this.medical = med;
	}

	public int getPatID() {
		return patID;
	}

	public void setPatID(int patID) {
		this.patID = patID;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isSms() {
		return sms;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}
	
	public String toString() {
		return "" + qty + this.units + " of " + medical.toString() + " - " + this.freqInDay + " per day";
	}
}
