/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.conditioning;

import org.isf.conditioning.model.Conditioning;
import org.isf.menu.model.User;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TestConditioning {

	private static final Boolean ASPIRATION = true;
	private static final Boolean CPAP = true;
	private static final Integer MCE = 4;
	private static final Integer VENTILATION = 2;
	private static final Double OXYGEN_DEBIT = 3.0;
	private static final Double SG_VOLUME = 10.0;
	private static final Double DIAZEPAM_DOSE = 12.0;
	private static final Double BOLUS_SS_VOLUME = 3.0;
	private static final String SNG_NUMBER = "SNG-123";
	private static final String OTHERS = "others note";
	private static final LocalDateTime PERFORMED_AT = LocalDateTime.of(2025, 1, 1, 10, 0);
	private static final int LOCK = 0;
	private static final String HIV_TEST = "INDETERMINATE";
	private static final String MALARIA = "undetermined";
	private static final Double BLOOD_GLUCOSE_LEVEL = 5.6;

	public Conditioning setup(Patient patient, User user, boolean usingSet) throws OHException {
		Conditioning conditioning;

		if (usingSet) {
			conditioning = new Conditioning();
			setParameters(conditioning, user, patient);
		} else {
			conditioning = new Conditioning(null, user, ASPIRATION, MCE, VENTILATION, OXYGEN_DEBIT, BOLUS_SS_VOLUME, DIAZEPAM_DOSE, SG_VOLUME, SNG_NUMBER, OTHERS, PERFORMED_AT, patient, CPAP, LOCK, MALARIA, BLOOD_GLUCOSE_LEVEL, HIV_TEST);
			conditioning.setLock(LOCK);
		}

		return conditioning;
	}

	public void setParameters(Conditioning conditioning, User user, Patient patient) {
		conditioning.setAspiration(ASPIRATION);
		conditioning.setMce(MCE);
		conditioning.setVentilation(VENTILATION);
		conditioning.setOxygenDebit(OXYGEN_DEBIT);
		conditioning.setSgVolume(SG_VOLUME);
		conditioning.setDiazepamDose(DIAZEPAM_DOSE);
		conditioning.setBolusSsVolume(BOLUS_SS_VOLUME);
		conditioning.setSngNumber(SNG_NUMBER);
		conditioning.setOthers(OTHERS);
		conditioning.setPerformedAt(PERFORMED_AT);
		conditioning.setPatient(patient);
		conditioning.setPerformedBy(user);
		conditioning.setMalaria(MALARIA);
		conditioning.setBloodGlucoseLevel(BLOOD_GLUCOSE_LEVEL);
		conditioning.setHivTest(HIV_TEST);
		conditioning.setLock(LOCK);
	}

	public void check(Conditioning conditioning) {
		assertThat(conditioning.getAspiration()).isEqualTo(ASPIRATION);
		assertThat(conditioning.getMce()).isEqualTo(MCE);
		assertThat(conditioning.getVentilation()).isEqualTo(VENTILATION);
		assertThat(conditioning.getOxygenDebit()).isEqualTo(OXYGEN_DEBIT);
		assertThat(conditioning.getSgVolume()).isEqualTo(SG_VOLUME);
		assertThat(conditioning.getDiazepamDose()).isEqualTo(DIAZEPAM_DOSE);
		assertThat(conditioning.getBolusSsVolume()).isEqualTo(BOLUS_SS_VOLUME);
		assertThat(conditioning.getSngNumber()).isEqualTo(SNG_NUMBER);
		assertThat(conditioning.getOthers()).isEqualTo(OTHERS);
		assertThat(conditioning.getPerformedAt()).isEqualTo(PERFORMED_AT);
		conditioning.setMalaria(MALARIA);
		conditioning.setBloodGlucoseLevel(BLOOD_GLUCOSE_LEVEL);
		conditioning.setHivTest(HIV_TEST);
		assertThat(conditioning.getLock()).isEqualTo(LOCK);
		assertThat(conditioning.getPatient()).isNotNull();
		assertThat(conditioning.getPerformedBy()).isNotNull();
	}
}
