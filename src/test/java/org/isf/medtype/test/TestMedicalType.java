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
package org.isf.medtype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.medtype.model.MedicalType;
import org.isf.utils.exception.OHException;

public class TestMedicalType {

	private String code = "Z";
	private String description = "TestDescription";

	public MedicalType setup(boolean usingSet) throws OHException {
		MedicalType medicalType;

		if (usingSet) {
			medicalType = new MedicalType();
			setParameters(medicalType);
		} else {
			// Create MedicalType with all parameters 
			medicalType = new MedicalType(code, description);
		}

		return medicalType;
	}

	public void setParameters(MedicalType medicalType) {
		medicalType.setCode(code);
		medicalType.setDescription(description);
	}

	public void check(MedicalType medicalType) {
		assertThat(medicalType.getCode()).isEqualTo(code);
		assertThat(medicalType.getDescription()).isEqualTo(description);
	}
}
