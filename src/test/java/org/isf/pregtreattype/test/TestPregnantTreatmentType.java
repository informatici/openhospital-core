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
package org.isf.pregtreattype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.exception.OHException;

public class TestPregnantTreatmentType {

	private String code = "ZZ";
	private String description = "TestDescription";

	public PregnantTreatmentType setup(boolean usingSet) throws OHException {
		PregnantTreatmentType pregnantTreatmentType;

		if (usingSet) {
			pregnantTreatmentType = new PregnantTreatmentType();
			setParameters(pregnantTreatmentType);
		} else {
			// Create PregnantTreatmentType with all parameters 
			pregnantTreatmentType = new PregnantTreatmentType(code, description);
		}

		return pregnantTreatmentType;
	}

	public void setParameters(PregnantTreatmentType pregnantTreatmentType) {
		pregnantTreatmentType.setCode(code);
		pregnantTreatmentType.setDescription(description);
	}

	public void check(PregnantTreatmentType pregnantTreatmentType) {
		assertThat(pregnantTreatmentType.getCode()).isEqualTo(code);
		assertThat(pregnantTreatmentType.getDescription()).isEqualTo(description);
	}
}
