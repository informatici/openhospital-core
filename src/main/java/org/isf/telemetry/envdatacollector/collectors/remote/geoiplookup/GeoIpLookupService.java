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
package org.isf.telemetry.envdatacollector.collectors.remote.geoiplookup;

import java.util.Properties;

import javax.annotation.Resource;

import org.isf.sms.providers.common.CustomCommonDecoder;
import org.isf.sms.providers.common.CustomCommonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.Feign;
import feign.slf4j.Slf4jLogger;

@Component
@PropertySource("classpath:telemetry.properties")
public class GeoIpLookupService {
	
	@Autowired
	private Environment env;

	private static final String SERVICE_NAME = "geoiplookup-remote-service";
	private static final Logger LOGGER = LoggerFactory.getLogger(GeoIpLookupService.class);

	public GeoIpLookup retrieveGeoIpInfo() {
		GeoIpLookupRemoteService httpClient = buildHttlClient();
		LOGGER.debug("GeoIpLookup request start");
		ResponseEntity<GeoIpLookup> rs = httpClient.retrieveGeoIPInfo();
		GeoIpLookup result = rs.getBody();
		LOGGER.debug("GeoIpLookup response: {}", result);
		return result;
	}

	private GeoIpLookupRemoteService buildHttlClient() {
		String baseUrl = this.env.getProperty(SERVICE_NAME  + ".ribbon.base-url");
		// For debug remember to update log level to: feign.Logger.Level.FULL. Happy debugging!
		return Feign.builder().encoder(new CustomCommonEncoder()).decoder(new CustomCommonDecoder()).logger(new Slf4jLogger(GeoIpLookupService.class))
						.logLevel(feign.Logger.Level.BASIC).contract(new SpringMvcContract()).target(GeoIpLookupRemoteService.class, baseUrl);
	}

}
