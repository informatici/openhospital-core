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
package org.isf.telemetry.manager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.isf.generaldata.GeneralData;
import org.isf.telemetry.model.Telemetry;
import org.isf.telemetry.model.TelemetryId;
import org.isf.telemetry.service.TelemetryRepository;
import org.springframework.stereotype.Component;

@Component
public class TelemetryManager {

	private TelemetryRepository telemetryRepository;

	public TelemetryManager(TelemetryRepository telemetryRepository) {
		this.telemetryRepository = telemetryRepository;
	}

	public Telemetry enable(Map<String, Boolean> consentMap) {
		Telemetry telemetry = retrieveOrBuildNewTelemetry();
		telemetry.setConsentMap(consentMap);
		telemetry.setActive(Boolean.TRUE);
		telemetry.setOptinDate(LocalDateTime.now());
		telemetry.setOptoutDate(null);
		return telemetry;
	}

	public Telemetry save(Telemetry telemetry) {
		return this.telemetryRepository.save(telemetry);
	}

	public Telemetry disable(Map<String, Boolean> consentMap) {
		Telemetry telemetry = retrieveOrBuildNewTelemetry();
		telemetry.setConsentMap(consentMap);
		telemetry.setActive(Boolean.FALSE);
		telemetry.setOptoutDate(LocalDateTime.now());
		return telemetry;
	}

	public Telemetry retrieveOrBuildNewTelemetry() {
		List<Telemetry> list = this.telemetryRepository.findAll();
		Telemetry telemetry = null;
		if (list.isEmpty()) {
			telemetry = new Telemetry();
			telemetry.setId(new TelemetryId());
			// TODO update the following field after sending message
			// telemetry.setSentTimestamp(new Date());
			telemetry.getId().setDatabaseUUID(this.generateUUID());
			telemetry.getId().setHardwareUUID(this.generateUUID());
			telemetry.getId().setOperativeSystemUUID(this.generateUUID());
			telemetry.getId().setSoftwareUUID(this.generateUUID());
		} else {
			telemetry = list.get(0);
		}
		return telemetry;
	}

	public Telemetry retrieveSettings() {
		return this.telemetryRepository.findFirstByOrderById_SoftwareUUIDAsc();
	}

	private String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public Telemetry updateStatusSuccess(String info) {
		return updateStatusCommon(info);
	}

	private Telemetry updateStatusCommon(String info) {
		Telemetry telemetry = retrieveOrBuildNewTelemetry();
		if (!GeneralData.DEBUG) {
			telemetry.setSentTimestamp(LocalDateTime.now());
		}
		telemetry.setInfo(info);
		return this.telemetryRepository.save(telemetry);
	}

	public Telemetry updateStatusFail(String info) {
		return updateStatusCommon(info);
	}

}
