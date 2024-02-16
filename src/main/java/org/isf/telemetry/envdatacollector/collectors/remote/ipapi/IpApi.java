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
package org.isf.telemetry.envdatacollector.collectors.remote.ipapi;

import java.io.Serializable;

import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoBean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IpApi extends GeoIpInfoBean implements Serializable {

	private static final long serialVersionUID = -4751558360322494817L;

	@JsonProperty("query")
	private String ip;

	@JsonProperty("countryCode")
	private String countryCode;

	@JsonProperty("country")
	private String countryName;

	@JsonProperty("regionName")
	private String regionName;

	@JsonProperty("city")
	private String city;

	@JsonProperty("zip")
	private String postalCode;

	@JsonProperty("timezone")
	private String timeZone;

	@JsonProperty("lat")
	private Double latitude;

	@JsonProperty("lon")
	private Double longitude;

	@JsonProperty("currency")
	private String currencyCode;

}