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
package org.isf.medicalstockward.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;

public class TestMovementWard {

	private LocalDateTime now = TimeTools.getNow();
	private LocalDateTime date = now.withMonth(2).withDayOfMonth(2);
	private boolean isPatient = false;
	private int age = 10;
	private float weight = 78;
	private String description = "TestDescriptionm";
	private Double quantity = 46.;
	private String units = "TestUni";

	public MovementWard setup(
			Ward ward,
			Patient patient,
			Medical medical,
			Ward wardTo,
			Ward wardFrom,
			Lot lot,
			boolean usingSet) throws OHException {
		MovementWard movementWard;

		if (usingSet) {
			movementWard = new MovementWard();
			setParameters(movementWard, ward, patient, medical, wardFrom, wardTo, lot);
		} else {
			// Create MovementWard with all parameters 
			movementWard = new MovementWard(ward, date, isPatient, patient, age, weight, description, medical, quantity, units, wardTo, wardFrom, lot);
		}

		return movementWard;
	}

	public void setParameters(
			MovementWard movementWard,
			Ward ward,
			Patient patient,
			Medical medical,
			Ward wardTo,
			Ward wardFrom,
			Lot lot) {
		movementWard.setAge(age);
		movementWard.setDate(date);
		movementWard.setDescription(description);
		movementWard.setMedical(medical);
		movementWard.setPatient(isPatient);
		movementWard.setPatient(patient);
		movementWard.setQuantity(quantity);
		movementWard.setUnits(units);
		movementWard.setWard(ward);
		movementWard.setWeight(weight);
		movementWard.setWardFrom(wardFrom);
		movementWard.setWardTo(wardTo);
		movementWard.setlot(lot);
	}

	public void check(MovementWard movementWard) {
		assertThat(movementWard.getAge()).isEqualTo(age);
		assertThat(movementWard.getDate()).isCloseTo(date, within(1, ChronoUnit.SECONDS));
		assertThat(movementWard.getDescription()).isEqualTo(description);
		assertThat(movementWard.isPatient()).isEqualTo(isPatient);
		assertThat(movementWard.getQuantity()).isEqualTo(quantity);
		assertThat(movementWard.getUnits()).isEqualTo(units);
		assertThat(movementWard.getWeight()).isCloseTo(weight, within(0.1F));
	}
}
