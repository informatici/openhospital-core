/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.encounter;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.encouter.model.Encounter;
import org.isf.encouter.model.EncounterStatus;
import org.isf.utils.exception.OHException;

public class TestEncounter {

	private String code = "Z";
	private EncounterStatus  status = EncounterStatus.OPEN;

	public Encounter setup(boolean usingSet) throws OHException {
		Encounter encounter;

		if (usingSet) {
			encounter = new Encounter();
			setParameters(encounter);
		} else {
			encounter = new Encounter(code, status);
		}

		return encounter;
	}

	public void setParameters(Encounter encounter) {
		encounter.setCode(code);
		encounter.setStatus(status);
	}

	public void check(Encounter encounter) {
		assertThat(encounter.getCode()).isEqualTo(code);
		assertThat(encounter.getStatus()).isEqualTo(status);
	}
}
