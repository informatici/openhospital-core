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
package org.isf.vaccine.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.utils.exception.OHException;
import org.isf.vaccine.model.Vaccine;
import org.isf.vactype.model.VaccineType;

public class TestVaccine {

	private String code = "Z";
	private String description = "TestDescription";

	public Vaccine setup(VaccineType vaccineType, boolean usingSet) throws OHException {
		Vaccine vaccine;

		if (usingSet) {
			vaccine = new Vaccine();
			setParameters(vaccineType, vaccine);
		} else {
			// Create Vaccine with all parameters 
			vaccine = new Vaccine(code, description, vaccineType);
		}

		return vaccine;
	}

	public void setParameters(VaccineType vaccineType, Vaccine vaccine) {
		vaccine.setCode(code);
		vaccine.setDescription(description);
		vaccine.setVaccineType(vaccineType);
	}

	public void check(Vaccine vaccine) {
		assertThat(vaccine.getCode()).isEqualTo(code);
		assertThat(vaccine.getDescription()).isEqualTo(description);
	}
}
