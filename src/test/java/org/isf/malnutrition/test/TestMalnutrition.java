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
package org.isf.malnutrition.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.admission.model.Admission;
import org.isf.malnutrition.model.Malnutrition;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

public class TestMalnutrition {

	private int code = 0;
	private LocalDateTime now = TimeTools.getNow();
	private LocalDateTime dateSupp = now.withMonth(1).withDayOfMonth(1);
	private LocalDateTime dateConf = now.withMonth(10).withDayOfMonth(11);
	private float height = (float) 185.47;
	private float weight = (float) 70.70;

	public Malnutrition setup(Admission admission, boolean usingSet) throws OHException {
		Malnutrition malnutrition;

		if (usingSet) {
			malnutrition = new Malnutrition();
			setParameters(admission, malnutrition);
		} else {
			// Create Malnutrition with all parameters 
			malnutrition = new Malnutrition(code, dateSupp, dateConf, admission, height, weight);
		}

		return malnutrition;
	}

	public void setParameters(Admission admission, Malnutrition malnutrition) {
		malnutrition.setAdmission(admission);
		malnutrition.setDateConf(dateConf);
		malnutrition.setDateSupp(dateSupp);
		malnutrition.setHeight(height);
		malnutrition.setWeight(weight);
	}

	public void check(Malnutrition malnutrition) {
		assertThat(malnutrition.getDateConf()).isCloseTo(dateConf, within(1, ChronoUnit.SECONDS));
		assertThat(malnutrition.getDateSupp()).isCloseTo(dateSupp, within(1, ChronoUnit.SECONDS));
		assertThat(malnutrition.getHeight()).isCloseTo(height, within(0.1F));
		assertThat(malnutrition.getWeight()).isCloseTo(weight, within(0.1F));
	}
}
