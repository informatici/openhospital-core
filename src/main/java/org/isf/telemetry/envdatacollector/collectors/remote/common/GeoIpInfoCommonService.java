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
package org.isf.telemetry.envdatacollector.collectors.remote.common;

import org.isf.sms.providers.common.CustomCommonDecoder;
import org.isf.sms.providers.common.CustomCommonEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;

import feign.Feign;
import feign.Logger.Level;
import feign.slf4j.Slf4jLogger;

public abstract class GeoIpInfoCommonService {

	public abstract GeoIpInfoBean retrieveIpInfo();

	public abstract String getServiceName();

	protected <T, S> T buildHttlClient(String baseUrl,
					Class<T> remoteServiceClass, Class<S> serviceClass) {
		// For debug update log level to: Level.FULL
		return Feign.builder().encoder(new CustomCommonEncoder()).decoder(new CustomCommonDecoder())
						.logger(new Slf4jLogger(serviceClass)).logLevel(Level.BASIC).contract(new SpringMvcContract())
						.target(remoteServiceClass, baseUrl);
	}
}