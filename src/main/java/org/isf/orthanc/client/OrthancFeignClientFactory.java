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
package org.isf.orthanc.client;

import org.isf.generaldata.OrthancConfig;
import org.isf.orthanc.utils.CustomErrorDecoder;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.stereotype.Component;

import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;

/**
 * @author Silevester D.
 * @since 1.15
 */
@Component
public class OrthancFeignClientFactory {

	private OrthancAPIClient client;

	public OrthancFeignClientFactory() {
		OrthancConfig.initialize();
	}

	/**
	 * Create a client to perform request
	 *
	 * @return an instance of {@link OrthancAPIClient}
	 * @throws OHServiceException When ORTHANC not properly configured
	 */
	public OrthancAPIClient createClient(boolean reset) throws OHServiceException {
		if (!validateConfiguration()) {
			throw new OHServiceException(new OHExceptionMessage("ORTHANC server is not properly configured"));
		}

		if (client != null && !reset) return client;

		client = Feign.builder()
			.encoder(new GsonEncoder())
			.decoder(new GsonDecoder())
			.requestInterceptor(new BasicAuthRequestInterceptor(OrthancConfig.ORTHANC_USERNAME, OrthancConfig.ORTHANC_PASSWORD))
			.logger(new Slf4jLogger(OrthancAPIClient.class))
			.errorDecoder(new CustomErrorDecoder())
			.logLevel(Logger.Level.FULL)
			.contract(new SpringMvcContract())
			.target(OrthancAPIClient.class, OrthancConfig.ORTHANC_BASE_URL);

		return client;
	}

	/**
	 * Validate ORTHANC configuration
	 * This method only ensures that necessary parameters have been set
	 *
	 * @return <code>true</code> if the configuration is valid, <code>false</code> otherwise
	 */
	public boolean validateConfiguration() {
		return OrthancConfig.ORTHANC_BASE_URL != null
			&& !OrthancConfig.ORTHANC_BASE_URL.isEmpty()
			&& OrthancConfig.ORTHANC_USERNAME != null
			&& !OrthancConfig.ORTHANC_USERNAME.isEmpty()
			&& OrthancConfig.ORTHANC_PASSWORD != null
			&& !OrthancConfig.ORTHANC_PASSWORD.isEmpty();
	}
}