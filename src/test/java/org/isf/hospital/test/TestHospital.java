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
package org.isf.hospital.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Time;

import org.isf.hospital.model.Hospital;
import org.isf.utils.exception.OHException;

public class TestHospital {

	private String code = "ZZ";
	private String description = "TestDescription";
	private String address = "TestAddress";
	private String city = "TestCity";
	private String telephone = "Testtelephone";
	private String fax = "TestFax";
	private String email = "TestEmail";
	private String currencyCod = "Cod";
	private Time startHour = Time.valueOf(Hospital.VISIT_START_TIME);
	private Time endHour = Time.valueOf(Hospital.VISIT_END_TIME);
	private int visitIncreemnt = Hospital.VISIT_INCREMENT;
	private int visitDuration = Hospital.VISIT_DURATION;

	public Hospital setup(boolean usingSet) throws OHException {
		Hospital hospital;

		if (usingSet) {
			hospital = new Hospital();
			setParameters(hospital);
		} else {
			// Create Hospital with all parameters 
			hospital = new Hospital(code, description, address, city, telephone, fax, email, currencyCod, startHour, endHour, visitIncreemnt, visitDuration);
		}

		return hospital;
	}

	public void setParameters(Hospital hospital) {
		hospital.setCode(code);
		hospital.setDescription(description);
		hospital.setAddress(address);
		hospital.setCity(city);
		hospital.setTelephone(telephone);
		hospital.setEmail(email);
		hospital.setFax(fax);
		hospital.setCurrencyCod(currencyCod);
		hospital.setVisitStartTime(startHour);
		hospital.setVisitEndTime(endHour);
		hospital.setVisitIncrement(visitIncreemnt);
		hospital.setVisitDuration(visitDuration);
	}

	public void check(Hospital hospital) {
		assertThat(hospital.getCode()).isEqualTo(code);
		assertThat(hospital.getDescription()).isEqualTo(description);
		assertThat(hospital.getAddress()).isEqualTo(address);
		assertThat(hospital.getCity()).isEqualTo(city);
		assertThat(hospital.getTelephone()).isEqualTo(telephone);
		assertThat(hospital.getEmail()).isEqualTo(email);
		assertThat(hospital.getFax()).isEqualTo(fax);
		assertThat(hospital.getCurrencyCod()).isEqualTo(currencyCod);
		assertThat(hospital.getVisitStartTime()).isEqualTo(startHour);
		assertThat(hospital.getVisitEndTime()).isEqualTo(endHour);
		assertThat(hospital.getVisitIncrement()).isEqualTo(visitIncreemnt);
		assertThat(hospital.getVisitDuration()).isEqualTo(visitDuration);
	}

}
