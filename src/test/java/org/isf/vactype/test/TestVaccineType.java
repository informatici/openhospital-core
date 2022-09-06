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
package org.isf.vactype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.utils.exception.OHException;
import org.isf.vactype.model.VaccineType;

public class TestVaccineType {

	private String code = "Z";
	private String description = "TestDescription";

	public VaccineType setup(boolean usingSet) throws OHException {
		VaccineType vaccineType;

		if (usingSet) {
			vaccineType = new VaccineType();
			setParameters(vaccineType);
		} else {
			// Create VaccineType with all parameters 
			vaccineType = new VaccineType(code, description);
		}

		return vaccineType;
	}

	public void setParameters(VaccineType vaccineType) {
		vaccineType.setCode(code);
		vaccineType.setDescription(description);
	}

	public void check(VaccineType vaccineType) {
		assertThat(vaccineType.getCode()).isEqualTo(code);
		assertThat(vaccineType.getDescription()).isEqualTo(description);
	}
}
