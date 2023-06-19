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

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.isf.generaldata.Version;
import org.isf.menu.service.MenuIoOperations;
import org.isf.opd.service.OpdIoOperations;
import org.isf.patient.service.PatientIoOperations;
import org.isf.telemetry.envdatacollector.AbstractDataCollector;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoBean;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoCommonService;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoSettings;
import org.isf.telemetry.envdatacollector.constants.CollectorsConst;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.service.WardIoOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 30)
@Component
public class OpenHospitalDataCollector extends AbstractDataCollector {

	private static final String ID = "FUN_OH";
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenHospitalDataCollector.class);

	@Autowired
	private PatientIoOperations patientIoOperations;

	@Autowired
	private MenuIoOperations menuIoOperations;

	@Autowired
	private WardIoOperations wardIoOperations;

	@Autowired
	private OpdIoOperations opdIoOperations;

	@Autowired
	private List<GeoIpInfoCommonService> geoIpServices;

	@Autowired
	private GeoIpInfoSettings settings;

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Hospital information (ex. Italy, 100 beds, 5 wards, etc.)";
	}

	@Override
	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting Open Hospital data...");
		Map<String, String> result = new LinkedHashMap<>();
		try {

			String geoIpServiceName = settings.get("telemetry.enabled.geo.ip.lookup.service");
			LOGGER.debug(geoIpServiceName + " - " + geoIpServices.size());
			this.geoIpServices.forEach(service -> {
				if (service.getServiceName().equals(geoIpServiceName)) {
					GeoIpInfoBean json = service.retrieveIpInfo();
					LOGGER.debug(json.toString());
					result.put(CollectorsConst.LOC_COUNTRY_NAME, json.getCountryName());
					result.put(CollectorsConst.LOC_COUNTRY_CODE, json.getCountryCode());
					result.put(CollectorsConst.LOC_REGION_NAME, json.getRegionName());
					result.put(CollectorsConst.LOC_CITY, json.getCity());
					result.put(CollectorsConst.LOC_ZIP_CODE, json.getPostalCode());
					result.put(CollectorsConst.LOC_TIMEZONE, json.getTimeZone());
					result.put(CollectorsConst.LOC_CURRENCY_CODE, json.getCurrencyCode());
					return;
				}
			});

			Version.initialize();
			result.put(CollectorsConst.APP_VERSION, Version.VER_MAJOR.concat(".").concat(Version.VER_MINOR).concat(".").concat(Version.VER_RELEASE));
			// result.put(CollectorsConst.APP_VER_MAJOR, Version.VER_MAJOR);
			// result.put(CollectorsConst.APP_VER_MINOR, Version.VER_MINOR);
			// result.put(CollectorsConst.APP_RELEASE, Version.VER_RELEASE);

			result.put(CollectorsConst.OH_TOTAL_ACTIVE_PATIENTS, String.valueOf(patientIoOperations.countAllActivePatients()));
			result.put(CollectorsConst.OH_TOTAL_ACTIVE_USERS, String.valueOf(this.menuIoOperations.countAllActive()));
			result.put(CollectorsConst.OH_TOTAL_ACTIVE_WARDS, String.valueOf(this.wardIoOperations.countAllActiveWards()));
			result.put(CollectorsConst.OH_TOTAL_ACTIVE_BEDS, String.valueOf(this.wardIoOperations.countAllActiveBeds()));

			LocalDateTime lastUsedTime = opdIoOperations.lastOpdCreationDate();
			if (lastUsedTime == null) {
				lastUsedTime = LocalDateTime.now();
			}
			result.put(CollectorsConst.TIME_LAST_USED, String.valueOf(lastUsedTime));
		} catch (OHServiceException e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.error(e.toString());
			throw new OHException("Data collector [" + ID + "]", e);
		}
		return result;
	}

}
