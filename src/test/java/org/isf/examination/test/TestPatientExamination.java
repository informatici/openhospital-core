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
package org.isf.examination.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.examination.model.PatientExamination;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class TestPatientExamination {

	private LocalDateTime pex_date = LocalDateTime.of(2020, 1, 10, 0, 0, 0);
	private Integer pex_height = 170;
	private Double pex_weight = 60.;
	private Integer pex_ap_min = 80;
	private Integer pex_ap_max = 120;
	private Integer pex_hr = 60;
	private Double pex_temp = 36.;
	private Double pex_sat = 1.;
	private Integer pex_hgt = 85;
	private Integer pex_diuresis = 100;
	private String pex_diuresis_desc = "physiological";
	private String pex_bowel_desc = "regular";
	private Integer pex_rr = 20;
	private String pex_ausc = "normal";
	private String pex_note = "";

	public PatientExamination setup(Patient patient, boolean usingSet) throws OHException {
		PatientExamination patientExamination;

		if (usingSet) {
			patientExamination = new PatientExamination();
			setParameters(patientExamination, patient);
		} else {
			// Create Patient Examination with all parameters 
			patientExamination = new PatientExamination(pex_date, patient, pex_height, pex_weight,
					pex_ap_min, pex_ap_max, pex_hr, pex_temp, pex_sat,
					pex_hgt, pex_diuresis, pex_diuresis_desc, pex_bowel_desc, pex_rr, pex_ausc, pex_note);
		}

		return patientExamination;
	}

	private void setParameters(PatientExamination patientExamination, Patient patient) {
		patientExamination.setPatient(patient);
		patientExamination.setPex_date(pex_date);
		patientExamination.setPex_hr(pex_hr);
		patientExamination.setPex_height(pex_height);
		patientExamination.setPex_note(pex_note);
		patientExamination.setPex_ap_max(pex_ap_max);
		patientExamination.setPex_ap_min(pex_ap_min);
		patientExamination.setPex_sat(pex_sat);
		patientExamination.setPex_temp(pex_temp);
		patientExamination.setPex_weight(pex_weight);
		patientExamination.setPex_auscultation(pex_ausc);
		patientExamination.setPex_rr(pex_rr);
	}

	public void check(PatientExamination patientExamination) {
		//assertEquals(pex_date, foundPatientExamination.getPex_date());
		assertThat(patientExamination.getPex_hr()).isEqualTo(pex_hr);
		//assertEquals(pex_height, patientExamination.getPex_height());
		assertThat(patientExamination.getPex_note()).isEqualTo(pex_note);
		assertThat(patientExamination.getPex_ap_max()).isEqualTo(pex_ap_max);
		assertThat(patientExamination.getPex_ap_min()).isEqualTo(pex_ap_min);
		//assertEquals(pex_sat, foundPatientExamination.getPex_sat());
		//assertEquals(pex_temp, foundPatientExamination.getPex_temp());
		assertThat(patientExamination.getPex_weight()).isEqualTo(pex_weight);
	}
}