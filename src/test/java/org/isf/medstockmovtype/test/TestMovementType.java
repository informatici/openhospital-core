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
package org.isf.medstockmovtype.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.medstockmovtype.model.MovementType;
import org.isf.utils.exception.OHException;

public class TestMovementType {

	private String code = "ZZABCD";
	private String description = "TestDescription";
	private String type = "+";

	public MovementType setup(boolean usingSet) throws OHException {
		MovementType movementType;

		if (usingSet) {
			movementType = new MovementType();
			setParameters(movementType);
		} else {
			// Create MovementType with all parameters 
			movementType = new MovementType(code, description, type);
		}

		return movementType;
	}

	public void setParameters(MovementType movementType) {
		movementType.setCode(code);
		movementType.setDescription(description);
		movementType.setType(type);
	}

	public void check(MovementType movementType) {
		assertThat(movementType.getCode()).isEqualTo(code);
		assertThat(movementType.getDescription()).isEqualTo(description);
		assertThat(movementType.getType()).isEqualTo(type);
	}
}
