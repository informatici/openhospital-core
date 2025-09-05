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

import org.isf.encounter.model.Encounter;
import org.isf.encounter.model.EncounterStatus;
import org.isf.utils.exception.OHException;

import java.time.LocalDateTime;

public class TestEncounter {

	private String code = "Z";
	private EncounterStatus  status = EncounterStatus.OPEN;
	private LocalDateTime performedAt = LocalDateTime.of(2025, 1, 1, 10, 0);

	public Encounter setup(boolean usingSet) throws OHException {
		Encounter encounter;

		if (usingSet) {
			encounter = new Encounter();
			setParameters(encounter);
		} else {
			encounter = new Encounter(code, status, null, performedAt, null);
		}

		return encounter;
	}

	public void setParameters(Encounter encounter) {
		encounter.setCode(code);
		encounter.setStatus(status);
		encounter.setPerformedAt(performedAt);
	}

	public void check(Encounter encounter) {
		assertThat(encounter.getCode()).isEqualTo(code);
		assertThat(encounter.getStatus()).isEqualTo(status);
		assertThat(encounter.getPerformedAt()).isEqualTo(performedAt);
	}
}
