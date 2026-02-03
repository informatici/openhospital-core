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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.isf.OHCoreTestCase;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.Test;

class TestTelemetry extends OHCoreTestCase {

	@Test
	void testModel() {
		Telemetry telemetry = new Telemetry();

		LocalDateTime now = TimeTools.getNow();
		telemetry.setSentTimestamp(now);
		assertThat(telemetry.getSentTimestamp()).isEqualTo(now);

		telemetry.setActive(true);
		assertThat(telemetry.getActive()).isTrue();

		telemetry.setOptinDate(now.plusDays(1));
		assertThat(telemetry.getOptinDate()).isEqualTo(now.plusDays(1));

		telemetry.setOptoutDate(now.minusDays(1));
		assertThat(telemetry.getOptoutDate()).isEqualTo(now.minusDays(1));

		telemetry.setConsentData("consentData");
		assertThat(telemetry.getConsentData()).isEqualTo("consentData");

		telemetry.setInfo("info");
		assertThat(telemetry.getInfo()).isEqualTo("info");

		telemetry.setHashCode(-127);
		assertThat(telemetry.getHashCode()).isEqualTo(-127);

		TelemetryId telemetryId = new TelemetryId();
		telemetry.setId(telemetryId);
		assertThat(telemetry.getId()).isSameAs(telemetryId);

		Map<String, Boolean> consentMap = new HashMap<>();
		consentMap.put("string", false);
		consentMap.put("string2", true);
		consentMap.put("string3", true);
		consentMap.put("string4", false);
		telemetry.setConsentMap(consentMap);
		Map<String, Boolean> consentMap2 = telemetry.getConsentMap();
		assertThat(consentMap).isEqualTo(consentMap2);

		telemetry.setConsentMap(null);
		assertThat(telemetry.getConsentMap()).isNull();

		consentMap = new HashMap<>();
		telemetry.setConsentMap(consentMap);
		assertThat(telemetry.getConsentMap()).isEmpty();

		assertThat(telemetry.toString()).isNotNull();
	}
}
