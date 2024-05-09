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
package org.isf.telemetry.envdatacollector.collectors.remote.geoiplookup;

import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoCommonService;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GeoIpLookupService extends GeoIpInfoCommonService {

	private static final String SERVICE_NAME = "geoiplookup-remote-service";
	private static final Logger LOGGER = LoggerFactory.getLogger(GeoIpLookupService.class);

	private GeoIpInfoSettings settings;

	public GeoIpLookupService(GeoIpInfoSettings geoIpInfoSettings) {
		this.settings = geoIpInfoSettings;
	}

	public GeoIpLookup retrieveIpInfo() {
		GeoIpLookupRemoteService httpClient = super.buildHttlClient(this.settings.retrieveBaseUrl(this.getServiceName()), GeoIpLookupRemoteService.class,
						GeoIpLookupService.class);
		LOGGER.debug("GeoIpLookup request start");
		ResponseEntity<GeoIpLookup> rs = httpClient.retrieveIpInfo();
		GeoIpLookup result = rs.getBody();
		LOGGER.debug("GeoIpLookup response: {}", result);
		return result;
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

}