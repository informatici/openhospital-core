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
package org.isf.therapy.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.GregorianCalendar;

import org.isf.medicals.model.Medical;
import org.isf.patient.model.Patient;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.exception.OHException;

public class TestTherapy {

	private GregorianCalendar startDate = new GregorianCalendar(10, 9, 8);
	private GregorianCalendar endDate = new GregorianCalendar(11, 10, 9);
	private Double qty = 9.9;
	private int unitID = 10;
	private int freqInDay = 11;
	private int freqInPeriod = 12;
	private String note = "TestNote";
	private boolean notify = false;
	private boolean sms = true;

	public TherapyRow setup(Patient patient, Medical medical, boolean usingSet) throws OHException {
		TherapyRow therapyRow;

		if (usingSet) {
			therapyRow = new TherapyRow();
			_setParameters(patient, medical, therapyRow);
		} else {
			// Create TherapyRow with all parameters 
			therapyRow = new TherapyRow(0, patient, startDate, endDate,
					medical, qty, unitID, freqInDay, freqInPeriod, note, notify, sms);
		}

		return therapyRow;
	}

	public void _setParameters(Patient patient, Medical medical, TherapyRow therapyRow) {
		therapyRow.setEndDate(endDate);
		therapyRow.setFreqInDay(freqInDay);
		therapyRow.setFreqInPeriod(freqInPeriod);
		therapyRow.setMedical(medical);
		therapyRow.setNote(note);
		therapyRow.setNotify(notify);
		therapyRow.setPatient(patient);
		therapyRow.setQty(qty);
		therapyRow.setSms(sms);
		therapyRow.setStartDate(startDate);
		therapyRow.setUnitID(unitID);
	}

	public void check(TherapyRow therapyRow) {
		assertThat(therapyRow.getEndDate()).isEqualTo(endDate);
		assertThat(therapyRow.getFreqInDay()).isEqualTo(freqInDay);
		assertThat(therapyRow.getFreqInPeriod()).isEqualTo(freqInPeriod);
		assertThat(therapyRow.getNote()).isEqualTo(note);
		assertThat(therapyRow.isNotify()).isEqualTo(notify);
		assertThat(therapyRow.getQty()).isEqualTo(qty);
		assertThat(therapyRow.isSms()).isEqualTo(sms);
		assertThat(therapyRow.getStartDate()).isEqualTo(startDate);
		assertThat(therapyRow.getUnitID()).isEqualTo(unitID);
	}
}
