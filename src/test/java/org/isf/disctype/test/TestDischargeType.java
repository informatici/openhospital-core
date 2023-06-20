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
package org.isf.disctype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.disctype.model.DischargeType;
import org.isf.utils.exception.OHException;

public class TestDischargeType {

	private String code = "ZZ";
	private String description = "TestDescription";

	public DischargeType setup(boolean usingSet) throws OHException {
		DischargeType dischargeType;

		if (usingSet) {
			dischargeType = new DischargeType();
			setParameters(dischargeType);
		} else {
			// Create DischargeType with all parameters 
			dischargeType = new DischargeType(code, description);
		}

		return dischargeType;
	}

	public void setParameters(DischargeType dischargeType) {
		dischargeType.setCode(code);
		dischargeType.setDescription(description);
	}

	public void check(DischargeType dischargeType) {
		assertThat(dischargeType.getCode()).isEqualTo(code);
		assertThat(dischargeType.getDescription()).isEqualTo(description);
	}
}
