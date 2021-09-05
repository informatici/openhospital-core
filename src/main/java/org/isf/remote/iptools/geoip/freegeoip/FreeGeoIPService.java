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
package org.isf.remote.iptools.geoip.freegeoip;

import java.util.Properties;

import javax.annotation.Resource;

import org.isf.sms.providers.common.CustomCommonDecoder;
import org.isf.sms.providers.common.CustomCommonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.Feign;
import feign.slf4j.Slf4jLogger;

@Component
public class FreeGeoIPService {

	private static final String SERVICE_NAME = "freegeoip-remote-service";
	private static final Logger LOGGER = LoggerFactory.getLogger(FreeGeoIPService.class);

	@Resource(name = "ipinfoProperties")
	private Properties properties;

	public FreeGeoIPJSON retrieveGeoIpInfo() {
		FreeGeoIPRemoteService httpClient = buildHttlClient();
		LOGGER.debug("FreeGeoIP request start");
		ResponseEntity<FreeGeoIPJSON> rs = httpClient.retrieveGeoIPInfo();
		FreeGeoIPJSON result = rs.getBody();
		LOGGER.debug("FreeGeoIP response: {}", result);
		return result;
	}

	private FreeGeoIPRemoteService buildHttlClient() {
		String baseUrl = this.properties.getProperty(SERVICE_NAME + ".ribbon.base-url");
		// For debug remember to update log level to: feign.Logger.Level.FULL. Happy debugging!
		return Feign.builder().encoder(new CustomCommonEncoder()).decoder(new CustomCommonDecoder()).logger(new Slf4jLogger(FreeGeoIPService.class))
						.logLevel(feign.Logger.Level.BASIC).contract(new SpringMvcContract()).target(FreeGeoIPRemoteService.class, baseUrl);
	}

}
