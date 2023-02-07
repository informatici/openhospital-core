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
package org.isf.visits.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.visits.model.Visit;
import org.isf.ward.model.Ward;

public class TestVisit {

	private LocalDateTime date = LocalDateTime.of(10, 9, 8, 0, 0, 0);
	private String note = "TestNote";
	private boolean sms = true;
	private Integer duration = 10;
	private String service = "testService";
	private Ward ward;
	private Patient patient;

	public Visit setup(Patient patient, boolean usingSet, Ward ward) throws OHException {
		Visit visit;
		this.ward = ward;
		this.patient = patient;

		if (usingSet) {
			visit = new Visit();
			setParameters(patient, visit, ward);
		} else {
			// Create Visit with all parameters 
			visit = new Visit(0, date, patient, note, sms, ward, duration, service);
		}

		return visit;
	}

	public void setParameters(Patient patient, Visit visit, Ward ward) {
		visit.setDate(date);
		visit.setNote(note);
		visit.setPatient(patient);
		visit.setSms(sms);
		visit.setWard(ward);
		visit.setDuration(duration);
		visit.setService(service);
	}

	public void check(Visit visit) {
		assertThat(visit.getDate()).isCloseTo(date, within(1, ChronoUnit.SECONDS));
		assertThat(visit.getNote()).isEqualTo(note);
		assertThat(visit.isSms()).isEqualTo(sms);
		assertThat(visit.getDuration()).isEqualTo(duration);
		assertThat(visit.getWard()).isEqualTo(ward);
		assertThat(visit.getPatient()).isEqualTo(patient);
		assertThat(visit.getService()).isEqualTo(service);
	}
}
