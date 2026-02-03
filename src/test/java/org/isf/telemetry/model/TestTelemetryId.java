/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.telemetry.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.junit.jupiter.api.Test;

class TestTelemetryId extends OHCoreTestCase {

	@Test
	void testModel() {
		TelemetryId telemetryId = new TelemetryId();

		telemetryId.setSoftwareUUID("SoftwareUUID");
		assertThat(telemetryId.getSoftwareUUID()).isEqualTo("SoftwareUUID");

		telemetryId.setDatabaseUUID("DatabaseUUID");
		assertThat(telemetryId.getDatabaseUUID()).isEqualTo("DatabaseUUID");

		telemetryId.setHardwareUUID("HardwareUUID");
		assertThat(telemetryId.getHardwareUUID()).isEqualTo("HardwareUUID");

		telemetryId.setOperativeSystemUUID("OperativeSystemUUID");
		assertThat(telemetryId.getOperativeSystemUUID()).isEqualTo("OperativeSystemUUID");

		assertThat(telemetryId.equals(null)).isFalse();
		assertThat(telemetryId.equals(3)).isFalse();
		assertThat(telemetryId.equals(telemetryId)).isTrue();

		TelemetryId telemetryId2 = new TelemetryId();
		telemetryId2.setSoftwareUUID("SoftwareUUID");
		telemetryId2.setDatabaseUUID("DatabaseUUID");
		telemetryId2.setHardwareUUID("HardwareUUID");
		telemetryId2.setOperativeSystemUUID("OperativeSystemUUIDXXXXXXXX");
		assertThat(telemetryId.equals(telemetryId2)).isFalse();
	}
}
