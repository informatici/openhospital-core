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
package org.isf.admtype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.admtype.model.AdmissionType;
import org.isf.utils.exception.OHException;

public class TestAdmissionType {

	private String code = "ZZ";
	private String description = "TestDescription";

	public AdmissionType setup(boolean usingSet) throws OHException {
		AdmissionType admissionType;

		if (usingSet) {
			admissionType = new AdmissionType();
			setParameters(admissionType);
		} else {
			// Create AdmissionType with all parameters 
			admissionType = new AdmissionType(code, description);
		}

		return admissionType;
	}

	public void setParameters(AdmissionType admissionType) {
		admissionType.setCode(code);
		admissionType.setDescription(description);
	}

	public void check(AdmissionType admissionType) {
		assertThat(admissionType.getCode()).isEqualTo(code);
		assertThat(admissionType.getDescription()).isEqualTo(description);
	}
}
