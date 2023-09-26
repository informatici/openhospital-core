/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.telemetry.envdatacollector.collectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoBean;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoCommonService;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoSettings;
import org.isf.telemetry.envdatacollector.constants.CollectorsConstants;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 60)
@Component
public class _LocationDataCollector {

	private static final String ID = "TEL_LOCATION";
	private static final Logger LOGGER = LoggerFactory.getLogger(_LocationDataCollector.class);

	@Autowired
	private GeoIpInfoSettings settings;

	@Autowired
	List<GeoIpInfoCommonService> geoIpServices;

	public String getId() {
		return ID;
	}

	public String getDescription() {
		return "Location information (ex. Italy)";
	}

	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting location data...");
		Map<String, String> result = new HashMap<>();
		try {

			String geoIpServiceName = this.settings.get("telemetry.enabled.geo.ip.lookup.service");
			LOGGER.debug(geoIpServiceName + " - " + geoIpServices.size());
			this.geoIpServices.forEach(service -> {
				if (service.getServiceName().equals(geoIpServiceName)) {
					GeoIpInfoBean json = service.retrieveIpInfo();
					LOGGER.debug(json.toString());
					result.put(CollectorsConstants.LOC_COUNTRY_NAME, json.getCountryName());
					result.put(CollectorsConstants.LOC_COUNTRY_CODE, json.getCountryCode());
					result.put(CollectorsConstants.LOC_REGION_NAME, json.getRegionName());
					result.put(CollectorsConstants.LOC_CITY, json.getCity());
					result.put(CollectorsConstants.LOC_ZIP_CODE, json.getPostalCode());
					result.put(CollectorsConstants.LOC_TIMEZONE, json.getTimeZone());
					result.put(CollectorsConstants.LOC_CURRENCY_CODE, json.getCurrencyCode());
				}
			});
		} catch (RuntimeException e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.error(e.toString());
			throw new OHException("Data collector [" + ID + "]", e);
		}
		return result;
	}

}