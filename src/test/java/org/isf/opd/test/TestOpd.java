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
package org.isf.opd.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.disease.model.Disease;
import org.isf.generaldata.GeneralData;
import org.isf.opd.model.Opd;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.visits.model.Visit;
import org.isf.ward.model.Ward;

public class TestOpd {

	private LocalDateTime date = LocalDateTime.of(1984, 8, 14, 8, 0, 0);
	private int age = 9;
	private char sex = 'F';
	private int prog_year = 2008;
	private char newPatient = 'N';
	private String referralFrom = "R";
	private String referralTo = "R";
	private String userID = "TestUser";
	private String prescription = "prescription";

	public Opd setup(Patient patient, Disease disease, Ward ward, Visit nextVisit, boolean usingSet) throws OHException {

		Opd opd;

		if (usingSet) {
			opd = new Opd();
			setParameters(patient, disease, ward, nextVisit, opd);
		} else {
			// Create Opd with all parameters 
			opd = new Opd(prog_year, sex, age, disease);
			opd.setDate(date);
			/*opd.setReason(reason);
			opd.setTherapies(therapies);*/
			opd.setPrescription(prescription);
			opd.setNewPatient(newPatient);
			opd.setReferralFrom(referralFrom);
			opd.setReferralTo(referralTo);
			opd.setUserID(userID);
			opd.setPatient(patient);
			opd.setDisease2(disease);
			opd.setDisease3(disease);
			opd.setWard(ward);
			opd.setNextVisit(nextVisit);
		}

		return opd;
	}

	public void setParameters(Patient patient, Disease disease, Ward ward, Visit nextVisit, Opd opd) {
		opd.setDate(date);
		opd.setAge(age);
		opd.setSex(sex);
		/*opd.setReason(reason);
		opd.setTherapies(therapies);*/
		opd.setPrescription(prescription);
		opd.setProgYear(prog_year);
		opd.setNewPatient(newPatient);
		opd.setReferralFrom(referralFrom);
		opd.setReferralTo(referralTo);
		opd.setUserID(userID);
		opd.setPatient(patient);
		opd.setDisease(disease);
		opd.setDisease2(disease);
		opd.setDisease3(disease);
		opd.setWard(ward);
		opd.setNextVisit(nextVisit);
	}

	public void check(Opd opd) {
		assertThat(opd.getDate()).isCloseTo(date, within(1, ChronoUnit.SECONDS));
		if (!(GeneralData.OPDEXTENDED && opd.getPatient() != null)) {
			// skip checks as OpdBrowserManager sets values from patient
			// thus only do checks when not OPDEXTENDED and patient == null
			assertThat(opd.getAge()).isEqualTo(age);
			assertThat(opd.getSex()).isEqualTo(sex);
		}
		/*assertThat(opd.getReason()).isEqualTo(reason);
		assertThat(opd.getTherapies()).isEqualTo(therapies);*/
		assertThat(opd.getPrescription()).isEqualTo(prescription);
		assertThat(opd.getProgYear()).isEqualTo(prog_year);
		assertThat(opd.getNewPatient()).isEqualTo(newPatient);
		assertThat(opd.getReferralFrom()).isEqualTo(referralFrom);
		assertThat(opd.getReferralTo()).isEqualTo(referralTo);
		assertThat(opd.getUserID()).isEqualTo(userID);
	}
}
